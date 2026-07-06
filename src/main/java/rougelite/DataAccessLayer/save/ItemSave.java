package rougelite.DataAccessLayer.save;

public class ItemSave {
    public ItemType type;

    public int x;
    public int y;

    public String varietyName;
    public String subtype;
    public int amount;
    public int currentAmmo;
    public String name;

    public enum ItemType {
        WEAPON,
        AMMO,
        HEALTH_POTION,
        REINFORCE,
        DARK_CROWN
    }
}