package tw.edu.ntu.csie.cmlab.ccliao.solver.search;

import java.util.Iterator;

public interface GameState extends Iterator<GameState> {
    void prepareNextPossibleStates();
    boolean isGoal();
    boolean isEndState();
    void overwrite(GameState nextState);

    void setThreshold(float percentage);

}
