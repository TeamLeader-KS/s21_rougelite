package rougelite.PresentationLayer.ui;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.logic.GameEngine;
import rougelite.BusinessLogicLayer.mapGenerator.*;
import rougelite.BusinessLogicLayer.objects.*;
import rougelite.PresentationLayer.mapView.GameWindow;
import rougelite.BusinessLogicLayer.logic.WeaponEngine;

import java.io.IOException;
import java.util.List;

public class GameRender {
    private Screen screen;
    private GameWindow gameWindow;
    private GameEngine gameEngine;
    private WeaponEngine weaponEngine;

    public GameRender(Screen screen, GameWindow gameWindow, GameEngine gameEngine, WeaponEngine weaponEngine) {
        this.screen = screen;
        this.gameWindow = gameWindow;
        this.gameEngine = gameEngine;
        this.weaponEngine = weaponEngine;
    }

    public void render(GameLevel level, Player player) throws IOException {
        try {
            screen.clear();

            int px = player.getLevelX();
            int py = player.getLevelY();

            level.openCellsInRadius(px, py, 2);

            int currentRoom = -1;

            List<DrawRoomsMap> roomsList = level.getRooms();
            for (int i = 0; i < roomsList.size(); i++) {
                DrawRoomsMap area = roomsList.get(i);   // ← без (Room)
                if (px >= area.getX() && px < area.getX() + area.getLength() &&
                        py >= area.getY() && py < area.getY() + area.getHeight()) {
                    currentRoom = i;
                    break;
                }
            }

            if (currentRoom != -1) {
                level.openRoom(currentRoom);
            }

            for (DrawRoomsMap room : level.getRooms()) {
                if (level.getDungeonLevel() == 21)
                {
                    drawLastRoomWithFog(room, level);
                } else {
                    drawRoomWithFog(room, level);
                }

            }

            drawCorridorsWithFog(level);

            Portal portal = level.getPortal();
            if (portal != null && level.isExplored(portal.getX(), portal.getY())) {
                drawPortal(portal);
            }

            drawPortalStonesWithFog(level);
            drawWeaponWithFog(level);
            drawAmmoWithFog(level);
            drawHealthPotionWithFog(level);
            drawReinforceWithFog(level);
            drawCrownWithFog(level);

            EnemyRender.drawEnemies(screen, level);

            WeaponRender.drawPlayer(screen, player);
            WeaponRender.drawProjectiles(screen, weaponEngine);

            WeaponRender.drawRays(screen, weaponEngine);

            WeaponRender.drawWaves(screen, weaponEngine, level, player);

            ShowStatistic.draw(screen, player, gameEngine, gameWindow.getLength(), gameWindow.getHeight());

            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawRoomWithFog(DrawRoomsMap area, GameLevel level) {
        int startX = area.getX();
        int startY = area.getY();
        int[][] tiles = area.getTiles();
        int height = area.getHeight();
        int length = area.getLength();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                int screenX = startX + j;
                int screenY = startY + i;
                if (screenX >= 0 && screenX < gameWindow.getLength() &&
                        screenY >= 0 && screenY < gameWindow.getHeight()) {
                    if (level.isExplored(screenX, screenY)) {
                        drawRoomTile(area, i, j, screenX, screenY);
                    } else {
                        screen.setCharacter(screenX, screenY,
                                new TextCharacter(' ', TextColor.ANSI.BLACK, TextColor.ANSI.BLACK));
                    }
                }
            }
        }
    }

