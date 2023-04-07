import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        Main project = new Main();
        project.Menu();

        // TODO
        // check if earliest and latest works : OK
        // implements total float : OK
        // implement computing critical path : OK but need to be check on multiple path
        // verify if everything work on testing on graph
        // change the display as in the project pdf
        // menu : bug when asking again for file number -> take the previous input


    }

    public void Menu(){

        Graph graph = new Graph("file" +  getUserFileNumber() + ".txt");
        System.out.println("\n");
        System.out.println("Adjacent Matrix:\n" + graph);

        if(!graph.isCyclic()){
            graph.computeRanks();
            graph.computeDates();
            graph.computeTotalFloats();
            graph.displayCriticalPath();

            //System.out.println(graph.displayScheduling());
        }
        else{
            System.out.println("cyclic so not possible to compute dates");
        }
        System.out.print("\n");
        System.out.println("what to do next ? ");
        getUserInputContinue();
    }



    public String getUserFileNumber(){
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.print("Enter file number please : ");

        String choosenFile = myObj.nextLine();  // Read user input
        int choosenFileAsInteger = Integer.parseInt(choosenFile);

        if(choosenFileAsInteger >= 0 && choosenFileAsInteger <= 12){
            System.out.println("you choose the file " + choosenFile);
        }else{
            System.out.println("enter a number between 0 and 12 both include \n");
            getUserFileNumber();
        }

        return choosenFile;

    }

    public void getUserInputContinue(){
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.print("Do you want to work on an other file ? Press yes or no : ");

        String choice= myObj.nextLine();  // Read user input

        if(choice.equals("yes")){
            Menu();

        }else if(choice.equals("no")){
            System.out.println("bye bye mon gars");
            System.exit(0);
        }
        else{
            System.out.print("enter a good response please ");
            getUserInputContinue();
        }


    }
}
