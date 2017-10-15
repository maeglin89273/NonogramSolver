package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.BitArray;
import tw.edu.ntu.csie.cmlab.ccliao.board.Board;
import tw.edu.ntu.csie.cmlab.ccliao.board.BoardState;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.GameState;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.HeuristicGameState;

import java.util.*;

import static tw.edu.ntu.csie.cmlab.ccliao.solver.NonogramSolver.intersectPossibilities;

public class NonogramCombHeuristicState implements HeuristicGameState {

    private float threshold = 0.2f;
    private int branchProgress = 0;

    private final List<BitArray>[] validCols;
    private Board board;
    private final int cost;
    private final PriorityRow[] sortedRows;
    private final int progress;

    private Iterator<GameState> nextStateIterator;
    private int branchNumber;

    // branch contructor
    protected NonogramCombHeuristicState(Board board, PriorityRow[] sortedRows, List<BitArray>[] validCols, int progess, float threshold) {
        this.board = board;
        this.sortedRows = sortedRows;
        this.validCols = validCols;
        this.progress = progess;
//        this.cost = remainingColNumber(this.validCols);
        this.cost = remainingRowNumber(this.sortedRows) + remainingColNumber(this.validCols);
        this.threshold = threshold;
    }

    // root constructor
    public NonogramCombHeuristicState(Board board, List<BitArray>[] validRows, List<BitArray>[] validCols) {
        this(board, sortValidRows(validRows), validCols, 0, 1);

    }

    private static PriorityRow[] sortValidRows(List<BitArray>[] validRows) {
        PriorityRow[] pRows = new PriorityRow[validRows.length];

        for (int i = 0; i < validRows.length; i++) {
            pRows[i] = new PriorityRow(i, validRows[i]);
        }

        Arrays.sort(pRows, Comparator.comparingInt(pRow -> pRow.possiblities.size()));
        return pRows;
    }

    private static int remainingColNumber(List<BitArray>[] validCols) {
        return Arrays.stream(validCols).mapToInt(List::size).sum();
    }

    private static int remainingRowNumber(PriorityRow[] sortedRows) {
        return Arrays.stream(sortedRows).mapToInt(pRow -> pRow.possiblities.size()).sum();
    }

    @Override
    public void prepareNextPossibleStates() {
        Queue<GameState> nextStates = new PriorityQueue<>();
        final int advancedProgress = this.progress + 1; // current progress is also the advanced row index

        PriorityRow pRow = this.sortedRows[0];
        Board possibleBoard = board.clone();
        for (BitArray possibleRow: pRow.possiblities) {

            List<BitArray>[] nextValidCols = this.assumeRow(pRow.idx, possibleRow);

            if (nextValidCols != null) {

                possibleBoard.getBoardState().setRow(pRow.idx, possibleRow);
                PriorityRow[] nextSortedRows = this.sortedRowsConstrainedByCols(nextValidCols);
                nextStates.offer(new NonogramCombHeuristicState(possibleBoard.clone(), nextSortedRows, nextValidCols, advancedProgress, this.threshold));
            }
        }
        this.branchNumber = nextStates.size();
        this.nextStateIterator = nextStates.iterator();
    }

    private PriorityRow[] sortedRowsConstrainedByCols(List<BitArray>[] nextValidCols) {

        NonogramSolver.Intersection[] intersectionArray = new NonogramSolver.Intersection[nextValidCols.length];
        int i = 0;
        for (List<BitArray> pCols: nextValidCols) {
            intersectionArray[i++] = intersectPossibilities(pCols);
        }

        PriorityRow[] sRowsCopy = this.sortedRowsCopyWithoutFirst();
        int rowIdx;
        for (int colIdx = 0; colIdx < nextValidCols.length; colIdx++) {
            for (int pRowIdx = 0; pRowIdx < sRowsCopy.length; pRowIdx++) {
                rowIdx = sRowsCopy[pRowIdx].idx;

                if (intersectionArray[colIdx].filledCells.get(rowIdx)) {
                    NonogramSolver.notMatchCellRemove(sRowsCopy[pRowIdx].possiblities, colIdx, true);

                } else if (intersectionArray[colIdx].blankCells.get(rowIdx)) {
                    NonogramSolver.notMatchCellRemove(sRowsCopy[pRowIdx].possiblities, colIdx, false);
                }

            }
        }

        Arrays.sort(sRowsCopy, Comparator.comparingInt(pRow -> pRow.possiblities.size()));
        return sRowsCopy;
    }

    private PriorityRow[] sortedRowsCopyWithoutFirst() {
        PriorityRow[] copy = new PriorityRow[this.sortedRows.length - 1];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = new PriorityRow(sortedRows[i + 1].idx, new LinkedList<>(sortedRows[i + 1].possiblities));
        }

        return copy;
    }


    private List<BitArray>[] assumeRow(int rowIdx, BitArray possibleRow) {

        List<BitArray>[] nextValidCols = new List[validCols.length];

        for (int colIdx = 0; colIdx < this.validCols.length; colIdx++) {

            List<BitArray> pCols = this.validCols[colIdx];
            List<BitArray >nextPCols = new LinkedList<>();
            nextValidCols[colIdx] = nextPCols;
            for (BitArray col: pCols) { // filter out columns that match this row
                if (col.get(rowIdx) == possibleRow.get(colIdx)) {
                    nextPCols.add(col);
                }
            }
            if (nextPCols.isEmpty()) {
                return null;
            }
        }


        return nextValidCols;
    }

    @Override
    public int compareTo(HeuristicGameState gameStates) {
        return -(int)Math.signum(this.getCost() - gameStates.getCost());
    }


    @Override
    public boolean isGoal() {
        if (this.progress < this.sortedRows.length) {
            return false;
        }

        return this.board.isBoardValid();
    }

    @Override
    public boolean isEndState() {
        return this.progress == this.sortedRows.length;
    }

    @Override
    public void overwrite(GameState state) {
        if (!(state instanceof NonogramGameState)) {
            return;
        }

        NonogramGameState targetState = (NonogramGameState) state;
        this.board.getBoardState().overwrite(targetState.getBoard().getBoardState());

    }

    @Override
    public void setThreshold(float percentage) {
        this.threshold = percentage;
        this.branchProgress = 0;
    }


    @Override
    public boolean hasNext() {
        if (calBranchPercentage() > threshold) {
            return false;
        }
        return this.nextStateIterator.hasNext();
    }

    @Override
    public GameState next() {
        this.branchProgress++;
        return this.nextStateIterator.next();
    }

    private float calBranchPercentage() {
        return  this.branchProgress / (float)this.branchNumber;
    }

    @Override
    public float getCost() {
        return this.cost;
    }

    private static class PriorityRow {
        private int idx;
        private List<BitArray> possiblities;
        PriorityRow(int idx, List<BitArray> possiblities) {
            this.idx = idx;
            this.possiblities = possiblities;
        }
    }

}
