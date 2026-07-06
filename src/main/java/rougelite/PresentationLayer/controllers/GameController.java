package rougelite.PresentationLayer.controllers;

import com.googlecode.lanterna.screen.Screen;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.characters.behavior.ZombieBehavior;
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior;
import rougelite.BusinessLogicLayer.inventory.HiddenBackpack;
import rougelite.BusinessLogicLayer.logic.*;
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap;
import rougelite.BusinessLogicLayer.objects.DarkCrown;
import rougelite.BusinessLogicLayer.objects.GameObject;
import rougelite.BusinessLogicLayer.objects.Portal;
import rougelite.BusinessLogicLayer.objects.PortalStone;
import rougelite.BusinessLogicLayer.message.GameMessages;
import rougelite.DataAccessLayer.SaveLoadManager;
import rougelite.PresentationLayer.input.InputControl;
import rougelite.PresentationLayer.input.PlayerCommands;
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel;
import rougelite.PresentationLayer.mapView.GameWindow;
import rougelite.BusinessLogicLayer.mapGenerator.Room;
import rougelite.PresentationLayer.ui.*;
import rougelite.PresentationLayer.ui.Messages;
import rougelite.BusinessLogicLayer.logic.WeaponEngine;
import rougelite.PresentationLayer.ui.Information;
import rougelite.BusinessLogicLayer.characters.Enemy;  // ← ДОБАВИТЬ!
import rougelite.PresentationLayer.ui.ExitWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    private final InputControl inputControl;
    private final GameEngine gameEngine;
    private final InventoryEngine inventoryEngine;
    private final PocketEngine pocketEngine;
    private final MoveLogic moveLogic;
    private final GameRender renderer;
    private final GameWindow gameWindow;
    private final Screen screen;
    private final WeaponEngine weaponEngine;
    private final HandsEngine handsEngine;

    private boolean running;
    private boolean inventoryOpen = false;
    private boolean pocketOpen = false;
    private boolean exitMenuOpen = false;
    private boolean restartGame = false;
    private boolean crownPicked = false;

    private List<Leaderboard.LeaderboardEntry> recording = new ArrayList<>();




    public GameController(Screen screen, GameWindow gameWindow, Player player, GameLevel gameLevel, WeaponEngine weaponEngine, GameEngine gameEngine, List<Leaderboard.LeaderboardEntry> leadRecords) {
        this.screen = screen;
        this.gameWindow = gameWindow;
        this.inputControl = new InputControl();
        this.gameEngine = gameEngine;
        this.inventoryEngine = new InventoryEngine(player);
        this.pocketEngine = new PocketEngine(player);
        this.moveLogic = new MoveLogic();
        this.renderer = new GameRender(screen, gameWindow, gameEngine, weaponEngine);
        this.running = true;
        this.weaponEngine = weaponEngine;
        this.handsEngine = new HandsEngine(player);
        this.recording = leadRecords != null ? leadRecords : new ArrayList<>();
    }

    public void start() throws Exception {
        while (running) {

            weaponEngine.update();

            updateEnemies();

            // ===== ПРОВЕРКА СМЕРТИ ИГРОКА =====
            if (!gameEngine.getPlayer().isAlive()) {
                Player player = gameEngine.getPlayer();

                System.out.println("💀 Игрок умер! Создаём запись в таблицу лидеров...");
                System.out.println("   Имя: " + player.getName());
                System.out.println("   Уровень: " + player.getCurrentPlayerLevel());
                System.out.println("   Сокровища: " + player.getBackpack().getTreasure());

                Leaderboard.LeaderboardEntry entry = new Leaderboard.LeaderboardEntry
                        (
                                player.getName(),
                                player.getCurrentPlayerLevel(),
                                player.getBackpack().getTreasure()
                        );
                        recording.add(entry);

                System.out.println("✅ Запись добавлена! Всего записей: " + recording.size());
                for (Leaderboard.LeaderboardEntry e : recording) {
                    System.out.println("   📝 " + e.getPlayerName() + " | Уровень: " + e.getLevel() + " | Сокровища: " + e.getTreasure());
                }

                SaveLoadManager.saveLeaderboard(recording);
                System.out.println("💾 Таблица лидеров сохранена в файл!");

                SaveLoadManager.deleteSave();

                GameOverWindow.draw(screen, gameWindow.getLength(), gameWindow.getHeight());
                screen.refresh();
                screen.readInput();
                screen.readInput();
                screen.readInput();

                running = false;
                break;
            }


            // Отрисовка
            renderer.render(gameEngine.getCurrentLevel(), gameEngine.getPlayer());

            String msg = Messages.getCurrent();
            if (msg != null) {
                MessageOutput.showGameMessage(screen, msg, gameWindow.getLength());
            }

            PlayerCommands cmd = inputControl.readInput(screen);

            switch (cmd) {
                // ==================== ДВИЖЕНИЕ ====================
                case MOVE_UP:
                case MOVE_DOWN:
                case MOVE_LEFT:
                case MOVE_RIGHT:
                    handleMovement(cmd);
                    break;

                // ==================== ИНВЕНТАРЬ ====================
                case OPEN_INVENTORY:
                case OPEN_WEAPON:
                case OPEN_AMMO:
                case OPEN_POTION:
                case OPEN_REINFORCE:
                    selectInventory(cmd);
                    break;

                // ==================== КАРМАН ====================
                case OPEN_POCKET:
                    selectPocket();
                    break;

                // ==================== ИСПОЛЬЗОВАНИЕ ПРЕДМЕТОВ ====================
                case USE_ITEM_1:
                case USE_ITEM_2:
                case USE_ITEM_3:
                case USE_ITEM_4:
                case USE_ITEM_5:
                case USE_ITEM_6:
                case USE_ITEM_7:
                case USE_ITEM_8:
                case USE_ITEM_9:
                    int slot = cmd.ordinal() - PlayerCommands.USE_ITEM_1.ordinal();
                    useItemFromCurrentTab(slot);
                    renderer.render(gameEngine.getCurrentLevel(), gameEngine.getPlayer());
                    break;

                // ==================== КАМЕНЬ ПОРТАЛА ====================
                case USE_STONE:
                    String result = gameEngine.useStoneOnPortal();
                    if (result != null) {
                        Messages.show(result, 1000);
                    }
                    break;

                // ==================== ВХОД В ПОРТАЛ ====================
                case USE_PORTAL:
                    if (gameEngine.canEnterPortal()) {
                        nextLevel();
                    }
                    break;

                // ============ ДОСТАТЬ / УБРАТЬ ОРУЖИЕ ===============
                case MOVE_EQUIP_UNEQUIP_WEAPON:
                    String message = inventoryEngine.equipUnequipWeapon();
                    if (message != null)
                    {
                        Messages.show(message, 500);
                    }
                    break;

                // ============ ДОСТАТЬ / УБРАТЬ РУКИ ===========
                case MOVE_EQUIP_UNEQUIP_HANDS:

                    String handsMsg = handsEngine.switchHands();
                    if (handsMsg != null) {
                        Messages.show(handsMsg, 500);
                    }
                    break;

                // ============ СТРЕЛЬБА ===========
                case MOVE_FIRE:
                    weaponEngine.shoot();
                    break;

                // ============ ИНФОРМАЦИЯ =============
                case OPEN_INFORMATION:
                    Information.draw(screen, gameWindow.getLength(), gameWindow.getHeight());
                    while (true)
                    {
                        PlayerCommands infoCmd = inputControl.readInput(screen);
                        if (infoCmd == PlayerCommands.OPEN_INFORMATION)
                        {
                            break;
                        }
                    }
                    break;

                // ==================== МЕНЮ ВЫХОДА ====================
                case OPEN_GAME_MENU:
                    selectMenu();
                    break;

                case OPEN_LEADER_BOARD:
                    System.out.println("📊 Открываем таблицу лидеров. Записей: " + recording.size());
                    System.out.println("📊 Список записей: " + recording); //
                    Leaderboard.draw(screen, recording, gameWindow.getLength(), gameWindow.getHeight());
                    while (true)
                    {
                        PlayerCommands leaderCmd = inputControl.readInput(screen);
                        if (leaderCmd == PlayerCommands.OPEN_LEADER_BOARD)
                        {
                            break;
                        }
                    }
                    break;

                case USE_USER_ENTER:
                    if (crownPicked) {
                        VictoryWindow.draw(screen, gameWindow.getLength(), gameWindow.getHeight());
                        screen.refresh();
                        running = false;
                        return;
                    }

                    break;

                default:
                    break;
            }
        }
    }

    // ==================== ОБРАБОТКА ДВИЖЕНИЯ ====================
    private void handleMovement(PlayerCommands cmd) {
        MoveLogic.Direction direction = null;
        switch (cmd) {
            case MOVE_UP:
                direction = MoveLogic.Direction.UP;
                break;
            case MOVE_DOWN:
                direction = MoveLogic.Direction.DOWN;
                break;
            case MOVE_LEFT:
                direction = MoveLogic.Direction.LEFT;
                weaponEngine.updateDirection(direction);
                break;
            case MOVE_RIGHT:
                direction = MoveLogic.Direction.RIGHT;
                weaponEngine.updateDirection(direction);
                break;
        }

    if (direction == null) return;

    MoveLogic.MoveResult moveResult = moveLogic.tryMove(
            gameEngine.getPlayer(), direction, gameEngine.getCurrentLevel());

        if (!moveResult.isSuccess()) return;

        // ===== ПРОВЕРКА ПОБЕДЫ ИГРОКА =====

        if (moveResult.getPickedItem() != null) {
        // И если этот предмет - ТЕМНАЯ КОРОНА
        if (moveResult.getPickedItem() instanceof DarkCrown) {

            // ДОБАВЛЯЕМ ЗАПИСЬ В ТАБЛИЦУ ЛИДЕРОВ ПРИ ПОБЕДЕ
            Player player = gameEngine.getPlayer();
            System.out.println("👑 Победа! Запись в таблицу лидеров...");
            System.out.println("   Имя: " + player.getName());
            System.out.println("   Уровень: " + player.getCurrentPlayerLevel());
            System.out.println("   Сокровища: " + player.getBackpack().getTreasure());

            Leaderboard.LeaderboardEntry entry = new Leaderboard.LeaderboardEntry(
                    player.getName(),
                    player.getCurrentPlayerLevel(),
                    player.getBackpack().getTreasure()
            );
            recording.add(entry);
            SaveLoadManager.saveLeaderboard(recording);
            System.out.println("✅ Запись добавлена! Всего записей: " + recording.size());

            // 1. Покажи сообщение о находке
            Messages.show(GameMessages.PICKUP_CROWN, 2000);

            try {

                SaveLoadManager.deleteSave();
                VictoryWindow.draw(screen, gameWindow.getLength(), gameWindow.getHeight());
                inputControl.readInput(screen);
                inputControl.readInput(screen);
                inputControl.readInput(screen);
                screen.refresh();

                running = false;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        InventoryEngine.AddResult result = inventoryEngine.addItem(moveResult.getPickedItem());
        Messages.show(result.message, 1000);
        if (result.success) {
            gameEngine.getCurrentLevel().removeItem(moveResult.getPickedItem());
        }
    }

        if (moveResult.getPickedStone() != null) {
        String msg = pocketEngine.addStone(moveResult.getPickedStone());
        if (msg != null) {
            Messages.show(msg, 1000);
        }
    }

        if (gameEngine.canEnterPortal()) {
        Messages.show(GameMessages.ENTER_PORTAL, 1000);
    }

        weaponEngine.clearRays();
        weaponEngine.clearWaves();
}

    // ==================== МЕНЮ ВЫХОДА ====================
    private void selectMenu() {
        try {
            if (exitMenuOpen) {
                exitMenuOpen = false;
                renderer.render(gameEngine.getCurrentLevel(), gameEngine.getPlayer());
                return;
            }

            exitMenuOpen = true;
            ExitWindow.setDefaultSelection();

            while (exitMenuOpen)
            {
                ExitWindow.draw(screen, gameWindow.getLength(), gameWindow.getHeight());

                PlayerCommands menuCmd = inputControl.readInput(screen);

                switch (menuCmd)
                {
                    case MOVE_UP:
                        ExitWindow.prevItem();
                        break;
                    case MOVE_DOWN:
                        ExitWindow.nextItem();
                        break;
                    case USE_USER_ENTER:
                        int choice = ExitWindow.getSelectedIndex();
                        exitMenuOpen = false;

                        switch (choice)
                        {
                            case 0:
                                SaveLoadManager.deleteSave();
                                restartGame = true;
                                running = false;
                                break;
                            case 1:
                                SaveLoadManager.saveGame(gameEngine, gameEngine.getPlayer(), gameEngine.getCurrentLevel());
                                System.out.println("💾 Сохраняем игру, уровень: " + gameEngine.getCurrentDungeonLevel());
                                running = false;
                                break;
                            case 2:
                                List<Leaderboard.LeaderboardEntry> entries = new ArrayList<>();
                                Leaderboard.draw(screen, recording, gameWindow.getLength(), gameWindow.getHeight());
                                while (true) {
                                    PlayerCommands leaderCmd = inputControl.readInput(screen);
                                    if (leaderCmd == PlayerCommands.OPEN_GAME_MENU) {
                                        break;
                                    }
                                }
                                break;

                        }
                        break;

                    case OPEN_GAME_MENU:
                        exitMenuOpen = false;
                        break;
                    default:
                        break;
                }
            }

            renderer.render(gameEngine.getCurrentLevel(), gameEngine.getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== ИНВЕНТАРЬ ====================
    private void selectInventory(PlayerCommands cmd) {
        try {
            if (inventoryOpen) {
                inventoryOpen = false;
                renderer.render(gameEngine.getCurrentLevel(), gameEngine.getPlayer());
                return;
            }

            inventoryOpen = true;

            switch (cmd) {
                case OPEN_WEAPON:
                    BackpackWindow.setTab(BackpackWindow.Tab.WEAPON);
                    break;
                case OPEN_AMMO:
                    BackpackWindow.setTab(BackpackWindow.Tab.AMMO);
                    break;
                case OPEN_POTION:
                    BackpackWindow.setTab(BackpackWindow.Tab.POTION);
                    break;
                case OPEN_REINFORCE:
                    BackpackWindow.setTab(BackpackWindow.Tab.REINFORCE);
                    break;
                default:
                    break;
            }

            while (inventoryOpen) {
                BackpackWindow.draw(screen, inventoryEngine.getBackpack(),
                        gameWindow.getLength(), gameWindow.getHeight());

                PlayerCommands inventoryCmd = inputControl.readInput(screen);
                switch (inventoryCmd) {
                    case OPEN_INVENTORY:
                    case OPEN_WEAPON:
                    case OPEN_AMMO:
                    case OPEN_POTION:
                    case OPEN_REINFORCE:
                    case EXIT:
                        inventoryOpen = false;
                        break;
                    case MOVE_LEFT:
                        BackpackWindow.prevTab();
                        break;
                    case MOVE_RIGHT:
                        BackpackWindow.nextTab();
                        break;
                    default:
                        if (inventoryCmd.ordinal() >= PlayerCommands.USE_ITEM_1.ordinal() &&
                                inventoryCmd.ordinal() <= PlayerCommands.USE_ITEM_9.ordinal()) {
                            int idx = inventoryCmd.ordinal() - PlayerCommands.USE_ITEM_1.ordinal();
                            useItemFromCurrentTab(idx);

                            inventoryOpen = false;
                        }
                        break;
                }
            }
            renderer.render(gameEngine.getCurrentLevel(), gameEngine.getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== КАРМАН ====================
    private void selectPocket() {
        try {
            if (pocketOpen) {
                pocketOpen = false;
                renderer.render(gameEngine.getCurrentLevel(), gameEngine.getPlayer());
                return;
            }

            pocketOpen = true;
            while (pocketOpen) {
                PocketWindow.draw(screen, pocketEngine.getPocket(),
                        gameWindow.getLength(), gameWindow.getHeight());

                PlayerCommands pocketCmd = inputControl.readInput(screen);
                switch (pocketCmd) {
                    case OPEN_POCKET:
                    case EXIT:
                        pocketOpen = false;
                        break;
                    default:
                        break;
                }
            }
            renderer.render(gameEngine.getCurrentLevel(), gameEngine.getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== ИСПОЛЬЗОВАНИЕ ПРЕДМЕТОВ ====================
    private void useItemFromCurrentTab(int slot) {
        String msg = null;

        switch (BackpackWindow.getCurrentTab()) {
            case WEAPON:
                msg = inventoryEngine.useWeapon(slot);
                break;
            case AMMO:
                msg = inventoryEngine.useAmmo(slot);
                break;
            case POTION:
                msg = inventoryEngine.useHealthPotion(slot);
                break;
            case REINFORCE:
                msg = "Этот предмет даёт пассивный бонус";
                break;
        }

        if (msg != null) {
            Messages.show(msg, 1000);
        }
    }

    // ==================== СЛЕДУЮЩИЙ УРОВЕНЬ ====================
    private void nextLevel() {
        try {
            GameLevel newLevel = gameEngine.nextLevel(gameWindow.getLength(), gameWindow.getHeight());
            newLevel.clearVisibility();
            weaponEngine.setCurrentLevel(newLevel);
            renderer.render(newLevel, gameEngine.getPlayer());
            Messages.show(GameMessages.PORTAL_NEXT_LEVEL, 3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateEnemies() {
        GameLevel level = gameEngine.getCurrentLevel();
        Player player = gameEngine.getPlayer();

        for (Enemy enemy : level.getEnemies()) {
            if (enemy.isAlive()) {
                DrawRoomsMap room = level.getRoomAt(enemy.getY(), enemy.getX());

                if (room != null) {
                    EnemyBehavior behavior = enemy.getBehavior();

                    if (behavior != null) {
                        behavior.enemyMovePattern(enemy, room, player, level);
                    }
                }
            }
        }
    }

    public boolean isRestartGame() {
        return restartGame;
    }
}