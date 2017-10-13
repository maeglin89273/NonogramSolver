package tw.edu.ntu.csie.cmlab.ccliao.board;

public class BoardHint {
    private int[][][] hintData;

    private static final int ROW_IDX = 0;
    private static final int COL_IDX = 1;

    BoardHint(int boardSize) {
        this.hintData = new int[2][boardSize][];

    }

    void setRow(int i, int[] row) {
        this.hintData[ROW_IDX][i] = row;
    }

    void setColumn(int i, int[] column) {
        this.hintData[COL_IDX][i] = column;
    }


    public int[] getRow(int i) {
        return this.hintData[ROW_IDX][i];
    }


    public int[] getColumn(int i) {
        return this.hintData[COL_IDX][i];
    }

    public int[][] getRows() {
        return this.hintData[ROW_IDX];
    }

    public int[][] getColumns() {
        return this.hintData[COL_IDX];
    }

    public int[] getLineHint(int axis, int i) {
        return this.hintData[axis][i];
    }
}
