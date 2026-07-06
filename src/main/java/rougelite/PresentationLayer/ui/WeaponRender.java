package rougelite.PresentationLayer.ui;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.logic.MoveLogic;
import rougelite.BusinessLogicLayer.logic.WeaponEngine;
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap;
import rougelite.BusinessLogicLayer.objects.Hands;
import rougelite.BusinessLogicLayer.objects.Weapon;
import rougelite.BusinessLogicLayer.logic.MoveLogic.Direction;
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel;
import rougelite.BusinessLogicLayer.mapGenerator.Room;
import java.util.List;

public class WeaponRender {

    // Отрисовка игрока с учётом экипированного оружия
    public static void drawPlayer(Screen screen, Player player) {
        Weapon weapon = player.getBackpack().getEquippedWeapon();
        Hands hands = player.getEquippedHands();
        Direction facing = player.getFacing(); // или side

        // Рисуем самого игрока
        screen.setCharacter(player.getLevelX(), player.getLevelY(),
                new TextCharacter('@', TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK));

        // Если есть оружие — рисуем его рядом
        if (weapon != null) {
            char weaponSymbol = getWeaponSymbol(weapon, facing == Direction.LEFT);
            TextColor weaponColor = TextColor.ANSI.YELLOW_BRIGHT;  // ОДИН ЦВЕТ ДЛЯ ВСЕГО ОРУЖИЯ

            int weaponX = player.getLevelX();
            int weaponY = player.getLevelY();

            switch (facing) {
                case LEFT:
                    weaponX = player.getLevelX() - 1;
                    break;
                case RIGHT:
                    weaponX = player.getLevelX() + 1;
                    break;
                default:
                    break;
            }

            screen.setCharacter(weaponX, weaponY,
                    new TextCharacter(weaponSymbol, weaponColor, TextColor.ANSI.BLACK));
        }

        else if (hands != null)
        {
            char handsSymbol = getHandsSymbol(hands, facing == Direction.LEFT);
            TextColor handsColor = TextColor.ANSI.YELLOW_BRIGHT;

            int handsX = player.getLevelX();
            int handsY = player.getLevelY();

            switch (facing)
            {
                case LEFT:
                    handsX = player.getLevelX() - 1;
                    break;
                case RIGHT:
                    handsX = player.getLevelX() + 1;
                    break;
                default:
                    break;
            }

            screen.setCharacter(handsX, handsY, new TextCharacter(handsSymbol, handsColor, TextColor.ANSI.BLACK));
        }
    }

    // Символ рук
    private static char getHandsSymbol(Hands hands, boolean isLeft)
    {
        if (hands == null) return  ' ';

        switch (hands.getType())
        {
            case FISTS:
                return  ':';
            case NAILS:
                return  '≈'; // или просто '='
            default:
                return ' ';
        }
    }

    // Символ оружия
    private static char getWeaponSymbol(Weapon weapon, boolean isLeft) {
        if (weapon == null) return ' ';

        switch (weapon.getType()) {
            case KNIFE:
                return isLeft ? '→' : '←';
            case SWORD:
                return '⚔';
            case WARHAMMER:
                return '⚒';
            case BOW:
                return isLeft ? '{' : '}';
            case SHOTGUN:
            case MINIGUN:
            case LASER:
            case BLASTER:
                return isLeft ? '¬' : '⌐';
            case WARP_ANNIHILATOR:
                return '☠';
            default:
                return ' ';
        }
    }

    // Символ снаряда
    private static char getProjectileSymbol(Weapon weapon, MoveLogic.Direction direction) {
        switch (weapon.getType()) {
            case LASER:
                return '-';
            case BLASTER:
                return '~';
            case MINIGUN:
                return '•';
            case SHOTGUN:
                return '░';
            case BOW:
                if (direction == MoveLogic.Direction.RIGHT) {
                    return '→';
                } else {
                    return '←';
                }
            default:
                return '•';
        }
    }

    // Цвет снаряда
    private static TextColor getProjectileColor(Weapon weapon) {
        switch (weapon.getType()) {
            case LASER:
                return TextColor.ANSI.RED_BRIGHT;
            case BLASTER:
                return TextColor.ANSI.BLUE_BRIGHT;
            case MINIGUN:
                return TextColor.ANSI.WHITE_BRIGHT;
            case SHOTGUN:
                return TextColor.ANSI.YELLOW_BRIGHT;
            case BOW:
                return TextColor.ANSI.GREEN_BRIGHT;
            default:
                return TextColor.ANSI.CYAN;
        }
    }


    public static void drawProjectiles(Screen screen, WeaponEngine weaponEngine) {
        for (WeaponEngine.Projectile p : weaponEngine.getProjectiles()) {
            char symbol = getProjectileSymbol(p.weapon, p.direction);  // ← передаём направление
            TextColor color = getProjectileColor(p.weapon);
            screen.setCharacter(p.x, p.y,
                    new TextCharacter(symbol, color, TextColor.ANSI.BLACK));
        }
    }

    // Отрисовка лучей (лазер, бластер)
    public static void drawRays(Screen screen, WeaponEngine weaponEngine) {
        for (WeaponEngine.Ray ray : weaponEngine.getRays()) {
            char rayChar;
            TextColor color;

            if (ray.weapon.getType() == Weapon.WeaponType.LASER) {
                rayChar = '═';
                color = TextColor.ANSI.RED_BRIGHT;
            } else { // BLASTER
                rayChar = '≈';
                color = TextColor.ANSI.BLUE_BRIGHT;
            }

            // Рисуем линию от start до end
            int step = (ray.startRayX < ray.endRayX) ? 1 : -1;
            for (int x = ray.startRayX; x != ray.endRayX + step; x += step) {
                screen.setCharacter(x, ray.startRayY,
                        new TextCharacter(rayChar, color, TextColor.ANSI.BLACK));
            }
        }
    }


    // Отрисовка волны аннигилятора с пульсацией
    public static void drawWaves(Screen screen, WeaponEngine weaponEngine, GameLevel level, Player player) {
        for (WeaponEngine.AnnihilationWave wave : weaponEngine.getWaves()) {
            TextColor color = TextColor.ANSI.MAGENTA_BRIGHT;
            char symbol = '░';

            DrawRoomsMap room = level.getRooms().get(wave.roomIndex);

            // Позиция оружия игрока
            int weaponX = player.getLevelX();
            int weaponY = player.getLevelY();
            Direction facing = player.getFacing();

            // Позиция оружия относительно игрока
            switch (facing) {
                case LEFT:
                    weaponX = player.getLevelX() - 1;
                    break;
                case RIGHT:
                    weaponX = player.getLevelX() + 1;
                    break;
                default:
                    break;
            }

            for (int y = room.getY(); y < room.getY() + room.getHeight(); y++) {
                for (int x = room.getX(); x < room.getX() + room.getLength(); x++) {
                    int tile = level.getLevelMap()[y][x];
                    if (tile == 0 || tile == 3) {
                        // Не затираем игрока
                        if (x == player.getLevelX() && y == player.getLevelY()) {
                            continue;
                        }
                        // Не затираем оружие
                        if (player.getBackpack().getEquippedWeapon() != null) {
                            if (x == weaponX && y == weaponY) {
                                continue;
                            }
                        }
                        screen.setCharacter(x, y,
                                new TextCharacter(symbol, color, TextColor.ANSI.BLACK));
                    }
                }
            }
        }
    }
}