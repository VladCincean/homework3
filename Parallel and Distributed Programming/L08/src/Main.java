import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Main {
    private final static int NR_THREADS = 4;

    private static void generateHamiltonianGraph(int N, int[][] graph) {
        List<Integer> cycle = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            cycle.add(i);
        }

        Collections.shuffle(cycle, new Random(System.nanoTime()));

        for (int i = 1; i < N; i++) {
            graph[cycle.get(i - 1)][cycle.get(i)] = 1;
            graph[cycle.get(i)][cycle.get(i - 1)] = 1;
        }
        graph[cycle.get(0)][cycle.get(N - 1)] = 1;

        // add some other random edges
        Random random = new Random(System.nanoTime());
        for (int i = 0; i < N * (N / 2); i++) {
            int a = random.nextInt();
            int b = random.nextInt();

            a = a < 0 ? ((-a) % N) : (a % N);
            b = b < 0 ? ((-b) % N) : (b % N);

            if (a == b) {
                continue;
            }

            graph[a][b] = 1;
            graph[b][a] = 1;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int graph1[][] = {
                {0, 1, 0, 1, 0},
                {1, 0, 1, 1, 1},
                {0, 1, 0, 0, 1},
                {1, 1, 0, 0, 1},
                {0, 1, 1, 1, 0},
        };
        HamiltonianCycle hc1 = new HamiltonianCycle(graph1, 5);
        hc1.printGraph();
        boolean hc1Check = hc1.hamCycle(NR_THREADS);
        if (hc1Check) {
            hc1.printSolution();
        }
        else {
            System.out.println("There is no Hamiltonian Cycle :(");
        }

        System.out.println("-------------------------------------------------------");

        int graph2[][] = {
                {0, 1, 0, 1, 0},
                {1, 0, 1, 1, 1},
                {0, 1, 0, 0, 1},
                {1, 1, 0, 0, 0},
                {0, 1, 1, 0, 0},
        };
        HamiltonianCycle hc2 = new HamiltonianCycle(graph2, 5);
        hc2.printGraph();
        boolean hc2Check = hc2.hamCycle(NR_THREADS);
        if (hc2Check) {
            hc2.printSolution();
        }
        else {
            System.out.println("There is no Hamiltonian Cycle :(");
        }

        System.out.println("-------------------------------------------------------");

        int[][] graph3 = new int[10][10];
        generateHamiltonianGraph(10, graph3);
        HamiltonianCycle hc3 = new HamiltonianCycle(graph3, 10);
        hc3.printGraph();
        boolean hc3Check = hc3.hamCycle(NR_THREADS);
        if (hc3Check) {
            hc3.printSolution();
        }
        else {
            System.out.println("There is no Hamiltonian Cycle :(");
        }
    }
}
