package rougelite.BusinessLogicLayer.interfaces;

import rougelite.BusinessLogicLayer.characters.Player;

public interface CanEquip {
    void equip(Player player);
    void unequip(Player player);
    int getWeaponPower();
}
