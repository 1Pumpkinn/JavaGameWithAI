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
    private static final int WORLD_SIZE = 600;

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

        world = new World(WORLD_SIZE, WORLD_SIZE, 100);

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
            world = new World(WORLD_SIZE, WORLD_SIZE, 100);
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
