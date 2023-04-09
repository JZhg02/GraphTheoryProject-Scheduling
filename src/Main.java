import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        String fileName = "table" +  getUserFileNumber();
        try {
            outputFiles(fileName);
        } catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("This is "+fileName+"\n");
        Graph graph = new Graph(fileName+".txt");
        System.out.print("\n");
        System.out.println("Adjacent Matrix:\n" + graph);

        System.out.println("Checking if there are cycles and negative edges...\n");
        if(!graph.isCyclic() && graph.noNegativeDuration()){
            System.out.println("\nComputing ranks and dates...\n");
            graph.computeRanks();
            graph.computeDates();
            graph.computeTotalFloats();
            graph.displayDatesTable();
            graph.displayCriticalPath();
        }
        else{
            System.out.println("This is a cyclic graph, so we won't proceed further. ");
        }
        System.out.print("\n");
        System.out.println("What do you want to do next ? ");
        getUserInputContinue();
    }


    public String getUserFileNumber(){
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.print("Enter file number please (1 to 12) : ");

        String chosenFile = myObj.nextLine();  // Read user input
        int chosenFileAsInteger = Integer.parseInt(chosenFile);

        if(chosenFileAsInteger >= 0 && chosenFileAsInteger <= 12){
            System.out.println("Chosen file : " + chosenFile);
        }else{
            System.out.println("Enter a number between 1 and 12 included: \n");
            getUserFileNumber();
        }
        return chosenFile;
    }


    public void getUserInputContinue(){
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.print("Do you want to work on another file ? Enter 'yes' or 'no' : ");

        String choice= myObj.nextLine();  // Read user input

        if(choice.equals("yes")){
            Menu();

        }else if(choice.equals("no")){
            System.out.println("Exiting...");
            System.exit(0);
        }
        else{
            System.out.print("Enter either 'yes' or 'no': ");
            getUserInputContinue();
        }
    }


    public void outputFiles(String fileName) throws IOException {
        String path = "src/OutputFiles/" + fileName + "_output.txt";
        Files.createDirectories(Paths.get(path.substring(0, path.lastIndexOf("/"))));
        FileOutputStream file = new FileOutputStream(path);
        TeePrintStream tee = new TeePrintStream(file, System.out);
        System.setOut(tee);
    }
}
