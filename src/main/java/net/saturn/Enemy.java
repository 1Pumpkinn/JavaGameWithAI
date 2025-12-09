package net.saturn;

public class Enemy {
    private int x, y;
    private double vx, vy;
    private int energy;
    private final double speed;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.vx = (Math.random() - 0.5) * 2;
        this.vy = (Math.random() - 0.5) * 2;
        this.energy = 150;
        this.speed = 2.5 + Math.random() * 2;
    }

    public void move(int worldWidth, int worldHeight, double speedModifier) {
        vx += (Math.random() - 0.5) * 0.3;
        vy += (Math.random() - 0.5) * 0.3;

        double maxVel = speed * speedModifier;
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

        energy--;
    }

    public void moveTowards(int targetX, int targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            vx += (dx / dist) * speed * 0.15;
            vy += (dy / dist) * speed * 0.15;
        }
    }

    public double distanceTo(int targetX, int targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void feed() {
        energy += 100;
    }

    public boolean isAlive() {
        return energy > 0;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getEnergy() { return energy; }
}