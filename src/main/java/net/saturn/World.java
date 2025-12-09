package net.saturn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    private final int width;
    private final int height;
    private List<Creature> creatures;
    private List<Food> food;
    private int generation;
    private int ticksSinceReproduction;
    private Random rand;

    public World(int width, int height, int initialPopulation) {
        this.width = width;
        this.height = height;
        this.creatures = new ArrayList<>();
        this.food = new ArrayList<>();
        this.generation = 1;
        this.ticksSinceReproduction = 0;
        this.rand = new Random();

        // Create initial population
        for (int i = 0; i < initialPopulation; i++) {
            creatures.add(Creature.random(width, height));
        }

        // Spawn initial food
        spawnFood(EvolutionSimulationGUI.FOOD_AMOUNT);
    }

    public void update() {
        // Move creatures and let them seek food
        for (Creature creature : creatures) {
            Food nearestFood = findNearestFood(creature);
            if (nearestFood != null) {
                double detectionRange = creature.getSense() * 20;
                if (creature.distanceTo(nearestFood.x, nearestFood.y) < detectionRange) {
                    creature.moveTowards(nearestFood.x, nearestFood.y);
                }
            }
            creature.move(width, height);
        }

        // Check for food consumption
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

        // Metabolize energy
        for (Creature creature : creatures) {
            creature.metabolize();
        }

        // Remove dead creatures
        creatures.removeIf(c -> !c.isAlive());

        // Reproduction cycle
        ticksSinceReproduction++;
        if (ticksSinceReproduction > 100) {
            reproduce();
            ticksSinceReproduction = 0;
            generation++;
        }

        // Respawn food periodically
        if (rand.nextInt(10) == 0) {
            spawnFood(5);
        }

        // Prevent extinction
        if (creatures.isEmpty()) {
            for (int i = 0; i < 20; i++) {
                creatures.add(Creature.random(width, height));
            }
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

    private void reproduce() {
        List<Creature> parents = new ArrayList<>();
        for (Creature c : creatures) {
            if (c.canReproduce()) {
                parents.add(c);
            }
        }

        // Sort by fitness
        parents.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));

        // Top performers reproduce
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

    private void spawnFood(int amount) {
        for (int i = 0; i < amount; i++) {
            food.add(new Food(rand.nextInt(width), rand.nextInt(height)));
        }
    }

    public List<Creature> getCreatures() {
        return creatures;
    }

    public List<Food> getFood() {
        return food;
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

