import java.util.ArrayList;

public class MaxFlowMinCut {
    public static class SuccPath{
        ArrayList<GraphNode.EdgeInfo> path;
        int flow;

        SuccPath(ArrayList<GraphNode.EdgeInfo> p, int f){
            this.path = p;
            this.flow = f;
        }
    }

    public static int getMaxFlowOfPath(ArrayList<GraphNode.EdgeInfo> path){
        int maxFlow = path.get(0).remainingCap;
        for (int i = 0; i< path.size(); i++
        ) {
            if(maxFlow> path.get(i).remainingCap){
                maxFlow = path.get(i).remainingCap;
            }
        }
        return maxFlow;
    }

    public static String printPath(ArrayList<GraphNode.EdgeInfo> path){
        StringBuilder sb = new StringBuilder();
        sb.append(path.get(0).from + " ");
        for(int i = 0; i< path.size(); i++){
            sb.append(path.get(i).to + " ");
        }
        return sb.toString();
    }



    public static ArrayList<SuccPath> createMaxFlowGraph (Graph graph) throws CloneNotSupportedException {
        //create a residual graph copy
        Graph resGraph = (Graph) graph.clone();
        for (GraphNode node: resGraph.G
             ) {
            resGraph.G[node.nodeID].visited = true;
            for (GraphNode.EdgeInfo edge: node.succ
                 ) {
                if(!resGraph.G[edge.to].visited){
                    resGraph.G[edge.to].addResEdge(edge);
                }
            }
        }
        resGraph.resetVisited();

        //create successfulPaths list
        ArrayList<SuccPath> successfullPaths = new ArrayList<>();

        //Residual loop
        boolean augmentedPathStillExists = true;
        while(augmentedPathStillExists) {
            //reset visited in resGraph
            resGraph.resetVisited();

            //find shortest valid path in resGraph
            ArrayList<ArrayList<GraphNode.EdgeInfo>> queueOfPaths = new ArrayList<>();
            for (GraphNode.EdgeInfo edge: resGraph.G[0].succ
            ) {
                if(edge.remainingCap > 0){
                    ArrayList<GraphNode.EdgeInfo> path = new ArrayList<>();
                    path.add(edge);
                    queueOfPaths.add(path);
                    resGraph.G[edge.from].visited = true;
                }

            }
            if(queueOfPaths.isEmpty()){
                augmentedPathStillExists = false;
            }
            while (!queueOfPaths.isEmpty()) {
                ArrayList<GraphNode.EdgeInfo> curPath = queueOfPaths.get(0);
                int lastNodeOfCurrentPath = curPath.get(curPath.size()-1).to;

                if(resGraph.G[lastNodeOfCurrentPath].nodeID == graph.vertexCt-1) {
                    //find maxAllowedTransport for path in resGraph
                    int maxFlow = getMaxFlowOfPath(curPath);
                    if (maxFlow > 0) {
                        //update path in residual graph
                        for (GraphNode.EdgeInfo edgeInfo : curPath
                        ) {
                            edgeInfo.addAmountCarried(maxFlow);
                            if(edgeInfo.resEdge != null) edgeInfo.resEdge.addAmountCarried(maxFlow*-1);
                        }
                        break;
                    }
                }else {
                    for (GraphNode.EdgeInfo successor : resGraph.G[lastNodeOfCurrentPath].succ
                    ) {
                        if (successor.remainingCap > 0 && !resGraph.G[successor.to].visited) {
                            ArrayList<GraphNode.EdgeInfo> path = new ArrayList<>(curPath);
                            path.add(successor);
                            resGraph.G[successor.from].visited = true;
                            queueOfPaths.add(path);
                        }
                    }
                }

                queueOfPaths.remove(curPath);
                if(queueOfPaths.isEmpty()){
                    augmentedPathStillExists = false;
                }

            }

        }


        //Main Graph loop
        ArrayList<ArrayList<GraphNode.EdgeInfo>> pathsTakenQueue = new ArrayList<>();
        for (GraphNode.EdgeInfo edge: resGraph.G[0].succ
        ) {
            if(!edge.isTypeResEdge()){
                ArrayList<GraphNode.EdgeInfo> path = new ArrayList<>();
                path.add(edge);
                pathsTakenQueue.add(path);
                resGraph.G[edge.from].visited = true;
            }
        }

        while(!pathsTakenQueue.isEmpty()){
            ArrayList<GraphNode.EdgeInfo> curPath = pathsTakenQueue.get(0);
            int lastNodeOfCurrentPath = curPath.get(curPath.size()-1).to;
            if(resGraph.G[lastNodeOfCurrentPath].nodeID == graph.vertexCt-1) {
                int max = findMaxAllowedTransport(graph, curPath);
                updateGraph(graph, curPath, max);
                if(max!=0) {
                    successfullPaths.add(new SuccPath(curPath, max));
                }
            }else {
                for (GraphNode.EdgeInfo successor : resGraph.G[lastNodeOfCurrentPath].succ) {
                    if (!successor.isTypeResEdge() && successor.amountCarried>0) {
                        ArrayList<GraphNode.EdgeInfo> path = new ArrayList<>(curPath);
                        path.add(successor);
                        resGraph.G[successor.from].visited = true;
                        pathsTakenQueue.add(path);
                    }
                }
            }
            pathsTakenQueue.remove(curPath);
        }

        return successfullPaths;
    }

