import java.io.File;
import java.util.Scanner;
import java.util.Arrays;

public class Graph implements Cloneable{
    int vertexCt;  // Number of vertices in the graph.
    GraphNode[] G;  // Adjacency list for graph.
    String graphName;  //The file from which the graph was created.

    public Graph() {
        this.vertexCt = 0;
        this.graphName = "";
    }
    public Object clone() throws CloneNotSupportedException {
        Graph g = (Graph) super.clone();
        g = new Graph();
        g.graphName = this.graphName;
        g.makeGraph(g.graphName);
        return g;
    }

    public static void main(String[] args) {
        Graph graph1 = new Graph();
        graph1.makeGraph("demands2.txt");
        System.out.println(graph1.toString());
        ;
    }

    public int getVertexCt() {
        return vertexCt;
    }

    public boolean addEdge(int source, int destination, int cap) {
        //System.out.println("addEdge " + source + "->" + destination + "(" + cap + ")");
        if (source < 0 || source >= vertexCt) return false;
        if (destination < 0 || destination >= vertexCt) return false;
        //add edge
        G[source].addEdge(source, destination, cap);
        return true;
    }

    public void resetVisited(){
        for (GraphNode node: G
             ) {
            node.visited = false;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("The Graph " + graphName + " \n");

        for (int i = 0; i < vertexCt; i++) {
            sb.append(G[i].toString());
        }
        return sb.toString();
    }

    public void makeGraph(String filename) {
        try {
            graphName = filename;
            Scanner reader = new Scanner(new File(filename));
            vertexCt = reader.nextInt();
            G = new GraphNode[vertexCt];
            for (int i = 0; i < vertexCt; i++) {
                G[i] = new GraphNode(i);
            }
            while (reader.hasNextInt()) {
                int v1 = reader.nextInt();
                int v2 = reader.nextInt();
                int cap = reader.nextInt();
                if (!addEdge(v1, v2, cap))
                    throw new Exception();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}