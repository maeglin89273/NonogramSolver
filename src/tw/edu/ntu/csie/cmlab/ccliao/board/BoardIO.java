package tw.edu.ntu.csie.cmlab.ccliao.board;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Pattern;

public class BoardIO {

    public static Iterable<Board> readQuestions(Scanner input) {
        Pattern boardBeginPattern = Pattern.compile("\\$\\d+");
        List<Board> boards = new LinkedList<>();
        List<int[]> hints = new LinkedList<>();

        while (true) {
            if (input.hasNextLine()) {
                String line = input.nextLine();

                if (boardBeginPattern.matcher(line).matches()) {
                    if (!hints.isEmpty()) { // last round
                        boards.add(packBoard(hints));
                        hints.clear();
                    }
                } else {
                    int[] hint = line.isEmpty()? new int[0]: Arrays.stream(line.split("\t")).mapToInt(Integer::parseInt).toArray();
                    hints.add(hint);
                }

            } else {
                break;
            }
        }

        boards.add(packBoard(hints));

        return boards;
    }

    private static Board packBoard(List<int[]> hints) {
        int boardSize = hints.size() / 2;
        BoardHint hint = new BoardHint(boardSize);

        int counter = 0;
        for (int[] hintCell: hints) {
            if (counter < boardSize) {
                hint.setColumn(counter, hintCell);
            } else {
                hint.setRow(counter - boardSize, hintCell);
            }
            counter++;
        }

        hint.doStatistic();
        return new Board(new BoardState(boardSize), hint);
    }

    private static Board inversePackBoard(List<int[]> hints) {
        int boardSize = hints.size() / 2;
        BoardHint hint = new BoardHint(boardSize);

        int counter = 0;
        for (int[] hintCell: hints) {
            if (counter < boardSize) {
                hint.setColumn(boardSize - 1 - counter, hintCell);
            } else {
                hint.setRow(  counter - boardSize, inverseHint(hintCell));
            }
            counter++;
        }

        hint.doStatistic();
        return new Board(new BoardState(boardSize), hint);
    }

    private static int[] inverseHint(int[] hintCell) {
        int[] inverseHint = new int[hintCell.length];
        for (int i = 0; i < inverseHint.length; i++) {
            inverseHint[i] = hintCell[hintCell.length - 1 - i];
        }

        return inverseHint;
    }

    public static void writeBoards(PrintStream printer, Iterable<Board> boards) throws IOException {

        int counter  = 1;
        for (Board board: boards) {
            printer.println("$" + counter);
            BoardState state = board.getBoardState();
            for (int row = 0; row < state.size; row++) {
                String[] rowStrs = convertBitsToStrings(state.grid[row]);
                printer.println(String.join("\t", rowStrs));
            }
            counter++;
        }

        printer.close();

    }

    private static String[] convertBitsToStrings(BitArray row) {
        String[] strs = new String[row.length()];

        for (int i = 0; i < row.length(); i++) {
            strs[i] = row.get(i)? "1": "0";
        }
        return strs;
    }
}
