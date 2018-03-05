package client.model;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Parsa on 1/22/2018 AD.
 */
public class Path {

    private ArrayList<RoadCell> road;

    public ArrayList<GrassCell> grassCells;

    public ArrayList<Tower> towers;

    public ArrayList<EnemyWave> enemyWaves;

    public double totalDefence;

    Path(ArrayList<RoadCell> road) {
        this.road = road;
        towers = new ArrayList<>();
        grassCells = new ArrayList<>();
        enemyWaves = new ArrayList<>();
        totalDefence = 0;
    }

    public ArrayList<RoadCell> getRoad() {
        return road;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Path: ");

        for (RoadCell aRoad : road) {
            result.append("(").append(aRoad.getLocation().getX()).append(",").append(aRoad.getLocation().getY()).append(")").append(" ");
        }
        return result.toString();
    }
}