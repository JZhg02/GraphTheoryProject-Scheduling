public class Main {
    public static void main(String[] args){

        Graph graph = new Graph("table4.txt");
        System.out.println("\n");
        System.out.println("Adjacent Matrix:\n"+graph);
        System.out.println("The rank of each vertex of the graph is : \n");
        if(!graph.isCyclic()){
            graph.computeRanks();

            //graph.computeDates();
            graph.computePredecessors();
            graph.computeSuccessors();
            System.out.println("\n");
            graph.computeDatesPerPredecessors();
            System.out.println("\n");
            graph.computeDatesPerSuccesor();
        }
        else{
            System.out.println("cyclic so not possible to compute dates");
        }
    }
}
