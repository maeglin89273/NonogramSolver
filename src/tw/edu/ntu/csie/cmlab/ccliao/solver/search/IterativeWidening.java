package tw.edu.ntu.csie.cmlab.ccliao.solver.search;

public class IterativeWidening extends TreeSearch {

    @Override
    public boolean search(GameState initialState) {

        DepthFirstSearch dfs = new DepthFirstSearch();

        for (float threshold = 0.2f; threshold <= 1; threshold *= 2) {
            initialState.setThreshold(threshold);
            if (dfs.search(initialState)) {
                System.out.println(threshold);
                return true;
            }
        }
    //final try
        initialState.setThreshold(1);
        return dfs.search(initialState);
    }
}
