package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.BitArray;
import tw.edu.ntu.csie.cmlab.ccliao.board.Board;
import tw.edu.ntu.csie.cmlab.ccliao.board.BoardHint;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.DepthFirstSearch;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.TreeSearch;

import java.util.*;

public class NonogramSolver {
    public static final String SILLY_DFS = "silly-dfs";
    public static final String DFS2 = "DFS2";
    public static final String IDFA_STAR = "IDFA*";

    private final String algorithm;
    private final boolean wantPresolve;

    private List<BitArray>[] validRows; // [rowIdx][validPossibilityIdx]
    private List<BitArray>[] validCols; // [colIdx][validPossibilityIdx]

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

        this.validRows = generateValidLinesByHints(hint.getRows(), board.getSize());

        AnalysisLogger.logLineCombimationNumber("row", this.validRows);


        if (this.wantPresolve) {
            if (log) {
                AnalysisLogger.startTiming();
                this.presolve(board);
                AnalysisLogger.stopTiming("presolve");
                AnalysisLogger.logLineCombimationNumber("row", this.validRows);
                AnalysisLogger.logLineCombimationNumber("col", this.validCols);
            } else {
                this.presolve(board);
            }
        }


        TreeSearch searchAlgorithm;
        boolean hasSolved = false;
        switch (this.algorithm) {
            case SILLY_DFS:
                searchAlgorithm = new DepthFirstSearch();
                hasSolved = searchAlgorithm.search(new NonogramGameState(board, this.validRows));
                break;

            case DFS2:
                break;

            case IDFA_STAR:
                break;

            default:
                System.out.println("unkonwn solving algorithm: " + this.algorithm);
                return board;
        }

        if (log) {
            AnalysisLogger.divider();
        }
        return hasSolved? board: null;
    }


    private static List<BitArray>[] generateValidLinesByHints(int[][] hints, final int lineLength) {
        List<BitArray>[] rowOfLines = new List[hints.length];

//        doing the for loop in parallel is not speeding up, slow down about 4~6 times
//        Arrays.stream(hints).parallel().map(hint -> generateValidLinesByHint(hint, lineLength)).toArray(List[]::new);

        for (int i = 0; i < rowOfLines.length; i++) {
            rowOfLines[i] = generateValidLinesByHint(hints[i], lineLength);
        }

        return rowOfLines;
    }

    private void presolve(Board board) {
        BoardHint hint = board.getHint();
        this.validCols = generateValidLinesByHints(hint.getColumns(), board.getSize());

        boolean hasElimination;
        do {
            hasElimination = eliminatePossiblities(this.validRows, this.validCols);
            hasElimination |= eliminatePossiblities(this.validCols, this.validRows);
        } while (hasElimination);

    }

    private boolean eliminatePossiblities(List<BitArray>[] axis1, List<BitArray>[] axis2) {
        Intersection[] intersectionArray = new Intersection[axis1.length];
        int i = 0;
        for (List<BitArray> a1Lines: axis1) {
            intersectionArray[i++] = intersectPossibilities(a1Lines);
        }

        boolean hasElimination = false;
        for (int i1 = 0; i1 < axis1.length; i1++) {
            for (int i2 = 0; i2 < axis2.length; i2++) {
                if (intersectionArray[i1].filledCells.get(i2)) {
                    hasElimination |= notMatchCellRemove(axis2[i2], i1, true);

                } else if(intersectionArray[i1].blankCells.get(i2)) {
                    hasElimination |= notMatchCellRemove(axis2[i2], i1, false);
                }
            }
        }
        return hasElimination;
    }

    private static boolean notMatchCellRemove(List<BitArray> possibilities, int i1, boolean cellValue) {
        return possibilities.removeIf(line -> line.get(i1) != cellValue);
    }

    private static Intersection intersectPossibilities(List<BitArray> possibilities) {
        int lineSize = possibilities.get(0).size();
        Intersection intersection = new Intersection(lineSize);

        for (BitArray line: possibilities) {
            intersection.filledCells.and(line);
            intersection.blankCells.or(line);
        }

        intersection.blankCells.flip(0, lineSize);

        return intersection;
    }

    private static class Intersection {
        private BitArray filledCells;
        private BitArray blankCells;

        Intersection(int lineSize) {
            this.filledCells = new BitArray(lineSize);
            this.blankCells = new BitArray(lineSize);

            this.filledCells.set(0, lineSize);
        }

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
        //since the permutation ordering is important, so the stack operations operate at the tail
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

            } else { //deepen the stack
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
