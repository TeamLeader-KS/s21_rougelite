package rougelite.BusinessLogicLayer.objects;

import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.interfaces.ConsumableItem;
import rougelite.BusinessLogicLayer.interfaces.ObjectInfo;
import rougelite.BusinessLogicLayer.message.GameMessages;

public class HealthPotion extends GameObject implements ObjectInfo, ConsumableItem {
    private HealthPotionType type;
    private String name;
    private int healthPower;

    public enum HealthPotionType {
        LOW_HEALTH("Малое зелье здоровья", 25),
        AVERAGE_HEALTH("Среднее зелье здоровья", 50),
        BIG_HEALTH("Большое зелье здоровья", 100);

        private final String russianHealthPotionName;
        private final int healthPower;

        HealthPotionType(String russianHealthPotionName, int healthPower) {
            this.russianHealthPotionName = russianHealthPotionName;
            this.healthPower = healthPower;
        }

        public String getRussianHealthPotionName() {
            return russianHealthPotionName;
        }

        public int getHealthPower() {
            return healthPower;
        }
    }

    public HealthPotion(HealthPotionType type) {
        this.type = type;
        this.name = type.getRussianHealthPotionName();
        this.healthPower = type.getHealthPower();
    }

    public HealthPotionType getType() {
        return type;
    }

    public int getHealthPower() {
        return healthPower;
    }

    public String getName() {
        return name;
    }

    @Override
    public String reportObject() {
        return GameMessages.PICKUP_POTION;
    }

    @Override
    public String useItem(Player player) {
        if (player.getHealth() < player.getMaxHealth()) {
            int newHealth = Math.min(player.getHealth() + healthPower, player.getMaxHealth());
            player.setHealth(newHealth);
            return GameMessages.formatUseHealthPotion(name);
        } else {
            return GameMessages.FULL_HEALTH;
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getHealth() < player.getMaxHealth();
    }
}