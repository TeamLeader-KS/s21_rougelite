package rougelite.BusinessLogicLayer.logic;

import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.message.GameMessages;
import rougelite.BusinessLogicLayer.objects.Hands;
import rougelite.BusinessLogicLayer.inventory.HiddenBackpack;
import rougelite.PresentationLayer.ui.Messages;

public class HandsEngine {
    private final Player player;

    public HandsEngine(Player player) {
        this.player = player;
    }

    public String switchHands() {
        HiddenBackpack.HandsChange change = player.switchHands();

        switch (change) {
            case EQUIP_FISTS:
                return GameMessages.EQUIP_FISTS;
            case EQUIP_NAILS:
                return GameMessages.EQUIP_NAILS;
            case UNEQUIP_FISTS:
                return GameMessages.UNEQUIP_FISTS;
            case UNEQUIP_NAILS:
                return GameMessages.UNEQUIP_NAILS;
            default:
                return null;
        }
    }

    public Hands getEquippedHands() {
        return player.getEquippedHands();
    }

}