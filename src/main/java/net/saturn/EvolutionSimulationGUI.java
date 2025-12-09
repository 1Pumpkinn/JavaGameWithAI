package net.saturn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EvolutionSimulationGUI extends JFrame {
    public static final double MUTATION_RATE = 0.1;
    public static final int FOOD_AMOUNT = 150;

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final int WORLD_SIZE = 1000;

    private World world;
    private SimulationPanel simulationPanel;
    private StatsPanel statsPanel;
    private Timer timer;
    private int generation = 0;
    private boolean running = false;

    public EvolutionSimulationGUI() {
        setTitle("Evolution Simulator");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        world = new World(WORLD_SIZE, WORLD_SIZE, 500);

        simulationPanel = new SimulationPanel(world);
        statsPanel = new StatsPanel();

        add(simulationPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.EAST);
        add(createControlPanel(), BorderLayout.SOUTH);

        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) {
                    world.update();
                    generation = world.getGeneration();
                    simulationPanel.repaint();
                    statsPanel.update(world);
                }
            }
        });

        setLocationRelativeTo(null);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(40, 40, 50));

        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause");
        JButton resetButton = new JButton("Reset");
        JButton speedUpButton = new JButton("Speed Up");
        JButton slowDownButton = new JButton("Slow Down");

        startButton.addActionListener(e -> {
            running = true;
            timer.start();
        });

        pauseButton.addActionListener(e -> running = false);

        resetButton.addActionListener(e -> {
            running = false;
            world = new World(WORLD_SIZE, WORLD_SIZE, 500);
            generation = 0;
            simulationPanel.setWorld(world);
            simulationPanel.repaint();
            statsPanel.update(world);
        });

        speedUpButton.addActionListener(e -> {
            int delay = timer.getDelay();
            if (delay > 10) timer.setDelay(delay - 10);
        });

        slowDownButton.addActionListener(e -> {
            int delay = timer.getDelay();
            if (delay < 200) timer.setDelay(delay + 10);
        });

        panel.add(startButton);
        panel.add(pauseButton);
        panel.add(resetButton);
        panel.add(speedUpButton);
        panel.add(slowDownButton);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EvolutionSimulationGUI gui = new EvolutionSimulationGUI();
            gui.setVisible(true);
        });
    }
}

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

        // Draw terrain
        g2d.drawImage(world.getTerrain().getTerrainImage(), offsetX, offsetY, null);

        // Draw water (blue puddles)
        for (Water water : world.getWater()) {
            g2d.setColor(new Color(50, 150, 255, 180));
            g2d.fillOval(offsetX + water.x - 8, offsetY + water.y - 8, 16, 16);
            g2d.setColor(new Color(100, 180, 255, 120));
            g2d.fillOval(offsetX + water.x - 5, offsetY + water.y - 5, 10, 10);
        }

        // Draw food (green dots)
        for (Food food : world.getFood()) {
            g2d.setColor(new Color(100, 200, 100));
            g2d.fillOval(offsetX + food.x - 3, offsetY + food.y - 3, 6, 6);
        }

        // Draw enemies (red triangles)
        for (Enemy enemy : world.getEnemies()) {
            int size = 12;
            int x = offsetX + enemy.getX();
            int y = offsetY + enemy.getY();

            int[] xPoints = {x, x - size/2, x + size/2};
            int[] yPoints = {y - size/2, y + size/2, y + size/2};

            g2d.setColor(new Color(255, 50, 50));
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(new Color(180, 0, 0));
            g2d.drawPolygon(xPoints, yPoints, 3);
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
        g2d.drawString("Enemies: " + world.getEnemies().size(), 20, 80);
    }
}

class StatsPanel extends JPanel {
    private JLabel genLabel;
    private JLabel popLabel;
    private JLabel speedLabel;
    private JLabel sizeLabel;
    private JLabel senseLabel;
    private JLabel foodLabel;

    public StatsPanel() {
        setPreferredSize(new Dimension(300, 600));
        setBackground(new Color(30, 30, 40));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("EVOLUTION STATS");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(title);
        add(Box.createRigidArea(new Dimension(0, 20)));

        genLabel = createStatLabel("Generation: 0");
        popLabel = createStatLabel("Population: 0");
        speedLabel = createStatLabel("Avg Speed: 0.00");
        sizeLabel = createStatLabel("Avg Size: 0.00");
        senseLabel = createStatLabel("Avg Sense: 0.00");
        foodLabel = createStatLabel("Food Available: 0");

        add(genLabel);
        add(popLabel);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(speedLabel);
        add(sizeLabel);
        add(senseLabel);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(foodLabel);

        add(Box.createRigidArea(new Dimension(0, 30)));
        JTextArea info = new JTextArea(
                "How it works:\n\n" +
                        "BIOMES:\n" +
                        "• Water (dark blue) - Blocks movement\n" +
                        "• Sand (tan) - Slow, little food\n" +
                        "• Grass (green) - Fast, lots of food\n" +
                        "• Forest (dark green) - Medium speed\n" +
                        "• Mountain (gray) - Slow\n" +
                        "• Snow (white) - Very slow\n\n" +
                        "CREATURES:\n" +
                        "• Red = Fast, Blue = Good sense\n" +
                        "• Must find food and water\n" +
                        "• Avoid red triangle enemies\n" +
                        "• Evolve to survive!"
        );
        info.setFont(new Font("Arial", Font.PLAIN, 12));
        info.setForeground(new Color(180, 180, 190));
        info.setBackground(new Color(30, 30, 40));
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(info);
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(new Color(200, 200, 210));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public void update(World world) {
        genLabel.setText("Generation: " + world.getGeneration());
        popLabel.setText("Population: " + world.getCreatures().size());
        speedLabel.setText(String.format("Avg Speed: %.2f", world.getAverageSpeed()));
        sizeLabel.setText(String.format("Avg Size: %.2f", world.getAverageSize()));
        senseLabel.setText(String.format("Avg Sense: %.2f", world.getAverageSense()));
        foodLabel.setText("Food Available: " + world.getFood().size());
    }
}