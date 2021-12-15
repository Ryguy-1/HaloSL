package generate_games;

public class Runner {
    public static MainBoard mainBoard;
    public static CheckValidConditions checkValidConditions;
    public static BitboardControlAndSeparation controlAndSeparation;
    public static Search search;
    public static GenerateDataset generateDataset;

    public static void main(String[] args){
        //initialize mainBoard FIRST
        mainBoard = new MainBoard();
        checkValidConditions = new CheckValidConditions();
        controlAndSeparation = new BitboardControlAndSeparation();
        search = new Search();
        int[][][] tensor = generateDataset.getInputTensor(Runner.mainBoard.mainPosition);
        generateDataset.printTensor(tensor);
        // Longest ches game known to be 269 moves -> 250 good
        // generateDataset = new GenerateDataset(1000000, 250);
    }
}