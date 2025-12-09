package net.saturn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Population {
    private List<Creature> creatures;
    private Random rand;

    public Population(int size) {
        creatures = new ArrayList<>();
        rand = new Random();
        for (int i = 0; i < size; i++) {
            creatures.add(Creature.random());
        }
    }

    public void simulate(int foodAmount) {
        distributeFood(foodAmount);

        for (Creature c : creatures) {
            c.metabolize();
        }

        creatures.removeIf(c -> !c.isAlive());

        reproduce();
    }

    private void distributeFood(int foodAmount) {
        List<Creature> shuffled = new ArrayList<>(creatures);
        Collections.shuffle(shuffled);

        for (int i = 0; i < Math.min(foodAmount, shuffled.size()); i++) {
            Creature c = shuffled.get(i);
            double successChance = (c.getSpeed() * 0.05) + (c.getSense() * 0.05);
            if (Math.random() < successChance) {
                c.consume(50);
            }
        }
    }

    private void reproduce() {
        List<Creature> survivors = new ArrayList<>(creatures);
        survivors.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));

        int reproducers = Math.min(survivors.size(), survivors.size() / 2);
        List<Creature> offspring = new ArrayList<>();

        for (int i = 0; i < reproducers - 1; i++) {
            Creature parent1 = survivors.get(i);
            Creature parent2 = survivors.get(i + 1);
            offspring.add(parent1.reproduce(parent2));
        }

        creatures.addAll(offspring);
    }

    public int getSize() {
        return creatures.size();
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
