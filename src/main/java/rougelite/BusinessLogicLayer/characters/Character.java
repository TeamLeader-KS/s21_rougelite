package rougelite.BusinessLogicLayer.characters;

import rougelite.BusinessLogicLayer.logic.ExperienceCalculator;

public abstract class Character {

    private String name;
    private int currentHealth;
    private int maxHealth;
    private int levelUpHP = 20;
    private int strength;
    private int levelUpStr = 5;
    private int dexterity;
    private int levelUpDext = 5;
    private long experience;
    private String type;
    private boolean weapon;
    private int currentPlayerLevel;


    private String userName;

    private int x;
    private int y;

    public Character(String name, int health, int strength, int dexterity) {
        this.name = name;
        this.currentHealth = health;
        this.maxHealth = health;
        this.strength = strength;
        this.dexterity = dexterity;
        this.experience = 0;
        this.currentPlayerLevel = 1;
        this.weapon = false;
    }

    public void levelUpHealth() {
        this.maxHealth += levelUpHP;
        this.currentHealth = this.maxHealth;
    }


    public void levelUpStrength() {
        this.strength += levelUpStr;
    }

    public void levelUpDexterity() {
        this.dexterity += levelUpDext;
    }

    public void addNewLevelForPlayer(long exp)
    {
        this.experience += exp;
        int newLevel = ExperienceCalculator.getLevelByExp(this.experience);

        if (newLevel > this.currentPlayerLevel) {
            int levelUps = newLevel - this.currentPlayerLevel;
            for (int i = 0; i < levelUps; i++) {
                levelUpHealth();
                levelUpStrength();
                levelUpDexterity();
            }
            this.currentPlayerLevel = newLevel;
        }
    }

    public void addHealthBonus(int amount) {
        this.maxHealth += amount;
        this.currentHealth += amount;
    }

    public void addStrengthBonus(int amount) {
        this.strength += amount;
    }

    public void addDexterityBonus(int amount) {
        this.dexterity += amount;
    }

    public void takeDamage(int damage)
    {
        this.currentHealth -= damage;
        if (currentHealth < 0)
        {
            currentHealth = 0;
        }

        if (this instanceof Enemy)
        {
            ((Enemy) this).setAggression(true);
        }
    }

    public boolean takeDamageAndCheckDeath(int damage) {
        int healthBefore = this.getHealth();
        this.setHealth(this.getHealth() - damage);
        return (healthBefore > 0 && this.getHealth() <= 0);
    }

    public String getName() { return name; }
    public int getHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public long getExperience() { return experience; }
    public int getDexterity() { return dexterity; }
    public int getStrength() { return strength; }
    public String getType() { return type; }
    public boolean hasWeapon() { return weapon; }
    public int getCurrentPlayerLevel() { return currentPlayerLevel; }
    public boolean isAlive() { return currentHealth > 0; }

    public void setHealth(int health) { this.currentHealth = health; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public void setType(String type) { this.type = type; }
    public void setWeapon(boolean weapon) { this.weapon = weapon; }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }

    public String getUserName() { return userName; }

    public void printNameOfUser(String userName)
    {
        System.out.println(userName);
    }

    public void setCurrentPlayerLevel(int currentPlayerLevel) {
        this.currentPlayerLevel = currentPlayerLevel;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }
}