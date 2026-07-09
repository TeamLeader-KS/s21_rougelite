package rougelite.BusinessLogicLayer.objects;

import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.interfaces.ObjectInfo;
import rougelite.BusinessLogicLayer.message.GameMessages;

public class Reinforce extends GameObject implements ObjectInfo {
    private ReinforceType type;
    private String name;
    private int healthBooster;
    private int strengthBooster;
    private int dexterityBooster;

    public enum ReinforceType {
        HELMET("Призрачный капюшон разбойника", 100, 0, 0),
        ARMOR("Доспех теневого стража", 200, 0, 0),
        CLOAK("Накидка теней", 0, 0, 15),
        SHIELD("Щит ночного покрова", 150, 0, 0),
        GLOVES("Перчатки тёмной силы", 0, 25, 0),
        BELT("Пояс усиления духа", 0, 25, 0),
        BOOTS("Бесшумные сапоги охотника", 0, 0, 15),
        AMULET("Амулет неуловимого призрака", 0, 0, 15),
        RING("Усиливающее кольцо Тьмы", 0, 25, 0);

        private final String russianReinforceName;
        private final int healthBooster;
        private final int strengthBooster;
        private final int dexterityBooster;

        ReinforceType(String russianReinforceName, int healthBooster, int strengthBooster, int dexterityBooster) {
            this.russianReinforceName = russianReinforceName;
            this.healthBooster = healthBooster;
            this.strengthBooster = strengthBooster;
            this.dexterityBooster = dexterityBooster;
        }

        public String getRussianReinforceName() {
            return russianReinforceName;
        }

        public int getHealthBooster() {
            return healthBooster;
        }

        public int getStrengthBooster() {
            return strengthBooster;
        }

        public int getDexterityBooster() {
            return dexterityBooster;
        }
    }

    public Reinforce(ReinforceType type) {
        this.type = type;
        this.name = type.getRussianReinforceName();
        this.healthBooster = type.getHealthBooster();
        this.strengthBooster = type.getStrengthBooster();
        this.dexterityBooster = type.getDexterityBooster();
    }

    public ReinforceType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getHealthBooster() {
        return healthBooster;
    }

    public int getStrengthBooster() {
        return strengthBooster;
    }

    public int getDexterityBooster() {
        return dexterityBooster;
    }

    @Override
    public String reportObject() {
        return GameMessages.PICKUP_REINFORCE;
    }

    public void applyBonuses(Player player) {
        if (healthBooster > 0) {
            player.addHealthBonus(healthBooster);
        }
        if (strengthBooster > 0) {
            player.addStrengthBonus(strengthBooster);
        }
        if (dexterityBooster > 0) {
            player.addDexterityBonus(dexterityBooster);
        }
    }
}