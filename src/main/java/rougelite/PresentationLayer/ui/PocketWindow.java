package rougelite.PresentationLayer.ui;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import rougelite.BusinessLogicLayer.inventory.Pocket;
import rougelite.BusinessLogicLayer.objects.PortalStone;

import java.io.IOException;
import java.util.List;

public class PocketWindow {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 21;

    public static void draw(Screen screen, Pocket pocket, int terminalWidth, int terminalHeight) {
        try {
            int startX = (terminalWidth - WIDTH) / 2;
            int startY = (terminalHeight - HEIGHT) / 2;

            fillBackground(screen, startX, startY, WIDTH, HEIGHT);
            drawFrame(screen, startX, startY, WIDTH, HEIGHT);
            drawTitle(screen, startX, startY, WIDTH);
            drawStonesContent(screen, startX, startY + 3, WIDTH, pocket);

            screen.refresh();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
        // Верхняя граница
        screen.setCharacter(startX, startY, new TextCharacter('╔', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        for (int i = 1; i < width - 1; i++) {
            screen.setCharacter(startX + i, startY, new TextCharacter('═', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        }
        screen.setCharacter(startX + width - 1, startY, new TextCharacter('╗', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));

        // Левая и правая границы
        for (int i = 1; i < height - 1; i++) {
            screen.setCharacter(startX, startY + i, new TextCharacter('║', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
            screen.setCharacter(startX + width - 1, startY + i, new TextCharacter('║', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        }

        // Нижняя граница
        screen.setCharacter(startX, startY + height - 1, new TextCharacter('╚', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        for (int i = 1; i < width - 1; i++) {
            screen.setCharacter(startX + i, startY + height - 1, new TextCharacter('═', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        }
        screen.setCharacter(startX + width - 1, startY + height - 1, new TextCharacter('╝', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));

    }

    private static void drawTitle(Screen screen, int startX, int startY, int width) {
        String title = " КАРМАН ДЛЯ КАМНЕЙ ";
        int titleLength = title.length();
        int offset = (width - titleLength) / 2;

        for (int i = 0; i < titleLength; i++) {
            screen.setCharacter(startX + offset + i, startY,
                    new TextCharacter(title.charAt(i), TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.BLACK));
        }
    }

    private static void drawStonesContent(Screen screen, int startX, int startY, int width, Pocket pocket) {
        List<PortalStone> stones = pocket.getAllStones();
        int slotY = startY;

        for (int slot = 1; slot <= 8; slot++) {
            String slotText;
            if (slot - 1 < stones.size()) {
                PortalStone stone = stones.get(slot - 1);
                slotText = String.format("%d. %s", slot, stone.getPosition().getRussianName());
            } else {
                slotText = String.format("%d. (пусто)", slot);
            }

            for (int j = 0; j < slotText.length() && j < width - 5; j++) {
                TextColor color = (slot - 1 < stones.size()) ?
                        TextColor.ANSI.YELLOW : TextColor.ANSI.YELLOW_BRIGHT;
                screen.setCharacter(startX + 3 + j, slotY + (slot - 1) * 2,
                        new TextCharacter(slotText.charAt(j), color, TextColor.ANSI.BLACK));
            }
        }
    }
}