import java.util.Arrays;   // ← WAJIB
import java.util.PriorityQueue;
import java.util.Comparator;


public class Dijkstra {

    private final int[][] matrix;
    private final String[] labels;

    public Dijkstra(int[][] adjacencyMatrix, String[] labels) {
        this.matrix = adjacencyMatrix;
        this.labels = labels;
    }

    public int[] shortestPath(int startNode, int endNode) {
        int n = matrix.length;

        int[] distance = new int[n];
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];  // <— tambahan

        Arrays.fill(distance, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        distance[startNode] = 0;

        for (int i = 0; i < n - 1; i++) {
            int u = getMinDistance(distance, visited);
            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (matrix[u][v] > 0 && !visited[v]) {

                    int newDist = distance[u] + matrix[u][v];

                    if (newDist < distance[v]) {
                        distance[v] = newDist;
                        parent[v] = u;  // <— simpan jalur
                    }
                }
            }
        }

        return parent;
    }

    private int getMinDistance(int[] dist, boolean[] visited) {
        int min = Integer.MAX_VALUE;
        int index = -1;

        for (int i = 0; i < dist.length; i++) {
            if (!visited[i] && dist[i] < min) {
                min = dist[i];
                index = i;
            }
        }
        return index;
    }
}
