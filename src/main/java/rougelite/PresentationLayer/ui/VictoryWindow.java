package rougelite.PresentationLayer.ui;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import rougelite.BusinessLogicLayer.logic.EnterUserName;

import java.io.IOException;


public class VictoryWindow {

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
        screen.setCharacter(startX, startY, new TextCharacter('╔', TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));
        for (int i = 1; i < width - 1; i++) {
            screen.setCharacter(startX + i, startY, new TextCharacter('═', TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));
        }
        screen.setCharacter(startX + width - 1, startY, new TextCharacter('╗', TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));

        // бока
        for (int i = 1; i < height - 1; i++) {
            screen.setCharacter(startX, startY + i, new TextCharacter('║', TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));
            screen.setCharacter(startX + width - 1, startY + i, new TextCharacter('║', TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));
        }

        // низ
        screen.setCharacter(startX, startY + height - 1, new TextCharacter('╚', TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));
        for (int i = 1; i < width - 1; i++) {
            screen.setCharacter(startX + i, startY + height - 1, new TextCharacter('═', TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));
        }
        screen.setCharacter(startX + width - 1, startY + height - 1, new TextCharacter('╝', TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));
    }

    private static void drawTitle(Screen screen, int startX, int startY, int width) {
        String title = " === ЭПИЛОГ === ";
        int offset = (width - title.length()) / 2;
        for (int i = 0; i < title.length(); i++) {
            screen.setCharacter(startX + offset + i, startY,
                    new TextCharacter(title.charAt(i), TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));
        }
    }

    private static void drawContent(Screen screen, int startX, int startY, int width) {
        int y = startY + 3;
        int centerX = startX + width / 2;
        String heroName = EnterUserName.getUserName();

        y += 2;

        String[] gameplay = {
                "Ты победил! Слава Герою " + heroName + "!!!",
                " ",
                "Темный Владыка пал и Корона Тьмы теперь твоя!",
                "Одень ее и стань нашим новым повелителем, Великий " + heroName + "!",
                " ",
                "Отныне твое имя навсегда останется на скрижалях истории!!!",
                " ",
                '⚔'+ " " + '⚔'+ " " + '⚔'+ " " + "Владыка " + heroName + " " + '⚔' + " " + '⚔' + " " + '⚔'
        };
        for (String line : gameplay) {
            drawTextCentered(screen, centerX, y, line, TextColor.ANSI.GREEN_BRIGHT);
            y += 2;
        }
    }

    // TODO сделать имя игрока прошедшего игру, новым Темным Владыкой

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
