package net.saturn;

public class Creature {
    private final double speed;
    private final double size;
    private final double sense;
    private int energy;
    private int thirst;
    private int x, y;
    private double vx, vy;
    private int age;

    public Creature(double speed, double size, double sense, int x, int y) {
        this.speed = Math.max(0.1, Math.min(10, speed));
        this.size = Math.max(0.1, Math.min(10, size));
        this.sense = Math.max(0.1, Math.min(10, sense));
        this.energy = 100;
        this.thirst = 0;
        this.x = x;
        this.y = y;
        this.vx = (Math.random() - 0.5) * 2;
        this.vy = (Math.random() - 0.5) * 2;
        this.age = 0;
    }

    public static Creature random(int worldWidth, int worldHeight) {
        return new Creature(
                Math.random() * 10,
                Math.random() * 10,
                Math.random() * 10,
                (int)(Math.random() * worldWidth),
                (int)(Math.random() * worldHeight)
        );
    }

    public Creature reproduce(Creature partner, int worldWidth, int worldHeight) {
        double newSpeed = (this.speed + partner.speed) / 2;
        double newSize = (this.size + partner.size) / 2;
        double newSense = (this.sense + partner.sense) / 2;

        if (Math.random() < EvolutionSimulationGUI.MUTATION_RATE) {
            newSpeed += (Math.random() - 0.5) * 2;
        }
        if (Math.random() < EvolutionSimulationGUI.MUTATION_RATE) {
            newSize += (Math.random() - 0.5) * 2;
        }
        if (Math.random() < EvolutionSimulationGUI.MUTATION_RATE) {
            newSense += (Math.random() - 0.5) * 2;
        }

        int childX = (this.x + partner.x) / 2;
        int childY = (this.y + partner.y) / 2;

        return new Creature(newSpeed, newSize, newSense, childX, childY);
    }

    public void move(int worldWidth, int worldHeight, double speedModifier) {
        vx += (Math.random() - 0.5) * 0.5;
        vy += (Math.random() - 0.5) * 0.5;

        double maxVel = speed * 0.5 * speedModifier;
        double vel = Math.sqrt(vx * vx + vy * vy);
        if (vel > maxVel) {
            vx = (vx / vel) * maxVel;
            vy = (vy / vel) * maxVel;
        }

        x += vx;
        y += vy;

        if (x < 0) { x = 0; vx = Math.abs(vx); }
        if (x >= worldWidth) { x = worldWidth - 1; vx = -Math.abs(vx); }
        if (y < 0) { y = 0; vy = Math.abs(vy); }
        if (y >= worldHeight) { y = worldHeight - 1; vy = -Math.abs(vy); }

        age++;
    }

    public void moveTowards(int targetX, int targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            vx += (dx / dist) * speed * 0.1;
            vy += (dy / dist) * speed * 0.1;
        }
    }

    public double distanceTo(int targetX, int targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getFitness() {
        return speed + sense - (size * 0.3);
    }

    public void consume(int amount) {
        energy += amount;
    }

    public void drink(int amount) {
        thirst = Math.max(0, thirst - amount);
    }

    public void metabolize() {
        energy -= (int)(size * 0.5 + 1);
    }

    public void increaseThirst() {
        thirst += 1;
        if (thirst > 100) {
            energy -= 2;
        }
    }

    public boolean isAlive() {
        return energy > 0 && thirst < 150;
    }

    public boolean canReproduce() {
        return energy > 120 && age > 50 && thirst < 80;
    }

    public void spendReproductionEnergy() {
        energy -= 40;
    }

    public double getSpeed() { return speed; }
    public double getSize() { return size; }
    public double getSense() { return sense; }
    public int getEnergy() { return energy; }
    public int getThirst() { return thirst; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getAge() { return age; }
}