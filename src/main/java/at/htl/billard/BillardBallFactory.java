package at.htl.billard;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class BillardBallFactory implements EntityFactory {

    @Spawns("ball")
    public Entity newBall(SpawnData data)
    {

        int number = data.get("number");
        Color color = data.get("color");
        boolean striped = data.get("striped");

        // Schwarzer Rand
        Circle outline = new Circle(14, Color.BLACK);
        //TODO: ballgröße anpassen

        Circle base = new Circle(13);
        // Weiße Kugel ist weiß
        if (number == 0) {
            base.setFill(Color.WHITE);
            striped = false;
        } else {
            base.setFill(striped ? Color.WHITE : color);    //Entweder halbe oder volle Kugel
        }

        StackPane view = new StackPane(outline, base);

        //  Halbe Kugeln
        if (striped) {

            Circle stripe = new Circle(13);
            stripe.setFill(color);

            // Rechteck als Maske
            // Breite 26 (Durchmesser der Kugel), Höhe 13 (Hälfte der Kugel)
            Rectangle mask = new Rectangle(26, 13);

            // Das StackPane zentriert auf 0/0. Daher muss die Maske um die Hälfte der Breite und Höhe verschoben werden, damit sie die obere Hälfte der Kugel abdeckt.
            mask.setTranslateX(-13);
            mask.setTranslateY(-6.5); // Hälfte der Höhe

            // Den Kreis auf die Rechtecksform zuschneiden
            stripe.setClip(mask);
            view.getChildren().add(stripe);
        }

        // Weißer Mittelkreis
        if (number != 0) {
            Circle numberCircle = new Circle(7);
            numberCircle.setFill(Color.WHITE);
            numberCircle.setStroke(Color.BLACK);

            // Zahl in der Mitte
            Text text = new Text(String.valueOf(number));
            text.setFill(Color.BLACK);
            text.setStyle("-fx-font-weight: bold; -fx-font-size: 10;");

            view.getChildren().addAll(numberCircle, text);
        }

        return FXGL.entityBuilder(data)
                .view(view)
                .bbox(new HitBox(BoundingShape.circle(13)))  // Kollisionskreis mit Radius 13
                .with(new PhysicsComponent())  // Eigene Physik-Komponente hinzufügen
                .build();
    }
}