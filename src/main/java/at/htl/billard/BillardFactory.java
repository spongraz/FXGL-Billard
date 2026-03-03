package at.htl.billard;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class BillardFactory implements EntityFactory {

    @Spawns("ball")
    public Entity newBall(SpawnData data) {

        int number = data.get("number");
        Color color = data.get("color");
        boolean striped = data.get("striped"); //

        Circle outline = new Circle(16, Color.BLACK); // Outline
        Circle inner = new Circle(15, Color.WHITE);   // Grundfarbe

        StackPane view = new StackPane(outline, inner);

        if (striped) {
            Rectangle stripe = new Rectangle(28, 12);       //Besser machen irgendwie
            stripe.setArcWidth(6);
            stripe.setArcHeight(6);
            stripe.setFill(color);

            view.getChildren().add(stripe);
        } else {
            inner.setFill(color); // Vollfarbe
        }

        Text text = new Text(String.valueOf(number));
        text.setFill(Color.BLACK);
        view.getChildren().add(text);

        return FXGL.entityBuilder(data)
                .view(view)
                .build();
    }
}