package client;

import client.model.*;

import java.util.*;

public class Defence {

    private static final int GAP = 3;

    private static BuildTowerNextToRoadReturnValue buildTowerNextToRoad(World game, Path path, int expectedTowerRange) {
        BuildTowerNextToRoadReturnValue returnValue = new BuildTowerNextToRoadReturnValue();
        setSortedGrassTowerCanAttackARoadPath(game, path, expectedTowerRange);
        sortCells(game, path, expectedTowerRange);
        if (!path.grassCells.isEmpty()) {
            returnValue.wannaBuildTower = true;
            returnValue.bestPoint = path.grassCells.get(0).getLocation();
            returnValue.cell = path.grassCells.get(0);
        } else {
            returnValue.wannaBuildTower = false;
        }
        return returnValue;
    }

    private static void setSortedGrassTowerCanAttackARoadPath(World game, Path path, int expectedTowerAttackRange) {
        Set<GrassCell> grassCellsFound = new HashSet<>();
        for (RoadCell cell : path.getRoad()) {
            for (int i = -expectedTowerAttackRange; i <= expectedTowerAttackRange; i++) {
                for (int j = -expectedTowerAttackRange; j <= expectedTowerAttackRange; j++) {
                    if (i == j && i == 0)
                        continue;
                    int x = cell.getLocation().getX() + i;
                    int y = cell.getLocation().getY() + j;
                    if (x < 0 || x >= game.getDefenceMap().getWidth()
                            || y < 0 || y >= game.getDefenceMap().getHeight())
                        continue;
                    if (game.getDefenceMap().getCell(x, y) instanceof GrassCell)
                        grassCellsFound.add((GrassCell) game.getDefenceMap().getCell(x, y));
                }
            }
        }
        path.grassCells.clear();
        path.grassCells.addAll(grassCellsFound);

    }

    private static void sortCells(World game, Path path, int range) {
        ArrayList<GrassCell> result = new ArrayList<>();
        for (GrassCell cell : path.grassCells)
            if (cell.isEmpty()) {
                cell.surrounds = numberOfSurroundingGrassCell(game, cell);
                cell.attacks = numberOfCellsTowerCanAttack(game, path, cell, range);
                if (!adjacentToTower(game, cell))
                    result.add(cell);
            }
        result.sort(GrassCell.VALUE);
        path.grassCells.clear();
        path.grassCells.addAll(result);
    }

