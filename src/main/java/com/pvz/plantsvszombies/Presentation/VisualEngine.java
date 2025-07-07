package com.pvz.plantsvszombies.Presentation;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.*;
import com.pvz.plantsvszombies.Domain.Entities.Plants.*;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.NormalZombieGameObject;
import com.pvz.plantsvszombies.GameEngine.DayEngine;
import com.pvz.plantsvszombies.Presentation.Entities.*;
import com.pvz.plantsvszombies.GUI.Views.AbstractLevelView;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.*;
import com.pvz.plantsvszombies.Presentation.Entities.Zombies.NormalZombieVisualObject;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.UUID;

public class VisualEngine {
    private final int _width = 1280;
    private final int _height = 728;
    //
    private Class<? extends AbstractPlantVisualObject> selectedPlantType;
    //

    private final ArrayList<AbstractVisualObject> _visualObjects = new ArrayList<>();
    private AbstractLevelView _levelStage;

    private final IGameEngine _gameEngine;

    public VisualEngine(AbstractLevelView levelStage, IGameEngine gameEngine) {
        _levelStage = levelStage;
        _gameEngine = gameEngine;

        var temp_this = this;
        if (_gameEngine instanceof DayEngine) {
            ((DayEngine) _gameEngine).subscribeToMapSpawnEvent(new IEventSubscriber() {
                @Override
                public void _notify(AbstractGameObject gameObject) {
                    MapVisualObject object = new MapVisualObject((MapGameObject) gameObject, temp_this);
                    spawnVisualObject(object, 1);
                }
            });

            ((DayEngine) _gameEngine).subscribeToGameObjectSpawnEvent(new IEventSubscriber() {
                @Override
                public void _notify(AbstractGameObject gameObject) {
                    if (gameObject instanceof SunGameObject) {
                        SkySunVisualObject object = new SkySunVisualObject((SunGameObject) gameObject, temp_this);
                        spawnVisualObject(object);
                    }
                    if (gameObject instanceof AbstractZombieGameObject) {
                        NormalZombieVisualObject object = new NormalZombieVisualObject((NormalZombieGameObject) gameObject, temp_this);
                        spawnVisualObject(object);
                    }
                    gameObject.spawn();
                }
            });
        }
    }

    public int getWidth() {
        return this._width;
    }

    public int getHeight() {
        return this._height;
    }

    public void disposeObject(AbstractVisualObject obj) {
        Platform.runLater(()->{
            try{
                ((Pane) obj.getNode().getParent()).getChildren().remove(obj.getNode());
            }catch (Exception e){
                System.out.println("Exception in VisualEngine.java disposeObject()");
            }
        });
        _visualObjects.remove(obj);
//        _levelStage.getScene().getRoot().getChildrenUnmodifiable().remove(obj.getNode());
    }

    public void spawnVisualObject(AbstractVisualObject object) {
        Platform.runLater(() -> {
            _levelStage.getGameBoxPane().getChildren().add(object.getNode());
            object.spawn();
            this._visualObjects.add(object);
        });
    }

    public void spawnVisualObject(AbstractVisualObject object, int top_z_index) {
        Platform.runLater(() -> {
            _levelStage.getGameBoxPane().getChildren().add(object.getNode());
            for (int i = 0; i < top_z_index; i++) {
                object.getNode().toBack();
            }
            object.spawn();
            this._visualObjects.add(object);
        });
    }

    public void plant(Class<? extends AbstractPlantVisualObject> plantType, int x, int y, Coordinate coordinate) {
        Platform.runLater(() -> {

            if (plantType == PeashooterVisualObject.class) {
                String PeashooterObjectId = "Peashooter" + UUID.randomUUID();
                var obj = PeashooterGameObject.createPeashooterGameObject(this._gameEngine, PeashooterObjectId, coordinate, x, y);
                try {
                    this._gameEngine.plantObject(obj);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }

            else if (plantType == WallNutVisualObject.class) {
                String WalnutObjectId = "Walnut" + UUID.randomUUID();
                var obj = WallNutGameObject.createWallNutGameObject(this._gameEngine, WalnutObjectId, coordinate, x, y);
                try {
                    this._gameEngine.plantObject(obj);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
            //            else if (plantType == TallnutVisualObject) {
//                String PeashooterObjectId = "Tallnut" + UUID.randomUUID();
//                var obj = TallNutGameObject.createTallnutGameObject(this._gameEngine, PeashooterObjectId, coordinate, x, y);
//                try {
//                    this._gameEngine.plantObject(obj);
//                } catch (Exception ex) {
//                    System.out.println(ex.getMessage());
//                }
//            }
//
            else if (plantType == SunFlowerVisualObject.class) {
                String SunFlowerObjectId = "SunFlower" + UUID.randomUUID();
                var obj = SunFlowerGameObject.createSunFlowerGameObject(this._gameEngine, SunFlowerObjectId, coordinate, x, y);
                try {
                    this._gameEngine.plantObject(obj);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
//
//            else if (plantType == JalapenoVisualObject.class) {
//                String JalapenoObjectId = "Jalapeno" + UUID.randomUUID();
//                var obj = WallNutGameObject.createJalapenoGameObject(this._gameEngine, JalapenoObjectId, coordinate, x, y);
//                try {
//                    this._gameEngine.plantObject(obj);
//                } catch (Exception ex) {
//                    System.out.println(ex.getMessage());
//                }
//            }
//
//            else if (plantType == SnowpeaVisualObject.class) {
//                String SnowpeaObjectId = "Snowpea" + UUID.randomUUID();
//                var obj = WallNutGameObject.createWallnutGameObject(this._gameEngine, SnowpeaObjectId, coordinate, x, y);
//                try {
//                    this._gameEngine.plantObject(obj);
//                } catch (Exception ex) {
//                    System.out.println(ex.getMessage());
//                }
//            }
//
//            else if (plantType == CherryBombVisualObject.class) {
//                String CherryBombObjectId = "CherryBomb" + UUID.randomUUID();
//                var obj = WallNutGameObject.createCherryBombGameObject(this._gameEngine, CherryBombObjectId, coordinate, x, y);
//                try {
//                    this._gameEngine.plantObject(obj);
//                } catch (Exception ex) {
//                    System.out.println(ex.getMessage());
//                }
//            }
//
//                        }
            else if (plantType == RepeaterVisualObject.class) {
                String RepeaterObjectId = "RepeaterPea" + UUID.randomUUID();
                var obj = RepeaterGameObject.createRepeaterGameObject(this._gameEngine, RepeaterObjectId, coordinate, x, y);
                try {
                    this._gameEngine.plantObject(obj);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }


    //

    public void setSelectedPlantType(Class<? extends AbstractPlantVisualObject> plantType) {
        this.selectedPlantType = plantType;
        System.out.println(plantType.getName() + "have been selected");
    }

    public Class<? extends AbstractPlantVisualObject> getSelectedPlantType() {
        return this.selectedPlantType;
    }

    public void clearSelectedPlantType() {
        this.selectedPlantType = null;
    }

    //


    public AbstractLevelView getLevelStage() {
        return this._levelStage;
    }

}