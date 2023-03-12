import java.util.ArrayList;

public class Vertex {

    String number;
    ArrayList<Edge> outgoingEdges = new ArrayList<>();
    int outDegree;
    ArrayList<Edge> incomingEdges = new ArrayList<>();
    int inDegree;
    int duration;
    int rank;

    Vertex(String givenNumber, int givenDuration){
        this.number = givenNumber;
        this.duration = givenDuration;
    }

    public void computeDegree(){
        inDegree = incomingEdges.size();
        outDegree = outgoingEdges.size();
    }

    @Override
    public String toString(){
        return this.number;
    }

}
