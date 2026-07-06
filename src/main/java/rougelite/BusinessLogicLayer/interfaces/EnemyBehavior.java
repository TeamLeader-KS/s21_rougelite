package rougelite.BusinessLogicLayer.interfaces;

import rougelite.BusinessLogicLayer.characters.Enemy;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap;
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel;
import rougelite.BusinessLogicLayer.objects.GameObject;

public interface EnemyBehavior {
    void enemyMovePattern(Enemy enemy, DrawRoomsMap room, Player player, GameLevel level);
    boolean[][] getAgroRadius();

    String pursuit(Enemy enemy, Player player, GameLevel level);
    boolean attackOnPlayer(Enemy enemy, Player player);
    void receivingDamage(Enemy enemy, int damage);

    GameObject dropGenerator(Enemy enemyType);



    default boolean isPlayerInAgroRadius(Enemy enemy, Player player, DrawRoomsMap room)
    {
        boolean[][] radius = getAgroRadius();
        int height = radius.length;
        int width = radius[0].length;
        int centerX = width / 2;
        int centerY = height / 2;

        int differenceX = player.getLevelX() - enemy.getX();
        int differenceY = player.getLevelY() - enemy.getY();
        int radiusX = differenceX + centerX;
        int radiusY = differenceY + centerY;

        if (radiusX < 0 || radiusX >= width || radiusY < 0 || radiusY >= height)
        {
            return false;
        }
        if (!radius[radiusY][radiusX])
        {
            return false;
        }

        return hasLineOfSight(enemy, player, room);
    }

    default boolean hasLineOfSight(Enemy enemy, Player player, DrawRoomsMap room) {
        int x1 = enemy.getX();
        int y1 = enemy.getY();
        int x2 = player.getLevelX();
        int y2 = player.getLevelY();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int currentX = x1;
        int currentY = y1;

        while (true) {
            if (currentX == x2 && currentY == y2) {
                return true;
            }
            if (currentX != x1 || currentY != y1) {
                int tile = room.getTileAt(currentX, currentY);
                if (tile == 1) {
                    return false;
                }
            }
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                currentX += sx;
            }
            if (e2 < dx) {
                err += dx;
                currentY += sy;
            }
            if (currentX < room.getX() || currentX >= room.getX() + room.getLength() ||
                    currentY < room.getY() || currentY >= room.getY() + room.getHeight()) {
                return false;
            }
        }
    }

}
