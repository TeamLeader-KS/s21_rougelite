package rougelite.BusinessLogicLayer.logic;

import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.inventory.Backpack;
import rougelite.BusinessLogicLayer.message.GameMessages;
import rougelite.BusinessLogicLayer.objects.*;
import java.util.List;

import java.awt.*;

public class InventoryEngine {

    private final Player player;
    private final Backpack backpack;

    public InventoryEngine(Player player) {
        this.player = player;
        this.backpack = player.getBackpack();
    }

    public static class AddResult
    {
        public final boolean success;
        public final String message;

        public AddResult(boolean success, String message)
        {
            this.message = message;
            this.success = success;
        }
    }

    // ==================== ДОБАВЛЕНИЕ ПРЕДМЕТОВ ====================

    public AddResult addItem(GameObject item) {

        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            if (backpack.hasWeapon(weapon.getType())) {
                return new AddResult (false, GameMessages.BACKPACK_HAS_WEAPON);
            }
            if (backpack.addWeapon(weapon)) {
                return new AddResult(true,  GameMessages.BACKPACK_ADD_WEAPON);
            }
            return new AddResult(false, GameMessages.BACKPACK_FULL);
        }

        if (item instanceof Ammo) {
            if (backpack.addAmmo((Ammo) item)) {
                return new AddResult(true, GameMessages.BACKPACK_ADD_AMMO);
            }
            return new AddResult(false, GameMessages.BACKPACK_FULL);
        }

        if (item instanceof HealthPotion) {
            if (backpack.addHealthPotion((HealthPotion) item)) {
                return new AddResult(true, GameMessages.BACKPACK_ADD_POTION);
            }
            return new AddResult(false, GameMessages.BACKPACK_FULL);
        }

        if (item instanceof Reinforce) {
            Reinforce reinforce = (Reinforce) item;
            if (backpack.hasReinforce(reinforce.getType())) {
                return new AddResult(false, GameMessages.BACKPACK_HAS_REINFORCE);
            }
            if (backpack.addReinforce(reinforce)) {
                applyReinforceBonuses(reinforce);
                return new AddResult(true, GameMessages.BACKPACK_ADD_REINFORCE);
            }
            return new AddResult(false, GameMessages.BACKPACK_FULL);
        }

        return new AddResult(false, "Неизвестный предмет");
    }

    private void applyReinforceBonuses(Reinforce reinforce) {
        if (reinforce.getHealthBooster() > 0) {
            player.addHealthBonus(reinforce.getHealthBooster());
        }
        if (reinforce.getStrengthBooster() > 0) {
            player.addStrengthBonus(reinforce.getStrengthBooster());
        }
        if (reinforce.getDexterityBooster() > 0) {
            player.addDexterityBonus(reinforce.getDexterityBooster());
        }
    }

    // ==================== ИСПОЛЬЗОВАНИЕ ПРЕДМЕТОВ ====================

    public String useWeapon(int slot) {
        if (slot >= 0 && slot < backpack.getWeaponsCount()) {
            backpack.equipWeapon(slot);
            Weapon weapon = backpack.getWeapons().get(slot);
            return GameMessages.formatEquipWeapon(weapon.getName());
        }
        return null;
    }

    public String equipFirstWeapon()
    {
        List<Weapon> weapons = backpack.getWeapons();
        if (!weapons.isEmpty())
        {
            Weapon firstWeapon = weapons.get(0);
            backpack.equipWeapon(0);
            return GameMessages.formatEquipWeapon(firstWeapon.getName());
        }
        return GameMessages.NO_WEAPON;
    }

    public String equipUnequipWeapon() {
        if (backpack.getEquippedWeapon() != null) {
            return unEquipWeapon();
        } else {
            return equipLastWeapon();
        }
    }

    public String equipLastWeapon()
    {
        if (backpack.hasLastWeapon())
        {
            backpack.equipLastWeapon();
            Weapon weapon = backpack.getEquippedWeapon();
            return GameMessages.formatEquipWeapon(weapon.getName());
        }
        return equipFirstWeapon();
    }

    public String unEquipWeapon()
    {
        Weapon current = backpack.getEquippedWeapon();
        if (current != null)
        {
            backpack.unEquippedWeapon();
            return GameMessages.formatUnequipWeapon((current.getName()));
        }
        return null;
    }

    public String useAmmo(int slot) {
        if (slot >= 0 && slot < backpack.getAmmoCount()) {
            Ammo ammo = backpack.getAmmo().get(slot);
            if (ammo.canUse(player))
            {
                String result = ammo.useItem(player);
                backpack.removeAmmo(slot);
                return result;
            } else {
                Weapon equipped = player.getBackpack().getEquippedWeapon();
                if (equipped == null) {
                    return GameMessages.NOT_EQUIP_WEAPON;
                }

                if (ammo.isBladedWeapon(equipped)) {
                    return GameMessages.BLADED_WEAPON;
                }

                if (!ammo.isReloadableWeapon(equipped, ammo.getType())) {
                    return GameMessages.IMPOSSIBLE_USE_AMMO;
                }

                if (equipped.getCurrentAmmo() >= equipped.getMaxAmmo()) {
                    return GameMessages.FULL_AMMO;
                }
            }
        }
        return null;
    }

    public String useHealthPotion(int slot) {
        if (slot >= 0 && slot < backpack.getHealthPotionCount()) {
            HealthPotion potion = backpack.getHealthPotion().get(slot);
            if (potion.canUse(player)) {
                String result = potion.useItem(player);
                backpack.removeHealthPotion(slot);
                return result;
            }
            return GameMessages.FULL_HEALTH;
        }
        return null;
    }


    public Backpack getBackpack() {
        return backpack;
    }

    public Weapon getEquippedWeapon() {
        return backpack.getEquippedWeapon();
    }
}