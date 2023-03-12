public class Main {
    public static void main(String[] args){

        Graph graph = new Graph("file0.txt");
        System.out.println("\n");
        System.out.println("Adjacent Matrix:\n"+graph);
        System.out.println("The rank of each vertex of the graph is : \n");
        graph.computeRanks();

    }
}
