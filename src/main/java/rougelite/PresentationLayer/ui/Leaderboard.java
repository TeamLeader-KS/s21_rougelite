package rougelite.PresentationLayer.ui;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard {

    private static final int TABLE_WIDTH = 120;
    private static final int TABLE_HEIGHT = 26;

    public static void draw(Screen screen, List<LeaderboardEntry> entries,
                            int terminalWidth, int terminalHeight) throws IOException {

        System.out.println("🎨 Отрисовка таблицы лидеров. Получено записей: " + (entries != null ? entries.size() : 0));

        // === СОРТИРУЕМ ПО СОКРОВИЩАМ ===
        List<LeaderboardEntry> sortedEntries = new ArrayList<>(entries != null ? entries : new ArrayList<>());
        sortedEntries.sort((a, b) -> Integer.compare(b.getTreasure(), a.getTreasure()));

        System.out.println("📊 После сортировки записей: " + sortedEntries.size());

        int startX = (terminalWidth - TABLE_WIDTH) / 2;
        int startY = (terminalHeight - TABLE_HEIGHT) / 2;

        fillBackground(screen, startX, startY, TABLE_WIDTH, TABLE_HEIGHT);
        drawFrame(screen, startX, startY, TABLE_WIDTH, TABLE_HEIGHT);
        drawTitle(screen, startX, startY, TABLE_WIDTH, " === ТАБЛИЦА ЛИДЕРОВ === ");
        drawHeader(screen, startX, startY + 3);

        // Отрисовываем только топ 10
        int maxRows = Math.min(sortedEntries.size(), 10);
        for (int i = 0; i < maxRows; i++) {
            drawRow(screen, startX, startY + 5 + i * 2, i + 1, sortedEntries.get(i));
        }
        for (int i = maxRows; i < 10; i++) {
            drawEmptyRow(screen, startX, startY + 5 + i * 2, i + 1);
        }

        screen.refresh();

    }

    private static void fillBackground(Screen screen, int startX, int startY, int width, int height) {
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                screen.setCharacter(startX + j, startY + i,
                        new TextCharacter(' ', TextColor.ANSI.BLACK, TextColor.ANSI.BLACK));
            }
        }
    }

    private static void drawFrame(Screen screen, int startX, int startY, int width, int height) {
        screen.setCharacter(startX, startY, new TextCharacter('╔', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        for (int i = 1; i < width - 1; i++) {
            screen.setCharacter(startX + i, startY, new TextCharacter('═', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        }
        screen.setCharacter(startX + width - 1, startY, new TextCharacter('╗', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));

        for (int i = 1; i < height - 1; i++) {
            screen.setCharacter(startX, startY + i, new TextCharacter('║', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
            screen.setCharacter(startX + width - 1, startY + i, new TextCharacter('║', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        }

        screen.setCharacter(startX, startY + height - 1, new TextCharacter('╚', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        for (int i = 1; i < width - 1; i++) {
            screen.setCharacter(startX + i, startY + height - 1, new TextCharacter('═', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        }
        screen.setCharacter(startX + width - 1, startY + height - 1, new TextCharacter('╝', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
    }

    private static void drawTitle(Screen screen, int startX, int startY, int width, String title) {
        int offset = (width - title.length()) / 2;
        drawText(screen, startX + offset, startY, title, TextColor.ANSI.YELLOW_BRIGHT);
    }

    private static void drawHeader(Screen screen, int startX, int y) {
        int step = TABLE_WIDTH / 5;
        int col1x = startX + step;
        int col2x = startX + step * 2;
        int col3x = startX + step * 3;
        int col4x = startX + step * 4;

        drawTextCentered(screen, col1x, y, "Позиция", TextColor.ANSI.YELLOW_BRIGHT);
        drawTextCentered(screen, col2x, y, "Игрок", TextColor.ANSI.YELLOW_BRIGHT);
        drawTextCentered(screen, col3x, y, "Уровень", TextColor.ANSI.YELLOW_BRIGHT);
        drawTextCentered(screen, col4x, y, "Сокровища", TextColor.ANSI.YELLOW_BRIGHT);

    }

    private static void drawRow(Screen screen, int startX, int y, int number, LeaderboardEntry entry) {
        int step = TABLE_WIDTH / 5;
        int col1x = startX + step;
        int col2x = startX + step * 2;
        int col3x = startX + step * 3;
        int col4x = startX + step * 4;

        String name = entry.getPlayerName();
        if (name.length() > 15) {
            name = name.substring(0, 12) + "...";
        }

        TextColor numberColor = TextColor.ANSI.YELLOW_BRIGHT;

        drawTextCentered(screen, col1x, y, String.valueOf(number), numberColor);
        drawTextCentered(screen, col2x, y, name, TextColor.ANSI.WHITE);
        drawTextCentered(screen, col3x, y, String.valueOf(entry.getLevel()), TextColor.ANSI.GREEN);
        drawTextCentered(screen, col4x, y, String.valueOf(entry.getTreasure()), TextColor.ANSI.YELLOW);
    }

    private static void drawEmptyRow(Screen screen, int startX, int y, int number) {
        int step = TABLE_WIDTH / 5;
        int col1x = startX + step;
        int col2x = startX + step * 2;
        int col3x = startX + step * 3;
        int col4x = startX + step * 4;

        drawTextCentered(screen, col1x, y, " - ", TextColor.ANSI.WHITE);
        drawTextCentered(screen, col2x, y, " - ", TextColor.ANSI.WHITE);
        drawTextCentered(screen, col3x, y, " - ", TextColor.ANSI.WHITE);
        drawTextCentered(screen, col4x, y, " - ", TextColor.ANSI.WHITE);
    }


    private static void drawText(Screen screen, int x, int y, String text, TextColor color) {
        for (int i = 0; i < text.length(); i++) {
            int posX = x + i;
            if (posX >= 0 && posX < screen.getTerminalSize().getColumns() && y >= 0 && y < screen.getTerminalSize().getRows()) {
                screen.setCharacter(posX, y,
                        new TextCharacter(text.charAt(i), color, TextColor.ANSI.BLACK));
            }
        }
    }

    private static void drawTextCentered(Screen screen, int centerX, int y, String text, TextColor color) {
        int startX = centerX - text.length() / 2;
        for (int i = 0; i < text.length(); i++) {
            int posX = startX + i;
            if (posX >= 0 && posX < screen.getTerminalSize().getColumns()) {
                screen.setCharacter(posX, y,
                        new TextCharacter(text.charAt(i), color, TextColor.ANSI.BLACK));
            }
        }
    }

    public static class LeaderboardEntry {
        private String playerName;
        private int level;
        private int treasure;

        public LeaderboardEntry(String playerName, int level, int treasure) {
            this.playerName = playerName;
            this.level = level;
            this.treasure = treasure;
        }

        public String getPlayerName() { return playerName; }
        public int getLevel() { return level; }
        public int getTreasure() { return treasure; }
    }
}

