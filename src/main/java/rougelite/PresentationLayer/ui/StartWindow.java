package rougelite.PresentationLayer.ui;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;

public class StartWindow {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 24;
    private static String enteredName = "";

    public static void draw(Screen screen, int terminalWidth, int terminalHeight) throws IOException {
        int startX = (terminalWidth - WIDTH) / 2;
        int startY = (terminalHeight - HEIGHT) / 2;

        fillBackground(screen, startX, startY, WIDTH, HEIGHT);
        drawFrame(screen, startX, startY, WIDTH, HEIGHT);
        drawTitle(screen, startX, startY, WIDTH);
        drawContent(screen, startX, startY, WIDTH, terminalWidth, terminalHeight);

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
        screen.setCharacter(startX, startY, new TextCharacter('╔', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        for (int i = 1; i < width - 1; i++) {
            screen.setCharacter(startX + i, startY, new TextCharacter('═', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        }
        screen.setCharacter(startX + width - 1, startY, new TextCharacter('╗', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));

        // бока
        for (int i = 1; i < height - 1; i++) {
            screen.setCharacter(startX, startY + i, new TextCharacter('║', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
            screen.setCharacter(startX + width - 1, startY + i, new TextCharacter('║', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        }

        // низ
        screen.setCharacter(startX, startY + height - 1, new TextCharacter('╚', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        for (int i = 1; i < width - 1; i++) {
            screen.setCharacter(startX + i, startY + height - 1, new TextCharacter('═', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        }
        screen.setCharacter(startX + width - 1, startY + height - 1, new TextCharacter('╝', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
    }

    private static void drawTitle(Screen screen, int startX, int startY, int width) {
        String title = " === СОЗДАНИЕ ПЕРСОНАЖА === ";
        int offset = (width - title.length()) / 2;
        for (int i = 0; i < title.length(); i++) {
            screen.setCharacter(startX + offset + i, startY,
                    new TextCharacter(title.charAt(i), TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.BLACK));
        }
    }

    private static void drawContent(Screen screen, int startX, int startY, int width, int terminalWidth, int terminalHeight) throws IOException {
        int centerX = startX + width / 2;
        int inscription = startY + 8;

        drawTextCentered(screen, centerX, inscription, "Скажи нам свое имя, путник, иначе ты навсегда останешься безымянным героем.", TextColor.ANSI.YELLOW);

        enteredName = drawInputField(screen, startX, startY, width, terminalWidth, terminalHeight);

    }

    private static String drawInputField(Screen screen, int startX, int startY, int width, int terminalWidth, int terminalHeight) throws IOException {
        StringBuilder name = new StringBuilder();

        int centerX = startX + width / 2;
        int inputY = startY + 16;

        boolean isInputComplete = false;

        while (!isInputComplete) {

            for (int i = 0; i < 8; i++) {
                screen.setCharacter(centerX - 4 + i, inputY,
                        new TextCharacter(' ', TextColor.ANSI.WHITE, TextColor.ANSI.BLACK));
            }

            screen.setCharacter(centerX - 5, inputY,
                    new TextCharacter(' ', TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.BLACK));
            screen.setCharacter(centerX + 4, inputY,
                    new TextCharacter(' ', TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.BLACK));
            screen.setCharacter(centerX - 6, inputY,
                    new TextCharacter('☠', TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.BLACK));
            screen.setCharacter(centerX + 5, inputY,
                    new TextCharacter('☠', TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.BLACK));

            for (int i = 0; i < name.length() && i < 8; i++) {
                screen.setCharacter(centerX - 4 + i, inputY,
                        new TextCharacter(name.charAt(i), TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));
            }

            screen.refresh();

            KeyStroke keyStroke = screen.pollInput();
            if (keyStroke != null) {

                if (keyStroke.getKeyType() == KeyType.Enter) { // ENTER - завершаем ввод
                    isInputComplete = true;
                }
                else if (keyStroke.getKeyType() == KeyType.Escape) {
                    return null;
                }
                else if (keyStroke.getKeyType() == KeyType.Backspace) {
                    if (name.length() > 0) {
                        name.deleteCharAt(name.length() - 1);
                    }
                }
                else if (keyStroke.getKeyType() == KeyType.Character) {
                    char c = keyStroke.getCharacter();
                    if (Character.isLetterOrDigit(c) && name.length() < 8) {
                        name.append(c);
                    }
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return name.toString();
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

    public static String getEnteredName() {
        return enteredName;
    }
}