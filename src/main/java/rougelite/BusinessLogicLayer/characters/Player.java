package rougelite.BusinessLogicLayer.characters;

import rougelite.BusinessLogicLayer.inventory.Backpack;
import rougelite.BusinessLogicLayer.inventory.HiddenBackpack;
import rougelite.BusinessLogicLayer.inventory.Pocket;
import rougelite.BusinessLogicLayer.logic.MoveLogic.Direction;
import rougelite.BusinessLogicLayer.objects.Hands;
import rougelite.BusinessLogicLayer.objects.Weapon;

public class Player extends Character {
    private int roomX;
    private int roomY;
    private Backpack backpack;
    private Pocket pocket;
    private Direction facing = Direction.RIGHT;
    private HiddenBackpack hiddenBackpack;


    private int levelX;
    private int levelY;

    public int getLevelX() { return levelX; }
    public int getLevelY() { return levelY; }
    public void setLevelPosition(int x, int y) {
        this.levelX = x;
        this.levelY = y;
    }

    public Player(String name, int health, int strength, int dexterity) {
        super(name, health, strength, dexterity);
        this.roomX = 0;
        this.roomY = 0;
        this.backpack = new Backpack();
        this.pocket = new Pocket();
        this.hiddenBackpack = new HiddenBackpack();
    }

    public HiddenBackpack.HandsChange switchHands() {
        return hiddenBackpack.switchHands();
    }

    public Hands getEquippedHands() {
        return hiddenBackpack.getEquippedHands();
    }

    public Weapon getEquippedWeapon() {
        Weapon equippedItem = backpack.getEquippedWeapon();
        if (equippedItem != null) {
            return equippedItem;
        }
        return null;
    }

    public int getRoomX() { return roomX; }
    public int getRoomY() { return roomY; }
    public void setRoomPosition(int x, int y) {
        this.roomX = x;
        this.roomY = y;
    }

    public void setFacing(Direction direction) {  // ← новый метод
        this.facing = direction;
    }

    public Direction getFacing() {  // ← новый метод
        return facing;
    }

    public Backpack getBackpack() {
        return backpack;
    }

    public Pocket getPocket() { return pocket; }

    private int currentRoomIndex;

    public int getCurrentRoomIndex() {
        return currentRoomIndex;
    }

    public void setCurrentRoomIndex(int currentRoomIndex) {
        this.currentRoomIndex = currentRoomIndex;
    }

    public void setCurrentPlayerLevel(int level) {
        super.setCurrentPlayerLevel(level);
    }

    public void setPocket(Pocket pocket) {
        this.pocket = pocket;
    }

    public HiddenBackpack getHiddenBackpack() {
        return hiddenBackpack;
    }

}

// TODO продолжить игру за зомби