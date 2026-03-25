package at.htl.billard;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class BillardApp extends GameApplication
{

        @Override
        protected void initSettings(GameSettings settings)
        {
                settings.setTitle("Billard Simulator");
                settings.setMainMenuEnabled(true);
              //  settings.setAppIcon("icon.png"); Geht nicht.
        }
        @Override
        protected void initGame()
        {
                FXGL.getGameWorld().addEntityFactory(new BillardBallFactory());
                FXGL.getGameScene().setBackgroundColor(Color.GREY);

                TableLayout layout = TableLayout.fromAppSize(getAppWidth(), getAppHeight());

                //Tisch spawnen
                Rectangle table = new Rectangle(layout.getTableW(), layout.getTableH());
                table.setFill(Color.DARKGREEN);
                table.setArcWidth(35);
                table.setArcHeight(35);
                table.setStroke(Color.SADDLEBROWN);
                table.setStrokeWidth(30);


                FXGL.entityBuilder()
                        .at(layout.getTableX(), layout.getTableY())
                        .view(table)
                        .zIndex(-10)    //Dass Tisch unter den Kugeln ist.
                        .buildAndAttach();

                spawnBalls();
                spawnPockets(layout);
        }

        @Override
        protected void initInput()
        {
                getInput().addAction(new UserAction("LMB") {
                        private double x;
                        private double y;

                        @Override
                        protected void onActionBegin() {
                               x = getInput().getMouseXWorld();
                               y = getInput().getMouseYWorld();
                        }

                        @Override
                        protected void onActionEnd() {
                                // Schießen nach loslassen
                        }

                }, MouseButton.PRIMARY);
        }
//        @Override
//        protected void initUI() {
//                Image img = new Image(getClass().getResourceAsStream("/assets/ui/cursors/cursor.png"));
//                getGameScene().getRoot().getScene().setCursor(new ImageCursor(img, 0, 0));
//        } // Versuch, Cursor zu ändern, geht nicht

        private void spawnBalls()
        {

                double startX = 450;   // Position des Racks
                double startY = 300;

                double offset = 35;    // Abstand zwischen Kugeln

                int number = 1;

                Color[] colors = {
                        Color.YELLOW,
                        Color.BLUE,
                        Color.RED,
                        Color.PURPLE,
                        Color.ORANGE,
                        Color.GREEN,
                        Color.SADDLEBROWN
                };

                int colorIndex = 0;

                // Kugeln in Rack-Formation spawnen
                for (int row = 0; row < 5; row++) {

                        for (int col = 0; col <= row; col++) {

                                double x = startX + row * offset;
                                double y = startY - row * offset / 2 + col * offset; // Mathematik, damit Kugeln im Rack spawnen

                                // Schwarze Kugel bei Position 3,1 spawnen
                                if (row == 2 && col == 1) {
                                        FXGL.spawn("ball",
                                                new SpawnData(x, y)
                                                        .put("number", 8)
                                                        .put("color", Color.BLACK)
                                                        .put("striped", false)
                                        );
                                }
                                else {
                                        while (number == 8) { // 8 skippen
                                                number++;
                                        }

                                        int assignedNumber = number;
                                        boolean striped = assignedNumber >= 9;

                                        FXGL.spawn("ball",
                                                new SpawnData(x, y)
                                                        .put("number", assignedNumber)
                                                        .put("color", colors[colorIndex])
                                                        .put("striped", striped)
                                        );

                                        number++;
                                        colorIndex++;

                                        if (colorIndex >= colors.length)
                                                colorIndex = 0;         //reset weil nur 7 farben da sind
                                }
                        }
                }
                // Weiße Kugel spawnen
                FXGL.spawn("ball",
                        new SpawnData(200, 300)
                                .put("number", 0)
                                .put("color", Color.WHITE)
                                .put("striped", false)
                );
        }
        public void spawnPockets(TableLayout layout) {
                double r = layout.getPocketRadius();

                for (double[] center : layout.getPocketCenters()) {
                        Circle pocket = new Circle(r, Color.BLACK);

                        // entityBuilder().at() will die obere linke Ecke vom View,
                        // deswegen um r verschieben.
                        FXGL.entityBuilder()
                                .at(center[0] - r, center[1] - r)
                                .view(pocket)
                                .zIndex(-5)   // über Tisch (-10), aber unter Banden (-8) und Kugeln
                                .buildAndAttach();
                }

                spawnCushions(layout);
        }
        private void spawnCushions(TableLayout layout) {
                for (TableLayout.CushionData cushion : layout.getCushions()) {
                        Polygon poly = new Polygon(cushion.points);
                        poly.setFill(Color.GREEN);
                        poly.setStroke(Color.DARKGREEN);
                        poly.setStrokeWidth(1.5);

                        FXGL.entityBuilder()
                                .at(0, 0)
                                .view(poly)
                                .zIndex(-8)
                                .buildAndAttach();
                }
        }
        public static void main(String[] args) {
                launch(args);
        }


}
