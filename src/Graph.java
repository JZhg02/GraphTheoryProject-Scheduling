import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {

    ArrayList<Vertex> vertices = new ArrayList<>();
    ArrayList<Edge> edges = new ArrayList<>();

    int highestRank;

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

        System.out.print("Vertex Rank : ");
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
        System.out.print("Vertex EarliestDate : ");
        computeDatesPerPredecessors();
        System.out.print("\n");
        System.out.print("Vertex LatestDate : ");
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
            System.out.println("earliest date : " + vertex.earliestDate);
        }

        System.out.print("\n");

    }


    void computeDatesPerSuccesor(){

        vertices.get(vertices.size() - 1).latestDate = new Couple(vertices.get(vertices.size() - 1).earliestDate.duration, -1);
        vertices.get(vertices.size() - 1).datesPerSuccessors.add(new Couple(vertices.get(vertices.size() - 1).earliestDate.duration, -1));

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

        // is working if only one critical path, not many :(

        ArrayList<ArrayList> allCriticalPaths = new ArrayList<>();

        ArrayList<Vertex> FirstCriticalPath = new ArrayList<>();

        // get the first vertex because for it, total float is always equal to 0
        FirstCriticalPath.add(vertices.get(0));

        allCriticalPaths.add(FirstCriticalPath);

        boolean newPathFound;
        int index = 0;

        do {
            newPathFound = false;

            ArrayList<Vertex> pathWorkingOn = allCriticalPaths.get(index);

            for(Vertex vertex : vertices){
                // loop on all vertex except the first one, ie it's rank == 0
                if(vertex.totalFloat == 0){ //
                    for(Edge edge : edges){
                        if(edge.from.equals(vertex)){
                            if(edge.to.totalFloat == 0){
                                pathWorkingOn.add(edge.to);
                            }
                        }
                    }
                }
            }

            System.out.println("index : " +  index);
            index++;


        }while (newPathFound);

        System.out.print("critical path : ");
        System.out.println(allCriticalPaths);




        }



    }


