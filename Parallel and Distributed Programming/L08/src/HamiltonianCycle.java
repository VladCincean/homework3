import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Based on: http://www.geeksforgeeks.org/backtracking-set-7-hamiltonian-cycle/
 */
public class HamiltonianCycle {
    private int N;
    private int[][] graph = new int[N][N];
    private int[] solution;
    private ExecutorService service;

    public HamiltonianCycle(int[][] graph, int N) {
        this.N = N;
        this.graph = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.graph[i][j] = graph[i][j];
            }
        }
        this.solution = new int[N];
    }

    private boolean isSafe(int v, int graph[][], int path[], int pos) {
        // check if this vertex is adjacent to a previous vertex
        if (graph[path[pos - 1]][v] == 0) {
            return false;
        }

        // check if hte vertex has already been included
        for (int i = 0; i < pos; i++) {
            if (path[i] == v) {
                return false;
            }
        }

        // then, it is safe
        return true;
    }

    private boolean hamCycleUtil(int graph[][], int path[], int pos, int nrThreads) throws ExecutionException, InterruptedException {
        // base case: if all vertices were included
        if (pos == N) {
            // if there is an edge from the last included vertex to the first vertex, then true
            // otherwise, false
            if (graph[path[pos - 1]][path[0]] == 1) {
                solution = Arrays.copyOf(path, N);
                return true;
            }
            else {
                return false;
            }
        }

        // try different vertices as a next candidate in Hamiltonian Cycle.
        // we don't try for 0 as it is already included
        if (nrThreads <= 1) {
            for (int v = 1; v < N; v++) {
                // check if it can be added to the Hamiltonian Cycle
                if (isSafe(v, graph, path, pos)) {
                    path[pos] = v;

                    // recursive call
                    if (hamCycleUtil(graph, path, pos + 1, nrThreads)) {
                        return true;
                    }

                    // backtrack - remove vertex v
                    path[pos] = -1;
                }
            }

            // we failed
            return false;
        }

        // if there, then nrThreads > 1 and we should do parallel computation
        List<Future<Boolean>> potentialResults = new ArrayList<>();
        for (int v = 1; v < N; v++) {
            if (isSafe(v, graph, path, pos)) {
                path[pos] = v;

                int[] pathCopy = Arrays.copyOf(path, N);
                potentialResults.add(service.submit(
                        () -> hamCycleUtil(graph, pathCopy, pos + 1, nrThreads / N)
                ));
            }
        }

        // get result candidate
        for (Future<Boolean> f : potentialResults) {
            Boolean res = f.get();
            if (res) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if there is a Hamiltonian Cycle
     * @return true|false
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public boolean hamCycle(int nrThreads) throws ExecutionException, InterruptedException {
        for (int i = 0; i < N; i++) {
            solution[i] = -1;
        }

        solution[0] = 0;

        service = Executors.newFixedThreadPool(nrThreads);
        boolean result = hamCycleUtil(graph, solution, 1, nrThreads);
        service.shutdownNow();

        return result;
    }

    public void printSolution() {
        System.out.print("Hamiltonian Cycle:");
        for (int i = 0; i < N; i++) {
            System.out.print(" " + solution[i]);
        }
        System.out.println(" " + solution[0]);
    }

    public void printGraph() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(" " + graph[i][j]);
            }
            System.out.println();
        }
    }
}
