package rougelite.PresentationLayer.ui;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import rougelite.BusinessLogicLayer.characters.Enemy;
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel;

public class EnemyRender {

    // Символы и цвета для каждого типа врага
    private static final char[] MONSTER_SYMBOLS = {
            'Z',  // ZOMBIE
            'V',  // VAMPIRE
            'G',  // GHOST
            'O',  // OGRE
            'S',  // SNAKE_MAGE
            'M'   // BOSS
    };

    private static final TextColor[] MONSTER_COLORS = {
            TextColor.ANSI.GREEN,   // ZOMBIE
            TextColor.ANSI.RED,     // VAMPIRE
            TextColor.ANSI.WHITE,   // GHOST
            TextColor.ANSI.YELLOW,  // OGRE
            TextColor.ANSI.WHITE,   // SNAKE_MAGE
            TextColor.ANSI.MAGENTA_BRIGHT   // BOSS
    };


public static void drawEnemies(Screen screen, GameLevel level) {
    for (Enemy enemy : level.getEnemies()) {
        if (enemy.getEnemyType() == Enemy.EnemyType.BOSS && !enemy.isVisible()) {
            screen.setCharacter(enemy.getX(), enemy.getY(),
                    new TextCharacter('♛', TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.BLACK));
        } else if (enemy.isVisible() && level.isExplored(enemy.getX(), enemy.getY())) {
            int index = enemy.getEnemyType().ordinal();
            char symbol = MONSTER_SYMBOLS[index];
            TextColor color = MONSTER_COLORS[index];
            screen.setCharacter(enemy.getX(), enemy.getY(),
                    new TextCharacter(symbol, color, TextColor.ANSI.BLACK));
        }
    }
}

}