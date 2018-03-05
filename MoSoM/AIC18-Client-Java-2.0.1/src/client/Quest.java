package client;

/*
 all the quests that have to be done in any turn
 */

import client.model.ArcherTower;
import client.model.Tower;
import client.model.World;

import java.util.ArrayList;

enum QuestCommand {
    CREATE_LIGHT_UNIT,
    CREATE_HEAVY_UNIT,
    CREATE_ARCHER_TOWER,
    CREATE_CANNON_TOWER,
    UPGRADE_A_TOWER,

}

public class Quest {

    public static void handleQuestsATurn(ArrayList<Quest> quests, World game) {
        for (Quest quest : quests) {
            switch (quest.getCommand()) {
                case CREATE_LIGHT_UNIT:
                    game.createLightUnit(Integer.parseInt(quest.getArgs()[0]));
                    System.out.println("created light unit in turn " + game.getCurrentTurn());
                    break;
                case CREATE_HEAVY_UNIT:
                    game.createHeavyUnit(Integer.parseInt(quest.getArgs()[0]));
                    System.out.println("created light unit in turn " + game.getCurrentTurn());
                    break;
                case CREATE_ARCHER_TOWER:
                    game.createArcherTower(Integer.parseInt(quest.getArgs()[0]),
                            Integer.parseInt(quest.getArgs()[1]),
                            Integer.parseInt(quest.getArgs()[2]));
                    System.out.println("built archer tower in turn " + game.getCurrentTurn());
                    break;
                case CREATE_CANNON_TOWER:
                    game.createCannonTower(Integer.parseInt(quest.getArgs()[0]),
                            Integer.parseInt(quest.getArgs()[1]),
                            Integer.parseInt(quest.getArgs()[2]));
                    System.out.println("built cannon tower in turn " + game.getCurrentTurn());
                    break;
                case UPGRADE_A_TOWER:
                    game.upgradeTower(Integer.parseInt(quest.getArgs()[0]));
                    System.out.println("upgraded tower in turn " + game.getCurrentTurn());
                    break;
            }
        }
    }

    private QuestCommand command;
    private String[] args;

    public Quest(QuestCommand command, String[] args) {
        this.command = command;
        this.args = args;
    }

    public QuestCommand getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }

}
