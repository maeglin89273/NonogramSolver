package tw.edu.ntu.csie.cmlab.ccliao.board;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.regex.Pattern;

public class BoardIO {

    public static Iterable<Board> readQuestions(Scanner input) {
        Pattern boardBeginPattern = Pattern.compile("\\$\\d+");
        List<Board> boards = new LinkedList<>();
        List<int[]> hints = new LinkedList<>();

        while (true) {
            if (input.hasNext(boardBeginPattern)) {
                input.nextLine();
                if (!hints.isEmpty()) { // last round
                    boards.add(packBoard(hints));
                    hints.clear();
                }


            } else if (input.hasNextLine()) {
                String line = input.nextLine();
                int[] hint = line.isEmpty()? new int[0]: Arrays.stream(line.split("\t")).mapToInt(Integer::parseInt).toArray();
                hints.add(hint);

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

        return new Board(new BoardState(boardSize), hint);
    }

    public static void writeBoards(String fileName, Iterable<Board> boards) throws IOException {
        Writer writer = new FileWriter(fileName);
        int counter  = 1;
        for (Board board: boards) {
            writer.write("$" + counter + "\n");
            BoardState state = board.getBoardState();
            for (int row = 0; row < state.size; row++) {
                String[] rowStrs = convertBitsToStrings(state.grid[row]);
                writer.write(String.join("\t", rowStrs) + "\n");
            }
            counter++;
        }

        writer.close();

    }

    private static String[] convertBitsToStrings(BitArray row) {
        String[] strs = new String[row.length()];

        for (int i = 0; i < row.length(); i++) {
            strs[i] = row.get(i)? "1": "0";
        }
        return strs;
    }
}