    public static void updateGraph(Graph graph, ArrayList<GraphNode.EdgeInfo> path, int maxFlow){
        for (GraphNode.EdgeInfo edge: path
             ) {
            for (int i = 0; i < graph.G[edge.from].succ.size(); i++) {
                GraphNode.EdgeInfo quereyEdge = graph.G[edge.from].succ.get(i);
                if(quereyEdge.to == edge.to){
                    quereyEdge.addAmountCarried(maxFlow);
                }
            }
        }
    }

    public static int findMaxAllowedTransport(Graph graph,ArrayList<GraphNode.EdgeInfo> path ){
        int maxFlow = 999999999;
        for (GraphNode.EdgeInfo edge: path
        ) {
            for (int i = 0; i < graph.G[edge.from].succ.size(); i++) {

                GraphNode.EdgeInfo quereyEdge = graph.G[edge.from].succ.get(i);
                if(quereyEdge.to == edge.to) {
                    if (maxFlow > quereyEdge.remainingCap)
                        maxFlow = quereyEdge.remainingCap;
                }
            }
        }
        return maxFlow;
    }



    public static ArrayList<GraphNode.EdgeInfo> calculateMinimumCuts(Graph graph){
        ArrayList<Integer> accessibleNodes = new ArrayList<>();
        accessibleNodes.add(0);
        ArrayList<Integer> minCutQueue = new ArrayList<>(accessibleNodes);
        while (!minCutQueue.isEmpty()){
            Integer curNode = minCutQueue.get(0);
            graph.G[curNode].visited = true;
            for (GraphNode.EdgeInfo successor: graph.G[curNode].succ
            ) {
                if(successor.remainingCap>0 && !graph.G[successor.to].visited){
                    accessibleNodes.add(successor.to);
                    minCutQueue.add(successor.to);
                }
            }
            minCutQueue.remove(curNode);
        }



        ArrayList<GraphNode.EdgeInfo> edgeList = new ArrayList<>();
        for (Integer node: accessibleNodes
        ) {
            for (GraphNode.EdgeInfo successor: graph.G[node].succ
            ) {
                if(!accessibleNodes.contains(successor.to)){
                    edgeList.add(successor);
                }
            }
        }
        return edgeList;
    }

    public static void displayMaxFlow(ArrayList<SuccPath> successfulPaths, Graph graph){
        System.out.println("Max Flow");
        int maxFlow = 0;
        for (SuccPath sucpath: successfulPaths
        ) {
            maxFlow += sucpath.flow;
        }

        for (SuccPath path: successfulPaths
        ) {
            StringBuilder sb = new StringBuilder();
            sb.append("Found Flow " + path.flow + ": " );
            sb.append(printPath(path.path));
            System.out.println(sb.toString());
        }
        System.out.println("Produced: " + maxFlow + "\n");

        for (GraphNode node: graph.G
        ) {
            for (GraphNode.EdgeInfo successor:node.succ
            ) {
                if(successor.amountCarried> 0){
                    System.out.println(successor.printTransport());
                }
            }
        }
    }

    public static void displayMinCut(ArrayList<GraphNode.EdgeInfo> edgeList){
        System.out.println();
        System.out.println("Min Cut:");
        for (GraphNode.EdgeInfo edge: edgeList
        ) {
            System.out.println(edge.printTransport());
        }
    }

    public static void main(String[] args) throws CloneNotSupportedException {
        // create graph from file
        ArrayList<String> filenames = new ArrayList<>();
        filenames.add("demands1.txt");
        filenames.add("demands2.txt");
        filenames.add("demands3.txt");
        filenames.add("demands4.txt");
        filenames.add("demands5.txt");
        filenames.add("demands6.txt");
        for (String file: filenames
             ) {
            Graph graph = new Graph();
            graph.makeGraph(file);
            ArrayList<SuccPath> successfulPaths = createMaxFlowGraph(graph);
            ArrayList<GraphNode.EdgeInfo> edgeList = calculateMinimumCuts(graph);
            System.out.println(file);
            displayMaxFlow(successfulPaths, graph);
            displayMinCut(edgeList);
            System.out.println("---------------------------");
            System.out.println();
        }
    }
}
