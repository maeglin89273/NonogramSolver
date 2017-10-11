package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.Board;

public class NonogramSolver {
    public static final String DFS1 = "DFS1";
    public static final String DFS2 = "DFS2";
    public static final String IDFA_STAR = "IDFA*";

    public static Board solve(Board blankBoard, String algorithm) {
        return solve(blankBoard, algorithm, false);
    }

    public static Board solve(Board blankBoard, String algorithm, boolean log) {
        return blankBoard;
    }
}
