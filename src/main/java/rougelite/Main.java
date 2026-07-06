package rougelite;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.logic.EnterUserName;
import rougelite.BusinessLogicLayer.logic.GameEngine;
import rougelite.BusinessLogicLayer.logic.Settings;
import rougelite.BusinessLogicLayer.logic.WeaponEngine;
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap;
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel;
import rougelite.BusinessLogicLayer.mapGenerator.LastRoom;
import rougelite.BusinessLogicLayer.objects.Portal;
import rougelite.DataAccessLayer.SaveLoadManager;
import rougelite.DataAccessLayer.load.LoadGame;
import rougelite.DataAccessLayer.save.SaveGame;
import rougelite.PresentationLayer.controllers.GameController;
import rougelite.PresentationLayer.mapView.GameWindow;
import rougelite.PresentationLayer.ui.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));

        DefaultTerminalFactory factory = new DefaultTerminalFactory();

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            factory.setInitialTerminalSize(new TerminalSize(240, 60));
        }

        Terminal terminal = factory.createTerminal();

        if (terminal instanceof SwingTerminalFrame) {
            SwingTerminalFrame frame = (SwingTerminalFrame) terminal;
            frame.setSize(1600, 900);
            frame.setLocationRelativeTo(null);
            Thread.sleep(200);
        }

        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        GameWindow gameWindow = new GameWindow(terminal);

        if (os.contains("mac")) {
            Thread.sleep(300);
            gameWindow.updateSize(terminal);
        }

        System.out.println("=== MAIN START ===");
        System.out.println("Окно: " + gameWindow.getLength() + " x " + gameWindow.getHeight());

        boolean restart = false;
        do {
            Player player;
            GameLevel gameLevel;
            GameEngine gameEngine;
            WeaponEngine weaponEngine;
            GameController controller;
            int dungeonLevel;
            List<Leaderboard.LeaderboardEntry> recording = new ArrayList<>();

            if (SaveLoadManager.hasSave()) {
                System.out.println("📂 Загружаем сохранение...");

                try {
                    SaveGame save = SaveLoadManager.loadGame();
                    dungeonLevel = save.dungeonLevel;

                    player = LoadGame.restorePlayer(save.player);
                    System.out.println("✅ Игрок восстановлен");

                    if (save.hands != null) {
                        LoadGame.restoreHands(player, save.hands);
                    }

                    // 2. Восстанавливаем инвентарь
                    LoadGame.restoreInventory(player, save.inventory);
                    System.out.println("✅ Инвентарь восстановлен");

                    Portal restoredPortal = null;
                    if (save.portal != null) {
                        restoredPortal = LoadGame.restorePortal(save.portal);
                        System.out.println("✅ Портал восстановлен");
                    }

                    gameLevel = LoadGame.restoreLevel(
                            save.level,
                            restoredPortal,
                            gameWindow.getLength(),
                            gameWindow.getHeight()
                    );

                    if (gameLevel == null) {
                        System.out.println("❌ Ошибка: уровень не восстановлен! Создаём новый...");
                        gameLevel = new GameLevel(dungeonLevel);
                        gameLevel.buildLevel(gameWindow.getLength(), gameWindow.getHeight());
                        gameLevel.generateEnemies(dungeonLevel);
                    }

                    if (save.pocket != null) {
                        player.setPocket(LoadGame.restorePocket(save.pocket, gameLevel));
                    }

                    if (save.player.position != null) {
                        player.setLevelPosition(save.player.position.x, save.player.position.y);
                        player.setRoomPosition(save.player.position.x, save.player.position.y);
                    }

                    gameEngine = new GameEngine(player, gameLevel);
                    gameEngine.setCurrentDungeonLevel(dungeonLevel);
                    weaponEngine = new WeaponEngine(player, gameLevel, gameEngine);
                    weaponEngine.setCurrentLevel(gameLevel);

                    recording = SaveLoadManager.loadLeaderboard();
                    System.out.println("✅ Таблица лидеров загружена!" + recording);

                    System.out.println("✅ Игра загружена! Уровень: " + dungeonLevel);
                    System.out.println("DEBUG: Установлен уровень подземелья = " + gameEngine.getCurrentDungeonLevel());


                } catch (Exception e) {
                    System.err.println("❌ Ошибка загрузки: " + e.getMessage());
                    e.printStackTrace();
                    SaveLoadManager.deleteSave();
                    recording = new ArrayList<>();

                    // Создаём новую игру...
                    continue;
                }

            } else {
                System.out.println("🆕 Новая игра");
                recording = SaveLoadManager.loadLeaderboard();

                PrologueWindow.draw(screen, gameWindow.getLength(), gameWindow.getHeight());
                screen.readInput();

                StartWindow.draw(screen, gameWindow.getLength(), gameWindow.getHeight());
                String userName = StartWindow.getEnteredName();
                System.out.println("Имя игрока: " + userName);

                EnterUserName.setUserName(userName);

                if (userName == null || userName.isEmpty())
                {
                    userName = "Unknown Hero";
                }

                dungeonLevel = Settings.gameLevel;
                gameLevel = new GameLevel(dungeonLevel);
                gameLevel.buildLevel(gameWindow.getLength(), gameWindow.getHeight());
                gameLevel.generateEnemies(dungeonLevel);

                // Базовые статы игрока
                player = new Player(userName, 100, 10, 10);

                DrawRoomsMap centerArea;
                if (gameLevel.getDungeonLevel() == 21) {
                    centerArea = gameLevel.getRooms().get(0);
                    if (centerArea instanceof LastRoom) {
                        LastRoom lastRoom = (LastRoom) centerArea;
                        player.setLevelPosition(lastRoom.getPlayerPositionX(), lastRoom.getPlayerPositionY());
                    }
                } else {
                    centerArea = gameLevel.getRooms().get(4);
                    int centerX = centerArea.getX() + centerArea.getLength() / 2;
                    int centerY = centerArea.getY() + centerArea.getHeight() / 2;
                    player.setLevelPosition(centerX, centerY);
                }
                player.setRoomPosition(player.getLevelX(), player.getLevelY());
                player.setCurrentRoomIndex(4);

                gameEngine = new GameEngine(player, gameLevel);
                weaponEngine = new WeaponEngine(player, gameLevel, gameEngine);
            }

            // пересборка контроллера
            controller = new GameController(screen, gameWindow, player, gameLevel, weaponEngine, gameEngine, recording);
            controller.start();
            restart = controller.isRestartGame();

        } while (restart);

        try {
            screen.close();
        } catch (Exception e) {
            System.err.println("Ошибка при закрытии экрана: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Выход из игры...");
        System.exit(0);
    }
}