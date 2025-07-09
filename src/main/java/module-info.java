module com.pvz.plantsvszombies {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.pvz.plantsvszombies.Presentation.GUI to javafx.fxml;
    exports com.pvz.plantsvszombies.Presentation.GUI;
}