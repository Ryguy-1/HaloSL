package generate_games;

import java.util.ArrayList;
import java.util.Random;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class GenerateDataset {

    Random random = new Random();
    private int maxMovesUntilStalemate;

    GenerateDataset(int numGames, int maxMovesUntilStalemate, int startIndex, int numGamesPerBatch){
        this.maxMovesUntilStalemate = maxMovesUntilStalemate;
        // Start Game Generation
        generateGames(numGames, startIndex, numGamesPerBatch);
    }

    private void generateGames(int numGames, int startIndex, int numGamesPerBatch){
        System.out.println("Generating " + numGames + " games");
        // Nested Array: indices: 0 = boardString, 1 = resultString
        ArrayList<String[]> boardsWithResults = new ArrayList<>();
        for(int outer = startIndex*numGamesPerBatch; outer < numGames+(startIndex*numGamesPerBatch); outer++){
            //play out game
            String[] moves = playGame();
            // get winning case (last index in moves array)
            String winningCase = moves[moves.length-1];
            // don't do anything if nobody won
            if (!(winningCase.equals("c") || winningCase.equals("l"))){
                outer--;
                continue;
            }
            // remove winning case from moves array and create movesNew array
            String[] movesNew = new String[moves.length-1];
            for (int i = 0; i < moves.length-1; i++){
                movesNew[i] = moves[i];
            }
            moves = movesNew;


            // Variables: moves array, winningCase string
            Runner.mainBoard.initializeNewBoard();
            ArrayList<String> positionStrings = new ArrayList<>();
            for (String move: moves){
                String thisPosition = "";
                Runner.mainBoard.mainPosition.fromToMove(move);
                for (int i = 0; i < 12; i++){
                    thisPosition += Runner.mainBoard.parseString(Runner.mainBoard.mainPosition.getCurrentBoard()[i]);
                }
                positionStrings.add(thisPosition);
            }
            // only add if isn't stalemat/draw
            if (winningCase.equals("c") || winningCase.equals("l")){
                for (String positionString: positionStrings){
                    boardsWithResults.add(new String[]{positionString, winningCase});
                }
            }
            if(outer % numGamesPerBatch == 0 && outer!=0){
                System.out.println("Games Generated: " + outer);
                saveBoardWithResultsToCsv(boardsWithResults, "batch" + outer/numGamesPerBatch +".csv");
                boardsWithResults.clear();
            }
        }

    }

    private void saveBoardWithResultsToCsv(ArrayList<String[]> boardsWithResults, String batchName){
        // Save BoardsWithResults to CSV
        String csv = "";
        for (String[] boardWithResult: boardsWithResults){
            csv += boardWithResult[0] + "," + boardWithResult[1] + "\n";
        }
        // Save to file
        String fileName = batchName;
        writeToFile(fileName, csv);
    }

    private void writeToFile(String fileName, String csv){
        // Write to file
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("generate_games/.data/" + fileName), "utf-8"))) {
                writer.write(csv);
        } catch (IOException ex) {
            // report
            System.out.println("Could not write to file");
        }

    }


    // Returns Moves Played Along with Result
    private String[] playGame(){
        // Uses Runner Main Board
        Runner.mainBoard.initializeNewBoard();
        // Saved Variables
        ArrayList<String> masterMoves = new ArrayList<>();
        //n = stalemate
        char winningCase = 'n'; 

        // Playing Conditions
        char toMove = 'c';
        int totalMoves = 0;
        GAMEPLAY: while (true){
            // Choose and Make Move
            String[] availableMoves = Runner.search.getPossibleMovesByCasing(Runner.mainBoard.mainPosition, toMove);
            String chosenMove = chooseMove(availableMoves, toMove);
            Runner.mainBoard.mainPosition.fromToMove(chosenMove);
            masterMoves.add(chosenMove);
            totalMoves++;
            // Check if other side is in checkmate
            boolean existsCheckmate = false;
            switch(toMove){
                case 'c':
                    existsCheckmate = Runner.search.lowerCaseIsInCheckmate(Runner.mainBoard.mainPosition);
                    break;
                case 'l':
                    existsCheckmate = Runner.search.capitalIsInCheckmate(Runner.mainBoard.mainPosition);
                    break;
            }
            
            // Checkmate Found
            if(existsCheckmate){
                winningCase = toMove;
                break GAMEPLAY;
            // Ran out of moves
            }else if(totalMoves==maxMovesUntilStalemate){
                winningCase = 'n';
                break GAMEPLAY;
            // Stalemate
            }else if(Runner.search.capitalIsInStalemate(Runner.mainBoard.mainPosition) || Runner.search.lowerCaseIsInStalemate(Runner.mainBoard.mainPosition)){
                winningCase = 'n';
                break GAMEPLAY;
            // Otherwise Continue Playing
            }else{
                switch(toMove){
                    case 'c':
                        toMove = 'l';
                        break;
                    case 'l':
                        toMove = 'c';
                        break;
                }
            }
            
        }
        String[] returnedArray = new String[masterMoves.size() + 1]; // last index for who won
        for(int i = 0; i < masterMoves.size(); i++){
            returnedArray[i] = masterMoves.get(i);
        }
        returnedArray[returnedArray.length-1] = winningCase + "";
        return returnedArray;
    }

    // Makes Random Move to Generate Dataset
    private String chooseMove(String[] availableMoves, char toMove){
        String chosenMove = availableMoves[random.nextInt(availableMoves.length)];
        return chosenMove;
    }

    public static String chooseMoveCNN(String[] availableMoves, char toMove){
        String java_to_python = "communication_gateway/java_to_python.txt";
        String python_to_java = "communication_gateway/python_to_java.txt";

        // Send Moves to Python
        Position[] movedPositions = new Position[availableMoves.length];
        for (int i = 0; i < availableMoves.length; i++){
            Position newPos = Runner.mainBoard.mainPosition.getPositionCopy();
            newPos.fromToMove(availableMoves[i]);
            movedPositions[i] = newPos;
        }

        // Convert Position Current Boards to String
        String[] positionStrings = new String[movedPositions.length];
        for (int i = 0; i < movedPositions.length; i++){
            String thisPosition = "";
            for (int j = 0; j < 12; j++){
                thisPosition += Runner.mainBoard.parseString(movedPositions[i].getCurrentBoard()[j]);
            }
            positionStrings[i] = thisPosition;
        }

        // Convert positionStrings to Single Column CSV
        String csv = "";
        for (String positionString: positionStrings){
            csv += positionString + "," +toMove + "\n";
        }

        // Send to Python
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(java_to_python), "utf-8"))) {
            writer.write(csv);
        } catch (IOException ex) {
            // report
            System.out.println("Could not write to file");
        }

        // Wait for Response (Python to Java file is not empty)
        while(true){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(new File(python_to_java).length() != 0){
                break;
            }
        }
        // Read Response (Index of Best Move)
        String bestMove = "";
        try (BufferedReader br = new BufferedReader(new FileReader(python_to_java))) {
            String line;
            while ((line = br.readLine()) != null) {
                bestMove = line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Delete Information in File
        try {
            FileWriter f2 = new FileWriter(python_to_java, false);
            f2.write("");
            f2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }          

        System.out.println("Best Move From Java: " + bestMove);
        // Return Best Move
        return availableMoves[Integer.parseInt(bestMove)];
    }

    // Transfer Format: See save_format.txt


    // (12, 8, 8)
    public static int[][][] getInputTensor(Position pos){
        int[][][] tensor = new int[12][8][8];
        Long[] currentBoard = pos.getCurrentBoard();

        String[] boardAsStrings = new String[12];
        for (int i = 0; i < currentBoard.length; i++){
            boardAsStrings[i] = Runner.mainBoard.parseString(currentBoard[i]);
        }

        String[][] bitboardArraysAsStringBits = new String[12][64];
        for(int i = 0; i < boardAsStrings.length; i++){
            String[] bits = boardAsStrings[i].split("");
            bitboardArraysAsStringBits[i] = bits;
        }
        
        for(int i = 0; i < bitboardArraysAsStringBits.length; i++){
            int rowNum = 0;
            for(int j = 0; j < bitboardArraysAsStringBits[i].length; j++){
                if (j%8==0 && j!=0){
                    rowNum++;
                }
                tensor[i][rowNum][j%8] = Integer.parseInt(bitboardArraysAsStringBits[i][j]);
            }
        }
        return tensor;
    }

    public static void printTensor(int[][][] tensor){
        for(int i = 0; i < tensor.length; i++){
            for(int j = 0; j < tensor[i].length; j++){
                for(int k = 0; k < tensor[i][j].length; k++){
                    System.out.print(tensor[i][j][k]);
                }
                System.out.println();
            }
            System.out.println("========================");
        }
    }

}