package tw.edu.ntu.csie.cmlab.ccliao.board;

import java.util.Arrays;

public class BoardState implements Cloneable {
    public final int size;

    public final BitArray[] grid; // row major


    public BoardState(int size) {
        this.size = size;
        this.grid = new BitArray[size];

        for (int i = 0; i < this.grid.length; i++) {
            this.grid[i] = new BitArray(size);
        }
    }

    private BoardState(BoardState state) {
        this.size = state.size;
        this.grid = Arrays.stream(state.grid).map(BitArray::clone).toArray(BitArray[]::new);
    }

    public void setRow(int i, BitArray row) {
        this.grid[i] = row;

    }

    public void setColumn(int i, BitArray col) {
        for (int j = 0; j < this.grid.length; j++) {
            this.grid[j].set(i, col.get(j));
        }
    }

    public BoardState clone() {
        return new BoardState(this);
    }

    public void overwrite(BoardState boardState) {
        if (this.size != boardState.size) {
            return;
        }

        for (int i = 0; i < this.grid.length; i++) {
            this.grid[i] = (BitArray) boardState.grid[i].clone();
        }
    }

    public void setLine(int axis, int i, BitArray line) {
        if (axis == 0) {
            this.setRow(i, line);
        } else {
            this.setColumn(i, line);
        }
    }
}
