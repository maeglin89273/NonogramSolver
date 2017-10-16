package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.BitArray;
import tw.edu.ntu.csie.cmlab.ccliao.board.Board;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.GameState;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.HeuristicGameState;

import java.util.*;


public class NonogramCombHeuristicState implements HeuristicGameState {

    private float threshold;
    private int branchProgress = 0;


    private Board board;
    private final int cost;


    private Iterator<GameState> nextStateIterator;
    private int branchNumber;


    private List<BitArray>[][] validLines;

    // branch contructor
    protected NonogramCombHeuristicState(Board board, List<BitArray>[][] validLines, float threshold) {
        this.board = board;

        this.validLines = validLines;

        this.cost = this.calculateCost();
        this.threshold = threshold;
    }

    // root constructor
    public NonogramCombHeuristicState(Board board, List<BitArray>[] validRows, List<BitArray>[] validCols) {
        this(board, new List[][]{validRows, validCols}, 1);

    }

    private int calculateCost() {
        return remainingLineNumber(this.validLines[0]) + remainingLineNumber(this.validLines[1]);
    }


    private static int remainingLineNumber(List<BitArray>[] possibleLines) {
        return Arrays.stream(possibleLines).mapToInt(pLine -> pLine.size()).sum();
    }

    @Override
    public void prepareNextPossibleStates() {
        Queue<GameState> nextStates = new PriorityQueue<>();


        PickResult result = this.pickLessPossibleLine();
        List<BitArray> pLine = result.minLine;
        Board possibleBoard = board.clone();
        for (BitArray possibleLine: pLine) {

            List<BitArray>[][] nextValidLines = this.assumeLine(possibleLine, result);

            if (nextValidLines != null) {

                possibleBoard.getBoardState().setLine(result.axis, result.idx, possibleLine);
                nextStates.offer(new NonogramCombHeuristicState(possibleBoard.clone(), nextValidLines, this.threshold));
            }
        }
        this.branchNumber = nextStates.size();
        this.nextStateIterator = nextStates.iterator();
    }

    private List<BitArray>[][] assumeLine(BitArray possibleLine, PickResult result) {
        List<BitArray>[][] validLinesCopy = copyValidLines();
        validLinesCopy[result.axis][result.idx].clear();
        validLinesCopy[result.axis][result.idx].add(possibleLine);

        if (!CrossSolve.solve(validLinesCopy[0], validLinesCopy[1])) {
            return null;
        }

        return validLinesCopy;
    }

    private List<BitArray>[][] copyValidLines() {
        List<BitArray>[][] copy = new List[this.validLines.length][];

        for (int axis = 0; axis < copy.length; axis++) {
            for (int i = 0; i < this.validLines[axis].length; i++) {
                copy[axis][i] = new LinkedList<>(this.validLines[axis][i]);
            }
        }
        return copy;
    }


    @Override
    public int compareTo(HeuristicGameState gameStates) {
        return -(int)Math.signum(this.getCost() - gameStates.getCost());
    }


    @Override
    public boolean isGoal() {
        if (!this.isEndState()) {
            return false;
        }

        return this.board.isBoardValid();
    }

    @Override
    public boolean isEndState() {
        return this.cost == 2 * board.getSize();
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


    PickResult pickLessPossibleLine() {
        PickResult result = new PickResult();

        List<BitArray> minLine = null;
        int minPossiblity = Integer.MAX_VALUE;
        int compare;

        int minIdx = 0;
        int minAxis = 0;

        for (int axis = 0; axis < validLines.length; axis++) {
            for (int i = 0; i < validLines[axis].length; i++) {
                compare = validLines[axis][i].size();

                if (compare > 1 && compare < minPossiblity) {
                    minLine = validLines[axis][i];
                    minPossiblity = compare;
                    minAxis = axis;
                    minIdx = i;
                }
            }
        }

        assert minLine != null;

        result.minLine = minLine;
        result.axis = minAxis;
        result.idx = minIdx;
        return result;
    }

    static class PickResult {
        List<BitArray> minLine;
        int idx;
        int axis;
    }


}
