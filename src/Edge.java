public class Edge implements Comparable<Edge> {

    Vertex from;
    Vertex to;
    int duration;

    Edge(Vertex from, Vertex to, int givenDuration){
        this.from = from;
        this.to = to;
        this.duration = givenDuration;
    }

    @Override
    public int compareTo(Edge edgeToCompare){
        int vertexNumber = Integer.parseInt(edgeToCompare.from.number);
        return Integer.parseInt(from.number) - vertexNumber;
    }

    @Override
    public String toString(){
        return this.from+" -> "+this.to+" = "+this.duration;
    }

}
