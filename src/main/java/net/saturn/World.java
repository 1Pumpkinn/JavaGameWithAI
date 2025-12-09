package net.saturn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    private final int width;
    private final int height;
    private final Terrain terrain;
    private List<Creature> creatures;
    private List<Food> food;
    private List<Water> water;
    private List<Enemy> enemies;
    private int generation;
    private int ticksSinceReproduction;
    private Random rand;

    public World(int width, int height, int initialPopulation) {
        this.width = width;
        this.height = height;
        this.terrain = new Terrain(width, height, System.currentTimeMillis());
        this.creatures = new ArrayList<>();
        this.food = new ArrayList<>();
        this.water = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.generation = 1;
        this.ticksSinceReproduction = 0;
        this.rand = new Random();

        // Create initial population on walkable terrain
        for (int i = 0; i < initialPopulation; i++) {
            int x, y;
            do {
                x = rand.nextInt(width);
                y = rand.nextInt(height);
            } while (!terrain.getBiomeAt(x, y).isWalkable());

            creatures.add(new Creature(
                    Math.random() * 10,
                    Math.random() * 10,
                    Math.random() * 10,
                    x, y
            ));
        }

        // Spawn resources based on biomes
        spawnFoodInBiomes(100);
        spawnWaterInBiomes(50);
        spawnEnemies(5);
    }

    public void update() {
        // Move creatures
        for (Creature creature : creatures) {
            boolean needsWater = creature.getThirst() > 70;
            boolean needsFood = creature.getEnergy() < 80;

            if (needsWater) {
                Water nearestWater = findNearestWater(creature);
                if (nearestWater != null) {
                    double detectionRange = creature.getSense() * 20;
                    if (creature.distanceTo(nearestWater.x, nearestWater.y) < detectionRange) {
                        creature.moveTowards(nearestWater.x, nearestWater.y);
                    }
                }
            } else if (needsFood) {
                Food nearestFood = findNearestFood(creature);
                if (nearestFood != null) {
                    double detectionRange = creature.getSense() * 20;
                    if (creature.distanceTo(nearestFood.x, nearestFood.y) < detectionRange) {
                        creature.moveTowards(nearestFood.x, nearestFood.y);
                    }
                }
            }

            // Apply biome speed modifier
            Biome biome = terrain.getBiomeAt(creature.getX(), creature.getY());
            creature.move(width, height, biome.getSpeedModifier());
        }

        // Move enemies
        for (Enemy enemy : enemies) {
            Creature nearestCreature = findNearestCreature(enemy);
            if (nearestCreature != null) {
                double distance = enemy.distanceTo(nearestCreature.getX(), nearestCreature.getY());
                if (distance < 150) {
                    enemy.moveTowards(nearestCreature.getX(), nearestCreature.getY());
                }
            }
            Biome biome = terrain.getBiomeAt(enemy.getX(), enemy.getY());
            enemy.move(width, height, biome.getSpeedModifier());
        }

        // Food consumption
        List<Food> consumedFood = new ArrayList<>();
        for (Creature creature : creatures) {
            for (Food f : food) {
                if (creature.distanceTo(f.x, f.y) < 10) {
                    creature.consume(50);
                    consumedFood.add(f);
                    break;
                }
            }
        }
        food.removeAll(consumedFood);

        // Water drinking
        for (Creature creature : creatures) {
            for (Water w : water) {
                if (creature.distanceTo(w.x, w.y) < 15) {
                    creature.drink(40);
                    break;
                }
            }
        }

        // Enemy attacks
        List<Creature> killedCreatures = new ArrayList<>();
        for (Enemy enemy : enemies) {
            for (Creature creature : creatures) {
                if (enemy.distanceTo(creature.getX(), creature.getY()) < 15) {
                    killedCreatures.add(creature);
                    enemy.feed();
                    break;
                }
            }
        }
        creatures.removeAll(killedCreatures);

        // Metabolism
        for (Creature creature : creatures) {
            creature.metabolize();
            creature.increaseThirst();
        }

        creatures.removeIf(c -> !c.isAlive());
        enemies.removeIf(e -> !e.isAlive());

        // Reproduction
        ticksSinceReproduction++;
        if (ticksSinceReproduction > 100) {
            reproduce();
            ticksSinceReproduction = 0;
            generation++;
        }

        // Spawn resources in biomes
        if (rand.nextInt(10) == 0) {
            spawnFoodInBiomes(3);
        }
        if (rand.nextInt(15) == 0) {
            spawnWaterInBiomes(2);
        }
        if (rand.nextInt(200) == 0 && enemies.size() < 15) {
            spawnEnemies(1);
        }

        // Prevent extinction
        if (creatures.isEmpty()) {
            for (int i = 0; i < 20; i++) {
                int x, y;
                do {
                    x = rand.nextInt(width);
                    y = rand.nextInt(height);
                } while (!terrain.getBiomeAt(x, y).isWalkable());

                creatures.add(new Creature(
                        Math.random() * 10,
                        Math.random() * 10,
                        Math.random() * 10,
                        x, y
                ));
            }
        }
    }

    private void spawnFoodInBiomes(int amount) {
        for (int i = 0; i < amount; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            Biome biome = terrain.getBiomeAt(x, y);

            if (biome.getFoodSpawnRate() > 0 && rand.nextInt(20) < biome.getFoodSpawnRate()) {
                food.add(new Food(x, y));
            }
        }
    }

    private void spawnWaterInBiomes(int amount) {
        for (int i = 0; i < amount; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            Biome biome = terrain.getBiomeAt(x, y);

            if (biome.getWaterSpawnRate() > 0 && rand.nextInt(20) < biome.getWaterSpawnRate()) {
                water.add(new Water(x, y));
            }
        }
    }

    private void spawnEnemies(int amount) {
        for (int i = 0; i < amount; i++) {
            int x, y;
            do {
                x = rand.nextInt(width);
                y = rand.nextInt(height);
            } while (!terrain.getBiomeAt(x, y).isWalkable());

            enemies.add(new Enemy(x, y));
        }
    }

    private Food findNearestFood(Creature creature) {
        Food nearest = null;
        double minDist = Double.MAX_VALUE;

        for (Food f : food) {
            double dist = creature.distanceTo(f.x, f.y);
            if (dist < minDist) {
                minDist = dist;
                nearest = f;
            }
        }

        return nearest;
    }

    private Water findNearestWater(Creature creature) {
        Water nearest = null;
        double minDist = Double.MAX_VALUE;

        for (Water w : water) {
            double dist = creature.distanceTo(w.x, w.y);
            if (dist < minDist) {
                minDist = dist;
                nearest = w;
            }
        }

        return nearest;
    }

    private Creature findNearestCreature(Enemy enemy) {
        Creature nearest = null;
        double minDist = Double.MAX_VALUE;

        for (Creature c : creatures) {
            double dist = enemy.distanceTo(c.getX(), c.getY());
            if (dist < minDist) {
                minDist = dist;
                nearest = c;
            }
        }

        return nearest;
    }

    private void reproduce() {
        List<Creature> parents = new ArrayList<>();
        for (Creature c : creatures) {
            if (c.canReproduce()) {
                parents.add(c);
            }
        }

        parents.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));

        int numParents = Math.min(parents.size(), parents.size() / 2);
        for (int i = 0; i < numParents - 1; i += 2) {
            if (i + 1 < numParents) {
                Creature parent1 = parents.get(i);
                Creature parent2 = parents.get(i + 1);

                parent1.spendReproductionEnergy();
                parent2.spendReproductionEnergy();

                creatures.add(parent1.reproduce(parent2, width, height));
            }
        }
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public List<Creature> getCreatures() {
        return creatures;
    }

    public List<Food> getFood() {
        return food;
    }

    public List<Water> getWater() {
        return water;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public int getGeneration() {
        return generation;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getAverageSpeed() {
        return creatures.stream().mapToDouble(Creature::getSpeed).average().orElse(0);
    }

    public double getAverageSize() {
        return creatures.stream().mapToDouble(Creature::getSize).average().orElse(0);
    }

    public double getAverageSense() {
        return creatures.stream().mapToDouble(Creature::getSense).average().orElse(0);
    }
}