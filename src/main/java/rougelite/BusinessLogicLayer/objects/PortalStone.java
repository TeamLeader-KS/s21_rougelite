package rougelite.BusinessLogicLayer.objects;

import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.interfaces.ObjectInfo;
import rougelite.BusinessLogicLayer.interfaces.PortalStoneUsable;
import rougelite.BusinessLogicLayer.message.GameMessages;

import java.util.Random;

public class PortalStone extends GameObject implements ObjectInfo, PortalStoneUsable {
    private int sourceRoomIndex;
    private PositionOfStones position;
    private int stoneX;
    private int stoneY;



    public int getSourceRoomIndex() {
        return sourceRoomIndex;
    }

    public PositionOfStones getPosition() {
        return position;
    }

    public int getStoneX() {
        return stoneX;
    }

    public int getStoneY() {
        return stoneY;
    }

    @Override
    public String reportObject() {
        return GameMessages.PICKUP_PORTAL_STONE;
    }

    public String getInsertStoneMessage() {
        return String.format("%s установлен!", position.getRussianName());
    }

    @Override
    public String useOnPortal(Player player, Portal portal) {
        if (!canUseOnPortal(portal)) {
            return null;
        }
        portal.insertStone(sourceRoomIndex);
        player.getPocket().removeStone();
        return getInsertStoneMessage();
    }

    @Override
    public boolean canUseOnPortal(Portal portal) {
        return portal != null && !portal.hasStone(sourceRoomIndex);
    }

    // Новый конструктор для восстановления камня из сохранения (с конкретными координатами)
    public PortalStone(int sourceRoomIndex, int stoneX, int stoneY) {
        this.sourceRoomIndex = sourceRoomIndex;
        this.stoneX = stoneX;
        this.stoneY = stoneY;
        this.position = PositionOfStones.fromRoomIndex(sourceRoomIndex);
    }

    // Конструктор для создания нового камня (генерирует случайные координаты внутри комнаты)
    public PortalStone(int sourceRoomIndex, int roomX, int roomY, int roomLength, int roomHeight) {
        this.sourceRoomIndex = sourceRoomIndex;
        this.position = PositionOfStones.fromRoomIndex(sourceRoomIndex);
        Random rand = new Random();
        this.stoneX = roomX + 1 + rand.nextInt(Math.max(1, roomLength - 3));
        this.stoneY = roomY + 1 + rand.nextInt(Math.max(1, roomHeight - 3));
    }

    // Конструктор для камня в кармане (координаты не нужны)
    public PortalStone(int sourceRoomIndex) {
        this.sourceRoomIndex = sourceRoomIndex;
        this.position = PositionOfStones.fromRoomIndex(sourceRoomIndex);
        this.stoneX = -1;
        this.stoneY = -1;
    }
}