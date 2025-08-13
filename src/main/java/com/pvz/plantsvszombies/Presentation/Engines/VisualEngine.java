package com.pvz.plantsvszombies.Presentation.Engines;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.NormalBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.ShroomBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.SnowBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.FogGameObject;
import com.pvz.plantsvszombies.Domain.Entities.HypnotizedZombieGameObject;
import com.pvz.plantsvszombies.Domain.Entities.MapGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.*;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.*;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralTransformAnimation;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractAnimatedVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.Bullets.NormalBulletVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.Bullets.ShroomBulletVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.Bullets.SnowBulletVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.FogVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.MapVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.*;
import com.pvz.plantsvszombies.Presentation.Entities.Zombies.*;
import com.pvz.plantsvszombies.Presentation.GUI.Views.AbstractLevelView;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractVisualObject;
import com.pvz.plantsvszombies.Presentation.GUI.Views.DayMenu;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.UUID;

public abstract class VisualEngine {

    protected final int _width = 1280;
    protected final int _height = 728;

    protected Class<? extends AbstractPlantVisualObject> selectedPlantType;

    protected ArrayList<AbstractVisualObject> _visualObjects = new ArrayList<>();
    protected MapVisualObject _currentMapVisualObject;
    protected AbstractLevelView _levelStage;

    protected boolean _isShovelActivated = false;

    protected GameEngine _gameEngine;

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public void disposeObject(AbstractVisualObject obj) {
        Platform.runLater(() -> {
            try {
                ((Pane) obj.getNode().getParent()).getChildren().remove(obj.getNode());
            } catch (Exception e) {
                System.out.println("Exception in VisualEngine.java disposeObject()");
            }
        });
        _visualObjects.remove(obj);
    }

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

    public void spawnVisualObject(AbstractVisualObject object) {
        Platform.runLater(() -> {
            _levelStage.getGameBoxPane().getChildren().add(object.getNode());
            object.spawn();
            this._visualObjects.add(object);
        });
    }

    public void spawnVisualObject(AbstractVisualObject object, int z_index) {
        Platform.runLater(() -> {
            _levelStage.getGameBoxPane().getChildren().add(object.getNode());
            object.getNode().setViewOrder(-z_index);
            object.spawn();
            this._visualObjects.add(object);
        });
    }

