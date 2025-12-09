package net.saturn;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Terrain {
    private final int width;
    private final int height;
    private final double[][] heightMap;
    private final Biome[][] biomeMap;
    private final BufferedImage terrainImage;

    public Terrain(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.heightMap = new double[width][height];
        this.biomeMap = new Biome[width][height];
        this.terrainImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        generateTerrain(seed);
    }

    private void generateTerrain(long seed) {
        PerlinNoise noise = new PerlinNoise(seed);

        // Generate height map using multiple octaves of Perlin noise
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double frequency = 0.01;
                double amplitude = 1.0;
                double value = 0;
                double maxValue = 0;

                // Multiple octaves for more detail
                for (int octave = 0; octave < 5; octave++) {
                    value += noise.noise(x * frequency, y * frequency) * amplitude;
                    maxValue += amplitude;

                    frequency *= 2.0;
                    amplitude *= 0.5;
                }

                heightMap[x][y] = value / maxValue;
                biomeMap[x][y] = Biome.fromHeight(heightMap[x][y]);
            }
        }

        // Apply smoothing for better transitions
        smoothTerrain();

        // Generate terrain image
        Graphics2D graphics = terrainImage.createGraphics();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Biome biome = biomeMap[x][y];
                Color baseColor = biome.getColor();

                // Add slight variation to color
                int variation = (int) ((heightMap[x][y] * 20) - 10);
                int r = Math.max(0, Math.min(255, baseColor.getRed() + variation));
                int g = Math.max(0, Math.min(255, baseColor.getGreen() + variation));
                int b = Math.max(0, Math.min(255, baseColor.getBlue() + variation));

                graphics.setColor(new Color(r, g, b));
                graphics.fillRect(x, y, 1, 1);
            }
        }
        graphics.dispose();
    }

    private void smoothTerrain() {
        double[][] smoothed = new double[width][height];

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                double sum = 0;
                int count = 0;

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        sum += heightMap[x + dx][y + dy];
                        count++;
                    }
                }

                smoothed[x][y] = sum / count;
            }
        }

        // Copy smoothed values back
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                heightMap[x][y] = smoothed[x][y];
                biomeMap[x][y] = Biome.fromHeight(heightMap[x][y]);
            }
        }
    }

    public Biome getBiomeAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return Biome.WATER;
        }
        return biomeMap[x][y];
    }

    public double getHeightAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return 0;
        }
        return heightMap[x][y];
    }

    public BufferedImage getTerrainImage() {
        return terrainImage;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}