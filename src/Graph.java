import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Graph {

    ArrayList<Vertex> vertices = new ArrayList<>();
    ArrayList<Edge> edges = new ArrayList<>();

    Graph(String fileName) {

        try {
            // Reads the file
            BufferedReader reader = new BufferedReader(new FileReader("src/TestFiles/"+fileName));
            String line;

            // Initializes the vertices
            while((line = reader.readLine()) != null){
                String[] arr = line.split(" ");
                System.out.println(line);
                this.vertices.add(new Vertex(arr[0], Integer.parseInt(arr[1])));
            }

            // Initializes alpha and omega
            this.vertices.add(0, new Vertex("α", 0));
            this.vertices.add(new Vertex("Ω", 0));
            System.out.println(this.vertices);

            reader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

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
                    System.out.println("From "+this.vertices.get(0).number);
                    System.out.println("To "+this.vertices.get(Integer.parseInt(arr[0])));
                    System.out.println("Duration "+0);
                    this.vertices.get(0).outgoingEdges.add(edge);
                    this.vertices.get(Integer.parseInt(arr[0])).incomingEdges.add(edge);
                } else {
                    for(String predecessor : predecessors){
                        Edge edge = new Edge(this.vertices.get(Integer.parseInt(predecessor)), this.vertices.get(Integer.parseInt(arr[0])), this.vertices.get(Integer.parseInt(predecessor)).duration);
                        System.out.println("From "+this.vertices.get(Integer.parseInt(predecessor)));
                        System.out.println("To "+this.vertices.get(Integer.parseInt(arr[0])));
                        System.out.println("Duration "+this.vertices.get(Integer.parseInt(predecessor)).duration);
                        this.vertices.get(Integer.parseInt(predecessor)).outgoingEdges.add(edge);
                        this.vertices.get(Integer.parseInt(arr[0])).incomingEdges.add(edge);
                    }
                }
                // Initializes edges from N to omega
                Edge edge = new Edge(this.vertices.get(this.vertices.size()-2), this.vertices.get(this.vertices.size()-1), this.vertices.get(this.vertices.size()-2).duration);
                System.out.println("From "+this.vertices.get(this.vertices.size()-2));
                System.out.println("To "+this.vertices.get(this.vertices.size()-1));
                System.out.println("Duration "+this.vertices.get(this.vertices.size()-2).duration);
                this.vertices.get(this.vertices.size()-2).outgoingEdges.add(edge);
                this.vertices.get(this.vertices.size()-1).incomingEdges.add(edge);
            }
            reader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

}
