package rougelite.BusinessLogicLayer.characters;

import rougelite.BusinessLogicLayer.characters.behavior.MoveDirection;
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior;
import rougelite.BusinessLogicLayer.inventory.Backpack;
import rougelite.BusinessLogicLayer.logic.MoveLogic;
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap;
import rougelite.BusinessLogicLayer.mapGenerator.Room;
import rougelite.BusinessLogicLayer.message.GameMessages;
import rougelite.BusinessLogicLayer.objects.*;
import rougelite.BusinessLogicLayer.logic.Settings;
import rougelite.PresentationLayer.ui.Messages;


import java.util.Objects;
import java.util.Random;

import static rougelite.BusinessLogicLayer.message.GameMessages.BOSS_SCARES;
import static rougelite.BusinessLogicLayer.message.GameMessages.BOSS_SCARES_YOU;

public class Enemy extends Character {

    private EnemyType type;
    private EnemyBehavior behavior;
    private int level;
    private boolean aggression;
    private static final int DISTANCE_FROM_WALL = 2;
    private MoveDirection facing = MoveDirection.ENEMY_MOVE_LEFT;
    private boolean visible = true;

    public MoveDirection getFacing() {
        return facing;
    }

    public void setFacing(MoveDirection facing) {
        this.facing = facing;
    }


    public enum EnemyType {
        ZOMBIE("Зомби", 15, 5, 5),
        VAMPIRE("Вампир", 100, 10, 35),
        GHOST("Призрак", 50, 15, 15),
        OGRE("Огр", 200, 20, 10),
        SNAKE_MAGE("Змеиный маг", 150, 25, 50),
        BOSS("Владыка Тьмы", 10000, 60, 100);


        private final String russianEnemyName;
        private final int initialHealth;
        private final int initialStrength;
        private final int initialDexterity;


        EnemyType(String russianEnemyName, int initialHealth, int initialStrength, int initialDexterity) {
            this.russianEnemyName = russianEnemyName;
            this.initialHealth = initialHealth;
            this.initialStrength = initialStrength;
            this.initialDexterity = initialDexterity;
        }

        public String getRussianEnemyName() {
            return russianEnemyName;
        }

        public int getinitialHealth() {
            return initialHealth;
        }

        public int getinitialStrength() {
            return initialStrength;
        }

        public int getinitialDexterity() {
            return initialDexterity;
        }
    }

    public Enemy(EnemyType type, int x, int y, int dungeonLevel, EnemyBehavior behavior) // , int multiplierHealth, int multiplierStrength, int multiplierDexterity
    {
        super(
                type.getRussianEnemyName(),
                calculateHealth(type, dungeonLevel),
                calculateStrength(type, dungeonLevel),
                calculateDexterity(type, dungeonLevel));
        this.type = type;
        this.level = dungeonLevel;
        this.behavior = behavior;
        this.setPosition(x, y);
    }

    public static int calculateHealth(EnemyType type, int level) {
        return type.getinitialHealth() + ((level - 1) * Settings.ENEMY_HEALTH_MULTIPLIER);
    }

    public static int calculateStrength(EnemyType type, int level) {
        return type.getinitialStrength() + ((level - 1) * Settings.ENEMY_STRENGTH_MULTIPLIER);
    }

    public static int calculateDexterity(EnemyType type, int level) {
        return type.getinitialDexterity() + ((level - 1) * Settings.ENEMY_DEXTERITY_MULTIPLIER);
    }

    // TODO настроить экспу
    public int generatorExperience() {
        return this.getMaxHealth() * 2 + this.getStrength() * 2 + this.getDexterity() * 2;
    }

    public int generatorTreasure() {
        return this.getMaxHealth() + this.getStrength() + this.getDexterity();
    }


