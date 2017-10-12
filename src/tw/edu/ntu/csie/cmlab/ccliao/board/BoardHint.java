package tw.edu.ntu.csie.cmlab.ccliao.board;

public class BoardHint {
    private int[][] columns;
    private int[][] rows;

    BoardHint(int boardSize) {
        this.columns = new int[boardSize][];
        this.rows= new int[boardSize][];
    }

    void setRow(int i, int[] row) {
        this.rows[i] = row;
    }

    void setColumn(int i, int[] column) {
        this.columns[i] = column;
    }


    public int[] getRow(int i) {
        return this.rows[i];
    }


    public int[] getColumn(int i) {
        return this.columns[i];
    }

    public int[][] getRows() {
        return rows;
    }

    public int[][] getColumns() {
        return columns;
    }
}
