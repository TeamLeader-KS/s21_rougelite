package rougelite.BusinessLogicLayer.objects;

import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.interfaces.CanEquip;
import rougelite.BusinessLogicLayer.interfaces.ObjectInfo;
import rougelite.BusinessLogicLayer.message.GameMessages;

public class Weapon extends GameObject implements ObjectInfo, CanEquip {
    private WeaponType type;
    private String name;
    private int weaponPower;
    private int currentAmmo;

    public enum WeaponType {
        KNIFE("Сумеречный кинжал", 25, 0),
        SWORD("Парные мечи Династии", 50, 0),
        WARHAMMER("Боевой молот войны", 100, 0),
        BOW("Лук Темных Эльфов", 25, 35),
        SHOTGUN("Дробовик", 50, 25),
        MINIGUN("Многоствольный пулемёт \"М134 Миниган\"", 10, 100),
        LASER("Лазерная винтовка \"Люциус\"", 550, 20),
        BLASTER("Ионный бластер", 350, 20),
        WARP_ANNIHILATOR("Варп Аннигилятор", 1000, 1);

        private final String russianWeaponName;
        private final int weaponPower;
        private final int maxAmmo;

        WeaponType(String russianWeaponName, int weaponPower, int maxAmmo) {
            this.russianWeaponName = russianWeaponName;
            this.weaponPower = weaponPower;
            this.maxAmmo = maxAmmo;
        }

        public String getRussianWeaponName() {
            return russianWeaponName;
        }

        public int getWeaponPower() {
            return weaponPower;
        }

        public int getMaxAmmo() {
            return maxAmmo;
        }
    }

    public Weapon(WeaponType type) {
        this.type = type;
        this.name = type.getRussianWeaponName();
        this.weaponPower = type.getWeaponPower();
        this.currentAmmo = type.getMaxAmmo();
    }

    public void addAmmo(int amount) {
        this.currentAmmo = Math.min(this.currentAmmo + amount, type.getMaxAmmo());
    }

    public WeaponType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getWeaponPower() {
        return weaponPower;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public int getMaxAmmo() {
        return type.getMaxAmmo();
    }

    public void fire() {
        if (currentAmmo > 0) {
            currentAmmo--;
        }
    }

    @Override
    public String reportObject() {
        return GameMessages.PICKUP_WEAPON;
    }

    @Override
    public void equip(Player player) {
        // Экипировка через InventoryEngine
    }

    @Override
    public void unequip(Player player) {
        // Снятие экипировки через InventoryEngine
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setCurrentAmmo(int currentAmmo) {
        this.currentAmmo = currentAmmo;
    }
}