public class Couple {
    int duration;
    int vertex;

    public Couple(int duration, int vertex) {
        this.duration = duration;
        this.vertex = vertex;
    }

    public Couple(int duration) {
        this.duration = duration;
    }
    @Override
    public String toString(){
        return "" + duration + "_" + vertex;
    }
}
