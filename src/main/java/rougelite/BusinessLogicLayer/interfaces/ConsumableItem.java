package rougelite.BusinessLogicLayer.interfaces;

import rougelite.BusinessLogicLayer.characters.Player;

// Расходный материал
public interface ConsumableItem {
    String useItem(Player player);
    boolean canUse(Player player);
}