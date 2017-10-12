package tw.edu.ntu.csie.cmlab.ccliao.board;

import java.util.BitSet;

public class BoardState {
    public final int size;

    public final BitSet[] grid; // row major
    public final BitSet[] settedMask; // 0: indeterminated 1: determinated

//    public BoardState(BoardState copy) {
//        this.size = copy.size;
//        this.grid = copy.grid.clone();
//        this.grid
//    }

    public BoardState(int size) {
        this.size = size;
        this.grid = new BitSet[size];
        this.settedMask = new BitSet[size];
        for (int i = 0; i < this.grid.length; i++) {
            this.grid[i] = new BitSet(size);
            this.settedMask[i] = new BitSet(size);
        }
    }


}
