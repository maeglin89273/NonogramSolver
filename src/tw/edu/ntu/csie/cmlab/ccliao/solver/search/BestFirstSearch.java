package tw.edu.ntu.csie.cmlab.ccliao.solver.search;

import java.util.*;

public class BestFirstSearch extends TreeSearch {

    protected Queue<HeuristicGameState> queue = new PriorityQueue<>();


    @Override
    public boolean search(GameState initialState) {
        if (!(initialState instanceof HeuristicGameState)) {
            return false;
        }
        HeuristicGameState hInitialState = (HeuristicGameState)initialState;

        initialState.prepareNextPossibleStates();
        this.queue.add(hInitialState);

        while (!this.queue.isEmpty()) {
            GameState curState = this.queue.peek();

            if (!curState.hasNext()) { // branches depleted
                this.queue.poll();
                continue;
            }


            GameState nextState = curState.next();
            if (nextState.isGoal()) { //solution found
                initialState.overwrite(nextState);
                return true;
            } else if (nextState.isEndState()) {
                continue;
            }


            nextState.prepareNextPossibleStates();
            this.queue.add((HeuristicGameState)nextState);

        }

        System.out.println("Warning: cannot find a valid solution");
        return false;
    }
}
