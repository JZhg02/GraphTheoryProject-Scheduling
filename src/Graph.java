import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public boolean checkEdgeNoNegativeDuration(){
        //parcourir l'objet S, si on tombe sur un edge avec une direction < 0 => false, si ce n'est pas le cas on sort et on retourne True
        for(Edge edge : this.edges){
            if( edge.getDuration() < 0 ){
                System.out.println("the graph has negative edges");
                return false;
            }
        }
        System.out.println("the graph has no negative edges");
        return true;
    }

    /**
     * Construit un tableau des étiquettes des sommets du graphe à partir de la liste des sommets.
     * @return un tableau des étiquettes des sommets.
     */
    public String[] buildMappingIDVertex(){
        // Crée un tableau de chaînes de caractères de taille égale au nombre de sommets dans le graphe
        String[] MappingIDVertex = new String[vertices.size()];
        // Parcours la liste des sommets du graphe
        for ( int i = 0; i < vertices.size(); i++){
            // Pour chaque sommet, on ajoute l'étiquette de ce sommet au tableau des étiquettes
            // La méthode getNumber() retourne la chaîne de caractères qui représente l'étiquette du sommet
            // On duplique la chaîne en utilisant le constructeur de la classe String pour éviter les effets de bord
            MappingIDVertex[i] = new String(vertices.get(i).getNumber()) ;
        }
        // Retourne le tableau des étiquettes de sommets
        return MappingIDVertex;
    }

    /**
     * Retourne l'indice de l'étiquette d'un sommet dans le tableau des sommets
     * @param MappingIDVertex : tableau des sommets
     * @param labelVertex : étiquette du sommet dont on veut l'indice
     * @return l'indice de l'étiquette dans le tableau des sommets, -1 si l'étiquette n'est pas présente dans le tableau
     */
    public int InverseMappingIDVertex(String [] MappingIDVertex, String labelVertex){
        // Parcours le tableau des sommets
        for(int i = 0; i <MappingIDVertex.length; i++){
            // Si l'étiquette du sommet à l'indice i est égale à l'étiquette recherchée, retourne l'indice i
            if (MappingIDVertex[i].equals(labelVertex) == true){
                return i;
            }
        }
        // Si l'étiquette n'est pas trouvée, retourne -1
        return -1;
    }

    //Methode pour fabriquer la matrice d'adjacence
    public List<List<Integer>> buildAdjacencyMatrix(String [] MappingIDVertex){
        // parcouric l'ensemble des egdes pour chaque nouveu sommet et pusher la string qui l'identifie, chaque fois qu'on rencontre un edge dans lequel le sommet i est le point de départ( from) et le sommet d'adjacent sera le to.

        List<List<Integer>> AdjacencyMatrix = new ArrayList<>(MappingIDVertex.length);
        for(int i = 0; i < MappingIDVertex.length; i++ ){
            AdjacencyMatrix.add(new LinkedList<Integer>());
            for(Edge edge : this.edges){
                if(edge.getFrom().getNumber().equals(MappingIDVertex[i]) == true){
                    AdjacencyMatrix.get(i).add(InverseMappingIDVertex(MappingIDVertex, edge.getTo().getNumber())); // on récupère l'ID associé au label du sommet TO car il est adjacent au sommet qu'on traite
                }
            }
        }
        return AdjacencyMatrix;
    }

    public boolean isCyclicUtil(int i, boolean[] visited,
                                boolean[] recStack, List<List<Integer>> AdjacencyMatrix)
    {
        // Mark the current node as visited and
        // part of recursion stack
        if (recStack[i])
            return true;

        if (visited[i])
            return false;

        visited[i] = true;

        recStack[i] = true;
        List<Integer> children = AdjacencyMatrix.get(i);

        for (Integer c: children)
            if (isCyclicUtil(c, visited, recStack, AdjacencyMatrix))
                return true;

        recStack[i] = false;

        return false;
    }

    // Returns true if the graph contains a cycle, else false.
    public boolean isCyclic() {
        //build adjacency matrix
        String[] MappingIDVertex = buildMappingIDVertex();
        List<List<Integer>> AdjacencyMatrix = buildAdjacencyMatrix(MappingIDVertex);
        //System.out.println(AdjacencyMatrix);

        // Mark all the vertices as not visited and
        // not part of recursion stack
        boolean[] visited = new boolean[MappingIDVertex.length];
        boolean[] recStack = new boolean[MappingIDVertex.length];


        // Call the recursive helper function to
        // detect cycle in different DFS trees
        for (int i = 0; i < MappingIDVertex.length; i++){
            if (isCyclicUtil(i, visited, recStack, AdjacencyMatrix)) {
                return true;
            }
        }
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

        if(!leftVerticesToCheck.isEmpty()){
            System.out.println("Cycle present in the graph. \n");
        } else {
            System.out.println("No cycles detected, ranks were computed correctly. \n");
        }


        // Reset the degrees of all the vertices
        computeDegrees();

        // sort the vertices list by rank index with ascending order
        Collections.sort(vertices);

        System.out.println("Vertex Rank :");
        for(Vertex vertex : vertices){
            System.out.println("vertex : " + vertex.number + "/" + "rank : " + vertex.rank);
        }

        // Print the vertices for the smallest rank to the biggest
        //for(int i = 0; i <= rank ; i++){
            //for(Vertex vertex : vertices){
                //if(vertex.rank == i){
                    //System.out.println("Vertex " + vertex.number + " : rank -> " + vertex.rank);
                //}
            //}
        //}
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

    public String displayScheduling(){
       return "";
    }

    public void computeDates(){
        computePredecessors();
        System.out.print("\n");
        computeSuccessors();
        System.out.print("\n");
        System.out.println("Vertex EarliestDate : ");
        computeDatesPerPredecessors();
        System.out.print("\n");
        System.out.println("Vertex LatestDate : ");
        computeDatesPerSuccesor();
        System.out.print("\n");


    }




    void computePredecessors(){
        for (Vertex vertex : vertices){
            for(Edge edge : edges){
                if(edge.to == vertex){
                    vertex.predecessors.add(edge.from);
                }
            }
        }

        /*
        for (Vertex vertex : vertices){
            System.out.println("vertex number : " + vertex.number);
            System.out.println(vertex.predecessors);
            System.out.println("\n");
        }

         */
    }

    void computeSuccessors(){
        for (Vertex vertex : vertices){
            for(Edge edge : edges){
                if(edge.from == vertex){
                    vertex.successors.add(edge.to);
                }
            }
        }

        /*
        for (Vertex vertex : vertices){
            System.out.println("vertex number : " + vertex.number);
            System.out.println(vertex.successors);
            System.out.println("\n");
        }

         */

    }

    public Vertex getVertexByRank(int rank){
        for(Vertex vertex : vertices){
            if(vertex.rank == rank){
                return vertex;
            }
        }
        return null;
    }



    void computeDatesPerPredecessors() {

        vertices.get(0).earliestDate = new Couple(0, -1);
        vertices.get(0).datesPerPredecessors.add(new Couple(0, -1));

        for (int i = 1; i <= highestRank; i++) {
            for (Vertex vertex : vertices) {
                if (vertex.rank == i) {
                    for (Vertex predecessor : vertex.predecessors) {
                        vertex.datesPerPredecessors.add(new Couple(predecessor.earliestDate.duration + predecessor.duration, Integer.parseInt(predecessor.number)));

                    }
                    //System.out.println(vertex.number);
                    //System.out.println(vertex.datesPerPredecessors);
                    vertex.earliestDate = vertex.datesPerPredecessors.get(0);
                    for (Couple datesPerPredecessor : vertex.datesPerPredecessors) {
                        if (datesPerPredecessor.duration > vertex.earliestDate.duration) {
                            vertex.earliestDate = datesPerPredecessor;
                        }
                    }

                }
            }
        }

        for(Vertex vertex : vertices){
            System.out.println("vertex " + vertex.number + "/" + " date : " + vertex.earliestDate);
        }



        System.out.print("\n");


    }


    void computeDatesPerSuccesor(){

        longestDuration =  vertices.get(vertices.size() - 1).earliestDate.duration;
        System.out.println("longest duration : " + longestDuration);
        vertices.get(vertices.size() - 1).datesPerSuccessors.add(new Couple(vertices.get(vertices.size() - 1).earliestDate.duration, -1));
        vertices.get(vertices.size() - 1).latestDate = new Couple(vertices.get(vertices.size() - 1).earliestDate.duration, -1);


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

        for(Vertex vertex : vertices){
            System.out.println("latestDate : " + vertex.latestDate);
        }

    }

    void computeTotalFloats(){
        for(Vertex vertex : vertices){
            vertex.totalFloat = vertex.latestDate.duration - vertex.earliestDate.duration;
            System.out.println("vertex :" + vertex.number + " | " + "total float : " + vertex.totalFloat);
        }
        System.out.print("\n");
    }

    void displayCriticalPath(){

        // create a list to store all the critical path of the graph
        ArrayList<ArrayList> allCriticalPaths = new ArrayList<>();

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
            // initialize netpathfound variable
            newCriticalPathFound = false;

            // get a path in the paths list according to the index
            ArrayList<Vertex> pathWorkingOn = allCriticalPaths.get(index);

            System.out.println("path working on :" + pathWorkingOn);
            System.out.println("");

            /* here, the algorithm is the following :
            we loop on all the vertex in the graph
            if the vertex is equal to the last vertex of the current path, for example : we have the path 1 -> 2 -> 3
            then the vertex should be 3
            then, if the vertex total float is equals to 0, ie it belong to a critical path, we loop on all the edges
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

            ( In our programm, we are storing the edges exaxctly as above, so in a list which is sorted by the name of
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
            path should be the longest path, and thats not the case for all path that contain vertex with a total
            float equals to 0. We already store the longestDuration in a preivous algorithm, so we just need to check
            for all our path if the sum of the duration of their vertex is equal to the longestDuration. If this is the
            case, then it is a critical path and we can then display it.

             */



            for(Vertex vertex : vertices) {
                if(vertex == pathWorkingOn.get(pathWorkingOn.size() - 1)){
                    System.out.println("patjworlingon :" + pathWorkingOn);
                    System.out.println("last vertex ? : " +  pathWorkingOn.get(pathWorkingOn.size() - 1));
                    System.out.println("");
                    if (vertex.totalFloat == 0) { //
                        Edge LastEdgeChecked = null;
                        for (Edge edge : edges) {
                            System.out.println(edge);
                            if (edge.from.equals(vertex) && edge.to.totalFloat == 0) {
                                if (LastEdgeChecked != null && LastEdgeChecked.from == edge.from) {
                                    System.out.println("actuel edge : " + edge);
                                    System.out.println("fromEdgeAlreadySeen : " + LastEdgeChecked);
                                    ArrayList<Vertex> newPath = new ArrayList<>(pathWorkingOn);
                                    newPath.remove(newPath.size() - 1);
                                    System.out.println("pathWorkingOn : " + pathWorkingOn);
                                    newPath.add(edge.to);
                                    System.out.println("newpath : " + newPath);
                                    System.out.println("-------");
                                    allCriticalPaths.add(newPath);
                                    newCriticalPathFound = true;

                                }else if (pathWorkingOn.contains(edge.from)) {
                                        System.out.println("vertex to add : " + edge.to);
                                        pathWorkingOn.add(edge.to);
                                    }

                                LastEdgeChecked = edge;

                                }

                        }
                    }
                }
            }

            //System.out.println("all critical path : " + allCriticalPaths);

            index++;

        }while (newCriticalPathFound || index != allCriticalPaths.size());

        /*
        for(ArrayList path : allCriticalPaths){
            for(Vertex vertex : path){
                System.out.println(vertex);
            }
        }

         */

        System.out.println("Critical path : ");
        for(ArrayList<Vertex> critical_path : allCriticalPaths){
            int sum = 0;
            for(Vertex vertex : critical_path){
                sum += vertex.duration;
            }
            if(sum == longestDuration){

                for(Vertex vertex : critical_path){
                    System.out.print(vertex.number);
                    if(vertex != vertices.get(vertices.size() - 1)){
                        System.out.print(" -> ");
                    }
                }
                System.out.println("");

            }
        }

        /*
        System.out.print("critical path(s) after calculation: ");
        System.out.println(allCriticalPaths);

         */




        }



    }


