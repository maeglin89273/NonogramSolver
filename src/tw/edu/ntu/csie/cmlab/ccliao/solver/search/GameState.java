package tw.edu.ntu.csie.cmlab.ccliao.solver.search;

public interface GameState extends Iterable<GameState> {
    void prepareNextPossibleStates();
    boolean isGoal();
    boolean isEndState();
    void overwrite(GameState nextState);
}
