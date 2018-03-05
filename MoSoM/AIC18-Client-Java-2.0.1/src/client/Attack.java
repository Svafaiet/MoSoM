package client;

import client.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Attack {

    public static SendWaveReturnValue sendWave(World game, int turnEndCurrentWave, HashMap<Integer, ArrayList<Quest>> quests, Wave maybe, boolean lightUnit) {
        SendWaveReturnValue attackReturn = new SendWaveReturnValue();
        System.out.println(game.getCurrentTurn() + " " + maybe.getLength() + " " + maybe.getUnitsPerCell());
        if (game.getCurrentTurn() > turnEndCurrentWave) {
            for (int i = 0; i < maybe.getLength(); i++) {
                for (int j = 0; j < maybe.getUnitsPerCell(); j++) {
                    if (lightUnit) {
                        if (!quests.containsKey(game.getCurrentTurn() + i * maybe.getMoveSpeed()))
                            quests.put(game.getCurrentTurn() + i * maybe.getMoveSpeed(), new ArrayList<>());
                        quests.get(game.getCurrentTurn() + i * maybe.getMoveSpeed()).add(
                                new Quest(QuestCommand.CREATE_LIGHT_UNIT,
                                        new String[]{String.valueOf(chooseAttackPath(game, maybe))}));
                    } else {
                        if (!quests.containsKey(game.getCurrentTurn() + i * maybe.getMoveSpeed()))
                            quests.put(game.getCurrentTurn() + i * maybe.getMoveSpeed(), new ArrayList<>());
                        quests.get(game.getCurrentTurn() + i * maybe.getMoveSpeed()).add(
                                new Quest(QuestCommand.CREATE_HEAVY_UNIT,
                                        new String[]{String.valueOf(chooseAttackPath(game, maybe))}));
                    }
                }
            }
            attackReturn.waveSent = true;
            attackReturn.endTurnOfWave = game.getCurrentTurn() + maybe.getLength() * maybe.getMoveSpeed();
        } else {
            attackReturn.waveSent = false;
            attackReturn.endTurnOfWave = turnEndCurrentWave;
            Utils.now--;
        }
        return attackReturn;
    }

    public static Wave incomeWave(World game) {
        Wave wave = new Wave(1, LightUnit.MOVE_SPEED);
        int price = LightUnit.INITIAL_PRICE;
        int money = game.getMyInformation().getMoney();
        wave.setUnitsPerCell(5);
        wave.setLength(money / 2 / price / 5);
        return wave;
    }

    public static Wave hugeWave(World game, boolean lightUnit) {
        Wave wave;
        int price;
        int money = game.getMyInformation().getMoney();
        if (lightUnit) {
            wave = new Wave(1, LightUnit.MOVE_SPEED);
            price = LightUnit.INITIAL_PRICE;
        } else {
            wave = new Wave(1, HeavyUnit.MOVE_SPEED);
            price = HeavyUnit.INITIAL_PRICE;
        }
        wave.setUnitsPerCell(10);
        wave.setLength((money + 5000) / price / 10);
        return wave;
    }

    public static int chooseAttackPath(World game, Wave wave) {
        towersInAttackPaths(game);
        for (Path path : game.getAttackMapPaths()) {
            path.totalDefence = 0;
            for (Tower tower : path.towers) {
                double currentTowerDamage =  (numberOfCellsTowerCanDefend(game, path,
                        (GrassCell) game.getAttackMap().getCell(tower.getLocation().getX(), tower.getLocation().getY()),
                        tower.getAttackRange() - 1) + wave.getLength())
                        * wave.getMoveSpeed() / tower.getAttackSpeed() * tower.getDamage() * 1.0;
                if (tower instanceof CannonTower)
                    currentTowerDamage *= wave.getUnitsPerCell();
                path.totalDefence += currentTowerDamage;

            }
            //System.out.printf("calculated defensive path power in path %d: %f\n", path.hashCode(), path.totalDefence);
        }
        double min = Integer.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < game.getAttackMapPaths().size(); i++) {
            if (game.getAttackMapPaths().get(i).totalDefence < min) {
                min = game.getAttackMapPaths().get(i).totalDefence;
                index = i;
            }
        }
        return index;
    }

    private static void towersInAttackPaths(World game) {
        for (Path path : game.getAttackMapPaths()) {
            path.towers.clear();
        }
        for (Tower tower : game.getVisibleEnemyTowers()) {
            for (Path path : game.getAttackMapPaths()) {
                if (numberOfCellsTowerCanDefend(game, path,
                        (GrassCell) game.getAttackMap().getCell(tower.getLocation().getX(), tower.getLocation().getY()),
                        tower.getAttackRange()) > 0) {
                    path.towers.add(tower);
                }
            }
        }
    }

    private static int numberOfCellsTowerCanDefend(World game, Path path, GrassCell cell, int range) {
        int result = 0;
        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                int x = cell.getLocation().getX() + i;
                int y = cell.getLocation().getY() + j;
                if (x < 0 || x >= game.getDefenceMap().getWidth()
                        || y < 0 || y >= game.getAttackMap().getHeight()
                        || Math.abs(x - cell.getLocation().getX()) + Math.abs((y - cell.getLocation().getY())) > range)
                    continue;
                if (game.getAttackMap().getCell(x, y) instanceof RoadCell
                        && path.getRoad().contains(game.getAttackMap().getCell(x, y)))
                    result++;
            }
        }
        return result;
    }

}
