package at.htl.billard;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.paint.Color;

public class BillardApp extends GameApplication
{

        @Override
        protected void initSettings(GameSettings settings)
        {
                settings.setTitle("Billard Simulator");
                settings.setMainMenuEnabled(true);
                //   settings.setAppIcon("icon.png");
        }
        @Override
        protected void initGame()
        {
                FXGL.getGameWorld().addEntityFactory(new BillardFactory());
                FXGL.spawn("ball", new SpawnData(200, 200)
                        .put("color", Color.BLUE)
                        .put("number", 1)
                        .put("striped", true));

        }
        public static void main(String[] args) {
                launch(args);
        }



}
