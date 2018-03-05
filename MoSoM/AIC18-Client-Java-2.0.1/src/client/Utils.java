package client;

import client.model.*;

class Utils {

    public static int now = 0   ;

    public static int step(World game) {
        now++;
        return now;
        //double coeff = 0.385;
        //return (int) Math.pow(game.getCurrentTurn(), coeff);
    }

    public static int manhataanDistance(Point p1, Point p2) {
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }


    public static int unitHealth(Unit unit) {
        int result;
        if (unit instanceof LightUnit)
            result = (int) (LightUnit.INITIAL_HEALTH * Math.pow(LightUnit.HEALTH_COEFF, unit.getLevel()) + 1);
        else
            result = (int) (HeavyUnit.INITIAL_HEALTH * Math.pow(HeavyUnit.HEALTH_COEFF, unit.getLevel()) + 1);
        return result;
    }
}
