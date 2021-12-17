package generate_games;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class MainBoard {

    //capital = white
    //lower case = black
    //r = rook, n = knight, b = bishop, q = queen, k = king, p = pawn
    //order in array: r, n, b, q, k, p, R, N, B, Q, K, P

    public Position mainPosition;

    private Long longEnd1 = parseLong("1000000000000000000000000000000000000000000000000000000000000000", 2);

    String[][] visualRepresentation = {
            {"r", "n", "b", "q", "k", "b", "n", "r"},
            {"p", "p", "p", "p", "p", "p", "p", "p"},
            {"",  "",  "",  "",  "",  "",  "",  ""},
            {"",  "",  "",  "",  "",  "",  "",  ""},
            {"",  "",  "",  "",  "",  "",  "",  ""},
            {"",  "",  "",  "",  "",  "",  "",  ""},
            {"P", "P", "P", "P", "P", "P", "P", "P"},
            {"R", "N", "B", "Q", "K", "B", "N", "R"}
    };

    // String[][] visualRepresentation = {
    //         {"r", "n", "b", "q", "k", "b", "n", "r"},
    //         {"p", "p", "p", "p", "p", "", "", "p"},
    //         {"",  "",  "",  "",  "",  "",  "",  ""},
    //         {"",  "",  "",  "",  "",  "p",  "p",  "Q"},
    //         {"",  "",  "",  "",  "P",  "",  "",  ""},
    //         {"",  "",  "",  "",  "",  "",  "",  ""},
    //         {"P", "P", "P", "P", "", "P", "P", "P"},
    //         {"R", "N", "B", "", "K", "B", "N", "R"}
    // };

    //temp random generator
    Random random;

    MainBoard(){
        initializeNewBoard();
        random = new Random();
    }

    public void initializeNewBoard(){

        Long[] mainBoardInitializer = new Long[12];

        //string array that will be converted to longs later
        String[] tempStrings = new String[12];
        //initialize setter strings
        for (int i = 0; i < tempStrings.length; i++) {
            StringBuilder temp = new StringBuilder();
            for (int j = 0; j < 64; j++) {
                temp.append("0");
            }
            tempStrings[i] = temp.toString();
        }


        //what index you are at in the bitboard string
        int counter = 0;

        //set each bitboard string in tempStrings to equal its position in the visual Representation. Each Case sets the String representing each bitboard
        //at the index of counter equal to 1 to say there is a piece present.
        for (int i = 0; i < visualRepresentation.length; i++) {
            for (int j = 0; j < visualRepresentation[i].length; j++) {
                switch(visualRepresentation[i][j]){
                    case "r":
                        tempStrings[0] = insertOneAtIndex(tempStrings[0], counter);
                        break;
                    case "n":
                        tempStrings[1] = insertOneAtIndex(tempStrings[1], counter);
                        break;
                    case "b":
                        tempStrings[2] = insertOneAtIndex(tempStrings[2], counter);
                        break;
                    case "q":
                        tempStrings[3] = insertOneAtIndex(tempStrings[3], counter);
                        break;
                    case "k":
                        tempStrings[4] = insertOneAtIndex(tempStrings[4], counter);
                        break;
                    case "p":
                        tempStrings[5] = insertOneAtIndex(tempStrings[5], counter);
                        break;
                    case "R":
                        tempStrings[6] = insertOneAtIndex(tempStrings[6], counter);
                        break;
                    case "N":
                        tempStrings[7] = insertOneAtIndex(tempStrings[7], counter);
                        break;
                    case "B":
                        tempStrings[8] = insertOneAtIndex(tempStrings[8], counter);
                        break;
                    case "Q":
                        tempStrings[9] = insertOneAtIndex(tempStrings[9], counter);
                        break;
                    case "K":
                        tempStrings[10] = insertOneAtIndex(tempStrings[10], counter);
                        break;
                    case "P":
                        tempStrings[11] = insertOneAtIndex(tempStrings[11], counter);
                        break;
                }
                counter++;
            }
        }
        for (int i = 0; i < mainBoardInitializer.length; i++) {
            mainBoardInitializer[i] = parseLong(tempStrings[i], 2);

        }
        mainPosition = new Position(mainBoardInitializer);

    }

    public void sysoMainBoard(){
        for (Long l : mainPosition.getCurrentBoard()) {
            System.out.println(parseString(l));
        }
    }

    public void sysoMainBoardLong(){
        for (Long l : mainPosition.getCurrentBoard()) {
            System.out.println(l);
        }
    }

    public String insertOneAtIndex(String s, int index){
        String temp = "";
        for (int i = 0; i < s.length(); i++) {
            if(i==index){
                temp+="1";
            }else{
                temp+=s.charAt(i);
            }
        }
        return temp;
    }

    public long parseLong(String s, int base) {
        return new BigInteger(s, base).longValue();
    }

    public String parseString(Long l){
        //toBinaryString cuts off unecessarry zeros, so for loop adds them back on
        String parsedLong = Long.toBinaryString(l);
        String returned = "";
        for (int i = 0; i < 64-parsedLong.length(); i++) {
            returned+="0";
        }
        return returned+parsedLong;
    }

    public void visualizeBitboard(long l){
        String temp = parseString(l);
        for (int i = 0; i < temp.length(); i++) {
            if(i%8==0){
                System.out.println();
            }
            if(temp.charAt(i)=='0'){
                System.out.print("0, ");
            }else{
                System.out.print("1, ");
            }
        }
        System.out.println();
    }

    public void visualizeBitboardArray(Long[] bitboards){
        long combined = 0l;
        for (int i = 0; i < bitboards.length; i++) {
            combined |= bitboards[i];
        }
        visualizeBitboard(combined);
        System.out.println();

    }

    //Logic Crazy Chess Implementation of Drawing For Testing Purposes (Modified)
    public void drawGameBoard(Long[] currentBoard) {
        String chessBoard[][]=new String[8][8];
        for (int i=0;i<64;i++) {
            chessBoard[i/8][i%8]=" ";
        }
        //order in array: r, n, b, q, k, p, R, N, B, Q, K, P
        for (int i=0;i<64;i++) {
            if (((currentBoard[11]<<i)&longEnd1)==longEnd1) {chessBoard[i/8][i%8]="P";}
            if (((currentBoard[7]<<i)&longEnd1)==longEnd1) {chessBoard[i/8][i%8]="N";}
            if (((currentBoard[8]<<i)&longEnd1)==longEnd1) {chessBoard[i/8][i%8]="B";}
            if (((currentBoard[6]<<i)&longEnd1)==longEnd1) {chessBoard[i/8][i%8]="R";}
            if (((currentBoard[9]<<i)&longEnd1)==longEnd1) {chessBoard[i/8][i%8]="Q";}
            if (((currentBoard[10]<<i)&longEnd1)==longEnd1) {chessBoard[i/8][i%8]="K";}
            if (((currentBoard[5]<<i)&longEnd1)==longEnd1) {chessBoard[i/8][i%8]="p";}
            if (((currentBoard[1]<<i)&longEnd1)==longEnd1) {chessBoard[i/8][i%8]="n";}
            if (((currentBoard[2]<<i)&longEnd1)==longEnd1) {chessBoard[i/8][i%8]="b";}
            if (((currentBoard[0]<<i)&longEnd1)==longEnd1) {chessBoard[i/8][i%8]="r";}
            if (((currentBoard[3]<<i)&longEnd1)==longEnd1) {chessBoard[i/8][i%8]="q";}
            if (((currentBoard[4]<<i)&longEnd1)==longEnd1) {chessBoard[i/8][i%8]="k";}
        }
        for (int i=0;i<8;i++) {
            System.out.println(Arrays.toString(chessBoard[i]));
        }
    }



}