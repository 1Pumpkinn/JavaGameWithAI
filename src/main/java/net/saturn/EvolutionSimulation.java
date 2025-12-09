package net.saturn;

public class EvolutionSimulation {
    private static final int POPULATION_SIZE = 100;
    private static final int GENERATIONS = 50;
    private static final double MUTATION_RATE = 0.1;
    private static final int FOOD_AMOUNT = 150;

    public static void main(String[] args) {
        Population population = new Population(POPULATION_SIZE);

        System.out.println("=== EVOLUTION SIMULATION ===\n");

        for (int gen = 1; gen <= GENERATIONS; gen++) {
            population.simulate(FOOD_AMOUNT);

            if (gen % 10 == 0 || gen == 1) {
                System.out.printf("Generation %d:\n", gen);
                System.out.printf("  Population: %d creatures\n", population.getSize());
                System.out.printf("  Avg Speed: %.2f\n", population.getAverageSpeed());
                System.out.printf("  Avg Size: %.2f\n", population.getAverageSize());
                System.out.printf("  Avg Sense: %.2f\n", population.getAverageSense());
                System.out.println();
            }
        }

        System.out.println("=== SIMULATION COMPLETE ===");
        System.out.println("Final population traits:");
        System.out.printf("  Speed: %.2f (higher = faster)\n", population.getAverageSpeed());
        System.out.printf("  Size: %.2f (higher = bigger)\n", population.getAverageSize());
        System.out.printf("  Sense: %.2f (higher = better food detection)\n", population.getAverageSense());
    }
}