    private void drawLastRoomWithFog(DrawRoomsMap area, GameLevel level) {
        int startX = area.getX();
        int startY = area.getY();
        int[][] tiles = area.getTiles();
        int height = area.getHeight();
        int length = area.getLength();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                int screenX = startX + j;
                int screenY = startY + i;
                if (screenX >= 0 && screenX < gameWindow.getLength() &&
                        screenY >= 0 && screenY < gameWindow.getHeight()) {
                    drawRoomTile(area, i, j, screenX, screenY);
                }
            }
        }
    }

    private void drawRoomTile(DrawRoomsMap room, int row, int col, int screenX, int screenY) {
        char symbol = ' ';
        TextColor color = TextColor.ANSI.WHITE;
        int tile = room.getTiles()[row][col];
        int length = room.getLength();
        int height = room.getHeight();

        switch (tile) {
            case 1: // Стена
                if (row == 0 && col == 0) {
                    symbol = '╔';
                } else if (row == 0 && col == length - 1) {
                    symbol = '╗';
                } else if (row == height - 1 && col == 0) {
                    symbol = '╚';
                } else if (row == height - 1 && col == length - 1) {
                    symbol = '╝';
                } else if (row == 0 || row == height - 1) {
                    symbol = '═';
                } else if (col == 0 || col == length - 1) {
                    symbol = '║';
                } else {
                    symbol = '#';
                }
                color = TextColor.ANSI.YELLOW;
                break;

            case 2: // Дверь
                symbol = 'D';
                color = TextColor.ANSI.GREEN;
                break;

            case 0: // Пол внутри комнаты
                symbol = '·';
                color = TextColor.ANSI.CYAN;
                break;

            default: // Пустота
                symbol = ' ';
                color = TextColor.ANSI.BLACK;
        }

        screen.setCharacter(screenX, screenY,
                new TextCharacter(symbol, color, TextColor.ANSI.BLACK));
    }

    private void drawCorridorsWithFog(GameLevel level) {
        for (Corridor corridor : level.getCorridors()) {
            for (int[] point : corridor.getPath()) {
                int x = point[0];
                int y = point[1];

                if (x >= 0 && x < gameWindow.getLength() &&
                        y >= 0 && y < gameWindow.getHeight()) {

                    if (level.isExplored(x, y)) {
                        boolean isDoor = false;
                        for (DoorCoordinates.Door door : DoorCoordinates.getDoors()) {
                            if (door.getX() == x && door.getY() == y) {
                                isDoor = true;
                                break;
                            }
                        }

                        if (!isDoor) {
                            screen.setCharacter(x, y,
                                    new TextCharacter('░', TextColor.ANSI.CYAN, TextColor.ANSI.BLACK));
                        }
                    } else {
                        // Туман войны
                        screen.setCharacter(x, y,
                                new TextCharacter(' ', TextColor.ANSI.BLACK, TextColor.ANSI.BLACK));
                    }
                }
            }
        }
    }

    private void drawPortal(Portal portal) {
        int centerX = portal.getX();
        int centerY = portal.getY();
        int startX = centerX - 1;
        int startY = centerY - 1;

        int[][] mapping = {
                {0, 1, 2},
                {3, -1, 5},
                {6, 7, 8}
        };

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int roomIndex = mapping[row][col];
                char symbol;
                TextColor color;

                if (roomIndex == -1) {
                    if (portal.isOpen())
                    {
                        symbol = '░';
                        color = TextColor.ANSI.BLUE_BRIGHT;
                    } else {
                        symbol = ' ';
                        color = TextColor.ANSI.RED;
                    }
                } else if (portal.hasStone(roomIndex)) {
                    symbol = '●';
                    color = TextColor.ANSI.BLUE_BRIGHT;
                } else {
                    symbol = '○';
                    color = TextColor.ANSI.RED;
                }

                int screenX = startX + col;
                int screenY = startY + row;

                if (screenX >= 0 && screenX < gameWindow.getLength() &&
                        screenY >= 0 && screenY < gameWindow.getHeight()) {
                    screen.setCharacter(screenX, screenY,
                            new TextCharacter(symbol, color, TextColor.ANSI.BLACK));
                }
            }
        }
    }

    private void drawPortalStonesWithFog(GameLevel level) {
        List<DrawRoomsMap> roomsList = level.getRooms();
        for (DrawRoomsMap drawRoom : roomsList) {
            PortalStone stone = drawRoom.getPortalStone();
            if (stone == null) continue;
            int stoneX = stone.getStoneX();
            int stoneY = stone.getStoneY();
            if (level.isExplored(stoneX, stoneY)) {
                screen.setCharacter(stoneX, stoneY,
                        new TextCharacter('●', TextColor.ANSI.BLUE_BRIGHT, TextColor.ANSI.BLACK));
            }
        }
    }

    public static void showMessage(Screen screen, String message, int terminalWidth) throws IOException {
        int x = (terminalWidth - message.length()) / 2;
        int y = 2;

        for (int i = 0; i < message.length(); i++) {
            screen.setCharacter(x + i, y,
                    new TextCharacter(message.charAt(i), TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.BLACK));
        }
    }


    private void drawWeaponWithFog(GameLevel level) {
        for (GameObject item : level.getItems()) {
            if (item instanceof Weapon && level.isExplored(item.getX(), item.getY())) {
                Weapon weapon = (Weapon) item;
                char symbol;
                TextColor color;

                switch (weapon.getType()) {
                    case KNIFE:
                        symbol = '←';
                        color = TextColor.ANSI.YELLOW_BRIGHT;
                        break;
                    case SWORD:
                        symbol = '⚔';
                        color = TextColor.ANSI.WHITE_BRIGHT;
                        break;
                    case WARHAMMER:
                        symbol = '⚒';
                        color = TextColor.ANSI.YELLOW;
                        break;
                    case BOW:
                        symbol = '}';
                        color = TextColor.ANSI.GREEN_BRIGHT;
                        break;
                    case SHOTGUN:
                        symbol = '⌐';
                        color = TextColor.ANSI.YELLOW_BRIGHT;
                        break;
                    case MINIGUN:
                        symbol = '⌐';
                        color = TextColor.ANSI.WHITE_BRIGHT;
                        break;
                    case LASER:
                        symbol = '⌐';
                        color = TextColor.ANSI.RED_BRIGHT;
                        break;
                    case BLASTER:
                        symbol = '⌐';
                        color = TextColor.ANSI.BLUE_BRIGHT;
                        break;
                    case WARP_ANNIHILATOR:
                        symbol = '☠';
                        color = TextColor.ANSI.MAGENTA_BRIGHT;
                        break;
                    default:
                        symbol = 'W';
                        color = TextColor.ANSI.YELLOW_BRIGHT;
                }
                screen.setCharacter(item.getX(), item.getY(),
                        new TextCharacter(symbol, color, TextColor.ANSI.BLACK));
            }
        }
    }

    private void drawAmmoWithFog(GameLevel level) {
        for (GameObject item : level.getItems()) {
            if (item instanceof Ammo && level.isExplored(item.getX(), item.getY())) {
                Ammo ammo = (Ammo) item;
                char symbol = '*';
                TextColor color = TextColor.ANSI.YELLOW_BRIGHT;

                switch (ammo.getType()) {
                    case BOW_AMMUNITION:
                        symbol = '↑';
                        color = TextColor.ANSI.GREEN_BRIGHT;
                        break;
                    case SHOTGUN_AMMUNITION:
                        symbol = '■';
                        color = TextColor.ANSI.YELLOW_BRIGHT;
                        break;
                    case MINIGUN_AMMUNITION:
                        symbol = '■';
                        color = TextColor.ANSI.WHITE_BRIGHT;
                        break;
                    case LASER_AMMUNITION:
                        symbol = '⚡';
                        color = TextColor.ANSI.RED_BRIGHT;
                        break;
                    case BLASTER_AMMUNITION:
                        symbol = '⚡';
                        color = TextColor.ANSI.CYAN_BRIGHT;
                        break;
                    case WARP_ANNIHILATOR_AMMUNITION:
                        symbol = '⚡';
                        color = TextColor.ANSI.MAGENTA_BRIGHT;
                        break;
                }
                screen.setCharacter(item.getX(), item.getY(),
                        new TextCharacter(symbol, color, TextColor.ANSI.BLACK));
            }
        }
    }

    private void drawHealthPotionWithFog(GameLevel level) {
        for (GameObject item : level.getItems()) {
            if (item instanceof HealthPotion && level.isExplored(item.getX(), item.getY())) {
                HealthPotion potion = (HealthPotion) item;
                TextColor color;
                switch (potion.getType()) {
                    case LOW_HEALTH:
                        color = TextColor.ANSI.YELLOW_BRIGHT;
                        break;
                    case AVERAGE_HEALTH:
                        color = TextColor.ANSI.GREEN_BRIGHT;
                        break;
                    case BIG_HEALTH:
                        color = TextColor.ANSI.RED_BRIGHT;
                        break;
                    default:
                        color = TextColor.ANSI.BLACK;
                }
                screen.setCharacter(item.getX(), item.getY(),
                        new TextCharacter('♥', color, TextColor.ANSI.BLACK));
            }
        }
    }

    private void drawReinforceWithFog(GameLevel level) {
        for (GameObject item : level.getItems()) {
            if (item instanceof Reinforce && level.isExplored(item.getX(), item.getY())) {
                screen.setCharacter(item.getX(), item.getY(),
                        new TextCharacter('?', TextColor.ANSI.MAGENTA, TextColor.ANSI.BLACK));
            }
        }
    }

    private void drawCrownWithFog(GameLevel level) {
        for (GameObject item : level.getItems()) {
            if (item instanceof DarkCrown) {
                int x = item.getX();
                int y = item.getY();
                boolean explored = level.isExplored(x, y);
                System.out.println("👑 Корона на позиции (" + x + ", " + y + ") исследована: " + explored);
                if (explored) {
                    screen.setCharacter(x, y,
                            new TextCharacter('♛', TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.BLACK));
                }
            }
        }
    }
}