import java.util.ArrayList;

public class Vertex implements Comparable<Vertex> {

    String number;
    ArrayList<Edge> outgoingEdges = new ArrayList<>();
    int outDegree;
    ArrayList<Edge> incomingEdges = new ArrayList<>();
    int inDegree;
    int duration;
    int rank;

    int totalFloat;

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
    public int compareTo(Vertex vertexToCompare){
        int rankNumber = vertexToCompare.rank;
        return rank - rankNumber;

    }

    @Override
    public String toString(){
        return this.number;
    }

    public String getNumber(){return this.number;}


}
