package tw.edu.ntu.csie.cmlab.ccliao.board;


import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

public class Board implements Cloneable {

    private BoardState state;
    private BoardHint hint;
    private BiFunction<Integer, Integer, Boolean>[] cellGetters;


    Board(BoardState state, BoardHint hint) {
        this.state = state;
        this.hint = hint;

        BiFunction<Integer, Integer, Boolean> rowCellGetter = (row, col) -> this.state.grid[row].get(col);
        BiFunction<Integer, Integer, Boolean> colCellGetter = (col, row) -> this.state.grid[row].get(col);

        this.cellGetters = new BiFunction[]{rowCellGetter, colCellGetter};

    }

    public BoardState getBoardState() {
        return this.state;
    }

    public int getSize() {
        return this.state.size;
    }

    public BoardHint getHint() {
        return hint;
    }

    public boolean isBoardValid() {


        for (int getterI = 0; getterI < cellGetters.length; getterI++) {
            for (int i = 0; i < state.size; i++) {
                if (!this.isLineValid(this.hint.getLineHint(getterI, i), i, cellGetters[getterI])) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isLineValid(int[] hint, int i, BiFunction<Integer, Integer, Boolean> cellGetter) {

        List<Integer> rowStrips = new LinkedList<>();

        int filledCellCount = 0;
        for (int j = 0; j < this.state.size; j++) {
            if (cellGetter.apply(i, j)) {
                filledCellCount++;

            } else if (filledCellCount > 0) {
                rowStrips.add(filledCellCount);
                filledCellCount = 0;
            }
        }

        if (filledCellCount > 0) {
            rowStrips.add(filledCellCount);
        }


        if (hint.length == rowStrips.size()) {
            int j = 0;
            for (int stripLen: rowStrips) {
                if (hint[j] != stripLen) {
                    return false;
                }
                j++;
            }

            return true;
        }

        return false;
    }


    @Override
    public Board clone() {
        return new Board(this.state.clone(), this.hint);
    }
}
