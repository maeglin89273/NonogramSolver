package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.BitArray;
import tw.edu.ntu.csie.cmlab.ccliao.board.Board;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.GameState;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.HeuristicGameState;

import java.util.*;


public class NNGMinCrossHeuristicState implements HeuristicGameState {

    private float threshold;
    private int branchProgress = 0;


    private Board board;
    private final int cost;


    private Iterator<GameState> nextStateIterator;
    private int branchNumber;


    private List<BitArray>[][] validLines;

    // branch contructor
    protected NNGMinCrossHeuristicState(Board board, List<BitArray>[][] validLines, float threshold) {
        this.board = board;

        this.validLines = validLines;

        this.cost = this.calculateAxisRemainingNumber();
        this.threshold = threshold;

    }

    // root constructor
    public NNGMinCrossHeuristicState(Board board, List<BitArray>[] validRows, List<BitArray>[] validCols) {
        this(board, new List[][]{validRows, validCols}, 1);

    }

    private int calculateAxisRemainingNumber() {
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

        for (BitArray possibleLine: pLine) {

            List<BitArray>[][] nextValidLines = this.assumeLine(possibleLine, result);

            if (nextValidLines != null) {
                nextStates.offer(new NNGMinCrossHeuristicState(board, nextValidLines, this.threshold));
            }
        }

        this.branchNumber = nextStates.size();
        this.nextStateIterator = nextStates.iterator();
    }

    private PickResult pickLessPossibleLine() {
        PickResult result = new PickResult();

        List<BitArray> minLine = null;
        int minPossiblity = Integer.MAX_VALUE;
        int compare;

        int minIdx = 0;
        int minAxis = 0;

        for (int axis = 0; axis < validLines.length; axis++) {
            for (int i = 0; i < validLines[axis].length; i++) {
                compare = validLines[axis][i].size();

                if (1 < compare && compare < minPossiblity) {
                    minPossiblity = compare;
                    minLine = validLines[axis][i];
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
            copy[axis] = new List[this.validLines[axis].length];
            for (int i = 0; i < copy[axis].length; i++) {
                copy[axis][i] = new LinkedList<>(this.validLines[axis][i]);
            }
        }
        return copy;
    }


    @Override
    public int compareTo(HeuristicGameState gameStates) {
        //sorted by descending order not ascending
        return (int)Math.signum(gameStates.getCost() - this.getCost());
    }


    @Override
    public boolean isGoal() {
        if (!this.isEndState()) {
            return false;
        }
        NonogramSolver.fillBoardByRows(board, validLines[0]);
        return this.board.isBoardValid();
    }



    @Override
    public boolean isEndState() {
        return this.cost == 2 * board.getSize();
    }

    @Override
    public void overwrite(GameState state) {
        // since we directly modify the original board. We do not do this again

    }

    @Override
    public void setThreshold(float percentage) {
        this.threshold = percentage;
        this.branchProgress = 0;
    }


    @Override
    public boolean hasNext() {
        if (calBranchPercentage() > this.threshold) {
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





}
