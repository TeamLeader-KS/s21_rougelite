package rougelite.BusinessLogicLayer.characters.behavior;

import rougelite.BusinessLogicLayer.characters.Enemy;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior;
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap;
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel;
import rougelite.BusinessLogicLayer.mapGenerator.Room;
import rougelite.BusinessLogicLayer.message.GameMessages;
import rougelite.BusinessLogicLayer.objects.DarkCrown;
import rougelite.BusinessLogicLayer.objects.GameObject;
import rougelite.PresentationLayer.ui.Messages;

import java.util.Random;

public class BossBehavior implements EnemyBehavior {

    private static final boolean[][] AGRO_RADIUS = {

            {false, true, true, true, true, true, true, true, false},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, false, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {false, true, true, true, true, true, true, true, false}

    };

    private GameLevel currentLevel;
    private boolean bossEnteredBattle = false;

    @Override
    public boolean[][] getAgroRadius() {
        return AGRO_RADIUS;
    }

    @Override
    public void enemyMovePattern(Enemy enemy, DrawRoomsMap room, Player player, GameLevel level) {
        Random random = new Random();
        this.currentLevel = level;

        if (!bossEnteredBattle) {
            enemy.setVisible(false);
        }
        if (this.isPlayerInAgroRadius(enemy, player, room)) {
            if (!enemy.isPlayerNoticed()) {
                enemy.setPlayerNoticed(true);
                Messages.show(GameMessages.BOSS_SCARES_YOU, 500);
                bossEnteredBattle = true;
                enemy.setVisible(true);
            }
            if (attackOnPlayer(enemy, player)) {
                return;
            }
        }

        if (!bossEnteredBattle) {
            MoveDirection[] directions = {
                    MoveDirection.ENEMY_SKIP_MOVE
            };
            MoveDirection direction = directions[random.nextInt(directions.length)];
            enemy.enemyMove(enemy, room, direction, player);
        } else {
            // Случайное движение (если атака не удалась или игрок вне радиуса)
            MoveDirection[] directions = {
                    MoveDirection.ENEMY_MOVE_UP,
                    MoveDirection.ENEMY_MOVE_DOWN,
                    MoveDirection.ENEMY_MOVE_LEFT,
                    MoveDirection.ENEMY_MOVE_RIGHT,
                    MoveDirection.ENEMY_MOVE_UP_LEFT,
                    MoveDirection.ENEMY_MOVE_UP_RIGHT,
                    MoveDirection.ENEMY_MOVE_DOWN_LEFT,
                    MoveDirection.ENEMY_MOVE_DOWN_RIGHT,
                    MoveDirection.ENEMY_MOVE_RANDOM,
                    MoveDirection.BOSS_SCARES
            };
            MoveDirection direction = directions[random.nextInt(directions.length)];
            enemy.enemyMove(enemy, room, direction, player);

        }
        return;
    }

    @Override
    public boolean attackOnPlayer(Enemy enemy, Player player)
    {
        boolean result = enemy.attackPlayer(player);
        if (result)
        {
            bossEnteredBattle = true;
            enemy.setVisible(true);

        }
        return result;
    }

    @Override
    public void receivingDamage(Enemy enemy, int damage) {
        int healthBefore = enemy.getHealth();
        enemy.takeDamage(damage);
        System.out.println("💥 Владыка Тьмы получил урон: " + damage + " (было: " + healthBefore + ", стало: " + enemy.getHealth() + ")");

        bossEnteredBattle = true;
        enemy.setVisible(true);

        if (!enemy.isAlive()) {
            System.out.println("☠️ Владыка Тьмы уничтожен!");
        }
    }

    @Override
    public String pursuit(Enemy enemy, Player player, GameLevel level) {
        // хз
        return null;
    }

    @Override
    public GameObject dropGenerator(Enemy enemyType) {
        System.out.println("dropGenerator: враг на (" + enemyType.getX() + ", " + enemyType.getY() + ")");
        if (!enemyType.isAlive()) {
            DarkCrown.DarkCrownType type = DarkCrown.DarkCrownType.DARK_CROWN;
            DarkCrown darkCrown = new DarkCrown(type);
            darkCrown.setPosition(enemyType.getX(), enemyType.getY());
            System.out.println("Корона создана на (" + darkCrown.getX() + ", " + darkCrown.getY() + ")");
            return darkCrown;
        } else {
            return null;
        }
    }


    // TODO сделать корону но с механикой портала! сообщение ты готов примерить корону правителя?
}