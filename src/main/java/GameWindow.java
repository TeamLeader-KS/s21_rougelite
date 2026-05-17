package s21_rougelite;

import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;

public class GameWindow {

    private int height;
    private int length;
    private int[][] window;

    public GameWindow(Terminal terminal) {
        try {
            // Берём реальные размеры терминала
            this.height = terminal.getTerminalSize().getRows();
            this.length = terminal.getTerminalSize().getColumns() - 1;
        } catch (IOException e) {
            e.printStackTrace();
            // Значения по умолчанию, если не получилось получить размер
            this.height = 50;
            this.length = 180;
        }

        // СОЗДАЁМ массив ПОСЛЕ того, как определили height и length
        this.window = new int[height][length];

        // ЗАПОЛНЯЕМ массив стенами и полом
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                if ((i == 0 || i == height - 1) || (j == 0 || j == length - 1)) {
                    window[i][j] = 1;  // стены по краям
                } else if (i == 4) {
                    window[i][j] = 1;  // горизонтальная стена на строке 4
                } else {
                    window[i][j] = 0;  // пол
                }
            }
        }
    }

    // Метод для обновления размера окна (если нужно)
    public void updateSize(Terminal terminal) throws IOException {
        this.height = terminal.getTerminalSize().getRows();
        this.length = terminal.getTerminalSize().getColumns() - 1;
        this.window = new int[height][length];
        // Перезаполняем
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                if ((i == 0 || i == height - 1) || (j == 0 || j == length - 1)) {
                    window[i][j] = 1;
                } else if (i == 4) {
                    window[i][j] = 1;
                } else {
                    window[i][j] = 0;
                }
            }
        }
    }

    public int[][] getWindow() { return window; }
    public int getHeight() { return height; }
    public int getLength() { return length; }
}