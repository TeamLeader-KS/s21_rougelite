package rougelite.BusinessLogicLayer.inventory;

import rougelite.BusinessLogicLayer.objects.Weapon;
import rougelite.BusinessLogicLayer.objects.Ammo;
import rougelite.BusinessLogicLayer.objects.HealthPotion;
import rougelite.BusinessLogicLayer.objects.Reinforce;

import java.util.ArrayList;
import java.util.List;

public class Backpack {

    private static final int MAX_ITEMS = 9;
    private List<Weapon> weapons;
    private List<Ammo> ammo;
    private List<HealthPotion> healthPotion;
    private List<Reinforce> reinforces;
    private int treasure;
    private Weapon equippedWeapon;
    private Weapon lastEquippedWeapon;

    public Backpack() {
        this.weapons = new ArrayList<>();
        this.ammo = new ArrayList<>();
        this.healthPotion = new ArrayList<>();
        this.reinforces = new ArrayList<>();
        this.treasure = 0;
        this.equippedWeapon = null;
    }

    public boolean hasWeapon(Weapon.WeaponType type) {
        for (Weapon weapon : weapons) {
            if (weapon.getType() == type) {
                return true;
            }
        }
        return false;
    }

    public boolean addWeapon(Weapon weapon) {
        if (this.weapons.size() < MAX_ITEMS) {
            this.weapons.add(weapon);
            return true;
        }
        return false;
    }

    public List<Weapon> getWeapons() {
        return new ArrayList<>(weapons);
    }

    public int getWeaponsCount() {
        return weapons.size();
    }

    public void unEquippedWeapon()
    {
        if (this.equippedWeapon != null)
        {
            this.lastEquippedWeapon = this.equippedWeapon;
        }
        this.equippedWeapon = null;
    }

    public void equipLastWeapon()
    {
        if (this.lastEquippedWeapon != null)
        {
            this.equippedWeapon = this.lastEquippedWeapon;
        }
    }

    public boolean hasLastWeapon()
    {
        return lastEquippedWeapon != null;
    }

    public Weapon getLastEquippedWeapon()
    {
        return lastEquippedWeapon;
    }

    public boolean addAmmo(Ammo ammoItem) {
        if (this.ammo.size() < MAX_ITEMS) {
            this.ammo.add(ammoItem);
            return true;
        }
        return false;
    }

    public List<Ammo> getAmmo() {
        return new ArrayList<>(ammo);
    }

    public boolean removeAmmo(int index) {
        if (index >= 0 && index < ammo.size()) {
            ammo.remove(index);
            return true;
        }
        return false;
    }

    public int getAmmoCount() {
        return ammo.size();
    }

    // ==================== ЗЕЛЬЯ ====================
    public boolean addHealthPotion(HealthPotion potion) {
        if (this.healthPotion.size() < MAX_ITEMS) {
            this.healthPotion.add(potion);
            return true;
        }
        return false;
    }

    public List<HealthPotion> getHealthPotion() {
        return new ArrayList<>(healthPotion);
    }

    public boolean removeHealthPotion(int index) {
        if (index >= 0 && index < healthPotion.size()) {
            healthPotion.remove(index);
            return true;
        }
        return false;
    }

    public int getHealthPotionCount() {
        return healthPotion.size();
    }


    public boolean hasReinforce(Reinforce.ReinforceType type) {
        for (Reinforce reinforce : reinforces) {
            if (reinforce.getType() == type) {
                return true;
            }
        }
        return false;
    }

    public boolean addReinforce(Reinforce reinforce) {
        if (this.reinforces.size() < MAX_ITEMS) {
            this.reinforces.add(reinforce);
            return true;
        }
        return false;
    }

    public List<Reinforce> getReinforces() {
        return new ArrayList<>(reinforces);
    }

    public Reinforce getReinforce(int index) {
        if (index >= 0 && index < reinforces.size()) {
            return reinforces.get(index);
        }
        return null;
    }

    public int getReinforcesCount() {
        return reinforces.size();
    }

    public void addTreasure(int amount) {
        this.treasure += amount;
    }

    public int getTreasure() {
        return treasure;
    }

    public void equipWeapon(int index) {
        if (index >= 0 && index < weapons.size()) {
            this.equippedWeapon = weapons.get(index);
        }
    }

    public void unEquipWeapon() {
        this.equippedWeapon = null;
    }

    public int getWeaponIndex(Weapon weapon) {
        return weapons.indexOf(weapon);
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public void clear() {
        this.weapons.clear();
        this.ammo.clear();
        this.healthPotion.clear();
        this.reinforces.clear();
    }
}