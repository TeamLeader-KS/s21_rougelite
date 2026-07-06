package rougelite.BusinessLogicLayer.mapGenerator;

import rougelite.BusinessLogicLayer.objects.Portal;
import rougelite.BusinessLogicLayer.objects.PortalStone;
import rougelite.BusinessLogicLayer.mapGenerator.DoorCoordinates.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room implements DrawRoomsMap {
    private static final int MIN_HEIGHT = 8;
    private static final int MAX_HEIGHT = 10;
    private static final int MIN_LENGTH = 15;
    private static final int MAX_LENGTH = 30;

    private int height;
    private int length;
    private int[][] tiles;
    private int roomIndex;
    private boolean isCenterRoom;
    private Portal portal;
    private PortalStone stone;

    private int x;
    private int y;

    public Room(int index, boolean isCenterRoom) {
        Random rand = new Random();
        this.roomIndex = index;
        this.isCenterRoom = isCenterRoom;

        this.height = MIN_HEIGHT + rand.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1);
        this.length = MIN_LENGTH + rand.nextInt(MAX_LENGTH - MIN_LENGTH + 1);

        this.tiles = new int[height][length];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                if (i == 0 || i == height - 1 || j == 0 || j == length - 1) {
                    tiles[i][j] = 1;
                } else {
                    tiles[i][j] = 0;
                }
            }
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Только для центральной комнаты
    public void generateCenterDoors() {
        Random rand = new Random();
        int doorCount = rand.nextInt(4) + 1;
        List<Direction> availableSides = new ArrayList<>();
        availableSides.add(Direction.NORTH);
        availableSides.add(Direction.SOUTH);
        availableSides.add(Direction.EAST);
        availableSides.add(Direction.WEST);

        for (int i = 0; i < doorCount && !availableSides.isEmpty(); i++) {
            int sideIndex = rand.nextInt(availableSides.size());
            Direction side = availableSides.remove(sideIndex);
            addDoorOnSide(side);
        }
    }

    public void generateNonCenterDoors() {
        if (isAdjacentToCenter()) {
            // Комнаты вокруг центра
            checkAndAddDoorIfCentralHas(Direction.NORTH);
            checkAndAddDoorIfCentralHas(Direction.SOUTH);
            checkAndAddDoorIfCentralHas(Direction.EAST);
            checkAndAddDoorIfCentralHas(Direction.WEST);
        } else {
            // Угловые комнаты
            addDoorIfNeeded(Direction.NORTH);
            addDoorIfNeeded(Direction.SOUTH);
            addDoorIfNeeded(Direction.EAST);
            addDoorIfNeeded(Direction.WEST);
        }
    }

    private boolean isAdjacentToCenter() {
        return roomIndex == 1 || roomIndex == 3 || roomIndex == 5 || roomIndex == 7;
    }

    private void checkAndAddDoorIfCentralHas(Direction side) {
        if (!hasNeighborRoom(side)) {
            return;
        }

        int neighborIndex = getNeighborIndex(side);

        if (neighborIndex == 4) {
            Direction oppositeSide = getOppositeSide(side);
            DoorCoordinates.Door centralDoor = DoorCoordinates.getDoor(4, oppositeSide);

            if (centralDoor != null) {
                addDoorOnSide(side);
            }
        } else {
            addDoorOnSide(side);
        }
    }

    private void addDoorIfNeeded(Direction side) {
        if (hasNeighborRoom(side)) {
            addDoorOnSide(side);
        }
    }

    private int getNeighborIndex(Direction side) {
        int gridX = (roomIndex % 3);
        int gridY = (roomIndex / 3);

        switch (side) {
            case NORTH: return (gridY - 1) * 3 + gridX;
            case SOUTH: return (gridY + 1) * 3 + gridX;
            case EAST:  return gridY * 3 + (gridX + 1);
            case WEST:  return gridY * 3 + (gridX - 1);
        }
        return -1;
    }

    private Direction getOppositeSide(Direction side) {
        switch (side) {
            case NORTH: return Direction.SOUTH;
            case SOUTH: return Direction.NORTH;
            case EAST:  return Direction.WEST;
            case WEST:  return Direction.EAST;
        }
        return side;
    }

    private boolean hasNeighborRoom(Direction side) {
        int gridX = (roomIndex % 3);
        int gridY = (roomIndex / 3);

        switch (side) {
            case NORTH: return gridY > 0;
            case SOUTH: return gridY < 2;
            case EAST:  return gridX < 2;
            case WEST:  return gridX > 0;
        }
        return false;
    }

    private void addDoorOnSide(Direction side) {
        Random rand = new Random();
        int localX = 0, localY = 0;
        int doorX = 0, doorY = 0;

        switch (side) {
            case NORTH:
                localX = rand.nextInt(length - 2) + 1;
                localY = 0;
                doorX = x + localX;
                doorY = y;
                break;
            case SOUTH:
                localX = rand.nextInt(length - 2) + 1;
                localY = height - 1;
                doorX = x + localX;
                doorY = y + height - 1;
                break;
            case EAST:
                localX = length - 1;
                localY = rand.nextInt(height - 2) + 1;
                doorX = x + length - 1;
                doorY = y + localY;
                break;
            case WEST:
                localX = 0;
                localY = rand.nextInt(height - 2) + 1;
                doorX = x;
                doorY = y + localY;
                break;
        }

        DoorCoordinates.Door door = new DoorCoordinates.Door(roomIndex, doorX, doorY, side);
        DoorCoordinates.addDoor(door);
        tiles[localY][localX] = 2;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHeight() { return height; }
    public int getLength() { return length; }
    public int[][] getTiles() { return tiles; }
    public int getRoomIndex() { return roomIndex; }
    public boolean isCenterRoom() { return isCenterRoom; }
    public Portal getPortal() { return portal; }
    public PortalStone getPortalStone() { return stone; }
    public void setPortal(Portal portal) { this.portal = portal; }
    public void setPortalStone(PortalStone stone) { this.stone = stone; }

    public int getTileAt(int x, int y) {
        int localX = x - this.x;
        int localY = y - this.y;
        if (localX < 0 || localX >= length || localY < 0 || localY >= height)
        {
            return -1;
        }
        return tiles[localY][localX];
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setTiles(int[][] tiles) {
        this.tiles = tiles;
    }

}

// TODO сделать секретную комнату дверь в которую открывается после убийства змее-мага!