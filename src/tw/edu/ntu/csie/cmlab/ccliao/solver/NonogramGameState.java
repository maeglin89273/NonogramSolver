package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.BitArray;
import tw.edu.ntu.csie.cmlab.ccliao.board.Board;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.GameState;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NonogramGameState implements GameState {

    protected float threshold;
    protected int branchProgress = 0;
    protected int branchNumber;

    protected final Board board;
    protected final int progress;
    protected List<BitArray>[] validLines;

    protected Iterator<GameState> nextStateIterator;

    // branch contructor
    protected NonogramGameState(Board board, List<BitArray>[] validLines, int progress, float threshold) {

        this.board = board;
        this.validLines = validLines;
        this.progress = progress;
        this.threshold = threshold;
    }

    // root constructor
    public NonogramGameState(Board board, List<BitArray>[] validLines) {
        this(board, validLines, 0, 1f);
    }

    @Override
    public void prepareNextPossibleStates() {
        List<GameState> nextStates = new LinkedList<>();
        final int advancedProgress = this.progress + 1; // current progress is also the advanced row index

        for (BitArray possibleRow: this.validLines[this.progress]) {
            Board newBoard = board.clone();
            newBoard.getBoardState().setRow(this.progress, possibleRow);
            nextStates.add(new NonogramGameState(newBoard, validLines, advancedProgress, this.threshold));
        }


        this.branchNumber = nextStates.size();
        this.nextStateIterator = nextStates.iterator();
    }

    @Override
    public boolean isGoal() {
        if (this.progress < validLines.length) {
            return false;
        }
        return this.board.isBoardValid();
    }

    @Override
    public boolean isEndState() {
        return this.progress == validLines.length;
    }

    @Override
    public void overwrite(GameState state) {
        if (!(state instanceof NonogramGameState)) {
            return;
        }

        NonogramGameState targetState = (NonogramGameState) state;
        this.board.getBoardState().overwrite(targetState.getBoard().getBoardState());


    }



    public Board getBoard() {
        return this.board;
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
}
