module at.htl.billard {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens at.htl.billard to javafx.fxml;
    exports at.htl.billard;
}