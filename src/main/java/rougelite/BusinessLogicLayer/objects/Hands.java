package rougelite.BusinessLogicLayer.objects;

public class Hands {

    private HandsType type;
    private String name;
    private int handsPower;
    private int currentAmmo;


    public enum HandsType
    {
        FISTS("Кулаки", 5, 0),
        NAILS("Зубы и Ногти", 5, 0);

        private final String russianHandsName;
        private final int handsPower;
        private final int maxAmmo;

        HandsType(String russianHandsName, int handsPower, int maxAmmo)
        {
            this.russianHandsName = russianHandsName;
            this.handsPower = handsPower;
            this.maxAmmo = maxAmmo;
        }

        public String getRussianWeaponName() {
            return russianHandsName;
        }

        public int getHandsPower() {
            return handsPower;
        }

        public int getMaxAmmo() {
            return maxAmmo;
        }
    }

    public Hands(HandsType type)
    {
        this.type = type;
        this.name = type.getRussianWeaponName();
        this.handsPower = type.getHandsPower();
        this.currentAmmo = type.getMaxAmmo();

    }

    public HandsType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getHandsPower() {
        return handsPower;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }
}
