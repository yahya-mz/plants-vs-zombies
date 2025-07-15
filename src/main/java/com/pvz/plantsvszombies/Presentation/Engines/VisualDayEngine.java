package com.pvz.plantsvszombies.Presentation.Engines;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.*;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.AbstractBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.NormalBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.*;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.*;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Engines.DayEngine;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralTransformAnimation;
import com.pvz.plantsvszombies.Presentation.Entities.*;
import com.pvz.plantsvszombies.Presentation.Entities.Bullets.NormalBulletVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.Zombies.*;
import com.pvz.plantsvszombies.Presentation.GUI.Views.AbstractLevelView;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.*;
import com.pvz.plantsvszombies.Presentation.GUI.Views.DayMenu;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.UUID;

public class VisualDayEngine implements IVisualEngine {
    private final int _width = 1280;
    private final int _height = 728;
    //
    private Class<? extends AbstractPlantVisualObject> selectedPlantType;
    //

    private final ArrayList<AbstractVisualObject> _visualObjects = new ArrayList<>();
    private MapVisualObject _currentMapVisualObject;
    private final AbstractLevelView _levelStage;

    private final DayEngine _gameEngine;

    public VisualDayEngine(AbstractLevelView levelStage, DayEngine gameEngine) {
        _levelStage = levelStage;
        _gameEngine = gameEngine;

        var temp_this = this;
        _gameEngine.subscribeToGameObjectSpawnEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                if (gameObject instanceof MapGameObject) {
                    _currentMapVisualObject = new MapVisualObject((MapGameObject) gameObject, temp_this);
                    spawnVisualObject(_currentMapVisualObject, 1);
                }
                if (gameObject instanceof SunGameObject) {
                    SkySunVisualObject object = new SkySunVisualObject((SunGameObject) gameObject, temp_this);
                    spawnVisualObject(object);
                }
                if (gameObject instanceof AbstractBulletGameObject) {
                    if (gameObject instanceof NormalBulletGameObject bullet) {
                        _currentMapVisualObject.spawnByCoordinate(new NormalBulletVisualObject(bullet, temp_this));
                    }
                }
                if (gameObject instanceof AbstractPlantGameObject) {
                    AbstractVisualObject visualObject = null; // 'might not have been initialized' occurs if we don't put the = null
                    switch (gameObject) {
                        case PeashooterGameObject p -> {
                            visualObject = new PeashooterVisualObject(p, temp_this);
                            _currentMapVisualObject.plant(visualObject, p.getRow(), p.getColumn());
                        }
                        case RepeaterGameObject rp -> {
                            visualObject = new RepeaterVisualObject(rp, temp_this);
                            _currentMapVisualObject.plant(visualObject, rp.getRow(), rp.getColumn());
                        }
                        case SnowPeaGameObject sp -> {
                            visualObject = new SnowPeaVisualObject(sp, temp_this);
                            _currentMapVisualObject.plant(visualObject, sp.getRow(), sp.getColumn());
                        }
                        case WallNutGameObject wn -> {
                            visualObject = new WallNutVisualObject(wn, temp_this);
                            _currentMapVisualObject.plant(visualObject, wn.getRow(), wn.getColumn());
                        }
                        case SunFlowerGameObject sn -> {
                            visualObject = new SunFlowerVisualObject(sn, temp_this);
                            _currentMapVisualObject.plant(visualObject, sn.getRow(), sn.getColumn());
                        }
                        case CherryBombGameObject cb -> {
                            visualObject = new CherryBombVisualObject(cb, temp_this);
                            _currentMapVisualObject.plant(visualObject, cb.getRow(), cb.getColumn());
                        }
                        case TallNutGameObject tl -> {
                            visualObject = new TallnutVisualObject(tl, temp_this);
                            _currentMapVisualObject.plant(visualObject, tl.getRow(), tl.getColumn());
                        }
                        case JalapenoGameObject jl -> {
                            visualObject = new JalapenoVisualObject(jl, temp_this);
                            _currentMapVisualObject.plant(visualObject, jl.getRow(), jl.getColumn());
                        }
                        default -> {
                        }
                    }
                    temp_this._visualObjects.add(visualObject);
                }
                if (gameObject instanceof AbstractZombieGameObject) {
                    switch (gameObject) {
                        case NormalZombieGameObject no -> {
                            NormalZombieVisualObject object = new NormalZombieVisualObject(no, temp_this);
                            spawnVisualObject(object);
                        }
                        case ConeHeadZombieGameObject ch -> {
                            ConeHeadZombieVisualObject object = new ConeHeadZombieVisualObject(ch, temp_this);
                            spawnVisualObject(object);
                        }
                        case ScreenDoorZombieGameObject sd -> {
                            ScreenDoorZombieVisualObject object = new ScreenDoorZombieVisualObject(sd, temp_this);
                            spawnVisualObject(object);
                        }

                        default -> {
                        }
                    }
                }
            }
        });
        _gameEngine.subscribeToLostEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    playLosingSequence(gameObject.getId());
                });
            }
        });
        _gameEngine.subscribeToWinEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {

            }
        });

    }

    @Override
    public int getWidth() {
        return this._width;
    }

    @Override
    public int getHeight() {
        return this._height;
    }

    @Override
    public void disposeObject(AbstractVisualObject obj) {
        Platform.runLater(() -> {
            try {
                ((Pane) obj.getNode().getParent()).getChildren().remove(obj.getNode());
            } catch (Exception e) {
                System.out.println("Exception in VisualEngine.java disposeObject()");
            }
        });
        _visualObjects.remove(obj);
//        _levelStage.getScene().getRoot().getChildrenUnmodifiable().remove(obj.getNode());
    }

    @Override
    public void stopEngine() {
        _levelStage.getScene().getRoot().getChildrenUnmodifiable().forEach(c -> {
            c.setDisable(true);
        });
        _visualObjects.forEach(obj -> {
            if (obj instanceof AbstractAnimatedVisualObject) {
                ((AbstractAnimatedVisualObject) obj).stopAnimation();
            }
        });
    }

    @Override
    public void spawnVisualObject(AbstractVisualObject object) {
        Platform.runLater(() -> {
            _levelStage.getGameBoxPane().getChildren().add(object.getNode());
            object.spawn();
            this._visualObjects.add(object);
        });
    }

    @Override
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

    @Override
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
            } else if (plantType == WallNutVisualObject.class) {
                String WalnutObjectId = "Walnut" + UUID.randomUUID();
                var obj = WallNutGameObject.createWallNutGameObject(this._gameEngine, WalnutObjectId, coordinate, x, y);
                try {
                    this._gameEngine.plantObject(obj);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (plantType == TallnutVisualObject.class) {
                String PeashooterObjectId = "Tallnut" + UUID.randomUUID();
                var obj = TallNutGameObject.createTallNutGameObject(this._gameEngine, PeashooterObjectId, coordinate, x, y);
                try {
                    this._gameEngine.plantObject(obj);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (plantType == SunFlowerVisualObject.class) {
                String SunFlowerObjectId = "SunFlower" + UUID.randomUUID();
                var obj = SunFlowerGameObject.createSunFlowerGameObject(this._gameEngine, SunFlowerObjectId, coordinate, x, y);
                try {
                    this._gameEngine.plantObject(obj);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (plantType == JalapenoVisualObject.class) {
                String JalapenoObjectId = "Jalapeno" + UUID.randomUUID();
                var obj = JalapenoGameObject.createJalapenoGameObject(this._gameEngine, JalapenoObjectId, coordinate, x, y);
                try {
                    this._gameEngine.plantObject(obj);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (plantType == SnowPeaVisualObject.class) {
                String SnowpeaObjectId = "Snowpea" + UUID.randomUUID();
                var obj = SnowPeaGameObject.createSnowPeaGameObject(this._gameEngine, SnowpeaObjectId, coordinate, x, y);
                try {
                    this._gameEngine.plantObject(obj);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (plantType == CherryBombVisualObject.class) {
                String CherryBombObjectId = "CherryBomb" + UUID.randomUUID();
                var obj = CherryBombGameObject.createCherryBombGameObject(this._gameEngine, CherryBombObjectId, coordinate, x, y);
                try {
                    this._gameEngine.plantObject(obj);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
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

    @Override
    public void setSelectedPlantType(Class<? extends AbstractPlantVisualObject> plantType) {
        this.selectedPlantType = plantType;
        System.out.println(plantType.getName() + "have been selected");
    }

    @Override
    public Class<? extends AbstractPlantVisualObject> getSelectedPlantType() {
        return this.selectedPlantType;
    }

    @Override
    public void clearSelectedPlantType() {
        this.selectedPlantType = null;
    }

    @Override
    public AbstractLevelView getLevelStage() {
        return this._levelStage;
    }

    private void playLosingSequence(String winnerZombieId) {
        var winnerZombie = _visualObjects.stream().filter(obj -> {
            return obj instanceof AbstractZombieVisualObject &&
                    ((AbstractZombieVisualObject) obj).getGameObject().getId()
                            .equals(winnerZombieId);
        }).findFirst().get();

        Platform.runLater(() -> {
            winnerZombie.getNode().relocate(100, _height / 2.5);
            switch (winnerZombie) {
                case NormalZombieVisualObject p -> {
                    p.changeStateTo(NormalZombieVisualObject.States.MOVING);
                }
                case ConeHeadZombieVisualObject ch -> {
                    ch.changeStateTo(ConeHeadZombieVisualObject.States.MOVING);
                }
                case ScreenDoorZombieVisualObject sd -> {
                    sd.changeStateTo(ScreenDoorZombieVisualObject.States.MOVING);
                }
                case ImpZombieVisualObject p -> {
                    p.changeStateTo(ImpZombieVisualObject.States.MOVING);
                }
                default -> {
                }
            }
            GeneralTransformAnimation.attach(winnerZombie).transformX(1, -100).then((e) -> {
                ((AbstractAnimatedVisualObject) winnerZombie).stopAnimation();
                DayMenu.createLostPopup().show();
            });
        });
    }
}