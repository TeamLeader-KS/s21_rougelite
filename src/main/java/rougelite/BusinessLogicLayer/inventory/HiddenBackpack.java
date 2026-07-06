package rougelite.BusinessLogicLayer.inventory;

import rougelite.BusinessLogicLayer.objects.Hands;
import java.util.ArrayList;
import java.util.List;

public class HiddenBackpack {
    private static final int MAX_ITEMS = 2;
    private List<Hands> handsList;
    private Hands equippedHands;
    private int state = 0;
    private int nextHand = 0;

    public HiddenBackpack() {
        this.handsList = new ArrayList<>();
        this.handsList.add(new Hands(Hands.HandsType.FISTS));
        this.handsList.add(new Hands(Hands.HandsType.NAILS));
        this.equippedHands = null;
        this.nextHand = 0;
    }

    public Hands getEquippedHands() {
        return equippedHands;
    }

    public List<Hands> getAllHands() {
        return new ArrayList<>(handsList);
    }

    public enum HandsChange {
        EQUIP_FISTS,
        EQUIP_NAILS,
        UNEQUIP_FISTS,
        UNEQUIP_NAILS,
        NONE
    }

    public HandsChange switchHands() {
        if (handsList.isEmpty()) return HandsChange.NONE;

        if (equippedHands == null) {
            equippedHands = handsList.get(nextHand);
            HandsChange result = (nextHand == 0) ? HandsChange.EQUIP_FISTS : HandsChange.EQUIP_NAILS;
            nextHand = (nextHand + 1) % 2;
            return result;
        } else {
            HandsChange result = (equippedHands.getType() == Hands.HandsType.FISTS)
                    ? HandsChange.UNEQUIP_FISTS
                    : HandsChange.UNEQUIP_NAILS;
            equippedHands = null;
            return result;
        }
    }

    public void setEquippedHands(Hands hands) {
        this.equippedHands = hands;
    }
}