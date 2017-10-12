package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.Board;
import tw.edu.ntu.csie.cmlab.ccliao.board.BoardHint;

import java.util.*;

public class NonogramSolver {
    public static final String DFS1 = "DFS1";
    public static final String DFS2 = "DFS2";
    public static final String IDFA_STAR = "IDFA*";

    private final String algorithm;
    private final boolean wantPresolve;

    private BitSet[][] validRows; // [rowIdx][validPossibilityIdx]
    private BitSet[][] validCols; // [colIdx][validPossibilityIdx]

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
            case DFS1:
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

    private static BitSet[][] generateValidLinesFromHints(int[][] hints, final int lineLength) {
        BitSet[][] rowOfLines = new BitSet[hints.length][];
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

    private static BitSet[] generateValidLinesFromHint(int[] hint, final int lineLength) {
        if (hint.length == 0) {
            return new BitSet[] {new BitSet(lineLength)};
        }


        List<BitSet> validLines = new LinkedList<>();


        final int allocatableSpaceAmount = lineLength - (Arrays.stream(hint).sum() + (hint.length - 1));
        SpaceAllocationRecord record = new SpaceAllocationRecord(allocatableSpaceAmount);


        Deque<SpaceAllocationRecord> spaceAllocationStack = new LinkedList<>();
        spaceAllocationStack.push(record);

        while (!spaceAllocationStack.isEmpty()) {
            record = spaceAllocationStack.peek();
            record.useSpace();

            if (record.isOverUsed()) { // end of level
                spaceAllocationStack.pop();

            } else if (!record.hasAllocableSpace() || spaceAllocationStack.size() == hint.length) {
                // no more space to allocate or at the last space inserting point
                validLines.add(generateLine(hint, spaceAllocationStack, lineLength));
            } else {

                record = new SpaceAllocationRecord(record.allocableAmount());
                spaceAllocationStack.push(record);
            }

        }

        return validLines.toArray(new BitSet[validLines.size()]);

    }

    private static BitSet generateLine(int[] hint, Iterable<SpaceAllocationRecord> insertedSpaces, final int lineLength) {
        BitSet line = new BitSet(lineLength);
        Iterator<SpaceAllocationRecord> iterator = insertedSpaces.iterator();

        int spaceAmount = iterator.next().usedSpaceAmount;
        int stripStart = spaceAmount; // skip the first space
        int stripEnd = stripStart + hint[0];
        line.set(stripStart, stripEnd); // fill the first strip


        for (int i = 1; i < hint.length; i++) {
            spaceAmount = 1 + (iterator.hasNext()? iterator.next().usedSpaceAmount: 0);
            stripStart =  stripEnd + spaceAmount;
            stripEnd = stripStart + hint[i];
            line.set(stripStart, stripEnd);
        }


        return line;
    }

    private static class SpaceAllocationRecord {
        private int usedSpaceAmount;
        private final int remainingSpaceAmount;

        private SpaceAllocationRecord(int remainingSpaceAmount) {
            this.usedSpaceAmount = -1;
            this.remainingSpaceAmount = remainingSpaceAmount;
        }

        void useSpace() {
            this.usedSpaceAmount++;
        }

        boolean isOverUsed() {
            return this.usedSpaceAmount > this.remainingSpaceAmount;
        }

        int allocableAmount() {
            return this.remainingSpaceAmount - this.usedSpaceAmount;
        }

        boolean hasAllocableSpace() {
            return allocableAmount() > 0;
        }
    }
}
