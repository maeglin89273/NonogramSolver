package tw.edu.ntu.csie.cmlab.ccliao.board;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

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
                String[] rowStrs = convertBytesToStrings(state.data[row]);
                writer.write(String.join("\t", rowStrs) + "\n");
            }
            counter++;
        }

        writer.close();

    }

    private static String[] convertBytesToStrings(byte[] bytes) {
        String[] strs = new String[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            strs[i] = Byte.toString(bytes[i]);
        }
        return strs;
    }
}
