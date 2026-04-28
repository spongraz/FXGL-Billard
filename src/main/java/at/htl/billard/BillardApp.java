package at.htl.billard;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class BillardApp extends GameApplication
{
        private Entity whiteBall;
        private boolean whiteBallSelected = false;
        private Entity guideLine;

        @Override
        protected void initSettings(GameSettings settings)
        {
                settings.setTitle("Billard Simulator");
                settings.setMainMenuEnabled(true);
              //  settings.setAppIcon("icon.png"); //Geht nicht.
        }
//        @Override
//        protected void initUI() {
//                FXGL.getPrimaryStage().getIcons().clear();
//                FXGL.getPrimaryStage().getIcons().add(
//                        new Image(getClass().getResourceAsStream("/icon.png"))
//                );
//        }
        @Override
        protected void initGame()
        {
                FXGL.getGameWorld().addEntityFactory(new BillardBallFactory());
                FXGL.getGameScene().setBackgroundColor(Color.GREY);

                // Schwerkraft auf 0 setzen (keine Schwerkraft)
                FXGL.getPhysicsWorld().setGravity(0, 0);

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

                // Grenzen des Tisches für alle Kugeln festlegen
                setBallBoundaries(layout);
        }

        @Override
        protected void onUpdate(double tpf) {

               var balls = FXGL.getGameWorld().getEntitiesByComponent(PhysicsComponent.class);

                if (whiteBallSelected && whiteBall != null) {
                        // Alte Linie löschen
                        if (guideLine != null) {
                                guideLine.removeFromWorld();
                        }

                        // Ballmittelpunkt
                        double ballCenterX = whiteBall.getX() + 13;
                        double ballCenterY = whiteBall.getY() + 13;
                        double mouseX = getInput().getMouseXWorld();
                        double mouseY = getInput().getMouseYWorld();

                        // Neue Linie zeichnen
                        Line line = new Line(ballCenterX, ballCenterY, mouseX, mouseY);
                        line.setStroke(Color.LIGHTGREY);
                        line.setStrokeWidth(2);

                        guideLine = FXGL.entityBuilder()
                                .at(0, 0)
                                .view(line)
                                .zIndex(100)  // Im Vordergrund
                                .buildAndAttach();

                        // Hilfslinie nur anzeigen, wenn sie lang genug ist (mindestens 2.5 Pixel)
                        double lineLength = Math.sqrt(
                                Math.pow(line.getEndX() - line.getStartX(), 2) +
                                Math.pow(line.getEndY() - line.getStartY(), 2)
                        );
                        if(lineLength <= 2.5 ) {
                                line.setVisible(false);
                        }
                }
                // Kollisionserkennung zwischen den Kugeln aufrufen
                checkBallCollisions();
                checkPocketCollisions();
        }

        private void checkPocketCollisions() {
                var balls = FXGL.getGameWorld().getEntitiesByComponent(PhysicsComponent.class);
                TableLayout layout = TableLayout.fromAppSize(getAppWidth(), getAppHeight());
                double r = layout.getPocketRadius();

                for (Entity ball : balls) {
                        double bx = ball.getX() + 13;
                        double by = ball.getY() + 13;
                        for (double[] center : layout.getPocketCenters()) {
                                double dist = Math.sqrt(Math.pow(bx - center[0], 2) + Math.pow(by - center[1], 2));
                                if (dist < r) {
                                        ball.removeFromWorld();
                                        if (ball == whiteBall) {
                                                whiteBall = null;
                                                whiteBallSelected = false;
                                                System.out.println("Weiße Kugel weg :(");
                                                //TODO: Weiße Kugel neu spawnen
                                        }
                                        break;
                                }
                        }
                }
        }

        private void checkBallCollisions()
        {
                var balls = FXGL.getGameWorld().getEntitiesByComponent(PhysicsComponent.class);

                for (int i = 0; i < balls.size(); i++) {
                        for (int j = i + 1; j < balls.size(); j++) {
                                Entity ball1 = balls.get(i);
                                Entity ball2 = balls.get(j);

                                double dx = ball2.getX() - ball1.getX();
                                double dy = ball2.getY() - ball1.getY();
                                double distance = Math.sqrt(dx * dx + dy * dy);

                                // Überprüfen, ob sich die Kugeln berühren (Abstand < Durchmesser)
                                if (distance < 26) {  // 2 * Radius
                                        handleBallCollision(ball1, ball2);
                                }
                        }
                }
        }

        @Override
        protected void initInput()
        {
                getInput().addAction(new UserAction("LMB") {
                        @Override
                        protected void onActionBegin() {
                                // Mausklick registrieren
                                if (whiteBall != null) {
                                        double mouseX = getInput().getMouseXWorld();
                                        double mouseY = getInput().getMouseYWorld();
                                        double ballX = whiteBall.getX();
                                        double ballY = whiteBall.getY();
                                        // double speed = getInput().getMousePositionWorld().distance(ballX, ballY) * 0.1;

                                        // Prüfe ob Maus auf der Kugel ist
                                        double distance = Math.sqrt(
                                                Math.pow(mouseX - ballX - 11, 2) + Math.pow(mouseY - ballY - 11, 2)
                                        );

                                        if (distance <= 51) {
                                                System.out.println("Kugel wurde geklickt! :D");
                                                whiteBallSelected = true;
                                        }
                                }
                        }

                        @Override
                        protected void onActionEnd() {
                                if (whiteBall != null && whiteBallSelected) {
                                        // Ballmittelpunkt
                                        double ballCenterX = whiteBall.getX() + 13;
                                        double ballCenterY = whiteBall.getY() + 13;
                                        
                                        // Mausposition
                                        double mouseX = getInput().getMouseXWorld();
                                        double mouseY = getInput().getMouseYWorld();

                                        // Richtungsvektor (von Ball zur Maus)
                                        Vec2 direction = new Vec2(mouseX - ballCenterX, mouseY - ballCenterY);
                                        
                                        // Distanz berechnen
                                        double distance = direction.length();
                                        
                                        // Richtung normalisieren (Länge = 1)
                                        if (distance > 0) {
                                                direction = direction.normalize();
                                        }
                                        
                                        // Geschwindigkeit: 1 Pixel = 1 Geschwindigkeit (maximal 15)
                                        // Je länger die Linie gezogen wird, desto schneller wird die Kugel
                                        double speed = Math.min(distance * 0.1, 15);

                                        // Geschwindigkeit an die Physik-Komponente übergeben
                                        if (speed > 0) {
                                                PhysicsComponent physics = whiteBall.getComponent(PhysicsComponent.class);
                                                physics.setVelocity(direction.mul(speed));
                                        }
                                        
                                        System.out.println("Kugelgeschwindigkeit: " + speed);
                                }
                                
                                whiteBallSelected = false;
                                // Guideline löschen
                                if (guideLine != null) {
                                        guideLine.removeFromWorld();
                                        guideLine = null;
                                }
                        }

                }, MouseButton.PRIMARY);
        }
//        @Override
//        protected void initUI() {
//                Image img = new Image(getClass().getResourceAsStream("/assets/ui/cursors/cursor.png"));
//                getGameScene().getRoot().getScene().setCursor(new ImageCursor(img, 0, 0));
//        } // Versuch, Cursor zu ändern, geht nicht

        private void handleBallCollision(Entity ball1, Entity ball2) {
                PhysicsComponent physics1 = ball1.getComponent(PhysicsComponent.class);
                PhysicsComponent physics2 = ball2.getComponent(PhysicsComponent.class);

                if (physics1 == null || physics2 == null) return;

                double dx = ball2.getX() - ball1.getX();
                double dy = ball2.getY() - ball1.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance == 0) return;

                double nx = dx / distance;
                double ny = dy / distance;

                // Verhindern, dass die Kugeln ineinander stecken bleiben (Positionskorrektur)
                double overlap = 26 - distance;
                if (overlap > 0) {
                        double cx = (overlap / 2.0) * nx;
                        double cy = (overlap / 2.0) * ny;
                        ball1.setX(ball1.getX() - cx);
                        ball1.setY(ball1.getY() - cy);
                        ball2.setX(ball2.getX() + cx);
                        ball2.setY(ball2.getY() + cy);
                }

                Vec2 vel1 = physics1.getVelocity();
                Vec2 vel2 = physics2.getVelocity();

                // Relative Geschwindigkeit ermitteln (vel2 - vel1)
                double dvx = vel2.x - vel1.x;
                double dvy = vel2.y - vel1.y;

                // Geschwindigkeit entlang des Kollisionsvektors berechnen
                double dvn = dvx * nx + dvy * ny;

                // Kollision NUR verarbeiten, wenn sich die Kugeln aufeinander zubewegen
                // (relative Geschwindigkeit entlang der Kollisionsachse ist negativ, hier umgekehrt wegen Logik)
                if (dvn > 0) return;

                // Abprallfaktor (Elastizität, 0.9 bedeutet, dass 10% der Energie beim Stoß verloren gehen)
                double e = 0.9;

                // Stärke des Impulses berechnen (bei gleicher Masse beider Kugeln ergibt das 2)
                double impulse = -(1 + e) * dvn / 2.0;

                // Impuls auf beide Kugeln in die jeweilige Gegenrichtung anwenden
                physics1.addVelocity(new Vec2(-impulse * nx, -impulse * ny));
                physics2.addVelocity(new Vec2(impulse * nx, impulse * ny));
        }

        private void setBallBoundaries(TableLayout layout) {
                // Den Rand des Tisches für alle Kugeln festlegen
                var balls = FXGL.getGameWorld().getEntitiesByComponent(PhysicsComponent.class);

                double minX = layout.getTableX() + 40;  // Auf Cushiondicke und Stroke anpassen
                double minY = layout.getTableY() + 40;
                double maxX = layout.getTableX() + layout.getTableW() - 40;
                double maxY = layout.getTableY() + layout.getTableH() - 40;

                java.util.List<Vec2> pockets = new java.util.ArrayList<>();
                for (double[] center : layout.getPocketCenters()) {
                        pockets.add(new Vec2(center[0], center[1]));
                }

                for (Entity ball : balls) {
                        PhysicsComponent physics = ball.getComponent(PhysicsComponent.class);
                        physics.setBounds(minX, minY, maxX, maxY);
                        physics.setPockets(pockets, layout.getPocketRadius());
                }
        }
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
                whiteBall = FXGL.spawn("ball",
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
