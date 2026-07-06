package rougelite.BusinessLogicLayer.interfaces;

import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.objects.Portal;

public interface PortalStoneUsable {
    String useOnPortal(Player player, Portal portal);  // ← void замени на String
    boolean canUseOnPortal(Portal portal);
}