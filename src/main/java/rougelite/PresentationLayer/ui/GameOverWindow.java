package rougelite.PresentationLayer.ui;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import rougelite.BusinessLogicLayer.logic.EnterUserName;

import java.io.IOException;

public class GameOverWindow {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 24;

    public static void draw(Screen screen, int terminalWidth, int terminalHeight) throws IOException {
        int startX = (terminalWidth - WIDTH) / 2;
        int startY = (terminalHeight - HEIGHT) / 2;

        fillBackground(screen, startX, startY, WIDTH, HEIGHT);
        drawFrame(screen, startX, startY, WIDTH, HEIGHT);
        drawTitle(screen, startX, startY, WIDTH);
        drawContent(screen, startX, startY, WIDTH);

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
        // верх
        screen.setCharacter(startX, startY, new TextCharacter('╔', TextColor.ANSI.RED, TextColor.ANSI.BLACK));
        for (int i = 1; i < width - 1; i++) {
            screen.setCharacter(startX + i, startY, new TextCharacter('═', TextColor.ANSI.RED, TextColor.ANSI.BLACK));
        }
        screen.setCharacter(startX + width - 1, startY, new TextCharacter('╗', TextColor.ANSI.RED, TextColor.ANSI.BLACK));

        // бока
        for (int i = 1; i < height - 1; i++) {
            screen.setCharacter(startX, startY + i, new TextCharacter('║', TextColor.ANSI.RED, TextColor.ANSI.BLACK));
            screen.setCharacter(startX + width - 1, startY + i, new TextCharacter('║', TextColor.ANSI.RED, TextColor.ANSI.BLACK));
        }

        // низ
        screen.setCharacter(startX, startY + height - 1, new TextCharacter('╚', TextColor.ANSI.RED, TextColor.ANSI.BLACK));
        for (int i = 1; i < width - 1; i++) {
            screen.setCharacter(startX + i, startY + height - 1, new TextCharacter('═', TextColor.ANSI.RED, TextColor.ANSI.BLACK));
        }
        screen.setCharacter(startX + width - 1, startY + height - 1, new TextCharacter('╝', TextColor.ANSI.RED, TextColor.ANSI.BLACK));
    }

    private static void drawTitle(Screen screen, int startX, int startY, int width) {
        String title = " === ПОРАЖЕНИЕ === ";
        int offset = (width - title.length()) / 2;
        for (int i = 0; i < title.length(); i++) {
            screen.setCharacter(startX + offset + i, startY,
                    new TextCharacter(title.charAt(i), TextColor.ANSI.RED, TextColor.ANSI.BLACK));
        }
    }

    private static void drawContent(Screen screen, int startX, int startY, int width) {
        int y = startY + 3;
        int centerX = startX + width / 2;
        String heroName = EnterUserName.getUserName();

        y += 1;

        String[] gameplay = {
                "Путешественник " + heroName + " сражался храбро и погиб с честью!",
                " ",
                "Мы запомним твоё имя, но не надолго.",
                " ",
                "Если вообще запомним...",
                " ",
                '✞'+ " " + '✞'+ " " + '✞',
                " ",
                "Нам остается надеяться, что однажды появится настоящий герой, способный закончить начатое...",
        };
        for (String line : gameplay) {
            drawTextCentered(screen, centerX, y, line, TextColor.ANSI.RED);
            y += 2;
        }
    }

    private static void drawText(Screen screen, int x, int y, String text, TextColor color) {
        for (int i = 0; i < text.length(); i++) {
            int posX = x + i;
            if (posX >= 0 && posX < screen.getTerminalSize().getColumns()) {
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

}
