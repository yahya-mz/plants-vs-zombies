package com.pvz.plantsvszombies.GlobalMusicSettings;

import com.pvz.plantsvszombies.GlobalSettings;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;

/**
 * Manages audio playback for the Plants vs Zombies game.
 * Supports multiple simultaneous sounds and looping audio.
 */
public class SoundManager {

    private static final Map<SoundType, Media> mediaCache = new ConcurrentHashMap<>();
    private static final Map<SoundType, MediaPlayer> loopedPlayers = new ConcurrentHashMap<>();
    private static final Map<MediaPlayer, SoundType> instancePlayers = new ConcurrentHashMap<>();


    private static boolean isInitialized = false;
    private static double masterVolume = 1.0;
    private static double musicVolume = 0.7;
    private static double sfxVolume = 0.8;

    /**
     * Initializes the SoundManager
     */
    public static void initialize() {
        System.out.println("SoundManager.initialize() called");
        if (!isInitialized) {
            System.out.println("Initializing SoundManager...");
            // Pre-load commonly used sounds
            preloadSounds();
            isInitialized = true;
            System.out.println("SoundManager initialized successfully");
        } else {
            System.out.println("SoundManager already initialized");
        }
    }

    /**
     * Plays a sound effect
     * @param sound The sound type to play
     */
    public static void play(SoundType sound) {
        if (!isInitialized) {
            initialize();
        }
        System.out.println("[SoundManager] play called with: " + sound);

//        System.out.println("Attempting to play sound: " + sound);

        Media media = getMedia(sound);
//        if (media == null) {
//            System.err.println("Failed to load media for sound: " + sound);
//            return;
//        }

//        System.out.println("Media loaded successfully for: " + sound);

        if (sound.shouldLoop()) {
            System.out.println("Playing looped sound: " + sound);
            playLooped(sound, media);
        } else {
            System.out.println("Playing one-time sound: " + sound);
            playOnce(sound, media);
        }
    }

    /**
     * Stops a specific sound
     * @param sound The sound type to stop
     */
    public static void stop(SoundType sound) {
        // پلیر مشترک (مثل BACKGROUND)
        MediaPlayer shared = loopedPlayers.remove(sound);
        if (shared != null) shared.stop();

        // همه‌ی پلیرهای پر-اینستنسِ همین sound
        instancePlayers.keySet().removeIf(player -> {
            if (instancePlayers.get(player) == sound) {
                player.stop();
                return true;
            }
            return false;
        });
    }

    public static void pause(SoundType sound) {
        MediaPlayer player = loopedPlayers.get(sound);
        if (player != null && player.getStatus() == Status.PLAYING) {
            player.pause();
        }
    }
    public static void resume(SoundType sound) {
        MediaPlayer player = loopedPlayers.get(sound);
        if (player != null && player.getStatus() == Status.PAUSED) {
            player.play();
        }
    }
    public static void stopAll() {
        loopedPlayers.values().forEach(MediaPlayer::stop);
        loopedPlayers.clear();

        instancePlayers.keySet().forEach(MediaPlayer::stop);
        instancePlayers.clear();
    }
    public static void pauseAll() {
        loopedPlayers.values().forEach(p -> { if (p.getStatus() == Status.PLAYING) p.pause(); });
        instancePlayers.keySet().forEach(p -> { if (p.getStatus() == Status.PLAYING) p.pause(); });
    }

    public static void resumeAll() {
        loopedPlayers.values().forEach(p -> { if (p.getStatus() == Status.PAUSED) p.play(); });
        instancePlayers.keySet().forEach(p -> { if (p.getStatus() == Status.PAUSED) p.play(); });
    }

    public static void setMasterVolume(double volume) {
        masterVolume = Math.max(0.0, Math.min(1.0, volume));
        updateAllVolumes();
    }

    public static void setMusicVolume(double volume) {
        musicVolume = Math.max(0.0, Math.min(1.0, volume));
        updateAllVolumes();
    }

    public static void setSFXVolume(double volume) {
        sfxVolume = Math.max(0.0, Math.min(1.0, volume));
        updateAllVolumes();
    }

    public static double getMasterVolume() {
        return masterVolume;
    }


    public static double getMusicVolume() {
        return musicVolume;
    }


    public static double getSFXVolume() {
        return sfxVolume;
    }

