package s21_rougelite;

public abstract class Character {

    private String name;
    private int health;
    private int experience;
    private int dexterity;
    private int strength;
    private String type;
    private boolean aggression;
    private boolean weapon;

    public Character(String name, int health, int strength, int dexterity) {
        this.name = name;
        this.health = health;
        this.strength = strength;
        this.dexterity = dexterity;
        this.experience = 0;
        this.aggression = false;
        this.weapon = false;
    }

    // Геттеры и сеттеры
    public String getName() { return name; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public int getExperience() { return experience; }
    public void addExperience(int exp) { this.experience += exp; }
    public int getDexterity() { return dexterity; }
    public int getStrength() { return strength; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isAggression() { return aggression; }
    public void setAggression(boolean aggression) { this.aggression = aggression; }
    public boolean hasWeapon() { return weapon; }
    public void setWeapon(boolean weapon) { this.weapon = weapon; }
}