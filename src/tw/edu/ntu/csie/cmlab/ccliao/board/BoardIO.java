package tw.edu.ntu.csie.cmlab.ccliao.board;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.regex.Pattern;

public class BoardIO {

    public static Iterable<Board> readQuestions(Scanner input, int boardSize) {
        Pattern boardBeginPattern = Pattern.compile("\\$\\d+");
        List<Board> boards = new LinkedList<>();

        while (input.hasNext(boardBeginPattern)) {
            input.nextLine();
            BoardHint hint = new BoardHint(boardSize);

            for (int col = 0; col < boardSize; col++) {
                String line = input.nextLine();
                int[] colHint = line.isEmpty()? new int[0]: Arrays.stream(line.split("\t")).mapToInt(Integer::parseInt).toArray();
                hint.setColumn(col, colHint);
            }

            for (int row = 0; row < boardSize; row++) {
                String line = input.nextLine();
                int[] rowHint = line.isEmpty()? new int[0]: Arrays.stream(line.split("\t")).mapToInt(Integer::parseInt).toArray();
                hint.setRow(row, rowHint);
            }

            boards.add(new Board(new BoardState(boardSize), hint));
        }



        return boards;
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

    private static String[] convertBitsToStrings(BitSet row) {
        String[] strs = new String[row.size()];
        for (int i = 0; i < row.size(); i++) {
            strs[i] = row.get(i)? "1": "0";
        }
        return strs;
    }
}
