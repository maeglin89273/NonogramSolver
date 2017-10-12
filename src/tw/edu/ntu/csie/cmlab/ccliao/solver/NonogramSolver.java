package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.Board;
import tw.edu.ntu.csie.cmlab.ccliao.board.BoardHint;

import java.util.*;

public class NonogramSolver {
    public static final String SILLY_DFS = "Silly-DFS";
    public static final String DFS2 = "DFS2";
    public static final String IDFA_STAR = "IDFA*";

    private final String algorithm;
    private final boolean wantPresolve;

    private List<BitSet>[] validRows; // [rowIdx][validPossibilityIdx]
    private List<BitSet>[] validCols; // [colIdx][validPossibilityIdx]

    // make the constructor private, since avoiding reusing the solver
    private NonogramSolver(String algorithm) {
        this.algorithm = algorithm;
        this.wantPresolve = true;
    }

    public static Board solve(Board board, String algorithm) {
        return solve(board, algorithm, false);
    }

    public static Board solve(Board board, String algorithm, boolean log) {

        NonogramSolver solver = new NonogramSolver(algorithm);
        return solver.solveImpl(board, log);

    }

    private Board solveImpl(Board board, boolean log) {
        BoardHint hint = board.getHint();
        this.validRows = generateValidLinesFromHints(hint.getRows(), board.getSize());

        if (this.wantPresolve) {
            if (log) {
                long startTime = System.nanoTime();
                this.presolve(board);
                long duration = System.nanoTime() - startTime;
                System.out.format("presolve process takes: %d ns%n", duration);
            }
            this.presolve(board);
        }

        switch (this.algorithm) {
            case SILLY_DFS:
                break;

            case DFS2:
                break;

            case IDFA_STAR:
                break;

            default:
                System.out.println("unkonwn solving algorithm: " + this.algorithm);
                return board;
        }

        return board;
    }

    private static List<BitSet>[] generateValidLinesFromHints(int[][] hints, final int lineLength) {
        List<BitSet>[] rowOfLines = (List<BitSet>[]) new List[hints.length];
        for (int i = 0; i < rowOfLines.length; i++) {
            rowOfLines[i] = generateValidLinesFromHint(hints[i], lineLength);
        }

        return rowOfLines;
    }

    private void presolve(Board board) {
        BoardHint hint = board.getHint();
        this.validCols = generateValidLinesFromHints(hint.getColumns(), board.getSize());

        //todo: implement intersection iterating process

    }

    private static List<BitSet> generateValidLinesFromHint(int[] hint, final int lineLength) {
        List<BitSet> validLines = new LinkedList<>();

        if (hint.length == 0) { // special case, not fit the permutation algorithm
            validLines.add(new BitSet(lineLength));
            return validLines;
        }


        final int allocatableBlankAmount = lineLength - (Arrays.stream(hint).sum() + (hint.length - 1));
        BlankAllocationRecord record = new BlankAllocationRecord(allocatableBlankAmount);


        Deque<BlankAllocationRecord> blankAllocationStack = new LinkedList<>();
        blankAllocationStack.push(record);

        while (!blankAllocationStack.isEmpty()) {
            record = blankAllocationStack.peek();
            record.useBlank();

            if (record.isOverUsed()) { // end of level
                blankAllocationStack.pop();

            } else if (!record.hasAllocableBlank() || blankAllocationStack.size() == hint.length) {
                // no more blank to allocate or at the last blank inserting point
                validLines.add(generateLine(hint, blankAllocationStack, lineLength));
            } else {

                record = new BlankAllocationRecord(record.allocableAmount());
                blankAllocationStack.push(record);
            }

        }

        return validLines;

    }

    private static BitSet generateLine(int[] hint, Iterable<BlankAllocationRecord> insertedBlanks, final int lineLength) {
        BitSet line = new BitSet(lineLength);
        Iterator<BlankAllocationRecord> iterator = insertedBlanks.iterator();


        int stripStart = iterator.next().usedBlankAmount; // skip the first blank
        int stripEnd = stripStart + hint[0];
        line.set(stripStart, stripEnd); // fill the first strip

        int i = 1;
        while (iterator.hasNext()) { // blanks + strips
            stripStart =  stripEnd + iterator.next().usedBlankAmount + 1;
            stripEnd = stripStart + hint[i++];
            line.set(stripStart, stripEnd);
        }

        for (; i < hint.length; i++) { // remaining strips
            stripStart =  stripEnd + 1;
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

        boolean isOverUsed() {
            return this.usedBlankAmount > this.remainingBlankAmount;
        }

        int allocableAmount() {
            return this.remainingBlankAmount - this.usedBlankAmount;
        }

        boolean hasAllocableBlank() {
            return allocableAmount() > 0;
        }
    }
}
