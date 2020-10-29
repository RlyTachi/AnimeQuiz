package com.rlytachi.animequiz;

public class LevelChoice {
    private static int level;
    private Boolean[] levels = new Boolean[5];
    private String type;

    public LevelChoice(String type) {
        this.type = type;

        levels[0] = true;
        levels[1] = false;
        levels[2] = false;
        levels[3] = false;
        levels[4] = false;
    }

    public Boolean[] getLevels() {
        return levels;
    }

    public void setLevels(Boolean[] levels) {
        this.levels = levels;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int level) {
        LevelChoice.level = level;
    }
}
