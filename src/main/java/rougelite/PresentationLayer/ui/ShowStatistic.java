package rougelite.PresentationLayer.ui;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.logic.GameEngine;
import rougelite.BusinessLogicLayer.objects.Hands;
import rougelite.BusinessLogicLayer.objects.Weapon;

public class ShowStatistic {

    private static final int STATS_Y_OFFSET = 3;
    private static final int WEAPON_Y_OFFSET = 5;

    public static void draw(Screen screen, Player player, GameEngine gameEngine, int terminalWidth, int terminalHeight) {
        int y1 = terminalHeight - STATS_Y_OFFSET;
        int y2 = terminalHeight - WEAPON_Y_OFFSET;

        // Очищаем строку
        for (int i = 0; i < terminalWidth; i++) {
            screen.setCharacter(i, y1, new TextCharacter(' ', TextColor.ANSI.BLACK, TextColor.ANSI.BLACK));
        }

        // Очищаем строку с оружием
        for (int i = 0; i < terminalWidth; i++) {
            screen.setCharacter(i, y2, new TextCharacter(' ', TextColor.ANSI.BLACK, TextColor.ANSI.BLACK));
        }

        // Статы
        String health = String.format("Здоровье: %d/%d", player.getHealth(), player.getMaxHealth());
        String strength = String.format("Сила: %d", player.getStrength());
        String dexterity = String.format("Ловкость: %d", player.getDexterity());
        String exp = String.format("Опыт: %d", player.getExperience());
        String treasure = String.format("Сокровища: %d", player.getBackpack().getTreasure());
        String playerLevel = String.format("Уровень Персонажа: %d", player.getCurrentPlayerLevel());
        String dungeonLevel = String.format("Уровень подземелья: %d", gameEngine.getCurrentDungeonLevel());

        // ===== ОРУЖИЕ И РУКИ =====
        Weapon equippedWeapon = player.getBackpack().getEquippedWeapon();
        Hands equippedHands = player.getEquippedHands();

        String weaponName;
        if (equippedWeapon != null) {
            weaponName = equippedWeapon.getName();
        } else if (equippedHands != null) {
            weaponName = equippedHands.getName();
        } else {
            weaponName = "Пустые руки";
        }
        String weapon = String.format("Оружие: %s", weaponName);

        // ===== Б/К =====
        String ammo = "Амуниция: n/a";
            if (equippedWeapon != null && equippedWeapon.getMaxAmmo() > 0)
            {
                ammo = String.format("Амуниция: %d/%d", equippedWeapon.getCurrentAmmo(), equippedWeapon.getMaxAmmo());
            }
        // кнопки
        String information = String.format("Управление ('m')");
        String exit_menu = String.format("Игровое меню ('esc')");

        // Вычисляем позиции (равномерно от центра)
        int step = terminalWidth / 7;
        int center = terminalWidth / 2;

        int x1 = center - step * 3;
        int x2 = center - step * 2;
        int x3 = center - step;
        int x4 = center;
        int x5 = center + step;
        int x6 = center + step * 2;
        int x7 = center + step * 3;

        // оружие
        int x8 = center - step;
        int x9 = center + step;
        // кнопки
        int x10 = center - step * 3;
        int x11 = center + step * 3;

        // Статы
        drawTextCentered(screen, x1, y1, playerLevel);
        drawTextCentered(screen, x2, y1, exp);
        drawTextCentered(screen, x3, y1, strength);
        drawTextCentered(screen, x4, y1, health);
        drawTextCentered(screen, x5, y1, dexterity);
        drawTextCentered(screen, x6, y1, treasure);
        drawTextCentered(screen, x7, y1, dungeonLevel);
        // Оружие
        drawTextCentered(screen, x8, y2, weapon);
        drawTextCentered(screen, x9, y2, ammo);
        // кнопки
        drawTextCentered(screen, x10, y2, information);
        drawTextCentered(screen, x11, y2, exit_menu);
    }

    private static void drawTextCentered(Screen screen, int centerX, int y1, String text) {
        int startX = centerX - text.length() / 2;
        for (int i = 0; i < text.length(); i++) {
            if (startX + i >= 0 && startX + i < screen.getTerminalSize().getColumns()) {
                screen.setCharacter(startX + i, y1,
                        new TextCharacter(text.charAt(i), TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.BLACK));
            }
        }
    }
}