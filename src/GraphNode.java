import java.util.*;

public class GraphNode {

    public int nodeID;
    public LinkedList<EdgeInfo> succ;
    public int parent;
    public boolean visited;

    public GraphNode() {
        this(0);
    }
    public GraphNode(int nodeID) {
        this.nodeID = nodeID;
        this.succ = new LinkedList<EdgeInfo>();
        this.parent = 0;
        this.visited = false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nodeID + ": ");
        Iterator<EdgeInfo> itr = succ.iterator();
        while (itr.hasNext()) {
            sb.append(itr.next().toString());
        }
        sb.append("\n");
        return sb.toString();
    }

    public void addEdge(int v1, int v2, int capacity) {
        //System.out.println("GraphNode.addEdge " + v1 + "->" + v2 + "(" + capacity + ")");
        succ.addFirst(new EdgeInfo(v1, v2, capacity));
    }
    public void addEdge(int v1, int v2, int capacity, int amountCarried) {
        //System.out.println("GraphNode.addEdge " + v1 + "->" + v2 + "(" + capacity + ")");
        succ.addFirst(new EdgeInfo(v1, v2, capacity, amountCarried));
    }

    public void addResEdge(EdgeInfo edgeInfo) {
        edgeInfo.resEdge = new ResEdgeInfo(edgeInfo.to, edgeInfo.from, edgeInfo.capacity, edgeInfo.capacity, edgeInfo);
        succ.addFirst(edgeInfo.resEdge);
    }
    public class EdgeInfo {

        int from;        // source of edge
        int to;          // destination of edge
        int capacity;    // capacity of edge
        int amountCarried;
        int remainingCap;
        EdgeInfo resEdge = null;

        public EdgeInfo(int from, int to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.remainingCap = capacity;
            this.amountCarried = 0;
        }

        public EdgeInfo(int from, int to, int capacity, int amountCarried) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.remainingCap = capacity - amountCarried;
            this.amountCarried = amountCarried;

        }
        public boolean isTypeResEdge(){
            return false;
        }

        public String toString() {
            return "Edge " + from + "->" + to + " (" + amountCarried + "/" + capacity + ") ";
        }

        public String printTransport(){
            return "Edge (" + from + ", " + to + ") transports " + amountCarried + " cases.";
        }

        public boolean addAmountCarried(int amount){
            if (amountCarried + amount > capacity) return false;
            amountCarried += amount;
            remainingCap = capacity - amountCarried;
            return true;
        }
    }

    public class ResEdgeInfo extends EdgeInfo{
        public ResEdgeInfo(int from, int to, int capacity, int amountCarried, EdgeInfo realEdge) {
            super(from, to, capacity, amountCarried);
            this.resEdge = realEdge;
        }

        public String toString() {
            return "ResEdge " + from + "->" + to + " (" + amountCarried + "/" + capacity + ") ";
        }

        public boolean isTypeResEdge(){
            return true;
        }
    }

}
