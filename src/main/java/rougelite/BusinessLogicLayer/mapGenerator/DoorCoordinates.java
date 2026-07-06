package rougelite.BusinessLogicLayer.mapGenerator;

import java.util.ArrayList;
import java.util.List;

public class DoorCoordinates {
    private static List<Door> doors = new ArrayList<>();

    public static class Door {
        private int roomIndex;
        private int x;
        private int y;
        private Direction side;

        public Door(int roomIndex, int x, int y, Direction side) {
            this.roomIndex = roomIndex;
            this.x = x;
            this.y = y;
            this.side = side;
        }

        public int getRoomIndex() { return roomIndex; }
        public int getX() { return x; }
        public int getY() { return y; }
        public Direction getSide() { return side; }
    }

    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    public static void addDoor(Door door) {
        doors.add(door);
    }

    public static List<Door> getDoors() {
        return new ArrayList<>(doors);
    }

    public static void clear() {
        doors.clear();
    }

    public static Door getDoor(int roomIndex, Direction side) {
        for (Door door : doors) {
            if (door.getRoomIndex() == roomIndex && door.getSide() == side) {
                return door;
            }
        }
        return null;
    }
}