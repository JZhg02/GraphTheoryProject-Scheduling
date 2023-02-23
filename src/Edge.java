public class Edge {

    Vertex from;
    Vertex to;
    int duration;

    Edge(Vertex from, Vertex to, int givenDuration){
        this.from = from;
        this.to = to;
        this.duration = givenDuration;
    }

    @Override
    public String toString(){
        return this.from+" -> "+this.to+" = "+this.duration;
    }

}