    private static void playLooped(SoundType sound, Media media) {
        Platform.runLater(() -> {
            System.out.println("playLooped on FX Thread: " + sound);

            // برای موزیک پس‌زمینه پلیر مشترک
            if (sound == SoundType.NIGHT_BACKGROUND || sound == SoundType.ZOMBIE_GROAN) {
                MediaPlayer player = loopedPlayers.get(sound);
                if (player == null) {
                    player = new MediaPlayer(media);
                    player.setCycleCount(1);
                    player.setVolume(getVolumeForSound(sound));

                    double delaySeconds = sound.getLoopDelay();
                    MediaPlayer finalPlayer = player;
                    player.setOnEndOfMedia(() -> {
                        if (!sound.shouldLoop()) return;
                        if (loopedPlayers.get(sound) != finalPlayer) return;
                        Runnable restart = () -> {
                            if (loopedPlayers.get(sound) != finalPlayer) return;
                            finalPlayer.seek(javafx.util.Duration.ZERO);
                            finalPlayer.play();
                        };
                        if (delaySeconds > 0) {
                            javafx.animation.PauseTransition pause =
                                    new javafx.animation.PauseTransition(javafx.util.Duration.seconds(delaySeconds));
                            pause.setOnFinished(e -> restart.run());
                            pause.play();
                        } else {
                            restart.run();
                        }
                    });
                    loopedPlayers.put(sound, player);
                }
                player.play();
                return;
            }

            // برای بقیه‌ی صداهای لوپی (مثل ناله‌ی زامبی) هر بار یک پلیر جدید بساز
            MediaPlayer player = new MediaPlayer(media);
            player.setCycleCount(1);
            player.setVolume(getVolumeForSound(sound));
            instancePlayers.put(player, sound); // ثبت برای کنترل‌های stopAll/pauseAll/...

            double delaySeconds = sound.getLoopDelay();
            player.setOnEndOfMedia(() -> {
                if (!sound.shouldLoop()) return;
                // اگر در این فاصله stop شده باشه، ممکنه از map حذف شده باشه
                if (!instancePlayers.containsKey(player)) return;

                Runnable restart = () -> {
                    if (!instancePlayers.containsKey(player)) return;
                    player.seek(javafx.util.Duration.ZERO);
                    player.play();
                };

                if (delaySeconds > 0) {
                    javafx.animation.PauseTransition pause =
                            new javafx.animation.PauseTransition(javafx.util.Duration.seconds(delaySeconds));
                    pause.setOnFinished(e -> restart.run());
                    pause.play();
                } else {
                    restart.run();
                }
            });

            // پاکسازی وقتی دستی stop کنیم یا به هر دلیل متوقف/دیسپوز شود
            player.setOnStopped(() -> instancePlayers.remove(player));

            player.play();
            System.out.println("Instance loop player started for: " + sound);
        });
    }


    private static void playOnce(SoundType sound, Media media) {
        Platform.runLater(() -> {
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(getVolumeForSound(sound));

            // Clean up when done
            player.setOnEndOfMedia(() -> {
                player.dispose();
            });

            player.play();
        });
    }

    private static Media getMedia(SoundType sound) {
        return mediaCache.computeIfAbsent(sound, s -> {
            String resourcePath = "/" + sound.getFileName();
//            System.out.println("Loading resource: " + resourcePath);
            String mediaUrl = GlobalSettings.getResource(resourcePath);
            System.out.println("Media URL: " + mediaUrl);

//            if (mediaUrl.isEmpty()) {
//                System.err.println("Could not find audio file: " + resourcePath);
//                return null;
//            }

            try {
                Media media = new Media(Objects.requireNonNull(mediaUrl));
//                System.out.println("Media created successfully for: " + sound);
                return media;
            } catch (Exception e) {
                System.err.println("Error creating Media for " + sound.getFileName() + ": " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        });
    }

    private static double getVolumeForSound(SoundType sound) {
        double baseVolume = isMusicSound(sound) ? musicVolume : sfxVolume; // 0..1
        double perSound = sound.getVolumeScale();                           // 0..1 (از enum)
        return baseVolume * masterVolume * perSound;
    }

    // اگر جایی ولوم هر صدا را runtime تغییر دادی:
    public static void refreshVolumes() {
        updateAllVolumes(); // همون متد داخلی موجود
    }

    private static boolean isMusicSound(SoundType sound) {
        return sound == SoundType.NIGHT_BACKGROUND;
    }

    private static void updateAllVolumes() {
        loopedPlayers.forEach((sound, player) -> player.setVolume(getVolumeForSound(sound)));
        // برای پر-اینستنس‌ها هم صدا را به‌روز کنیم
        instancePlayers.forEach((player, sound) -> player.setVolume(getVolumeForSound(sound)));
    }

    private static void preloadSounds() {
        // Pre-load commonly used sounds
        SoundType[] commonSounds = {
                SoundType.ZOMBIE_ATTACK,
                SoundType.NIGHT_BACKGROUND,
                SoundType.ZOMBIE_GROAN
        };

        for (SoundType sound : commonSounds) {
            getMedia(sound);
        }
    }
}