    public void enemyMove(Enemy enemy, DrawRoomsMap room, MoveDirection direction, Player player) {
        Random random = new Random();
        int newX = this.getX();
        int newY = this.getY();

        switch (direction) {
            case ENEMY_MOVE_UP -> newY--;
            case ENEMY_MOVE_DOWN -> newY++;
            case ENEMY_MOVE_LEFT -> newX--;
            case ENEMY_MOVE_RIGHT -> newX++;
            case ENEMY_MOVE_UP_LEFT -> {
                newX--;
                newY--;
            }
            case ENEMY_MOVE_UP_RIGHT -> {
                newX++;
                newY--;
            }
            case ENEMY_MOVE_DOWN_LEFT -> {
                newX--;
                newY++;
            }
            case ENEMY_MOVE_DOWN_RIGHT -> {
                newX++;
                newY++;
            }
            case ENEMY_SKIP_MOVE -> {
                return;
            }
            case ENEMY_MOVE_RANDOM -> {
                newX = room.getX() + DISTANCE_FROM_WALL + random.nextInt(room.getLength() - DISTANCE_FROM_WALL * 2);
                newY = room.getY() + DISTANCE_FROM_WALL + random.nextInt(room.getHeight() - DISTANCE_FROM_WALL * 2);
            }
            case BOSS_SCARES -> {
                String scares = BOSS_SCARES[new Random().nextInt(BOSS_SCARES.length)];
                Messages.show(scares, 1000);
            }
        }

        if (newX == player.getLevelX() && newY == player.getLevelY()) {
            return;
        }

        boolean isInsideRoom =
                (newX >= room.getX() + DISTANCE_FROM_WALL) &&
                        (newX <= room.getX() + room.getLength() - DISTANCE_FROM_WALL) &&
                        (newY >= room.getY() + DISTANCE_FROM_WALL) &&
                        (newY <= room.getY() + room.getHeight() - DISTANCE_FROM_WALL);

        if (isInsideRoom) {
            if (direction != MoveDirection.ENEMY_SKIP_MOVE) {
                this.facing = direction;
            }
            this.setPosition(newX, newY);
        }
    }

    public boolean attackPlayer(Player player) {
        if (!this.isAlive() || !player.isAlive()) return false;

        int dx = Math.abs(this.getX() - player.getLevelX());
        int dy = Math.abs(this.getY() - player.getLevelY());

        if (dx <= 1 && dy <= 1 && (dx + dy > 0)) {
            int damage = this.getStrength();
            player.takeDamage(damage);
            String message = getAttackMessage();
            Messages.show(message, 500);
            System.out.println("💥 " + this.getName() + " атакует! Урон: " + damage);
            return true;
        }
        return false;
    }

    private String getAttackMessage() {
        String message;
        switch (this.type) {
            case ZOMBIE -> message = GameMessages.ZOMBIE_ATTACKED_YOU;
            case VAMPIRE -> message = GameMessages.VAMPIRE_ATTACKED_YOU;
            case GHOST -> message = GameMessages.GHOST_ATTACKED_YOU;
            case OGRE -> message = GameMessages.OGRE_ATTACKED_YOU;
            case SNAKE_MAGE -> message = GameMessages.SNAKE_MAGE_ATTACKED_YOU;
            case BOSS -> message = GameMessages.BOSS_ATTACKED_YOU;
            default -> message = "Враг нанес тебе ранение!";
        }
        return message;
    }

    public EnemyBehavior getBehavior() {
        return behavior;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Enemy enemy = (Enemy) obj;
        return getX() == enemy.getX() && getY() == enemy.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isAggression() {
        return aggression;
    }

    public void setAggression(boolean aggression) {
        this.aggression = aggression;
    }

    public EnemyType getEnemyType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    private boolean playerNoticed = false;

    public boolean isPlayerNoticed() {
        return playerNoticed;
    }

    public void setPlayerNoticed(boolean noticed) {
        this.playerNoticed = noticed;
    }

    public boolean isDisguised() {
        return this.getEnemyType() == EnemyType.BOSS && !this.isVisible();
    }


}