    private static int numberOfSurroundingGrassCell(World game, GrassCell cell) {
        int result = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int x = cell.getLocation().getX() + i;
                int y = cell.getLocation().getY() + j;
                if (x < 0 || x >= game.getDefenceMap().getWidth()
                        || y < 0 || y >= game.getDefenceMap().getHeight()
                        || Math.abs(x - cell.getLocation().getX()) + Math.abs((y - cell.getLocation().getY())) > 2)
                    continue;
                if (game.getDefenceMap().getCell(x, y) instanceof GrassCell)
                    result++;
            }
        }
        return result;
    }

    private static boolean adjacentToTower(World game, Cell cell) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i * j != 0 || (i == j && i == 0))
                    continue;
                int x = cell.getLocation().getX() + i;
                int y = cell.getLocation().getY() + j;
                if (x < 0 || x >= game.getDefenceMap().getWidth()
                        || y < 0 || y >= game.getDefenceMap().getHeight())
                    continue;
                if (game.getDefenceMap().getCell(x, y) instanceof GrassCell
                        && !((GrassCell) game.getDefenceMap().getCell(x, y)).isEmpty())
                    return true;
            }
        }
        return false;
    }

    private static int numberOfCellsTowerCanAttack(World game, Path path, Tower tower, int range) {
        Cell cell = game.getDefenceMap().getCell(tower.getLocation().getX(), tower.getLocation().getY());
        return numberOfCellsTowerCanAttack(game, path, cell, range);
    }

    private static int numberOfCellsTowerCanAttack(World game, Path path, Cell cell, int range) {
        int result = 0;
        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                int x = cell.getLocation().getX() + i;
                int y = cell.getLocation().getY() + j;
                if (x < 0 || x >= game.getDefenceMap().getWidth()
                        || y < 0 || y >= game.getDefenceMap().getHeight()
                        || Math.abs(x - cell.getLocation().getX()) + Math.abs((y - cell.getLocation().getY())) > range)
                    continue;
                if (game.getDefenceMap().getCell(x, y) instanceof RoadCell)
                    if (path != null) {
                        if (path.getRoad().contains(game.getDefenceMap().getCell(x, y)))
                            result++;
                    } else {
                        result++;
                    }
            }
        }
        return result;
    }

    private static void sortTowersByCoverage(World game, Path path) {
        for (Tower tower : path.towers) {
            tower.compareValue = numberOfCellsTowerCanAttack(game, null, tower, tower.getAttackRange());
        }
        path.towers.sort(Comparator.comparingInt(t -> -1 * t.compareValue));
    }

    public static void defend(World game, HashMap<Integer, ArrayList<Quest>> quests) {
        detectTowersCovering(game);
        for (Path path : game.getDefenceMapPaths()) {
            sortTowersByCoverage(game, path);
        }
        putEnemyWavesInPaths(game);
        for (Path path : game.getDefenceMapPaths()) path.totalDefence = remainingDefence(game, path);
        game.getDefenceMapPaths().sort(Comparator.comparingDouble(p -> p.totalDefence));
        for (Path path : game.getDefenceMapPaths()) {
            if (path.totalDefence < -1) {
                //TODO: change this
                double midLevel = 0;
                for (Tower tower : path.towers) midLevel += tower.getLevel();
                midLevel /= path.towers.size();
                if (path.enemyWaves.get(0).units.get(0).getLevel() > midLevel) {
                    sortTowersByCoverage(game, path);
                    for (int i = 0; i < path.towers.size(); i++) {
                        if (path.towers.get(i).getLevel() - midLevel < 1) {
                            if (!quests.containsKey(game.getCurrentTurn()))
                                quests.put(game.getCurrentTurn(), new ArrayList<>());
                            quests.get(game.getCurrentTurn()).add(0,
                                    new Quest(QuestCommand.UPGRADE_A_TOWER,
                                            new String[]{String.valueOf(path.towers.get(i).getId())}));
                            break;
                        }
                    }
                }
                if (path.totalDefence + 20 * game.getCurrentTurn() / 100 < -1) {
                    // TODO: random between archer and cannon
                    Random random = new Random();
                    int a = random.nextInt(100);
                    if (a < 60) {
                        BuildTowerNextToRoadReturnValue returnValue = buildTowerNextToRoad(game, path, ArcherTower.ATTACK_RANGE);
                        if (!quests.containsKey(game.getCurrentTurn()))
                            quests.put(game.getCurrentTurn(), new ArrayList<>());
                        quests.get(game.getCurrentTurn()).add(0,
                                new Quest(QuestCommand.CREATE_ARCHER_TOWER,
                                        new String[]{String.valueOf((int) Math.pow(game.getCurrentTurn(), 0.23)),
                                                String.valueOf(returnValue.bestPoint.getX()),
                                                String.valueOf(returnValue.bestPoint.getY())}));
                    } else {
                        BuildTowerNextToRoadReturnValue returnValue = buildTowerNextToRoad(game, path, CannonTower.ATTACK_RANGE);
                        if (!quests.containsKey(game.getCurrentTurn()))
                            quests.put(game.getCurrentTurn(), new ArrayList<>());
                        quests.get(game.getCurrentTurn()).add(0,
                                new Quest(QuestCommand.CREATE_CANNON_TOWER,
                                        new String[]{String.valueOf((int) Math.pow(game.getCurrentTurn(), 0.23)),
                                                String.valueOf(returnValue.bestPoint.getX()),
                                                String.valueOf(returnValue.bestPoint.getY())}));
                    }
                }
            }
        }
    }

    private static void putEnemyWavesInPaths(World game) {
        for (Path path : game.getDefenceMapPaths()) {
            ArrayList<EnemyWave> waves = new ArrayList<>();
            int lastI = Integer.MAX_VALUE;
            for (int i = path.getRoad().size() - 1; i >= 0; i--) {
                RoadCell roadCell = path.getRoad().get(i);
                if (!roadCell.getUnits().isEmpty()) {
                    if (lastI - i < GAP) {
                        waves.get(waves.size() - 1).units.addAll(roadCell.getUnits());
                    } else {
                        if (!waves.isEmpty())
                            waves.get(waves.size() - 1).length = waves.get(waves.size() - 1).startingI - i + 1;
                        waves.add(new EnemyWave(i, roadCell.getUnits()));
                    }
                    lastI = i;
                }
            }
            path.enemyWaves = waves;
        }
    }

    private static final int SAFETY = 200;

    private static double remainingDefence(World game, Path path) {
        double totalWavesRemainingHealth = 0;
        for (EnemyWave wave : path.enemyWaves) {
            for (Unit unit : wave.units) {
                wave.totalHealth += Utils.unitHealth(unit);
                wave.speed += unit.getMoveSpeed();
            }
            //wave.totalHealth *= COEFF;
            wave.totalHealth += SAFETY;
            wave.speed /= wave.units.size();
            for (Tower tower : path.towers) {
                wave.totalHealth -= (wave.length + numberOfCellsTowerCanAttack(game, path, tower,
                        tower.getAttackRange())) * wave.speed / tower.getAttackSpeed() * tower.getDamage();
            }
            totalWavesRemainingHealth += wave.totalHealth;
        }
        return -1 * totalWavesRemainingHealth;
    }

    private static void detectTowersCovering(World game) {
        for (Path path : game.getDefenceMapPaths())
            path.towers.clear();
        for (Tower tower : game.getMyTowers()) {
            for (Path path : game.getDefenceMapPaths()) {
                if (numberOfCellsTowerCanAttack(game, path, tower, tower.getAttackRange()) > 0) {
                    path.towers.add(tower);
                }
            }
        }
    }

}
