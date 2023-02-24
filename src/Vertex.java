import java.util.ArrayList;

public class Vertex {

    String number;
    ArrayList<Edge> outgoingEdges = new ArrayList<>();
    ArrayList<Edge> incomingEdges = new ArrayList<>();
    int duration;
    int rank;

    Vertex(String givenNumber, int givenDuration){
        this.number = givenNumber;
        this.duration = givenDuration;
    }

    @Override
    public String toString(){
        return this.number;
    }

}
