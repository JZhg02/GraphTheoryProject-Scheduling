import java.util.ArrayList;

public class Vertex {

    String number;
    ArrayList<Edge> outgoingEdges = new ArrayList<>();
    int outDegre;
    ArrayList<Edge> incomingEdges = new ArrayList<>();
    int inDegre;
    int duration;
    int rank;

    Vertex(String givenNumber, int givenDuration){
        this.number = givenNumber;
        this.duration = givenDuration;
    }

    public void computeDegre(){
        inDegre = incomingEdges.size();
        outDegre = outgoingEdges.size();

    }

    @Override
    public String toString(){
        return this.number;
    }

}
