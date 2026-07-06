package rougelite.PresentationLayer.input;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import rougelite.PresentationLayer.input.PlayerCommands;  // импорт

import static rougelite.PresentationLayer.input.PlayerCommands.MOVE_EQUIP_UNEQUIP_HANDS;

public class InputControl {

    public PlayerCommands readInput(Screen screen) throws Exception {
        KeyStroke key = screen.readInput();
        if (key == null) return PlayerCommands.NONE;

        switch (key.getKeyType()) {
            case ArrowUp:    return PlayerCommands.MOVE_UP;
            case ArrowDown:  return PlayerCommands.MOVE_DOWN;
            case ArrowLeft:  return PlayerCommands.MOVE_LEFT;
            case ArrowRight: return PlayerCommands.MOVE_RIGHT;
            case Escape:     return PlayerCommands.OPEN_GAME_MENU;
            case Enter:      return PlayerCommands.USE_USER_ENTER;
            case Character:
                char c = java.lang.Character.toLowerCase(key.getCharacter());
                if (c == 'w' || c == 'ц') return PlayerCommands.MOVE_UP;
                if (c == 's' || c == 'ы') return PlayerCommands.MOVE_DOWN;
                if (c == 'a' || c == 'ф') return PlayerCommands.MOVE_LEFT;
                if (c == 'd' || c == 'в') return PlayerCommands.MOVE_RIGHT;
                // рюкзак
                if (c == 'i' || c == 'ш') return PlayerCommands.OPEN_INVENTORY;
                if (c == 'h' || c == 'р') return PlayerCommands.OPEN_WEAPON;
                if (c == 'j' || c == 'о') return PlayerCommands.OPEN_AMMO;
                if (c == 'k' || c == 'л') return PlayerCommands.OPEN_POTION;
                if (c == 'l' || c == 'д') return PlayerCommands.OPEN_REINFORCE;
                // камни
                if (c == 'p' || c == 'з') return PlayerCommands.OPEN_POCKET;
                if (c == 'q' || c == 'й' || c == 0) return PlayerCommands.USE_STONE;
                // цифры
                if (c == '1') return PlayerCommands.USE_ITEM_1;
                if (c == '2') return PlayerCommands.USE_ITEM_2;
                if (c == '3') return PlayerCommands.USE_ITEM_3;
                if (c == '4') return PlayerCommands.USE_ITEM_4;
                if (c == '5') return PlayerCommands.USE_ITEM_5;
                if (c == '6') return PlayerCommands.USE_ITEM_6;
                if (c == '7') return PlayerCommands.USE_ITEM_7;
                if (c == '8') return PlayerCommands.USE_ITEM_8;
                if (c == '9') return PlayerCommands.USE_ITEM_9;
                // достать убрать оружие
                if (c == 'r' || c == 'к') return PlayerCommands.MOVE_EQUIP_UNEQUIP_WEAPON;
                // достать убрать руки
                if (c == 'f' || c == 'а') return PlayerCommands.MOVE_EQUIP_UNEQUIP_HANDS;
                // огонь
                if (c == ' ') return PlayerCommands.MOVE_FIRE;
                // войти в портал
                if (c == 'e' || c =='у') return PlayerCommands.USE_PORTAL;
                // меню информации
                if (c == 'm' || c == 'ь') return PlayerCommands.OPEN_INFORMATION;
                // таблица лидеров
                if (c == 'v' || c == 'м') return PlayerCommands.OPEN_LEADER_BOARD;
                break;
        }
        return PlayerCommands.NONE;
    }
}