    public void plant(Class<? extends AbstractPlantVisualObject> plantType, int x, int y, Coordinate coordinate) {
        Platform.runLater(() -> {
            try {
                if (plantType == PeashooterVisualObject.class) {
                    String PeashooterObjectId = "Peashooter_" + UUID.randomUUID();
                    var obj = PeashooterGameObject.createPeashooterGameObject(this._gameEngine, PeashooterObjectId, coordinate, x, y);
                    this._gameEngine.plantObject(obj);
                } else if (plantType == WallNutVisualObject.class) {
                    String WalnutObjectId = "Walnut_" + UUID.randomUUID();
                    var obj = WallNutGameObject.createWallNutGameObject(this._gameEngine, WalnutObjectId, coordinate, x, y);
                    this._gameEngine.plantObject(obj);
                } else if (plantType == TallnutVisualObject.class) {
                    String PeashooterObjectId = "Tallnut_" + UUID.randomUUID();
                    var obj = TallNutGameObject.createTallNutGameObject(this._gameEngine, PeashooterObjectId, coordinate, x, y);
                    this._gameEngine.plantObject(obj);
                } else if (plantType == SunFlowerVisualObject.class) {
                    String SunFlowerObjectId = "SunFlower_" + UUID.randomUUID();
                    var obj = SunFlowerGameObject.createSunFlowerGameObject(this._gameEngine, SunFlowerObjectId, coordinate, x, y);
                    this._gameEngine.plantObject(obj);
                } else if (plantType == JalapenoVisualObject.class) {
                    String JalapenoObjectId = "Jalapeno_" + UUID.randomUUID();
                    var obj = JalapenoGameObject.createJalapenoGameObject(this._gameEngine, JalapenoObjectId, coordinate, x, y);
                    this._gameEngine.plantObject(obj);
                } else if (plantType == SnowPeaVisualObject.class) {
                    String SnowpeaObjectId = "Snowpea_" + UUID.randomUUID();
                    var obj = SnowPeaGameObject.createSnowPeaGameObject(this._gameEngine, SnowpeaObjectId, coordinate, x, y);
                    this._gameEngine.plantObject(obj);
                } else if (plantType == CherryBombVisualObject.class) {
                    String CherryBombObjectId = "CherryBomb_" + UUID.randomUUID();
                    var obj = CherryBombGameObject.createCherryBombGameObject(this._gameEngine, CherryBombObjectId, coordinate, x, y);
                    this._gameEngine.plantObject(obj);
                } else if (plantType == RepeaterVisualObject.class) {
                    String RepeaterObjectId = "RepeaterPea_" + UUID.randomUUID();
                    var obj = RepeaterGameObject.createRepeaterGameObject(this._gameEngine, RepeaterObjectId, coordinate, x, y);
                    this._gameEngine.plantObject(obj);
                } else if (plantType == ScaredyShroomVisualObject.class) {
                    String scaredyShroomObjectId = "ScaredyShroom_" + UUID.randomUUID();
                    var obj = ScaredyShroomGameObject.createScaredyShroomGameObject(this._gameEngine, scaredyShroomObjectId, coordinate, x, y);
                    this._gameEngine.plantObject(obj);
                } else if (plantType == PuffshroomVisualObject.class) {
                    String puffShroomObjectId = "PuffShroom_" + UUID.randomUUID();
                    var obj = PuffShroomGameObject.createPuffShroomGameObject(this._gameEngine, puffShroomObjectId, coordinate, x, y);
                    this._gameEngine.plantObject(obj);
                } else if (plantType == IceshroomVisualObject.class) {
                    String iceShroomObjectId = "IceShroom_" + UUID.randomUUID();
                    var obj = IceShroomGameObject.createIceShroomGameObject(this._gameEngine, iceShroomObjectId, coordinate, x, y);
                    this._gameEngine.plantObject(obj);
                } else if (plantType == HypnoShroomVisualObject.class) {
                    String hypnoShroomObjectId = "HypnoShroom_" + UUID.randomUUID();
                    var obj = HypnoShroomGameObject.createHypnoShroomGameObject(this._gameEngine, hypnoShroomObjectId, coordinate, x, y);
                    this._gameEngine.plantObject(obj);
                } else if (plantType == BloverVisualObject.class) {
                    String bloverObjectId = "Blover_" + UUID.randomUUID();
                    var obj = BloverGameObject.createBloverGameObject(this._gameEngine, bloverObjectId, coordinate, x, y);
                    this._gameEngine.plantObject(obj);
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

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

    public AbstractLevelView getLevelStage() {
        return this._levelStage;
    }

    public void shovelRemover(int row, int col) {
        System.out.println("Shovel activated at: " + row + ", " + col);

        AbstractPlantVisualObject visualToRemove = null;

        for (AbstractVisualObject vo : _visualObjects) {
            if (vo instanceof AbstractPlantVisualObject plantVO) {
                if (plantVO.getRow() == row && plantVO.getColumn() == col) {
                    visualToRemove = plantVO;
                    break;
                }
            }
        }
        if (visualToRemove != null) {
            _gameEngine.disposeObject(visualToRemove.getGameObject());
            disposeObject(visualToRemove);
            _isShovelActivated = false;
            _levelStage.setIsShovelMode(false);
            System.out.println("Plant removed from both logic and visual.");
        } else {
            System.out.println("No plant found at selected location.");
        }
    }

    public void ActivateShovel() {
        _isShovelActivated = true;
    }

    public void DeactivateShovel() {
        _isShovelActivated = false;
    }

    public boolean isShovelActivated() {
        return _isShovelActivated;
    }

    public void playLosingSequence(String winnerZombieId) {
        var winnerZombie = _visualObjects.stream().filter(obj -> {
            return obj instanceof AbstractZombieVisualObject &&
                    ((AbstractZombieVisualObject) obj).getGameObject().getId()
                            .equals(winnerZombieId);
        }).findFirst().get();

        Platform.runLater(() -> {
            winnerZombie.getNode().relocate(100, _height / 2.5);
            switch (winnerZombie) {
                case NormalZombieVisualObject p -> {
                    p.changeStateTo(AbstractZombieGameObject.GeneralZombieState.MOVING_FORWARD);
                }
                case ConeHeadZombieVisualObject ch -> {
                    ch.changeStateTo(AbstractZombieGameObject.GeneralZombieState.MOVING_FORWARD);
                }
                case ScreenDoorZombieVisualObject sd -> {
                    sd.changeStateTo(AbstractZombieGameObject.GeneralZombieState.MOVING_FORWARD);
                }
                case ImpZombieVisualObject p -> {
                    p.changeStateTo(AbstractZombieGameObject.GeneralZombieState.MOVING_FORWARD);
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

    public void spawnGameObject(AbstractGameObject gameObject) {
        var temp_this = this;
        switch (gameObject) {
            // Map objects
            case MapGameObject m -> {
                _currentMapVisualObject = new MapVisualObject(m, temp_this);
                spawnVisualObject(_currentMapVisualObject, -10);
            }

            // Bullets
            case NormalBulletGameObject bullet -> {
                _currentMapVisualObject.spawnByCoordinate(new NormalBulletVisualObject(bullet, temp_this), 9);
            }
            case SnowBulletGameObject bullet -> {
                _currentMapVisualObject.spawnByCoordinate(new SnowBulletVisualObject(bullet, temp_this), 9);
            }
            case ShroomBulletGameObject bullet -> {
                _currentMapVisualObject.spawnByCoordinate(new ShroomBulletVisualObject(bullet, temp_this), 9);
            }

            // Hypnotized zombie
            case HypnotizedZombieGameObject hz -> {
                HypnotizedZombieVisualObject object = new HypnotizedZombieVisualObject(hz, temp_this);
                spawnVisualObject(object);
            }

            // Fog
            case FogGameObject fg -> {
                var object = new FogVisualObject(fg, temp_this);
                spawnVisualObject(object, 10);
            }

            // Plants
            case PeashooterGameObject p -> {
                var vo = new PeashooterVisualObject(p, temp_this);
                _currentMapVisualObject.plant(vo, p.getRow(), p.getColumn());
                temp_this._visualObjects.add(vo);
            }
            case RepeaterGameObject rp -> {
                var vo = new RepeaterVisualObject(rp, temp_this);
                _currentMapVisualObject.plant(vo, rp.getRow(), rp.getColumn());
                temp_this._visualObjects.add(vo);
            }
            case SnowPeaGameObject sp -> {
                var vo = new SnowPeaVisualObject(sp, temp_this);
                _currentMapVisualObject.plant(vo, sp.getRow(), sp.getColumn());
                temp_this._visualObjects.add(vo);
            }
            case WallNutGameObject wn -> {
                var vo = new WallNutVisualObject(wn, temp_this);
                _currentMapVisualObject.plant(vo, wn.getRow(), wn.getColumn());
                temp_this._visualObjects.add(vo);
            }
            case SunFlowerGameObject sn -> {
                var vo = new SunFlowerVisualObject(sn, temp_this);
                _currentMapVisualObject.plant(vo, sn.getRow(), sn.getColumn());
                temp_this._visualObjects.add(vo);
            }
            case CherryBombGameObject cb -> {
                var vo = new CherryBombVisualObject(cb, temp_this);
                _currentMapVisualObject.plant(vo, cb.getRow(), cb.getColumn());
                temp_this._visualObjects.add(vo);
            }
            case TallNutGameObject tl -> {
                var vo = new TallnutVisualObject(tl, temp_this);
                _currentMapVisualObject.plant(vo, tl.getRow(), tl.getColumn());
                temp_this._visualObjects.add(vo);
            }
            case JalapenoGameObject jl -> {
                var vo = new JalapenoVisualObject(jl, temp_this);
                _currentMapVisualObject.plant(vo, jl.getRow(), jl.getColumn());
                temp_this._visualObjects.add(vo);
            }
            case ScaredyShroomGameObject ss -> {
                var vo = new ScaredyShroomVisualObject(ss, temp_this);
                _currentMapVisualObject.plant(vo, ss.getRow(), ss.getColumn());
                temp_this._visualObjects.add(vo);
            }
            case PuffShroomGameObject ps -> {
                var vo = new PuffshroomVisualObject(ps, temp_this);
                _currentMapVisualObject.plant(vo, ps.getRow(), ps.getColumn());
                temp_this._visualObjects.add(vo);
            }
            case IceShroomGameObject is -> {
                var vo = new IceshroomVisualObject(is, temp_this);
                _currentMapVisualObject.plant(vo, is.getRow(), is.getColumn());
                temp_this._visualObjects.add(vo);
            }
            case HypnoShroomGameObject hs -> {
                var vo = new HypnoShroomVisualObject(hs, temp_this);
                _currentMapVisualObject.plant(vo, hs.getRow(), hs.getColumn());
                temp_this._visualObjects.add(vo);
            }
            case BloverGameObject bl -> {
                var vo = new BloverVisualObject(bl, temp_this);
                _currentMapVisualObject.plant(vo, bl.getRow(), bl.getColumn());
                temp_this._visualObjects.add(vo);
            }

            // Zombies
            case NormalZombieGameObject no -> {
                var object = new NormalZombieVisualObject(no, temp_this);
                spawnVisualObject(object);
            }
            case ConeHeadZombieGameObject ch -> {
                var object = new ConeHeadZombieVisualObject(ch, temp_this);
                spawnVisualObject(object);
            }
            case ScreenDoorZombieGameObject sd -> {
                var object = new ScreenDoorZombieVisualObject(sd, temp_this);
                spawnVisualObject(object);
            }
            case ImpZombieGameObject iz -> {
                var object = new ImpZombieVisualObject(iz, temp_this);
                spawnVisualObject(object);
            }

            // Default
            default -> {
                // Do nothing or log unhandled type
            }
        }
    }

}
