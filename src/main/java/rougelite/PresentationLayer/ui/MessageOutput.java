package rougelite.PresentationLayer.ui;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;

public class MessageOutput {

    // Вывод сообщения по центру на любой строке (для заголовков и важных сообщений)
    public static void showCenteredMessage(Screen screen, String message, int y, int width, TextColor color) {
        int messageLength = message.length();
        int offset = (width - messageLength) / 2;

        for (int i = 0; i < messageLength; i++) {
            screen.setCharacter(offset + i, y,
                    new TextCharacter(message.charAt(i), color, TextColor.ANSI.BLACK));
        }
    }

    // Вывод сообщения вверху экрана
    public static void showGameMessage(Screen screen, String message, int terminalWidth) {
        int messageLength = message.length();
        int offset = (terminalWidth - messageLength) / 2;
        int y = 2;
        TextColor color = TextColor.ANSI.YELLOW_BRIGHT;

        for (int i = 0; i < terminalWidth; i++) {
            screen.setCharacter(i, y, new TextCharacter(' ', TextColor.ANSI.BLACK, TextColor.ANSI.BLACK));
        }

        for (int i = 0; i < messageLength; i++) {
            screen.setCharacter(offset + i, y,
                    new TextCharacter(message.charAt(i), color, TextColor.ANSI.BLACK));
        }

        try {
            screen.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}