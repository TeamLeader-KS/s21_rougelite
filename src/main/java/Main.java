package s21_rougelite;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) throws Exception {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        Terminal terminal = factory.createTerminal();

        if (terminal instanceof SwingTerminalFrame) {
            SwingTerminalFrame frame = (SwingTerminalFrame) terminal;
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setLocationRelativeTo(null);

            // Даём время окну развернуться
            Thread.sleep(200);
        }

        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        // Теперь размеры будут правильные (после разворачивания)
        GameWindow gameWindow = new GameWindow(terminal);
        Room room = new Room();

        Move.startGame(screen, terminal, room, gameWindow);

        screen.close();
    }
}