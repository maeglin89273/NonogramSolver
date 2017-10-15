package tw.edu.ntu.csie.cmlab.ccliao.board;

import java.util.BitSet;

public class BitArray extends BitSet {
    private final int nbits;
    public BitArray(int nbits) {
        super(nbits);
        this.nbits = nbits;
    }


    // not stricky bound the array size by throwing exceptions from other methods
    @Override
    public int length() {
        return this.nbits;
    }

    @Override
    public Object clone() {
        BitArray copy = new BitArray(this.length());
        copy.or(this);
        return copy;
    }
}
