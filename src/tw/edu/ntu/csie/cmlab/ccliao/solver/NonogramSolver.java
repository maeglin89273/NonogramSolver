package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.BitArray;
import tw.edu.ntu.csie.cmlab.ccliao.board.Board;
import tw.edu.ntu.csie.cmlab.ccliao.board.BoardState;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.DepthFirstSearch;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.IterativeWidening;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.TreeSearch;

import java.util.*;
import java.util.function.Predicate;

public class NonogramSolver {
    public static final String SILLY_DFS = "silly-dfs";
    public static final String DFS1 = "dfs1";
    public static final String DFS2 = "dfs2";

    private final String algorithm;

    private List<BitArray>[] validRows; // [rowIdx][validPossibilityIdx]
    private List<BitArray>[] validCols; // [colIdx][validPossibilityIdx]

    // make the constructor private, since avoiding reusing the solver
    private NonogramSolver(String algorithm) {
        this.algorithm = algorithm;
    }

    public static Board solve(Board board, String algorithm) {
        return solve(board, algorithm, false);
    }

    public static Board solve(Board board, String algorithm, boolean log) {

        NonogramSolver solver = new NonogramSolver(algorithm);
        return solver.solveImpl(board, log);

    }

    public static void fillBoardByRows(Board board, List<BitArray>[] rows) {
        BoardState boardState = board.getBoardState();

        for (int i = 0; i < rows.length; i++) {
            boardState.setRow(i, rows[i].get(0));
        }
    }


    private Board solveImpl(Board board, boolean log) {
        AnalysisLogger.setEnabled(log);

        this.validRows = generateValidLinesByHints(board.getHint().getRows(), board.getSize());
        this.validCols = generateValidLinesByHints(board.getHint().getColumns(), board.getSize());
        CrossSolve.solve(this.validRows, this.validCols);

        if (this.isBoardSolved(board)) {
            return board;
        }

        AnalysisLogger.logLineCombimationNumber("after presolve row", this.validRows);
        AnalysisLogger.logLineCombimationNumber("after presolve col", this.validCols);

        TreeSearch searchAlgorithm;
        boolean hasSolved;

        AnalysisLogger.startTiming();
        switch (this.algorithm) {
            case SILLY_DFS:
                searchAlgorithm = new DepthFirstSearch();
                hasSolved = searchAlgorithm.search(new NonogramGameState(board, this.validRows));
                break;

            case DFS1:
                hasSolved = this.dfs1(board);
                break;

            case DFS2:
                searchAlgorithm = new IterativeWidening();
                hasSolved = searchAlgorithm.search(new NNGMinCrossHeuristicState(board, this.validRows, this.validCols));
                break;

            default:
                System.out.println("unkonwn solving algorithm: " + this.algorithm);
                return board;
        }

        AnalysisLogger.stopTiming("search");
        AnalysisLogger.divider();

        return hasSolved? board: null;
    }

    private boolean isBoardSolved(Board board) {
        Predicate<List<BitArray>> sizeEqOne = pLines -> pLines.size() == 1;
        if (Arrays.stream(this.validRows).allMatch(sizeEqOne) &&
            Arrays.stream(this.validCols).allMatch(sizeEqOne)) {

            fillBoardByRows(board, this.validRows);
            return board.isBoardValid();
        }

        return false;
    }

    private boolean dfs1(Board board) {
        TreeSearch searchAlgorithm = new IterativeWidening();
        if (combinationNumLog(this.validCols) < combinationNumLog(this.validRows)) { // solve column-wise
            return searchAlgorithm.search(new NNGHeuristicState(board, validCols, 0));

        } else { // solve row-wise
            return searchAlgorithm.search(new NNGHeuristicState(board, validRows, 1));
        }
    }

    private static double combinationNumLog(List<BitArray>[] validLines) {
        double logProduct = 0;
        for (List<BitArray> array: validLines) {
            logProduct += Math.log10(array.size());
        }
        return logProduct;
    }

    private static List<BitArray>[] generateValidLinesByHints(int[][] hints, final int lineLength) {
        List<BitArray>[] rowOfLines = new List[hints.length];

//        doing the for loop in parallel is not speeding up, slowing down about 4~6 times
//        Arrays.stream(hints).parallel().map(hint -> generateValidLinesByHint(hint, lineLength)).toArray(List[]::new);

        for (int i = 0; i < rowOfLines.length; i++) {
            rowOfLines[i] = generateValidLinesByHint(hints[i], lineLength);
        }

        return rowOfLines;
    }


    private static List<BitArray> generateValidLinesByHint(int[] hint, final int lineLength) {
        List<BitArray> validLines = new LinkedList<>();

        if (hint.length == 0) { // special case, not fit into the permutation algorithm
            validLines.add(new BitArray(lineLength));
            return validLines;
        }


        final int allocatableBlankAmount = lineLength - (Arrays.stream(hint).sum() + (hint.length - 1));


        BlankAllocationRecord record = new BlankAllocationRecord(allocatableBlankAmount);


        Deque<BlankAllocationRecord> blankAllocationStack = new LinkedList<>();
        //since the permutation ordering is important, so the queue operations operate at the tail
        blankAllocationStack.addLast(record);

        while (!blankAllocationStack.isEmpty()) {
            record = blankAllocationStack.peekLast();

            if (!record.hasAllocableBlank()) { // end of level
                blankAllocationStack.removeLast();
                continue;
            }

            record.useBlank();
            if (!record.hasAllocableBlank() || blankAllocationStack.size() == hint.length) {
                // no blank to allocate or at the last blank inserting point
                validLines.add(generateLine(hint, blankAllocationStack, lineLength));

            } else { //deepen the queue
                record = new BlankAllocationRecord(record.allocableAmount());
                blankAllocationStack.addLast(record);
            }

        }

        return validLines;

    }

    private static BitArray generateLine(int[] hint, Iterable<BlankAllocationRecord> insertedBlanks, final int lineLength) {
        BitArray line = new BitArray(lineLength);
        Iterator<BlankAllocationRecord> iterator = insertedBlanks.iterator();


        int stripStart = iterator.next().usedBlankAmount; // skip the first blank
        int stripEnd = stripStart + hint[0];
        line.set(stripStart, stripEnd); // fill the first strip

        int i = 1;
        while (iterator.hasNext()) { // blanks + strips
            stripStart = stripEnd + iterator.next().usedBlankAmount + 1;
            stripEnd = stripStart + hint[i++];
            line.set(stripStart, stripEnd);
        }

        for (; i < hint.length; i++) { // remaining strips
            stripStart = stripEnd + 1;
            stripEnd = stripStart + hint[i];
            line.set(stripStart, stripEnd);
        }


        return line;
    }

    private static class BlankAllocationRecord {
        private int usedBlankAmount;
        private final int remainingBlankAmount;

        private BlankAllocationRecord(int remainingBlankAmount) {
            this.usedBlankAmount = -1;
            this.remainingBlankAmount = remainingBlankAmount;
        }

        void useBlank() {
            this.usedBlankAmount++;
        }

        int allocableAmount() {
            return this.remainingBlankAmount - this.usedBlankAmount;
        }

        boolean hasAllocableBlank() {
            return allocableAmount() > 0;
        }
    }
}
