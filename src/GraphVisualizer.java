import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphVisualizer extends JFrame {
    private Graph graph;
    private GraphPanel graphPanel;

    public GraphVisualizer(Graph graph) {
        setTitle("Graph Visualizer with Shortest Path");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.graph = graph;
        this.graphPanel = new GraphPanel(graph);

        setLayout(new BorderLayout());
        add(graphPanel, BorderLayout.CENTER);

        // ============= TOP PANEL: dropdown + button ============= //
        JPanel topPanel = new JPanel(new FlowLayout());

        JLabel startLabel = new JLabel("Start:");
        JLabel endLabel = new JLabel("End:");

        JComboBox<String> startBox = new JComboBox<>(graph.getLabel());
        JComboBox<String> endBox = new JComboBox<>(graph.getLabel());

        JButton runButton = new JButton("Find Shortest Path");

        topPanel.add(startLabel);
        topPanel.add(startBox);
        topPanel.add(endLabel);
        topPanel.add(endBox);
        topPanel.add(runButton);

        add(topPanel, BorderLayout.NORTH);

        // ============= BOTTOM PANEL: display path sequence ============= //
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JLabel pathLabel = new JLabel("Path: -");
        bottomPanel.add(pathLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        // ============= BUTTON ACTION ============= //
        runButton.addActionListener(e -> {
            int start = startBox.getSelectedIndex();
            int end = endBox.getSelectedIndex();

            Dijkstra dijkstra = new Dijkstra(graph.getAdjacencyMatrix(), graph.getLabel());
            dijkstra.compute(start);

            int[] parent = dijkstra.getParent();
            int distance = dijkstra.getDistance(end);

            // set highlight path to graphPanel
            graphPanel.setShortestPath(parent, start, end);
            graphPanel.setTotalDistance(distance);

            // Generate readable path text
            String pathText = buildPathString(parent, start, end, graph.getLabel());
            pathLabel.setText("Path: " + pathText);
        });

        pack();
        setLocationRelativeTo(null);
    }

    private String buildPathString(int[] parent, int start, int end, String[] labels) {
        java.util.List<String> nodes = new java.util.ArrayList<>();
        int cur = end;

        while (cur != -1) {
            nodes.add(0, labels[cur]);
            if (cur == start) break;
            cur = parent[cur];
        }
        return String.join(" → ", nodes);
    }

    public static void main(String[] args) {

        int[][] adjacencyMatrix = {
                { 0,2,0,3,8,0,0,0,0,0 },
                { 2,0,3,0,1,0,0,0,0,0 },
                { 0,3,0,4,0,4,6,0,0,0 },
                { 3,0,4,0,0,0,2,0,0,0 },
                { 8,1,0,0,0,2,0,0,3,10 },
                { 0,0,4,0,2,0,8,0,0,3 },
                { 0,0,6,2,0,8,0,4,0,0 },
                { 0,0,0,0,0,0,4,0,0,3 },
                { 0,0,0,0,3,0,0,0,0,4 },
                { 0,0,0,0,10,3,0,3,4,0 }
        };

        String[] labels = {
                "MKS","SUB","CGK","BDG","DPS",
                "DHS","MLG","PDG","YOG","BTM"
        };

        Graph graph = new Graph(adjacencyMatrix, labels);

        SwingUtilities.invokeLater(() -> {
            GraphVisualizer visualizer = new GraphVisualizer(graph);
            visualizer.setVisible(true);
        });
    }
}
