module com.pvz.plantsvszombies {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    exports com.pvz.plantsvszombies.Domain.Common;
    exports com.pvz.plantsvszombies.Domain.Interfaces;
    exports com.pvz.plantsvszombies.Domain.Engines;
    exports com.pvz.plantsvszombies.Domain.Entities;
    exports com.pvz.plantsvszombies.Domain.Entities.Zombies;
    exports com.pvz.plantsvszombies.Domain.Entities.Plants;
    exports com.pvz.plantsvszombies.Domain.Entities.Bullets;
    exports com.pvz.plantsvszombies.Multiplayer.Events;
    exports com.pvz.plantsvszombies.Multiplayer.Network;
    exports com.pvz.plantsvszombies.Multiplayer.Engines;

    opens com.pvz.plantsvszombies.Presentation.GUI to javafx.fxml;
    exports com.pvz.plantsvszombies.Presentation.GUI;
}