package net.saturn;

import javax.swing.*;
import java.awt.*;

class SimulationPanel extends JPanel {
    private World world;

    public SimulationPanel(World world) {
        this.world = world;
        setPreferredSize(new Dimension(800, 800));
        setBackground(new Color(20, 25, 35));
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int offsetX = (getWidth() - world.getWidth()) / 2;
        int offsetY = (getHeight() - world.getHeight()) / 2;

        // Draw food
        for (Food food : world.getFood()) {
            g2d.setColor(new Color(100, 200, 100));
            g2d.fillOval(offsetX + food.x - 3, offsetY + food.y - 3, 6, 6);
        }

        // Draw creatures
        for (Creature creature : world.getCreatures()) {
            int size = (int) (creature.getSize() * 3 + 5);
            int x = offsetX + creature.getX() - size / 2;
            int y = offsetY + creature.getY() - size / 2;

            // Color based on speed (red) and sense (blue)
            int red = Math.min(255, (int) (creature.getSpeed() * 25));
            int blue = Math.min(255, (int) (creature.getSense() * 25));
            g2d.setColor(new Color(red, 100, blue));
            g2d.fillOval(x, y, size, size);

            // Draw energy bar
            int energyWidth = (int) ((creature.getEnergy() / 150.0) * size);
            g2d.setColor(new Color(255, 200, 0, 150));
            g2d.fillRect(x, y - 5, energyWidth, 3);
        }

        // Draw generation counter
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Generation: " + world.getGeneration(), 20, 30);
        g2d.drawString("Population: " + world.getCreatures().size(), 20, 55);
    }
}
