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
        // Longest chess game known to be 269 moves -> 250 good
        // (int numGames, int maxMovesUntilStalemate, int startIndex, int numGamesPerBatch)
        generateDataset = new GenerateDataset(10000, 500, 20, 100);
    }
}