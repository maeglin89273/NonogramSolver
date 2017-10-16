package tw.edu.ntu.csie.cmlab.ccliao.solver;

import tw.edu.ntu.csie.cmlab.ccliao.board.BitArray;

import java.util.List;

public class CrossSolve {
    public static boolean solve(List<BitArray>[] axis1, List<BitArray>[] axis2) {


        boolean hasElimination;
        EliminationResult result;
        do {
            result = eliminatePossiblities(axis1, axis2);
            if (result.error) {
                return false;
            }
            hasElimination = result.hasElimination;


            result = eliminatePossiblities(axis2, axis1);
            if (result.error) {
                return false;
            }
            hasElimination |= result.hasElimination;

        } while (hasElimination);
        return true;
    }

    private static EliminationResult eliminatePossiblities(List<BitArray>[] axis1, List<BitArray>[] axis2) {
        Intersection[] intersectionArray = new Intersection[axis1.length];
        int i = 0;
        for (List<BitArray> a1Lines: axis1) {
            intersectionArray[i++] = intersectPossibilities(a1Lines);
        }

        boolean hasElimination = false;
        for (int i1 = 0; i1 < axis1.length; i1++) {
            for (int i2 = 0; i2 < axis2.length; i2++) {
                if (intersectionArray[i1].filledCells.get(i2)) {
                    hasElimination |= notMatchCellRemove(axis2[i2], i1, true);
                    if (axis2[i2].isEmpty()) {
                        return new EliminationResult(true, hasElimination);
                    }

                } else if(intersectionArray[i1].blankCells.get(i2)) {
                    hasElimination |= notMatchCellRemove(axis2[i2], i1, false);
                    if (axis2[i2].isEmpty()) {
                        return new EliminationResult(true, hasElimination);
                    }
                }
            }
        }



        return new EliminationResult(false, hasElimination);

    }

    public static boolean notMatchCellRemove(List<BitArray> possibilities, int i1, boolean cellValue) {
        return possibilities.removeIf(line -> line.get(i1) != cellValue);
    }

    public static Intersection intersectPossibilities(List<BitArray> possibilities) {
        int lineSize = possibilities.get(0).size();
        Intersection intersection = new Intersection(lineSize);

        for (BitArray line: possibilities) {
            intersection.filledCells.and(line);
            intersection.blankCells.or(line);
        }

        intersection.blankCells.flip(0, lineSize);

        return intersection;
    }

    private static class EliminationResult {
        boolean error = false;
        boolean hasElimination = false;


        public EliminationResult(boolean error, boolean hasElimination) {
            this.error = error;
            this.hasElimination = hasElimination;
        }
    }

    public static class Intersection {
        BitArray filledCells;
        BitArray blankCells;

        Intersection(int lineSize) {
            this.filledCells = new BitArray(lineSize);
            this.blankCells = new BitArray(lineSize);

            this.filledCells.set(0, lineSize);
        }

    }
}
