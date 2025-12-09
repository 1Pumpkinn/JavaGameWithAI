package net.saturn;

public class Creature {
    private double speed;
    private double size;
    private double sense;
    private int energy;

    public Creature(double speed, double size, double sense) {
        this.speed = Math.max(0.1, speed);
        this.size = Math.max(0.1, size);
        this.sense = Math.max(0.1, sense);
        this.energy = 100;
    }

    public static Creature random() {
        return new Creature(
                Math.random() * 10,
                Math.random() * 10,
                Math.random() * 10
        );
    }

    public Creature reproduce(Creature partner) {
        double newSpeed = (this.speed + partner.speed) / 2;
        double newSize = (this.size + partner.size) / 2;
        double newSense = (this.sense + partner.sense) / 2;

        if (Math.random() < EvolutionSimulation.MUTATION_RATE) {
            newSpeed += (Math.random() - 0.5) * 2;
        }
        if (Math.random() < EvolutionSimulation.MUTATION_RATE) {
            newSize += (Math.random() - 0.5) * 2;
        }
        if (Math.random() < EvolutionSimulation.MUTATION_RATE) {
            newSense += (Math.random() - 0.5) * 2;
        }

        return new Creature(newSpeed, newSize, newSense);
    }

    public double getFitness() {
        return speed + sense - (size * 0.3);
    }

    public void consume(int amount) {
        energy += amount;
    }

    public void metabolize() {
        energy -= (int) (size * 2);
    }

    public boolean isAlive() {
        return energy > 0;
    }

    public double getSpeed() {
        return speed;
    }

    public double getSize() {
        return size;
    }

    public double getSense() {
        return sense;
    }

    public int getEnergy() {
        return energy;
    }
}
