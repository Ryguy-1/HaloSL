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
        // generateDataset = new GenerateDataset(1000000, 500, 0, 100);
        // GenerateDataset.chooseMoveCNN(Runner.search.getPossibleMovesByCasing(Runner.mainBoard.mainPosition, 'l'), 'l');
        String thisPosition = "";
        // Runner.mainBoard.mainPosition.fromToMove("e2e4");
        for (int i = 0; i < 12; i++){
            thisPosition += Runner.mainBoard.parseString(Runner.mainBoard.mainPosition.getCurrentBoard()[i]);
        }
        System.out.println(thisPosition);
    }
}