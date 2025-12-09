package net.saturn;

import java.awt.*;

public enum Biome {
    WATER(new Color(30, 90, 180), 0.0, 0.3, "Water"),
    SAND(new Color(240, 220, 130), 0.3, 0.45, "Sand"),
    GRASS(new Color(80, 160, 60), 0.45, 0.65, "Grass"),
    FOREST(new Color(40, 120, 40), 0.65, 0.80, "Forest"),
    MOUNTAIN(new Color(120, 120, 130), 0.80, 0.90, "Mountain"),
    SNOW(new Color(240, 250, 255), 0.90, 1.0, "Snow");

    private final Color color;
    private final double minHeight;
    private final double maxHeight;
    private final String name;

    Biome(Color color, double minHeight, double maxHeight, String name) {
        this.color = color;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public static Biome fromHeight(double height) {
        for (Biome biome : values()) {
            if (height >= biome.minHeight && height < biome.maxHeight) {
                return biome;
            }
        }
        return SNOW;
    }

    public boolean isWalkable() {
        return this != WATER;
    }

    public double getSpeedModifier() {
        switch (this) {
            case WATER: return 0.0;
            case SAND: return 0.7;
            case GRASS: return 1.0;
            case FOREST: return 0.8;
            case MOUNTAIN: return 0.6;
            case SNOW: return 0.5;
            default: return 1.0;
        }
    }

    public int getFoodSpawnRate() {
        switch (this) {
            case GRASS: return 10;
            case FOREST: return 15;
            case SAND: return 3;
            case MOUNTAIN: return 2;
            default: return 0;
        }
    }

    public int getWaterSpawnRate() {
        switch (this) {
            case WATER: return 0;
            case GRASS: return 5;
            case FOREST: return 8;
            default: return 0;
        }
    }
}