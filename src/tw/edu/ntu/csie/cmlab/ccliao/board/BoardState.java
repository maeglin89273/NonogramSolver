package tw.edu.ntu.csie.cmlab.ccliao.board;

public class BoardState {
    public final int size;
    public final byte[][] data;

    public static final byte FILLED = 1;
    public static final byte BLANK = 0;
    public static final byte NOTSURE = -1;

    public BoardState(BoardState copy) {
        this.size = copy.size;
        this.data = copy.data.clone();
    }

    public BoardState(int size) {
        this.size = size;
        this.data = new byte[size][size];
    }


}
