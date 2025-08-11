package com.pvz.plantsvszombies.GlobalMusicSettings;

public enum SoundType {
    // Zombie sounds
    ZOMBIE_ATTACK("Sounds/Zombie_Attack.mp3", true,0,50),
    ZOMBIE_Falling("Sounds/Zombie_Falling.mp3", false,0,50),
    ZOMBIE_GROAN("Sounds/Zombie_Groan.mp3",true, getRandomDelay(), 50),
    ZOMBIES_ARE_COMING("Sounds/Zombie_Groan.mp3",true, 0, 50),
    ZOMBIES_BRAINS("Sounds/Zombie_Brains.mp3",true, 0, 50),
//    ZOMBIE_BURN("Sonuds/Zombie_Die.mp3", true),
    IMP_ZOMBIE("Sounds/Imp_Zombie.mp3", false),
    SCREENDOOR_ZOMBIE_SHIELD_HIT("Sounds/Shield_Hit.mp3", false),

    CHERRY_BOMB_EXPLOSION("Sounds/Cherry_Bomb_Explosion.mp3", false,0,80),
    JALAPENO_EXPLOSION("Sounds/Jalapeno_Explosion.mp3", false,0,80),
    DOOM_SHROOM_EXPLOSION("Sounds/Doom_Shroom_Explosion.mp3", false),
    PUFF_SHROOM_SHOOTING("Sounds/Puff_Shroom_Shooting.mp3", true),
    PEASHOOTER_SHOOTING("Sounds/Peashooter_Shoots.mp3", true, 3),
    COFFEE_BEAN("Sounds/Coffee_Bean.mp3", false),
    BLOVER("Sounds/Blover.mp3", false),
    PLANT_FROZEN("Sounds/Plants_Frozen.mp3", false),
    GRAVE_BUSTER("Sounds/Grave_Buster.mp3", false),
    SNOWPEA_SPARKLES("Sounds/Snow_Pea_Sparkles.mp3", false),

    READ_SET_PLANT_SOUND("Sounds/Ready_Set_Plant.mp3", false),
    SHOVEL_SOUND("Sounds/Shovel.mp3", false),
    PLANT_SOUND("Sounds/Plant_Sound.mp3", false),
    SUN_POINTS("Sounds/Points.mp3", false),
//    CLICK("Sounds/Background.mp3", false),
    NIGHT_BACKGROUND("Sounds/Night_Background.mp3", true),
    DAY_BACKGROUND("Sounds/Day_Background.mp3", true),
    WIN_MUSIC("Sounds/Win_Music.mp3", false),
    LOSE_MUSIC("Sounds/Lose_Music.mp3", false),
    MID_WAVE("Sounds/Mid_Wave.mp3", false),
    FINAL_WAVE("Sounds/Final_Wave.mp3", false);



    // EXPLOSION("explosion.mp3", false);

    private final String fileName;
    private final boolean loop;
    private final double loopDelay; // بر حسب ثانیه
    private int volumePercent;      // 0..100

    SoundType(String fileName, boolean loop) {
        this(fileName, loop, 0,100);
    }

    SoundType(String fileName, boolean loop, double loopDelay) {
        this(fileName, loop, loopDelay, 100);
    }
    SoundType(String fileName, boolean loop, double loopDelay, int volumePercent) {
        this.fileName = fileName;
        this.loop = loop;
        this.loopDelay = loopDelay;
        this.volumePercent = clamp0to100(volumePercent);
    }

    public String getFileName() {
        return fileName;
    }
    public boolean shouldLoop() {
        return loop;
    }
    public int getVolumePercent() { return volumePercent; }
    public double getVolumeScale() { return volumePercent / 100.0; }
    public void setVolumePercent(int percent) {
        this.volumePercent = clamp0to100(percent);
    }
    private static int clamp0to100(int v) {
        return Math.max(0, Math.min(100, v));
    }
    public double getLoopDelay() {return loopDelay;}
    private static double getRandomDelay() {
        return 1 + Math.random() * 6; // بین ۱ تا ۳ ثانیه
    }
}
