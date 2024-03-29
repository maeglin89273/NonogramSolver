package tw.edu.ntu.csie.cmlab.ccliao.solver.search;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class DepthFirstSearch extends TreeSearch {

    protected Deque<GameState> stack = new LinkedList<>();


    @Override
    public boolean search(GameState initialState) {

        initialState.prepareNextPossibleStates();
        this.stack.push(initialState);

        while (!this.stack.isEmpty()) {
            GameState curState = this.stack.peek();

            if (!curState.hasNext()) { // branches depleted
                this.stack.pop();
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
            this.stack.push(nextState);

        }

//        System.out.println("Warning: cannot find a valid solution");
        return false;
    }
}
