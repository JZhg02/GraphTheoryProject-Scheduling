import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class Graph {

    ArrayList<Vertex> vertices = new ArrayList<>();
    ArrayList<Edge> edges = new ArrayList<>();

    int highestRank;

    int longestDuration;

    Graph(String fileName) {

        System.out.println("* Creating the scheduling graph:");
        try {
            // Reads the file
            BufferedReader reader = new BufferedReader(new FileReader("src/TestFiles/"+fileName));
            String line;

            // Initializes the vertices
            while((line = reader.readLine()) != null){
                String[] arr = line.split(" ");
                this.vertices.add(new Vertex(arr[0], Integer.parseInt(arr[1])));
            }

            // Initializes alpha (0) and omega (N+1)
            this.vertices.add(0, new Vertex("0", 0));
            this.vertices.add(new Vertex(String.valueOf(this.vertices.size()), 0));

            reader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        System.out.println("There are "+this.vertices.size()+" vertices.");

        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/TestFiles/"+fileName));
            String line;

            // Initializes the edges
            while((line = reader.readLine()) != null){
                String[] arr = line.split(" ");

                // Add numbers starting from index 2 (basically, add predecessors to array predecessors)
                ArrayList<String> predecessors = new ArrayList<>(Arrays.asList(arr).subList(2, arr.length));
                if(predecessors.isEmpty()){
                    Edge edge = new Edge(this.vertices.get(0), this.vertices.get(Integer.parseInt(arr[0])), 0);
                    this.vertices.get(0).outgoingEdges.add(edge);
                    this.vertices.get(Integer.parseInt(arr[0])).incomingEdges.add(edge);
                    this.edges.add(edge);
                } else {
                    for(String predecessor : predecessors){
                        Edge edge = new Edge(this.vertices.get(Integer.parseInt(predecessor)), this.vertices.get(Integer.parseInt(arr[0])), this.vertices.get(Integer.parseInt(predecessor)).duration);
                        this.vertices.get(Integer.parseInt(predecessor)).outgoingEdges.add(edge);
                        this.vertices.get(Integer.parseInt(arr[0])).incomingEdges.add(edge);
                        this.edges.add(edge);
                    }
                }
            }
            // Initialize edges from vertices without outgoing edges to omega (N+1)
            for(Vertex vertex : this.vertices){
                // If vertex has no outgoing edges and is not the last vertex (omega/N+1)
                if(vertex.outgoingEdges.isEmpty() && !Objects.equals(vertex.number, String.valueOf(this.vertices.size()-1))){
                    Edge edge = new Edge(vertex, this.vertices.get(this.vertices.size()-1), vertex.duration);
                    vertex.outgoingEdges.add(edge);
                    this.vertices.get(this.vertices.size()-1).incomingEdges.add(edge);
                    this.edges.add(edge);
                }
            }

            reader.close();

            System.out.println("And "+this.edges.size()+" edges.");

            // sort the edges by the ascending number of the starting vertex
            Collections.sort(edges);

            for(Edge edge: this.edges){
                System.out.println(edge);
            }

            computeDegrees();

        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("An error occurred, please try again.");
        }
    }


    public int checkEdge(int row, int col, ArrayList<Edge> array){
        for(Edge edge : array){
            if(Objects.equals(edge.from.number, String.valueOf(row)) && Objects.equals(edge.to.number, String.valueOf(col))){
                return edge.duration;
            }
        }
        return -1;
    }


    public boolean noNegativeDuration(){
        // for each edge, if the edge's duration < 0 => false, else => true
        for(Edge edge : this.edges){
            if( edge.getDuration() < 0 ){
                System.out.println("There's at least a negative edge in this graph.");
                return false;
            }
        }
        System.out.println("The graph has no negative edges. ");
        return true;
    }


    /**
     * Builds an array of vertex labels from the list of vertices in the graph.
     * @return an array of vertex labels.
     */
    public String[] buildMappingIDVertex(){
        // Create a new String array with the same size as the number of vertices in the graph
        String[] MappingIDVertex = new String[vertices.size()];
        // Loop through each vertex in the graph
        for (int i = 0; i < vertices.size(); i++){
            // For each vertex, add its label to the MappingIDVertex array
            // The getNumber() method returns the String label of the vertex
            // We duplicate the String using the String constructor to avoid side effects
            MappingIDVertex[i] = vertices.get(i).getNumber() ;
        }
        // Return the array of vertex labels
        return MappingIDVertex;
    }


    public int InverseMappingIDVertex(String [] MappingIDVertex, String labelVertex){
        // Loop through each vertex in the MappingIDVertex array
        for(int i = 0; i <MappingIDVertex.length; i++){
            // Check if the label of the vertex at index i matches the label we are searching for
            if (MappingIDVertex[i].equals(labelVertex)){
                // If it does, return the index i
                return i;
            }
        }
        // If the label is not found, return -1
        return -1;
    }


    //Build the adjacency Matrix
    public List<List<Integer>> buildAdjacencyMatrix(String [] MappingIDVertex){
        // Initialize an empty AdjacencyMatrix with the same size as the MappingIDVertex array
        List<List<Integer>> AdjacencyMatrix = new ArrayList<>(MappingIDVertex.length);
        // Loop through each vertex in the MappingIDVertex array
        for(int i = 0; i < MappingIDVertex.length; i++ ){
            // Create a new LinkedList for each vertex in the AdjacencyMatrix
            AdjacencyMatrix.add(new LinkedList<Integer>());
            // Loop through each edge in the graph
            for(Edge edge : this.edges){
                // Check if the "from" vertex of the edge matches the current vertex in the MappingIDVertex array
                if(edge.getFrom().getNumber().equals(MappingIDVertex[i])){
                    // If it matches, add the "to" vertex of the edge to the LinkedList of the current vertex in the AdjacencyMatrix
                    AdjacencyMatrix.get(i).add(InverseMappingIDVertex(MappingIDVertex, edge.getTo().getNumber()));
                }
            }
        }
        // Return the completed AdjacencyMatrix
        return AdjacencyMatrix;
    }


    // Returns true if the graph contains a cycle, else false.
    public boolean isCyclic() {
        // Build adjacency matrix
        String[] MappingIDVertex = buildMappingIDVertex();
        List<List<Integer>> AdjacencyMatrix = buildAdjacencyMatrix(MappingIDVertex);
        System.out.println(AdjacencyMatrix);

        // Initialize entry points and remaining vertices
        List<Integer> entryPoints = new ArrayList<>();
        List<Integer> remainingVertices = new ArrayList<>();
        for (int i = 0; i < MappingIDVertex.length; i++) {
            remainingVertices.add(i);
            if (AdjacencyMatrix.get(i).isEmpty()) {
                entryPoints.add(i);
            }
        }

        // Eliminate entry points until graph is empty or no entry points remain
        while (!remainingVertices.isEmpty()) {
            System.out.println("Exit points: " + entryPoints);

            if (entryPoints.isEmpty()) {
                // No entry points left but graph is not empty, cycle detected
                System.out.println("Eliminating exit points");
                System.out.println("Remaining vertices: " + remainingVertices);
                System.out.println("-> There is a cycle");
                return true;
            }

            // Remove entry points and their edges from graph
            for (int i : entryPoints) {
                remainingVertices.remove((Integer) i);
                for (List<Integer> edges : AdjacencyMatrix) {
                    edges.remove((Integer) i);
                }
            }

            // Find new entry points
            List<Integer> newEntryPoints = new ArrayList<>();
            for (int i : remainingVertices) {
                if (AdjacencyMatrix.get(i).isEmpty()) {
                    newEntryPoints.add(i);
                }
            }

            // Update entry points
            entryPoints = newEntryPoints;
            System.out.println("Eliminating exit points");
            System.out.println("Remaining vertices: " + remainingVertices);
        }

        // Graph is empty, no cycle detected
        System.out.println("Exit points: None");
        System.out.println("Eliminating exit points");
        System.out.println("Remaining vertices: None");
        System.out.println("-> There is no cycle");
        return false;
    }


    public int getVertexByNumber(int number){
        if(number <= vertices.size()){
            for(Vertex vertex : vertices){
                if ((Integer.parseInt(vertex.number) == number)){
                    return Integer.parseInt(vertex.number);
                }
            }
        }
        return -1;
    }


    public void computeDegrees(){
        for(Vertex vertex : vertices){
            vertex.computeDegree();
        }
    }


    public void computeRanks(){
        int rank = 0;
        ArrayList<Vertex> sourceVertices = new ArrayList<>();
        ArrayList<Vertex> leftVerticesToCheck = new ArrayList<>(vertices);

        // Adding alpha as it is the sole vertex in a scheduling graph to be rank 0
        // sourceVertices are the vertices without predecessors
        sourceVertices.add(vertices.get(0));
        leftVerticesToCheck.remove(sourceVertices.get(0));

        while(!sourceVertices.isEmpty()){

            // Assign the rank
            for(Vertex sourceVertex : sourceVertices ){
                vertices.get(getVertexByNumber(Integer.parseInt(sourceVertex.number))).rank = rank;
            }

            // For each vertex, remove by 1 the inDegree if they have a predecessor in sourceVertices
            for(Vertex leftVertex : leftVerticesToCheck){
                for(Vertex sourceVertex : sourceVertices){
                    for(Edge edge : leftVertex.incomingEdges){
                        if(edge.from == sourceVertex){
                            leftVertex.inDegree--;
                        }
                    }
                }
            }
            // Clearing the sourceVertices list to put the new vertices without predecessors
            sourceVertices.clear();

            // Add vertices without predecessors to source and removing them from left list
            for(Vertex vertex : vertices){
                // If the vertex in-degree is equal to 0, and we haven't checked it yet, then we have to check it so,
                // We add it to the source list and delete it from the leftVertices list
                if(vertex.inDegree == 0 && leftVerticesToCheck.contains(vertex)){
                    sourceVertices.add(vertex);
                    leftVerticesToCheck.remove(vertex);
                }
            }
            highestRank = rank;
            rank++;
        }

        // Reset the degrees of all the vertices
        computeDegrees();

        // sort the vertices list by rank index with ascending order
        Collections.sort(vertices);
    }


    @Override
    public String toString(){
        ArrayList<ArrayList<String>> matrix = new ArrayList<>();
        // Create first row
        ArrayList<String> firstRow = new ArrayList<>();
        for(int col = 0; col < this.vertices.size(); col++){
            firstRow.add(String.valueOf(col));
        }
        firstRow.add(0, null);
        matrix.add(firstRow);

        // Create the others rows
        ArrayList<Edge> copyEdges = (ArrayList<Edge>) this.edges.clone();
        for(int row = 0; row < this.vertices.size(); row++){
            ArrayList<String> newRow = new ArrayList<>();
            // Write the vertex number
            newRow.add(String.valueOf(row));
            // Fill the values with either * when there's nothing or the duration value of the task
            for(int col = 0; col < this.vertices.size(); col++){
                if(checkEdge(row, col, copyEdges) >=0 ){
                    newRow.add(String.valueOf(checkEdge(row, col, copyEdges)));
                } else {
                    newRow.add("*");
                }
            }
            matrix.add(newRow);
        }

        // Necessary code to make printing to terminal easier.
        StringBuilder strbld = new StringBuilder();
        for (ArrayList<String> strings : matrix) {
            for (int col = 0; col < matrix.get(0).size(); col++) {
                if (strings.get(col) == null) {
                    strbld.append("   ");
                } else if (Objects.equals(strings.get(col), "*")) {
                    strbld.append("  ").append(strings.get(col));
                } else if (Integer.parseInt(strings.get(col)) >= 0 && Integer.parseInt(strings.get(col)) <= 9) {
                    strbld.append("  ").append(strings.get(col));
                } else {
                    strbld.append(" ").append(strings.get(col));
                }
            }
            strbld.append("\n");
        }
        return strbld.toString();
    }


    public void computeDates(){
        computePredecessors();
        computeSuccessors();
        computeDatesPerPredecessors();
        computeDatesPerSuccessor();
    }


    public void computePredecessors(){
        for (Vertex vertex : vertices){
            for(Edge edge : edges){
                if(edge.to == vertex){
                    vertex.predecessors.add(edge.from);
                }
            }
        }
    }


    public void computeSuccessors(){
        for (Vertex vertex : vertices){
            for(Edge edge : edges){
                if(edge.from == vertex){
                    vertex.successors.add(edge.to);
                }
            }
        }
    }


    public void computeDatesPerPredecessors() {

        vertices.get(0).earliestDate = new Couple(0, 0);
        vertices.get(0).datesPerPredecessors.add(new Couple(0, 0));

        for (int i = 1; i <= highestRank; i++) {
            for (Vertex vertex : vertices) {
                if (vertex.rank == i) {
                    for (Vertex predecessor : vertex.predecessors) {
                        vertex.datesPerPredecessors.add(new Couple(predecessor.earliestDate.duration + predecessor.duration, Integer.parseInt(predecessor.number)));
                    }
                    vertex.earliestDate = vertex.datesPerPredecessors.get(0);
                    for (Couple datesPerPredecessor : vertex.datesPerPredecessors) {
                        if (datesPerPredecessor.duration > vertex.earliestDate.duration) {
                            vertex.earliestDate = datesPerPredecessor;
                        }
                    }

                }
            }
        }
    }


    public void computeDatesPerSuccessor(){

        longestDuration =  vertices.get(vertices.size() - 1).earliestDate.duration;
        vertices.get(vertices.size() - 1).datesPerSuccessors.add(new Couple(vertices.get(vertices.size() - 1).earliestDate.duration, this.vertices.size()-1));
        vertices.get(vertices.size() - 1).latestDate = new Couple(vertices.get(vertices.size() - 1).earliestDate.duration, this.vertices.size()-1);


        for (int i = highestRank - 1; i >= 0 ; i--) {
            for (Vertex vertex : vertices) {
                if (vertex.rank == i) {
                    for (Vertex successor : vertex.successors) {
                        vertex.datesPerSuccessors.add(new Couple(successor.latestDate.duration - vertex.duration, Integer.parseInt(successor.number)));
                    }
                    vertex.latestDate = vertex.datesPerSuccessors.get(0);
                    for (Couple datesPerSuccessor : vertex.datesPerSuccessors) {
                        if (datesPerSuccessor.duration < vertex.latestDate.duration) {
                            vertex.latestDate = datesPerSuccessor;
                        }
                    }

                }
            }
        }
    }


    public void computeTotalFloats(){
        for(Vertex vertex : vertices){
            vertex.totalFloat = vertex.latestDate.duration - vertex.earliestDate.duration;
        }
    }


    public void displayDatesTable(){
        ArrayList<ArrayList<String>> datesTable = new ArrayList<>();
        ArrayList<String> features = new ArrayList<>();
        features.add("Ranks");
        features.add("Vertex(Duration)");
        features.add("Predecessors");
        features.add("Dates per Predecessors");
        features.add("Earliest Date");
        features.add("Successors");
        features.add("Dates per Successors");
        features.add("Latest Date");
        features.add("Total Float");
        datesTable.add(features);
        for(Vertex vertex : this.vertices){
            ArrayList<String> vertexInfo = new ArrayList<>();
            vertexInfo.add(String.valueOf(vertex.rank));
            vertexInfo.add(vertex.number+"("+vertex.duration+")");
            vertexInfo.add(String.valueOf(vertex.predecessors));
            vertexInfo.add(String.valueOf(vertex.datesPerPredecessors));
            vertexInfo.add(String.valueOf(vertex.earliestDate));
            vertexInfo.add(String.valueOf(vertex.successors));
            vertexInfo.add(String.valueOf(vertex.datesPerSuccessors));
            vertexInfo.add(String.valueOf(vertex.latestDate));
            vertexInfo.add(String.valueOf(vertex.totalFloat));
            datesTable.add(vertexInfo);
        }

        int longestString = 0;
        for (ArrayList<String> stringArrayList : datesTable) {
            for (int j = 0; j < datesTable.get(0).size(); j++) {
                if (stringArrayList.get(j).length() > longestString) {
                    longestString = stringArrayList.get(j).length();
                }
            }
        }
        longestString++;

        StringBuilder strbld = new StringBuilder("");
        for(int col = 0; col < datesTable.get(0).size(); col++){
            for (ArrayList<String> strings : datesTable) {
                strbld.append(" ".repeat(Math.max(0, longestString - strings.get(col).length()))).append(strings.get(col));
            }
            strbld.append("\n");
        }
        System.out.println(strbld);
    }


    public void displayCriticalPath() {

        // create a list to store all the critical path of the graph
        ArrayList<ArrayList<Vertex>> allCriticalPaths = new ArrayList<>();

        // create the first critical path
        ArrayList<Vertex> FirstCriticalPath = new ArrayList<>();

        // add to it the entry vertex
        FirstCriticalPath.add(vertices.get(0));

        // add this path to the list containing all the possible other paths
        allCriticalPaths.add(FirstCriticalPath);

        // boolean conditions
        boolean newCriticalPathFound;
        int index = 0;

        // do while loop -> will loop if a newpath found is found ( newPathFound is true ) or if a path is list hasn't been updated yet
        do {
            // initialize newpathfound variable
            newCriticalPathFound = false;

            // get a path in the paths list according to the index
            ArrayList<Vertex> pathWorkingOn = allCriticalPaths.get(index);

            /* here, the algorithm is the following :
            we loop on all the vertex in the graph
            if the vertex is equal to the last vertex of the current path, for example : we have the path 1 -> 2 -> 3
            then the vertex should be 3
            then, if the vertex total float is equals to 0, ie it belongs to a critical path, we loop on all the edges
            of the graph and check for edges whose start on this vertex and has a "to vertex " with a total float of 0.
            If we found one, then we check if the current path contains the "from vertex" of the edge, and if it's the
            case we add the "to vertex " to the current critical path the algorithm is working on.

            */

            /*
            As you can see, we don't mention the "newCriticalPathFound" variable in the above algorithm.
            In fact, this algorithm only works for graph that only contains one and only one critical path.
            To deal with graph that contains more than one, we need to add more steps to our algorithm.

            First, we add a variable "LastEdgeChecked" that store the last edge the algorithm checked. For example,
            imagine that we are working on the following graph :

            1 -> 2 = 1
            1 -> 3 = 1

            ( In our program, we are storing the edges exactly as above, so in a list which is sorted by the name of
            the vertex )

            In this graph, the total float of is equal to 0 for all the vertex. So the edge 1 -> 3 is selected,
            the "LastEdgeChecked" variable will store the edge 1 -> 2.

            After, if the "from vertex " of the edge stored in LastEdgeChecked is equal to the from vertex of the
            current working on edge, then it means another possible critical path ( Remember that we already check if
            the total float value of the "from vertex " and the "to vertex" of the working on edge was equal to 0 ).

            So now, we found another possible critical path. So we have to create a new list that we add to our
            list which is storing all the critical path. This list will contain all the vertices of the
            critical path we're working on, and we will add to it the "to vertex", in other words the new vertex which
            was not contains in the critical path we're working on. We set the newCriticalPathFound variable to true
            to loop again on our critical paths.

            The critical path

            Later on, the algorithm will work on the new path that has been found to complete it. And of course, a new
            critical path can be found from this path.

            At the end, we have our paths ( or one path ). There are not really critical path because a critical
            path should be the longest path, and that's not the case for all path that contain vertex with a total
            float equals to 0. We already store the longestDuration in a previous algorithm, so we just need to check
            for all our path if the sum of the duration of their vertex is equal to the longestDuration. If this is the
            case, then it is a critical path and we can then display it.

             */

            for (Vertex vertex : vertices) {
                if (vertex == pathWorkingOn.get(pathWorkingOn.size() - 1)) {
                    if (vertex.totalFloat == 0) { //
                        Edge LastEdgeChecked = null;
                        for (Edge edge : edges) {
                            if (edge.from.equals(vertex) && edge.to.totalFloat == 0) {
                                if (LastEdgeChecked != null && LastEdgeChecked.from == edge.from) {
                                    ArrayList<Vertex> newPath = new ArrayList<>(pathWorkingOn);
                                    newPath.remove(newPath.size() - 1);
                                    newPath.add(edge.to);
                                    allCriticalPaths.add(newPath);
                                    newCriticalPathFound = true;

                                } else if (pathWorkingOn.contains(edge.from)) {
                                    pathWorkingOn.add(edge.to);
                                }
                                LastEdgeChecked = edge;
                            }
                        }
                    }
                }
            }
            index++;

        } while (newCriticalPathFound || index != allCriticalPaths.size());

        System.out.println("Critical path(s) : ");
        for (ArrayList<Vertex> critical_path : allCriticalPaths) {
            int sum = 0;
            for (Vertex vertex : critical_path) {
                sum += vertex.duration;
            }
            if (sum == longestDuration) {
                for (Vertex vertex : critical_path) {
                    System.out.print(vertex.number);
                    if (vertex != vertices.get(vertices.size() - 1)) {
                        System.out.print(" -> ");
                    }
                }
                System.out.print("\n");
            }
        }
    }
}


