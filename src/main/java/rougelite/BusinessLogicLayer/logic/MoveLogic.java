package rougelite.BusinessLogicLayer.logic;

import rougelite.BusinessLogicLayer.characters.Enemy;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap;
import rougelite.BusinessLogicLayer.objects.*;
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel;
import rougelite.BusinessLogicLayer.mapGenerator.Room;
import rougelite.BusinessLogicLayer.message.GameMessages;

public class MoveLogic {


    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public static class MoveResult {
        private final boolean success;
        private final boolean moved;
        private final String message;
        private final GameObject pickedItem;
        private final PortalStone pickedStone;
        private final int newX;
        private final int newY;

        private MoveResult(boolean success, boolean moved, String message,
                           GameObject pickedItem, PortalStone pickedStone,
                           int newX, int newY) {
            this.success = success;
            this.moved = moved;
            this.message = message;
            this.pickedItem = pickedItem;
            this.pickedStone = pickedStone;
            this.newX = newX;
            this.newY = newY;
        }

        public static MoveResult invalid() {
            return new MoveResult(false, false, null, null, null, -1, -1);
        }

        public static MoveResult move(int newX, int newY) {
            return new MoveResult(true, true, null, null, null, newX, newY);
        }

        public static MoveResult withPickup(int newX, int newY, GameObject item) {
            return new MoveResult(true, true, null, item, null, newX, newY);
        }

        public static MoveResult withStone(int newX, int newY, PortalStone stone) {
            return new MoveResult(true, true, stone.reportObject(), null, stone, newX, newY);
        }

        public boolean isSuccess() { return success; }
        public boolean isMoved() { return moved; }
        public String getMessage() { return message; }
        public GameObject getPickedItem() { return pickedItem; }
        public PortalStone getPickedStone() { return pickedStone; }
        public int getNewX() { return newX; }
        public int getNewY() { return newY; }
    }

    public MoveResult tryMove(Player player, Direction direction, GameLevel level) {
        int newX = player.getLevelX();
        int newY = player.getLevelY();

        switch (direction) {
            case UP:    newY--; break;
            case DOWN:  newY++; break;
            case LEFT:  newX--; break;
            case RIGHT: newX++; break;
            default: return MoveResult.invalid();
        }

        int[][] levelMap = level.getLevelMap();

        if (newY < 0 || newY >= levelMap.length || newX < 0 || newX >= levelMap[0].length) {
            return MoveResult.invalid();
        }

        int tile = levelMap[newY][newX];

        if (tile == 0 || tile == 2 || tile == 3) {

            for(Enemy enemy : level.getEnemies())
            {
                if (enemy.isAlive() && enemy.getX() == newX && enemy.getY() == newY)
                {
                    System.out.println("Тут стоит враг!");
                    return MoveResult.invalid();
                }
            }

            player.setLevelPosition(newX, newY);


            DrawRoomsMap area = level.getRoomAt(newY, newX);
            if (area != null && area instanceof Room) {
                Room room = (Room) area; // Безопасно приводим к Room
                player.setCurrentRoomIndex(room.getRoomIndex());
                int localX = newX - room.getX();
                int localY = newY - room.getY();
                player.setRoomPosition(localX, localY);
            }


            GameObject item = level.getItemAt(newX, newY);
            if (item != null) {
                return MoveResult.withPickup(newX, newY, item);
            }


            PortalStone stone = getStoneAtPosition(player, level, newX, newY);
            if (stone != null) {
                return MoveResult.withStone(newX, newY, stone);
            }

            return MoveResult.move(newX, newY);
        }

        return MoveResult.invalid();
    }

    private PortalStone getStoneAtPosition(Player player, GameLevel level, int x, int y) {
        DrawRoomsMap area = level.getRoomAt(y, x);
        if (area != null) {
            if (area instanceof Room)
            {
                Room room = (Room) area;
                PortalStone stone = room.getPortalStone();
                if (stone != null && stone.getStoneX() == x && stone.getStoneY() == y) {
                    room.setPortalStone(null);
                    return stone;
                }
            }
        }
        return null;
    }
}