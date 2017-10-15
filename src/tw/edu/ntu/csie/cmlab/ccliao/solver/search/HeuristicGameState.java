package tw.edu.ntu.csie.cmlab.ccliao.solver.search;

public interface HeuristicGameState extends GameState, Comparable<HeuristicGameState> {
    float getCost();

    @Override
    default int compareTo(HeuristicGameState gameStates) {
        return (int)Math.signum(this.getCost() - gameStates.getCost());
    }
}
