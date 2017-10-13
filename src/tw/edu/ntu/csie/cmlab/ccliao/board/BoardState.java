package tw.edu.ntu.csie.cmlab.ccliao.board;

import java.util.Arrays;

public class BoardState implements Cloneable {
    public final int size;

    public final BitArray[] grid; // row major
    public final BitArray[] settedMask; // 0: indeterminated 1: determinated


    public BoardState(int size) {
        this.size = size;
        this.grid = new BitArray[size];
        this.settedMask = new BitArray[size];
        for (int i = 0; i < this.grid.length; i++) {
            this.grid[i] = new BitArray(size);
            this.settedMask[i] = new BitArray(size);
        }
    }

    private BoardState(BoardState state) {
        this.size = state.size;
        this.grid = Arrays.stream(state.grid).map(BitArray::clone).toArray(BitArray[]::new);
        this.settedMask = Arrays.stream(state.settedMask).map(BitArray::clone).toArray(BitArray[]::new);
    }

    public void setRow(int i, BitArray row) {
        this.grid[i] = row;
        this.settedMask[i].set(0, this.settedMask[i].size());

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
            this.settedMask[i] = (BitArray) boardState.settedMask[i].clone();
        }
    }
}
