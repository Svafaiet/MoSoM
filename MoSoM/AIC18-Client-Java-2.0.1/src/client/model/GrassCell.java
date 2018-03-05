package client.model;

import java.util.Comparator;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class GrassCell extends Cell {

    public static final Comparator<GrassCell> VALUE = (c1, c2) -> {
        if (c1.attacks > c2.attacks) return -1;
        else if (c1.attacks < c2.attacks) return 1;
        else return Integer.compare(c1.surrounds, c2.surrounds);
    };

    private Tower tower;

    public int attacks;
    public int surrounds;

    public GrassCell(int x, int y, Tower tower) {
        super(x, y);
        this.tower=tower;
    }

    public boolean isEmpty() {
        if (this.tower == null)
            return true;
        else return false;
    }

    public Tower getTower() {
        return tower;
    }

    @Override
    public String toString() {
        return "g";
    }
}
