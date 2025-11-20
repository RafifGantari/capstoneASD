import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Panel for drawing the graph
class GraphPanel extends JPanel {
    private Graph graph;
    private Node draggedNode = null;

    // menyimpan edge yang merupakan bagian dari shortest path
    private java.util.Set<String> shortestEdges = new java.util.HashSet<>();

    // total jarak shortest path
    private int totalDistance = -1;

    // menyimpan node start dan end agar bisa diwarnai merah & ditampilkan
    private Integer startNode = null;
    private Integer endNode = null;

    public void setShortestPath(int[] parent, int start, int end) {
        shortestEdges.clear();
        this.startNode = start;
        this.endNode = end;

        int cur = end;
        while (parent[cur] != -1) {
            String key = parent[cur] + "-" + cur;
            shortestEdges.add(key);
            cur = parent[cur];
        }

        repaint();
    }

    public void setTotalDistance(int dist) {
        this.totalDistance = dist;
        repaint();
    }

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);

        // Mouse listener for dragging nodes
        MouseAdapter mouseHandler = new MouseAdapter() {
            private int offsetX, offsetY;

            @Override
            public void mousePressed(MouseEvent e) {
                for (Node node : graph.getNodes()) {
                    if (node.contains(e.getX(), e.getY())) {
                        draggedNode = node;
                        offsetX = e.getX() - node.getX();
                        offsetY = e.getY() - node.getY();
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    draggedNode.setX(e.getX() - offsetX);
                    draggedNode.setY(e.getY() - offsetY);
                    repaint();
                }
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw edges
        for (Edge edge : graph.getEdges()) {

            Node source = edge.getSource();
            Node target = edge.getTarget();

            String key = source.getId() + "-" + target.getId();

            if (shortestEdges.contains(key)) {
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(4));
            } else {
                g2d.setColor(Color.GRAY);
                g2d.setStroke(new BasicStroke(2));
            }

            g2d.drawLine(source.getX(), source.getY(), target.getX(), target.getY());
            drawArrow(g2d, source.getX(), source.getY(), target.getX(), target.getY());

            // weight
            int midX = (source.getX() + target.getX()) / 2;
            int midY = (source.getY() + target.getY()) / 2;
            g2d.setColor(Color.RED);
            g2d.drawString(String.valueOf(edge.getWeight()), midX, midY);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
        }

        // Draw nodes
        for (Node node : graph.getNodes()) {

            Color nodeColor = new Color(100, 150, 255);

            // start & end jadi merah
            if (startNode != null && (node.getId() == startNode || node.getId() == endNode)) {
                nodeColor = Color.RED;
            }

            g2d.setColor(nodeColor);
            g2d.fillOval(node.getX() - node.getRadius(),
                    node.getY() - node.getRadius(),
                    node.getRadius() * 2,
                    node.getRadius() * 2);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(node.getX() - node.getRadius(),
                    node.getY() - node.getRadius(),
                    node.getRadius() * 2,
                    node.getRadius() * 2);

            // label
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String label = graph.getLabel()[node.getId()];
            FontMetrics fm = g2d.getFontMetrics();
            int labelX = node.getX() - fm.stringWidth(label) / 2;
            int labelY = node.getY() + fm.getAscent() / 2;
            g2d.drawString(label, labelX, labelY);
        }

        // ===============================
        //   DISPLAY TOTAL DISTANCE TEXT
        // ===============================
        if (totalDistance >= 0 && startNode != null && endNode != null) {
            String startLabel = graph.getLabel()[startNode];
            String endLabel = graph.getLabel()[endNode];

            String text = "Total distance: " + totalDistance + " from " + startLabel + " to " + endLabel;

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString(text, 20, getHeight() - 20);
        }
    }

    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 10;

        int arrowX = x2 - (int)(25 * Math.cos(angle));
        int arrowY = y2 - (int)(25 * Math.sin(angle));

        int[] xPoints = {
                arrowX,
                arrowX - (int)(arrowSize * Math.cos(angle - Math.PI / 6)),
                arrowX - (int)(arrowSize * Math.cos(angle + Math.PI / 6))
        };

        int[] yPoints = {
                arrowY,
                arrowY - (int)(arrowSize * Math.sin(angle - Math.PI / 6)),
                arrowY - (int)(arrowSize * Math.sin(angle + Math.PI / 6))
        };

        g2d.fillPolygon(xPoints, yPoints, 3);
    }
}
