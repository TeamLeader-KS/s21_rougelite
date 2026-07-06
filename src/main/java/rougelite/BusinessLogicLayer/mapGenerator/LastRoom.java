package rougelite.BusinessLogicLayer.mapGenerator;

import rougelite.BusinessLogicLayer.objects.Portal;
import rougelite.BusinessLogicLayer.objects.PortalStone;

public class LastRoom implements DrawRoomsMap {
    private static final int LAST_HEIGHT = 11;
    private static final int LAST_LENGTH = 31;

    private int height;
    private int length;
    private int[][] tiles;
    private int roomIndex;
    private boolean isCenterRoom;
    private Portal portal;
    private PortalStone stone;

    private int x;
    private int y;

    public LastRoom(int index, boolean isCenterRoom) {
        this.roomIndex = index;
        this.isCenterRoom = isCenterRoom;
        this.height = LAST_HEIGHT;
        this.length = LAST_LENGTH;
        this.tiles = new int[LAST_HEIGHT][LAST_LENGTH];
        for (int i = 0; i < LAST_HEIGHT; i++) {
            for (int j = 0; j < LAST_LENGTH; j++) {
                if (i == 0 || i == LAST_HEIGHT - 1 || j == 0 || j == LAST_LENGTH - 1) {
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


    @Override public int getX() { return x; }
    @Override public int getY() { return y; }
    @Override public int getHeight() { return height; }
    @Override public int getLength() { return length; }
    @Override public int[][] getTiles() { return tiles; }
    @Override public int getRoomIndex() { return roomIndex; }
    @Override public boolean isCenterRoom() { return isCenterRoom; }

    @Override
    public int getTileAt(int x, int y) {
        int localX = x - this.x;
        int localY = y - this.y;
        if (localX < 0 || localX >= length || localY < 0 || localY >= height) return -1;
        return tiles[localY][localX];
    }

    @Override
    public Portal getPortal() { return portal; }

    @Override
    public void setPortal(Portal portal) { this.portal = portal; }

    @Override
    public PortalStone getPortalStone() { return stone; }

    @Override
    public void setPortalStone(PortalStone stone) { this.stone = stone; }

    public void generateNonCenterDoors() { }

    public int getPlayerPositionX() {
        return x + (length / 2 - 10); // левая часть
    }
    public int getPlayerPositionY() {
        return y + height / 2;
    }
    public int getBossPositionX() {
        return x + (length / 2 + 10); // правая часть
    }
    public int getBossPositionY() {
        return y + height / 2;
    }
}