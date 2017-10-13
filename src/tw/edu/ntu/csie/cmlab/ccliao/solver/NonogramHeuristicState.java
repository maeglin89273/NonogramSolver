package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.BitArray;
import tw.edu.ntu.csie.cmlab.ccliao.board.Board;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.GameState;

import java.util.LinkedList;
import java.util.List;

public class NonogramHeuristicState extends NonogramGameState {
    public NonogramHeuristicState(Board board, List<BitArray>[] validRows) {
        super(board, validRows);
    }

    @Override
    public void prepareNextPossibleStates() {
        List<GameState> nextStates = new LinkedList<>();

        for (BitArray possibleRow: this.validRows[progess]) {
            Board newBoard = board.clone();
            newBoard.getBoardState().setRow(progess + 1, possibleRow);
            nextStates.add(new NonogramGameState(newBoard, validRows, progess + 1));
        }

        this.nextStateIterator = nextStates.iterator();
    }
}
