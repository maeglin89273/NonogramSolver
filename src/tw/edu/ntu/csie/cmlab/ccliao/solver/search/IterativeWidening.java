package tw.edu.ntu.csie.cmlab.ccliao.solver.search;

public class IterativeWidening extends TreeSearch {

    @Override
    public boolean search(GameState initialState) {

        DepthFirstSearch dfs = new DepthFirstSearch();


        for (float f = 2; f <= 32; f *= 2) {
            initialState.setThreshold((f - 1)/f);
            if (dfs.search(initialState)) {
                System.out.format("branch width percentage: %f%n", (f - 1)/f);
                return true;
            }
        }
    //final try
        initialState.setThreshold(1);
        return dfs.search(initialState);
    }
}
