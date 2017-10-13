package tw.edu.ntu.csie.cmlab.ccliao;

import tw.edu.ntu.csie.cmlab.ccliao.board.Board;
import tw.edu.ntu.csie.cmlab.ccliao.board.BoardIO;
import tw.edu.ntu.csie.cmlab.ccliao.solver.NonogramSolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {


	    if ( args.length < 2) {
	        System.out.println("Main.java algorithm < question.txt");
	        return;
        }

        String algorithm = args[0];

        Scanner scanner = new Scanner(new FileInputStream(args[1]));

        Iterable<Board> boards = BoardIO.readQuestions(scanner);
        List<Board> solvedBoards = new LinkedList<>();

        for (Board board: boards) {
            board = NonogramSolver.solve(board, algorithm, true);

            if (board == null) {
                return;
            }
            solvedBoards.add(board);
        }

        BoardIO.writeBoards("my_solution.txt", solvedBoards);

    }
}
