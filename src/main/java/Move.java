package s21_rougelite;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

public class Move {

    public static void startGame(Screen screen, Terminal terminal, Room room, GameWindow gameWindow) throws Exception {
        screen.setCursorPosition(null);

        // Используем размеры из GameWindow (они уже подобраны под экран)
        int windowHeight = gameWindow.getHeight();
        int windowLength = gameWindow.getLength();
        int[][] windowMap = gameWindow.getWindow();

        int[][] roomMap = room.getRoom();
        int roomHeight = room.getHeight();
        int roomLength = room.getLength();

        // Выводим размеры для информации
        System.out.println("Окно: " + windowHeight + " x " + windowLength);
        System.out.println("Комната: " + roomHeight + " x " + roomLength);

        // Вычисляем отступы для комнаты (по центру окна)
        int offsetY = (windowHeight - roomHeight) / 2;
        int offsetX = (windowLength - roomLength) / 2;

        // Начальная позиция игрока
        int playerRoomX = roomLength / 2;
        int playerRoomY = roomHeight / 2;

        boolean running = true;

        while (running) {
            // Рисуем окно с комнатой (используем существующий метод Graphics)
            Graphics.drawWindowWithRoom(screen, windowMap, roomMap);

            // Рисуем игрока поверх
            int playerScreenX = offsetX + playerRoomX;
            int playerScreenY = offsetY + playerRoomY;
            screen.setCharacter(playerScreenX, playerScreenY,
                    new TextCharacter('@', TextColor.ANSI.GREEN, TextColor.ANSI.BLACK));

            screen.refresh();

            // Обработка ввода
            KeyStroke key = screen.readInput();
            if (key != null) {
                int newX = playerRoomX;
                int newY = playerRoomY;

                switch (key.getKeyType()) {
                    case ArrowUp:    newY--; break;
                    case ArrowDown:  newY++; break;
                    case ArrowLeft:  newX--; break;
                    case ArrowRight: newX++; break;
                    case Escape:     running = false; break;
                    case Character:
                        char c = java.lang.Character.toLowerCase(key.getCharacter());
                        if (c == 'w' || c == 'ц') newY--;
                        if (c == 's' || c == 'ы') newY++;
                        if (c == 'a' || c == 'ф') newX--;
                        if (c == 'd' || c == 'в') newX++;
                        break;
                    default: break;
                }

                // Проверка стен комнаты
                if (newX >= 1 && newX < roomLength - 1 &&
                        newY >= 1 && newY < roomHeight - 1) {
                    if (roomMap[newY][newX] == 0) {
                        playerRoomX = newX;
                        playerRoomY = newY;
                    }
                }
            }
        }
    }
}