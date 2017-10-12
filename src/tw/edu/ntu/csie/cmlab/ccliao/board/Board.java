package tw.edu.ntu.csie.cmlab.ccliao.board;


import java.util.LinkedList;
import java.util.List;

public class Board {

    private BoardState state;
    private BoardHint hint;

    Board(BoardState state, BoardHint hint) {
        this.state = state;
        this.hint = hint;
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
        for (int i = 0; i < state.size; i++) {
            if (!this.isRowValid(i)) {
                return false;
            }
        }

        for (int i = 0; i < state.size; i++) {
            if (!this.isColumnValid(i)) {
                return false;
            }
        }

        return true;
    }

    public boolean isRowValid(int i) {
        int[] rowHint = this.hint.getRow(i);

        List<Integer> rowStrips = new LinkedList<>();


        int filledCellCount = 0;

        // general case
        for (int col = 0; col < this.state.size; col++) {
            if (this.state.grid[i].get(col)) {
                filledCellCount++;

            } else if (filledCellCount > 0) {
                rowStrips.add(filledCellCount);
                filledCellCount = 0;
            }
        }

        if (filledCellCount > 0) {
            rowStrips.add(filledCellCount);
        }


        if (rowHint.length == rowStrips.size()) {
            int j = 0;
            for (int stripLen: rowStrips) {
                if (rowHint[j] != stripLen) {
                    return false;
                }
                j++;
            }

            return true;
        }

        return false;
    }

    public boolean isColumnValid(int i) {
        int[] colHint = this.hint.getColumn(i);

        List<Integer> colStrips = new LinkedList<>();


        int filledCellCount = 0;

        // general case
        for (int row = 0; row < this.state.size; row++) {
            if (this.state.grid[row].get(i)) {
                filledCellCount++;

            } else if (filledCellCount > 0) {
                colStrips.add(filledCellCount);
                filledCellCount = 0;
            }
        }

        if (filledCellCount > 0) {
            colStrips.add(filledCellCount);
        }


        if (colHint.length == colStrips.size()) {
            int j = 0;
            for (int stripLen: colStrips) {
                if (colHint[j] != stripLen) {
                    return false;
                }
                j++;
            }

            return true;
        }

        return false;
    }

}
