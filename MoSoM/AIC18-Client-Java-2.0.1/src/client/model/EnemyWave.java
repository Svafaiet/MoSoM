package client.model;

import java.util.ArrayList;
import java.util.Collection;

public class EnemyWave {

    public ArrayList<Unit> units;
    public int totalHealth;
    public int length;
    public double speed;
    public int startingI;

    public EnemyWave(int startingI, Collection<Unit> units) {
        this.units = new ArrayList<>(units);
        totalHealth = 0;
        length = 0;
        speed = 0;
        this.startingI = startingI;
    }
}

