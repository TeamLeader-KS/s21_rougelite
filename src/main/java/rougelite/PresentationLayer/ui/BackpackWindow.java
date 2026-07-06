package rougelite.PresentationLayer.ui;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import rougelite.BusinessLogicLayer.inventory.Backpack;
import rougelite.BusinessLogicLayer.objects.GameObject;
import rougelite.BusinessLogicLayer.objects.PortalStone;
import rougelite.BusinessLogicLayer.objects.Weapon;
import rougelite.BusinessLogicLayer.objects.Ammo;
import rougelite.BusinessLogicLayer.objects.HealthPotion;
import rougelite.BusinessLogicLayer.objects.Reinforce;
import rougelite.BusinessLogicLayer.objects.*;

import java.io.IOException;
import java.util.List;

public class BackpackWindow {

    private static final int WIDTH = 50;
    private static final int HEIGHT = 27;

    public static void draw(Screen screen, Backpack backpack, int terminalWidth, int terminalHeight) {
        try {
            int startX = (terminalWidth - WIDTH) / 2;
            int startY = (terminalHeight - HEIGHT) / 2;

            fillBackground(screen, startX, startY, WIDTH, HEIGHT);
            drawFrame(screen, startX, startY, WIDTH, HEIGHT);
            drawTitle(screen, startX, startY, WIDTH);
            drawInventoryTabs(screen, startX, startY + 2, backpack);

            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Заливка фона
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

        // Разделительная линия
        for (int i = 1; i < width - 1; i ++)
        {
            screen.setCharacter(startX + i, startY + 4, new TextCharacter('═', TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK));
        }
    }

    // надпись РЮКЗАК
    private static void drawTitle(Screen screen, int startX, int startY, int width) {
        String title = " РЮКЗАК ";
        int titleLength = title.length();
        int offset = (width - titleLength) / 2;

        for (int i = 0; i < titleLength; i++) {
            screen.setCharacter(startX + offset + i, startY,
                    new TextCharacter(title.charAt(i), TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.BLACK));
        }
    }

private static void drawTabWeaponContent(Screen screen, int startX, int startY, int width, int height, Backpack backpack) {

    List<Weapon> items = backpack.getWeapons();
    Weapon equippedWeapon = backpack.getEquippedWeapon();
    int slotY = startY;

    for (int slot = 1; slot <= 9; slot++) {
        String slotText;
        TextColor color;
        if (slot - 1 < items.size()) {
            Weapon weapon = items.get(slot - 1);
            String itemName = getItemName(weapon);
            slotText = String.format("%d. %s", slot, itemName);

            if (weapon.equals(equippedWeapon)) {
                color = TextColor.ANSI.YELLOW_BRIGHT;
            } else {
                color = TextColor.ANSI.YELLOW;
            }
        } else {
            slotText = String.format("%d. (пусто)", slot);
            color = TextColor.ANSI.YELLOW_BRIGHT;
        }

        for (int j = 0; j < slotText.length() && j < width - 5; j++) {
            screen.setCharacter(startX + 3 + j, slotY + (slot - 1) * 2,
                    new TextCharacter(slotText.charAt(j), color, TextColor.ANSI.BLACK));
        }
    }
}

    // надпись ПРИПАСЫ
    private static void drawTabAmmoContent(Screen screen, int startX, int startY, int width, int height, Backpack backpack) {

        List<Ammo> items = backpack.getAmmo();
        int slotY = startY;

        for (int slot = 1; slot <= 9; slot++) {
            String slotText;
            if (slot - 1 < items.size()) {
                Ammo ammo = items.get(slot - 1);
                String itemName = ammo.getName();
                slotText = String.format("%d. %s    " + "(%d ед.)", slot, itemName, ammo.getAmountAmmo());
            } else {
                slotText = String.format("%d. (пусто)", slot);
            }

            for (int j = 0; j < slotText.length() && j < width - 5; j++) {
                TextColor color = (slot - 1 < items.size()) ?
                        TextColor.ANSI.YELLOW : TextColor.ANSI.YELLOW_BRIGHT;
                screen.setCharacter(startX + 3 + j, slotY + (slot - 1) * 2,
                        new TextCharacter(slotText.charAt(j), color, TextColor.ANSI.BLACK));
            }
        }
    }

    // надпись ЗЕЛЬЯ
    private static void drawTabPotionContent(Screen screen, int startX, int startY, int width, int height, Backpack backpack) {

        List<HealthPotion> items = backpack.getHealthPotion();
        int slotY = startY;

        for (int slot = 1; slot <= 9; slot++) {
            String slotText;

            if (slot - 1 < items.size()) {
                TextColor color = TextColor.ANSI.YELLOW_BRIGHT;
                HealthPotion potion = items.get(slot - 1);
                String itemName = potion.getName();
                slotText = String.format("%d. %s    " + "(+%d HP)", slot, itemName, potion.getHealthPower());

                switch (potion.getType())
                {
                    case LOW_HEALTH:
                        color = TextColor.ANSI.YELLOW_BRIGHT;
                        break;
                    case AVERAGE_HEALTH:
                        color = TextColor.ANSI.GREEN_BRIGHT;
                        break;
                    case BIG_HEALTH:
                        color = TextColor.ANSI.RED_BRIGHT;
                        break;
                }
            } else {
                slotText = String.format("%d. (пусто)", slot);
            }

            for (int j = 0; j < slotText.length() && j < width - 5; j++) {
                TextColor color = (slot - 1 < items.size()) ?   // эти 2 строки закоммитить
                        TextColor.ANSI.YELLOW : TextColor.ANSI.YELLOW_BRIGHT;   // эти 2 строки закоммитить
                screen.setCharacter(startX + 3 + j, slotY + (slot - 1) * 2,
                        new TextCharacter(slotText.charAt(j), color, TextColor.ANSI.BLACK));
            }
        }
    }

    // надпись УСИЛЕНИЕ
    private static void drawTabReinforceContent(Screen screen, int startX, int startY, int width, int height, Backpack backpack) {

        List<Reinforce> items = backpack.getReinforces();
        int slotY = startY;

        for (int slot = 1; slot <= 9; slot++) {
            String slotText;
            if (slot - 1 < items.size()) {
                GameObject item = items.get(slot - 1);
                String itemName = getItemName(item);
                slotText = String.format("%d. %s", slot, itemName);
            } else {
                slotText = String.format("%d. (пусто)", slot);
            }

            for (int j = 0; j < slotText.length() && j < width - 5; j++) {
                TextColor color = (slot - 1 < items.size()) ?
                        TextColor.ANSI.YELLOW : TextColor.ANSI.YELLOW_BRIGHT;
                screen.setCharacter(startX + 3 + j, slotY + (slot - 1) * 2,
                        new TextCharacter(slotText.charAt(j), color, TextColor.ANSI.BLACK));
            }
        }
    }

    private static void drawInventoryTabs(Screen screen, int startX, int startY, Backpack backpack) {
        int contentStartY = startY + 5;
        int width = WIDTH;

        String[] tabNames = {"ОРУЖИЕ", "АМУНИЦИЯ", "ЗЕЛЬЯ", "ПРЕДМЕТЫ"};
        int tabWidth = 10;
        int spacing = 2;
        int totalWidth = tabNames.length * tabWidth + (tabNames.length - 1) * spacing;
        int tabStartX = startX + (width - totalWidth) / 2;

        for (int i = 0; i < tabNames.length; i++) {
            int tabX = tabStartX + i * (tabWidth + spacing);
            boolean isActive = (i == currentTab.ordinal());

            // Цвет текста для активной вкладки
            TextColor textColor = isActive ? TextColor.ANSI.YELLOW_BRIGHT : TextColor.ANSI.YELLOW;

            // текст вкладки
            String tabText = tabNames[i];
            int textOffset = (tabWidth - tabText.length()) / 2;
            for (int j = 0; j < tabText.length(); j++) {
                screen.setCharacter(tabX + textOffset + j, startY,
                        new TextCharacter(tabText.charAt(j), textColor, TextColor.ANSI.BLACK));
            }
        }

        switch (currentTab) {
            case WEAPON:
                drawTabWeaponContent(screen, startX, contentStartY, width, HEIGHT, backpack);
                break;
            case AMMO:
                drawTabAmmoContent(screen, startX, contentStartY, width, HEIGHT, backpack);
                break;
            case POTION:
                drawTabPotionContent(screen, startX, contentStartY, width, HEIGHT, backpack);
                break;
            case REINFORCE:
                drawTabReinforceContent(screen, startX, contentStartY, width, HEIGHT, backpack);
                break;
        }
    }

    private static String getItemName(GameObject item) {
        if (item instanceof PortalStone) {
            return ((PortalStone) item).getPosition().getRussianName();
        }
        if (item instanceof Weapon)
        {
            return ((Weapon) item).getName();
        }
        if (item instanceof Ammo)
        {
            return ((Ammo) item).getName();
        }
        if (item instanceof HealthPotion)
        {
            return ((HealthPotion) item).getName();
        }
        if (item instanceof Reinforce)
        {
            return ((Reinforce) item).getName();
        }

        return item.getClass().getSimpleName();
    }

    public enum Tab {
        WEAPON, AMMO, POTION, REINFORCE
    }

    private static Tab currentTab = Tab.WEAPON;

    public static void setTab(Tab tab) {
        currentTab = tab;
    }

    public static void nextTab() {
        int next = (currentTab.ordinal() + 1) % Tab.values().length;
        currentTab = Tab.values()[next];
    }

    public static void prevTab() {
        int prev = (currentTab.ordinal() - 1 + Tab.values().length) % Tab.values().length;
        currentTab = Tab.values()[prev];
    }

    public static Tab getCurrentTab() {
        return currentTab;
    }
}