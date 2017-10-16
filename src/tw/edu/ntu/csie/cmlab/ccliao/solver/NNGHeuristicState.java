package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.BitArray;
import tw.edu.ntu.csie.cmlab.ccliao.board.Board;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.GameState;
import tw.edu.ntu.csie.cmlab.ccliao.solver.search.HeuristicGameState;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Stream;

public class NNGHeuristicState extends NonogramGameState implements HeuristicGameState {

    private final int axis2;
    private final int cost;
    private final HintMatchTrace[] lineTrace;
    private List<NextStateData> nextStateTmps;


    // branch contructor
    protected NNGHeuristicState(Board board, HintMatchTrace[] lineTrace, List<BitArray>[] validlines, int axis2, int progess, float threshold) {
        super(board, validlines, progess, threshold);
        this.axis2 = axis2;
        this.lineTrace = lineTrace;
        this.cost = this.estimateBranchSize();
    }

    // root constructor
    public NNGHeuristicState(Board board, List<BitArray>[] validLines, int axis2) {
        this(board, HintMatchTrace.createLineOfEmptyTrace(board.getSize()), validLines, axis2, 0, 1f);
    }


    private int estimateBranchSize() {
        if (this.progress == this.validLines.length) {
            return 0;
        }

        this.nextStateTmps = new LinkedList<>();
        int axis = 1 - axis2; //flip the axis

        Board possibleBoard = board.clone();
        for (BitArray possibleLine: this.validLines[this.progress]) {

            // filter out impossible choice then give a cost score to the row
            HintMatchTrace[] nextLineTrace = assumeLine(possibleBoard, possibleLine, this.progress);
            if (nextLineTrace != null) {
                possibleBoard.getBoardState().setLine(axis, this.progress, possibleLine);
                nextStateTmps.add(new NextStateData(possibleBoard.clone(), nextLineTrace));
            }

        }

        return nextStateTmps.size();
    }

    @Override
    public void prepareNextPossibleStates() {
        Queue<GameState> nextStates = new PriorityQueue<>();
        final int advancedProgress = this.progress + 1; // current progress is also the advanced row index


        for (NextStateData nextStateTmp: this.nextStateTmps) {
            nextStates.offer(new NNGHeuristicState(nextStateTmp.board, nextStateTmp.assumptionTrace, validLines, axis2, advancedProgress, this.threshold));
        }


        this.branchNumber = nextStates.size();
        this.nextStateIterator = nextStates.iterator();
    }

    private HintMatchTrace[] assumeLine(Board board, BitArray possibleLine, int lineIdx) {

        int[][] axis2Hint = board.getHint().getAxisHint(this.axis2);
        HintMatchTrace[] newLineTrace = HintMatchTrace.createLineOfEmptyTrace(this.lineTrace.length);


        for (int axis2Idx = 0; axis2Idx < this.lineTrace.length; axis2Idx++) {

            int[] hint = axis2Hint[axis2Idx];
            HintMatchTrace cellTrace = newLineTrace[axis2Idx];

            int prevHintIdx = this.lineTrace[axis2Idx].hintIdx;
            int prevConsump = this.lineTrace[axis2Idx].cellConsumption;

            if (possibleLine.get(axis2Idx)) { // if the assumed cell is filled
                if (prevConsump == 0 && prevHintIdx < hint.length || // start consuming next hint
                     prevConsump > 0 && hint[prevHintIdx] > prevConsump) { // can continue consuming but check the hint bound

                    cellTrace.cellConsumption = prevConsump + 1;
                    cellTrace.hintIdx = prevHintIdx;

                } else { // break the hint
                    return null;
                }


            } else {
                if (prevConsump > 0 && hint[prevHintIdx] == prevConsump) { // end of consuming current hint
                    cellTrace.hintIdx = prevHintIdx + 1;

                } else if (prevConsump == 0) { // blanks continue
                    cellTrace.hintIdx = prevHintIdx;

                } else { // break the hint
                    return null;
                }

                //above conditions are validating the new blank cell
                // => not consuming the hint
                // => we need to check remaining row number is sufficient to supply remaining filled cells
                if (minRequiredCellNumberFrom(hint, cellTrace.hintIdx) > remainingLineNumber(lineIdx)) {
                    return null;
                }

                // the default cellConsumption is 0, so skip this line
//                newLineTrace[col].cellConsumption = 0;
            }


        }


        return newLineTrace;
    }

    @Override
    public int compareTo(HeuristicGameState gameStates) {
        return -(int)Math.signum(this.getCost() - gameStates.getCost());
    }

    private int remainingLineNumber(int lineIdx) {
        return this.board.getSize() - (lineIdx + 1);
    }

    private static int minRequiredCellNumberFrom(int[] hintLine, int beginIdx) {
        if (beginIdx >= hintLine.length) { // all hint cells are clear
            return 0;
        }

        int sum = 0;
        for (;beginIdx < hintLine.length - 1; beginIdx++) {
            sum += hintLine[beginIdx] + 1; // +1 is for separation
        }
        sum += hintLine[beginIdx];
        return sum;
    }

    @Override
    public float getCost() {
        return this.cost;
    }





    private static class NextStateData {
        Board board;
        HintMatchTrace[] assumptionTrace;
        NextStateData(Board board, HintMatchTrace[] assumptionTrace) {
            this.board = board;
            this.assumptionTrace = assumptionTrace;
        }
    }


    private static class HintMatchTrace implements Cloneable {
        private int hintIdx = 0;
        private int cellConsumption = 0; // the consumption progress in the indexed hint entry

        static HintMatchTrace[] createLineOfEmptyTrace(int size) {
            return Stream.generate(HintMatchTrace::new).limit(size).toArray(HintMatchTrace[]::new);
        }

    }
}
