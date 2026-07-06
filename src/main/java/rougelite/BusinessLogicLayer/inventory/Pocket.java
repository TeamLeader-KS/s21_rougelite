package rougelite.BusinessLogicLayer.inventory;

import rougelite.BusinessLogicLayer.objects.PortalStone;
import java.util.ArrayList;
import java.util.List;

public class Pocket {
    private static final int MAX_ITEMS = 8;
    private List<PortalStone> stones;

    public Pocket() {
        this.stones = new ArrayList<>();
    }

    public void addStone(PortalStone stone) {
        stones.add(stone);
    }

    public PortalStone getStone() {
        return stones.isEmpty() ? null : stones.get(0);
    }

    public void removeStone() {
        if (!stones.isEmpty()) {
            stones.remove(0);
        }
    }

    public void clearPocket() {
        stones.clear();
    }

    public boolean hasStone() {
        return !stones.isEmpty();
    }

    public int getStoneCount() {
        return stones.size();
    }

    public List<PortalStone> getAllStones() {
        return new ArrayList<>(stones);
    }
}