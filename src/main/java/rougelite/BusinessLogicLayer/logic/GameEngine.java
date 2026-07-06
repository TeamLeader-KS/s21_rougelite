package rougelite.BusinessLogicLayer.logic;

import rougelite.BusinessLogicLayer.characters.Enemy;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap;
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel;
import rougelite.BusinessLogicLayer.mapGenerator.LastRoom;
import rougelite.BusinessLogicLayer.message.GameMessages;
import rougelite.BusinessLogicLayer.objects.DarkCrown;
import rougelite.BusinessLogicLayer.objects.GameObject;
import rougelite.PresentationLayer.ui.Messages;

public class GameEngine {

    private final Player player;
    private GameLevel currentLevel;
    private final InventoryEngine inventoryEngine;
    private final PocketEngine pocketEngine;
    private int currentDungeonLevel = Settings.gameLevel;
    private boolean crownPicked = false;

    public GameEngine(Player player, GameLevel startLevel) {
        this.player = player;
        this.currentLevel = startLevel;
        this.inventoryEngine = new InventoryEngine(player);
        this.pocketEngine = new PocketEngine(player);
        this.currentDungeonLevel = startLevel.getDungeonLevel();
    }

    // ==================== ПОРТАЛ ====================

    public String useStoneOnPortal() {
        if (player.getCurrentRoomIndex() != 4) {
            return GameMessages.IMPOSSIBLE_USE_PORTAL_STONE;
        }

        return pocketEngine.useStoneOnPortal(currentLevel.getPortal());
    }

    public boolean canEnterPortal() {
        return pocketEngine.canEnterPortal(
                currentLevel.getPortal(),
                player.getCurrentRoomIndex(),
                player.getLevelX(),
                player.getLevelY()
        );
    }

    // ==================== УРОВНИ ====================

    public GameLevel nextLevel(int windowWidth, int windowHeight) {
        pocketEngine.clearPocket();

        GameLevel newLevel = new GameLevel(currentDungeonLevel);
        newLevel.buildLevel(windowWidth, windowHeight);
        newLevel.generateEnemies(currentDungeonLevel);

        currentDungeonLevel++;
        System.out.println("➡️ Переход на уровень: " + currentDungeonLevel);

        if (currentDungeonLevel == 21) {
            DrawRoomsMap area = newLevel.getRooms().get(0);
            if (area instanceof LastRoom) {
                LastRoom lastRoom = (LastRoom) area;
                int playerX = lastRoom.getPlayerPositionX();
                int playerY = lastRoom.getPlayerPositionY();
                player.setLevelPosition(playerX, playerY);
                player.setRoomPosition(playerX - lastRoom.getX(), playerY - lastRoom.getY());
                player.setCurrentRoomIndex(0);
            }
        } else {
            DrawRoomsMap centerRoom = newLevel.getRooms().get(4);
            int centerX = centerRoom.getX() + centerRoom.getLength() / 2;
            int centerY = centerRoom.getY() + centerRoom.getHeight() / 2;
            player.setLevelPosition(centerX, centerY);
            player.setRoomPosition(centerX - centerRoom.getX(), centerY - centerRoom.getY());
            player.setCurrentRoomIndex(4);
        }



        this.currentLevel = newLevel;
        return newLevel;
    }


    public Player getPlayer() {
        return player;
    }

    public GameLevel getCurrentLevel() {  // ← ИСПРАВЛЕНО: было getCurrentPlayerLevel
        return currentLevel;
    }

    public void setCurrentLevel(GameLevel level) {  // ← ИСПРАВЛЕНО: было setCurrentPlayerLevel
        this.currentLevel = level;
    }

    public InventoryEngine getInventoryEngine() {
        return inventoryEngine;
    }


    public int getCurrentDungeonLevel() {
        return currentDungeonLevel;
    }

    public void addExperienceFromEnemy(Enemy enemy) {
        if (enemy == null) {
            System.out.println("❌ addExperienceFromEnemy: enemy is null!");
            return;
        }

        System.out.println("📊 addExperienceFromEnemy вызван для: " + enemy.getName());
        System.out.println("📊 Текущий опыт игрока ДО: " + player.getExperience());

        int experience = enemy.generatorExperience();
        System.out.println("📊 Получено опыта: " + experience);

        player.addNewLevelForPlayer(experience);

        System.out.println("📊 Текущий опыт игрока ПОСЛЕ: " + player.getExperience());
        Messages.show(GameMessages.EXPERIENCE_UP, 500);
    }

    public void addTreasuresFromEnemy(Enemy enemy)
    {
        if (enemy == null && !enemy.isAlive())
        {
            return;
        }
        int treasures = enemy.generatorTreasure();
        player.getBackpack().addTreasure(treasures);
        Messages.show(GameMessages.TREASURE_UP, 500);
    }

    // TODO метод для короны
    public boolean canPickUpCrown() {
        Player player = getPlayer();
        GameLevel level = getCurrentLevel();

        GameObject item = level.getItemAt(player.getLevelX(), player.getLevelY());
        return item instanceof DarkCrown;
    }

    public void setCurrentDungeonLevel(int level) {
        this.currentDungeonLevel = level;
    }

}