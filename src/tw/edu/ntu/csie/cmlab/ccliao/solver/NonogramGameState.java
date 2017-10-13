package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.BitArray;
import tw.edu.ntu.csie.cmlab.ccliao.board.Board;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.GameState;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NonogramGameState implements GameState {
    protected final Board board;
    protected final int progess;
    protected List<BitArray>[] validRows;

    protected Iterator<GameState> nextStateIterator;

    public NonogramGameState(Board board, List<BitArray>[] validRows, int progess) {

        this.board = board;
        this.validRows = validRows;
        this.progess = progess;
    }

    public NonogramGameState(Board board, List<BitArray>[] validRows) {
        this(board, validRows, -1);
    }

    @Override
    public void prepareNextPossibleStates() {
        List<GameState> nextStates = new LinkedList<>();
        final int advancedProgress = progess + 1;

        for (BitArray possibleRow: this.validRows[advancedProgress]) {
            Board newBoard = board.clone();
            newBoard.getBoardState().setRow(advancedProgress, possibleRow);
            nextStates.add(new NonogramGameState(newBoard, validRows, advancedProgress));
        }

        this.nextStateIterator = nextStates.iterator();
    }

    @Override
    public boolean isGoal() {
        if (this.progess < validRows.length - 1) {
            return false;
        }
        return this.board.isBoardValid();
    }

    @Override
    public boolean isEndState() {
        return this.progess == validRows.length - 1;
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
    public Iterator<GameState> iterator() { // the iterator is state persistent
        return this.nextStateIterator;
    }

    public Board getBoard() {
        return this.board;
    }
}
