import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Graph {

    ArrayList<Vertex> vertices = new ArrayList<>();
    ArrayList<Edge> edges = new ArrayList<>();

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
            System.out.println(this.edges);

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

    public void showAdjacentMatrix(){
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
                if(checkEdge(row, col, copyEdges)>=0){
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
        System.out.println("Adjacency Matrix:\n"+strbld);
    }

}
