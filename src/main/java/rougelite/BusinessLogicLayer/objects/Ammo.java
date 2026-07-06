package rougelite.BusinessLogicLayer.objects;

import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.interfaces.ConsumableItem;
import rougelite.BusinessLogicLayer.interfaces.ObjectInfo;
import rougelite.BusinessLogicLayer.message.GameMessages;

public class Ammo extends GameObject implements ObjectInfo, ConsumableItem {
    private AmmoType type;
    private String name;
    private int amountAmmo;

    public enum AmmoType{
        BOW_AMMUNITION("Колчан со стрелами", 35),
        SHOTGUN_AMMUNITION("Патроны для дробовика", 25),
        MINIGUN_AMMUNITION("Пулеметная лента", 100),
        LASER_AMMUNITION("Лазерная энергоячейка", 20),
        BLASTER_AMMUNITION("Бластерная энергоячейка", 20),
        WARP_ANNIHILATOR_AMMUNITION("Энергия Варпа", 1);

        private final String russianAmmoName;
        private final int amountAmmo;

        AmmoType(String russianAmmoName, int amountAmmo) {
            this.russianAmmoName = russianAmmoName;
            this.amountAmmo = amountAmmo;
        }

        public String getRussianAmmoName() {
            return russianAmmoName;
        }

        public int getAmountAmmo() {
            return amountAmmo;
        }
    }

    public Ammo(AmmoType type) {
        this.type = type;
        this.name = type.getRussianAmmoName();
        this.amountAmmo = type.getAmountAmmo();
    }

    public AmmoType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getAmountAmmo() {
        return amountAmmo;
    }

    @Override
    public String reportObject() {
        return GameMessages.PICKUP_AMMO;
    }

    @Override
    public String useItem(Player player) {

        Weapon equipped = player.getBackpack().getEquippedWeapon();
        if (equipped == null) {
            return GameMessages.NOT_EQUIP_WEAPON;
        }

        if (isBladedWeapon(equipped)) {
            return GameMessages.BLADED_WEAPON;
        }

        if (!isReloadableWeapon(equipped, this.type)) {
            return GameMessages.IMPOSSIBLE_USE_AMMO;
        }

        if (equipped.getCurrentAmmo() >= equipped.getMaxAmmo()) {
            return GameMessages.FULL_AMMO;
        }

        equipped.addAmmo(this.amountAmmo);
        return String.format(GameMessages.USE_AMMO, equipped.getName());
    }

    @Override
    public boolean canUse(Player player) {
        Weapon equipped = player.getBackpack().getEquippedWeapon();

        if (equipped == null) {
            return false;
        }

        if (isBladedWeapon(equipped)) {
            return false;
        }

        if (!isReloadableWeapon(equipped, this.type)) {
            return false;
        }

        if (equipped.getCurrentAmmo() >= equipped.getMaxAmmo()) {
            return false;
        }

        return true;
    }



    public boolean isBladedWeapon(Weapon weapon)
    {
        Weapon.WeaponType type = weapon.getType();
        return type == Weapon.WeaponType.KNIFE ||
               type == Weapon.WeaponType.SWORD ||
               type == Weapon.WeaponType.WARHAMMER;
    }

    public boolean isReloadableWeapon(Weapon weapon, AmmoType ammoType)
    {
        switch (weapon.getType())
        {
            case BOW:
                return ammoType == AmmoType.BOW_AMMUNITION;
            case SHOTGUN:
                return ammoType == AmmoType.SHOTGUN_AMMUNITION;
            case MINIGUN:
                return ammoType == AmmoType.MINIGUN_AMMUNITION;
            case LASER:
                return ammoType == AmmoType.LASER_AMMUNITION;
            case BLASTER:
                return ammoType == AmmoType.BLASTER_AMMUNITION;
            case WARP_ANNIHILATOR:
                return ammoType == AmmoType.WARP_ANNIHILATOR_AMMUNITION;
            default:
                return false;
        }
    }

    public void setAmount(int amount) {
        this.amountAmmo = amount;
    }
}