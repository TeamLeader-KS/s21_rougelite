package rougelite.BusinessLogicLayer.mapGenerator;

import java.util.List;

public class Corridor {
    private int fromRoom;
    private int toRoom;
    private List<int[]> path;

    public Corridor(int fromRoom, int toRoom, List<int[]> path) {
        this.fromRoom = fromRoom;
        this.toRoom = toRoom;
        this.path = path;
    }

    public int getFromRoom() { return fromRoom; }
    public int getToRoom() { return toRoom; }
    public List<int[]> getPath() { return path; }
}