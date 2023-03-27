import java.util.ArrayList;

public class Vertex {

    String number;
    ArrayList<Edge> outgoingEdges = new ArrayList<>();
    int outDegree;
    ArrayList<Edge> incomingEdges = new ArrayList<>();
    int inDegree;
    int duration;
    int rank;

    ArrayList<Vertex> predecessors = new ArrayList<>();

    ArrayList<Couple> datesPerPredecessors = new ArrayList<>();

    Couple earliestDate;

    ArrayList<Vertex> successors = new ArrayList<>();

    ArrayList<Couple> datesPerSuccessors = new ArrayList<>();

    Couple latestDate;

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
