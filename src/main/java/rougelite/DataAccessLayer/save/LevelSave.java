package rougelite.DataAccessLayer.save;

import java.util.List;

public class LevelSave {
    public int dungeonLevel;
    public List<RoomSave> rooms;
    public List<CorridorSave> corridors;
    public List<ItemSave> itemsOnFloor;
    public List<EnemySave> enemies;
    public int[][] explorationMap;
    public int[][] levelMap;
}