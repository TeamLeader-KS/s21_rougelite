package rougelite.BusinessLogicLayer.objects;

import java.util.ArrayList;
import java.util.List;

public class Portal {
    private boolean[] stonesInserted;  // 8 камней (без центрального индекса 4)
    private int x, y;
    private boolean isOpen = false;

    public Portal(int x, int y) {
        this.x = x;
        this.y = y;
        this.stonesInserted = new boolean[9];  // индексы 0-8, индекс 4 не используется
        for (int i = 0; i < 9; i++) {
            stonesInserted[i] = false;
        }
    }

    public void insertStone(int roomIndex) {
        if (roomIndex >= 0 && roomIndex < 9 && roomIndex != 4) {
            stonesInserted[roomIndex] = true;
        }
    }

    public boolean isFullyActivated() {
        for (int i = 0; i < 9; i++) {
            if (i != 4 && !stonesInserted[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean hasStone(int roomIndex) {
        return stonesInserted[roomIndex];
    }

    public boolean isOpen() { return isOpen; }

    public void portalOpen(boolean open)
    { this.isOpen = open; }

    // В класс Portal
    public List<Integer> getInsertedStones() {
        List<Integer> insertedIndices = new ArrayList<>();
        for (int i = 0; i < stonesInserted.length; i++) {
            if (stonesInserted[i]) {
                insertedIndices.add(i);
            }
        }
        return insertedIndices;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}