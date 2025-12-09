package net.saturn;

import javax.swing.*;
import java.awt.*;

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
                        "• Red creatures = Fast\n" +
                        "• Blue creatures = Good sense\n" +
                        "• Bigger = More energy needed\n" +
                        "• Green dots = Food\n" +
                        "• Yellow bars = Energy level\n\n" +
                        "Creatures compete for food and " +
                        "evolve traits that help them survive!"
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