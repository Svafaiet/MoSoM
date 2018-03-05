package client;

import client.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class AI {

    private boolean firstRun = true;
    private HashMap<Integer, ArrayList<Quest>> quests = new HashMap<>();

    private void init(World game) {
        firstRun = false;
    }

    private int hugeWaveTurns;

    void simpleTurn(World game) {
        long start = System.currentTimeMillis();
        if (firstRun) {
            init(game);
        } else if (game.getCurrentTurn() < 650) {
            Defence.defend(game, quests);
            SendWaveReturnValue sendWaveReturnValue = Attack.sendWave(game, turnEndCurrentWave, quests, Attack.incomeWave(game), true);
            turnEndCurrentWave = sendWaveReturnValue.endTurnOfWave;
        } else if (game.getCurrentTurn() < 800) {
            Defence.defend(game, quests);
        } else if (game.getCurrentTurn() == 800) {
            Wave huge = Attack.hugeWave(game, true);
            hugeWaveTurns = huge.getLength() * huge.getMoveSpeed();
            SendWaveReturnValue sendWaveReturnValue = Attack.sendWave(game, turnEndCurrentWave, quests, huge, true);
            turnEndCurrentWave = sendWaveReturnValue.endTurnOfWave;
        } else if (game.getCurrentTurn() > 800 + hugeWaveTurns) {
            Defence.defend(game, quests);
        }
        if (quests.containsKey(game.getCurrentTurn()))
            Quest.handleQuestsATurn(quests.get(game.getCurrentTurn()), game);
        long end = System.currentTimeMillis();
        if ((end - start) >= 200)
            System.err.println("fuck! " + game.getCurrentTurn());
    }

    private int turnEndCurrentWave = -1;

    void complexTurn(World game) {
        simpleTurn(game);
    }

    /*

    private void randomTurn(World game) {
        int choice = rnd.nextInt() % 7;
        if (choice < 4) choice = 1;
        else choice = 2;
        Point p;
        switch (choice) {
            case 0:
                game.createLightUnit(rnd.nextInt(game.getAttackMapPaths().size()));
                break;
            case 1:
                game.createHeavyUnit(rnd.nextInt(game.getAttackMapPaths().size()));
                break;
            case 2:
                p = Utils.createTowerAtBestPosition(game, ArcherTower.ATTACK_RANGE);
                if (p != null)
                    game.createArcherTower(0, p.getX(), p.getY());
                break;
            case 3:
                p = Utils.createTowerAtBestPosition(game, CannonTower.ATTACK_RANGE);
                if (p != null)
                    game.createCannonTower(0, p.getX(), p.getY());
                break;
            case 4:
                game.plantBean(rnd.nextInt(game.getDefenceMap().getWidth()), rnd.nextInt(game.getDefenceMap().getHeight()));
                break;
            case 5:
                game.createStorm(rnd.nextInt(game.getDefenceMap().getWidth()), rnd.nextInt(game.getDefenceMap().getHeight()));
                break;
            case 6:
                game.upgradeTower(rnd.nextInt(game.getMyTowers().size()));
            default:
                break;
        }
    }

    */
}
