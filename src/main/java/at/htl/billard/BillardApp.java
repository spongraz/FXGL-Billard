package at.htl.billard;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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

                double appW = getAppWidth();
                double appH = getAppHeight();

                double tableW = appW * 0.8;
                double tableH = appH * 0.7;

                double x = (appW - tableW) / 2.0;
                double y = (appH - tableH) / 2.0;

                //Tisch spawnen
                Rectangle table = new Rectangle(tableW, tableH);
                table.setFill(Color.DARKGREEN);
                table.setArcWidth(30);
                table.setArcHeight(30);
                table.setStroke(Color.SADDLEBROWN);
                table.setStrokeWidth(25);

                FXGL.entityBuilder() //Dass Tisch unter den Kugeln ist.
                        .at(x, y)
                        .view(table)
                        .zIndex(-10)
                        .buildAndAttach();

                spawnBalls();
                spawnPockets();

        }
        private void spawnBalls() {

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
                        Color.SADDLEBROWN //Normal Brown is rot, deswegen Saddlebrown
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
        public void spawnPockets()
        {
                double appW = getAppWidth();
                double appH = getAppHeight();

                double tableW = appW * 0.8;
                double tableH = appH * 0.7;

                double tableX = (appW - tableW) / 2.0;
                double tableY = (appH - tableH) / 2.0;

                double r = 20;


                double[][] positions = {
                        { tableX+ r*2,              tableY  + r*2        },   // oben links
                        { tableX + tableW / 2, tableY   + r*2       },   // oben Mitte
                        { tableX + tableW,     tableY  +r * 2       },   // oben rechts
                        { tableX + r*2,              tableY + tableH },   // unten links
                        { tableX + tableW / 2, tableY + tableH },   // unten Mitte
                        { tableX + tableW,     tableY + tableH }    // unten rechts
                };

            for (double[] position : positions) {
                Circle pocket = new Circle(r, Color.BLACK);

                FXGL.entityBuilder()
                        .at(position[0] - r, position[1] - r)
                        .view(pocket)
                        .zIndex(-5)
                        .buildAndAttach();
            }
/*
Cushion-Winkel bei einem Poolbillardtisch

=== 1) Eck-Pockets (Corner Pockets) — Anzahl: 4 ===

Jede Ecktasche wird von zwei kurzen Cushion-Enden gebildet,
die symmetrisch nach innen zeigen.

Gesamtöffnungswinkel: ~142°

Da die Ecke einen 90°-Winkel hat, berechnet sich der
Cushion-Winkel pro Seite so:
  (180° - 142°) / 2 = 19° pro Seite

→ Jedes Cushion-Ende ist um 19° gegenüber der Bandenlinie
  nach innen geneigt.


=== 2) Seiten-Pockets (Side Pockets) — Anzahl: 2 ===

Die Mitteltaschen liegen an den langen Seiten des Tisches.
Beide Cushion-Enden zeigen entlang der geraden Längsbande
aufeinander zu.

Gesamtöffnungswinkel: ~104°

Der Winkel pro Seite ergibt sich aus dem Supplementärwinkel
zur geraden Bandenlinie (180°):
  (180° - 104°) / 2 = 38° pro Seite

→ Jedes Cushion-Ende ist um 38° gegenüber der Bandenlinie
  nach innen geneigt.


=== Übersicht ===

  Pocket-Typ  | Öffnungswinkel | Winkel pro Seite
  ------------|----------------|------------------
  Corner      |    ~142°       |      ~19°
  Side        |    ~104°       |      ~38°

TODO: Cushions hinzufügen.
*/



        }
        public static void main(String[] args) {
                launch(args);
        }


}
