package s21_rougelite;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;

public class Graphics {

    public static void drawWindowWithRoom(Screen screen, int[][] window, int[][] room) {
        int windowHeight = window.length;
        int windowWidth = window[0].length;
        int roomHeight = room.length;
        int roomWidth = room[0].length;

        int offsetY = (windowHeight - roomHeight) / 2;
        int offsetX = (windowWidth - roomWidth) / 2;

        // Создаем буфер для отрисовки
        char[][] buffer = new char[windowHeight][windowWidth];

        // Копируем окно (стены)
        for (int i = 0; i < windowHeight; i++) {
            for (int j = 0; j < windowWidth; j++) {
                buffer[i][j] = (window[i][j] == 1) ? '#' : ' ';
            }
        }

        // Рисуем комнату поверх (не затирая границы окна)
        for (int i = 0; i < roomHeight; i++) {
            for (int j = 0; j < roomWidth; j++) {
                int screenY = offsetY + i;
                int screenX = offsetX + j;

                // Не рисуем комнату на границах окна
                if (screenY > 0 && screenY < windowHeight - 1 &&
                        screenX > 0 && screenX < windowWidth - 1) {
                    if (room[i][j] == 1) {
                        buffer[screenY][screenX] = '*';
                    } else {
                        buffer[screenY][screenX] = ' ';
                    }
                }
            }
        }

        // Выводим буфер на экран
        for (int i = 0; i < windowHeight; i++) {
            for (int j = 0; j < windowWidth; j++) {
                screen.setCharacter(j, i, new TextCharacter(buffer[i][j],
                        TextColor.ANSI.WHITE, TextColor.ANSI.BLACK));
            }
        }
    }
}