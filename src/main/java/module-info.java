module com.pvz.plantsvszombies {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
//    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires javafx.media;

    exports com.pvz.plantsvszombies.Domain.Common;
    exports com.pvz.plantsvszombies.Domain.Interfaces;
    exports com.pvz.plantsvszombies.Domain.Engines;
    exports com.pvz.plantsvszombies.Domain.Entities;
    exports com.pvz.plantsvszombies.Domain.Entities.Zombies;
    exports com.pvz.plantsvszombies.Domain.Entities.Plants;
    exports com.pvz.plantsvszombies.Domain.Entities.Bullets;
    exports com.pvz.plantsvszombies;
    exports com.pvz.plantsvszombies.GlobalMusicSettings;
    opens com.pvz.plantsvszombies.GlobalMusicSettings;

    opens com.pvz.plantsvszombies.Domain.Common to com.fasterxml.jackson.databind;
    opens com.pvz.plantsvszombies.Domain.Interfaces to com.fasterxml.jackson.databind;
    opens com.pvz.plantsvszombies.Domain.Engines to com.fasterxml.jackson.databind;
    opens com.pvz.plantsvszombies.Domain.Entities to com.fasterxml.jackson.databind;
    opens com.pvz.plantsvszombies.Domain.Entities.Zombies to com.fasterxml.jackson.databind;
    opens com.pvz.plantsvszombies.Domain.Entities.Plants to com.fasterxml.jackson.databind;
    opens com.pvz.plantsvszombies.Domain.Entities.Bullets to com.fasterxml.jackson.databind;

    opens com.pvz.plantsvszombies.Presentation.GUI to javafx.fxml;
    exports com.pvz.plantsvszombies.Presentation.GUI;
}