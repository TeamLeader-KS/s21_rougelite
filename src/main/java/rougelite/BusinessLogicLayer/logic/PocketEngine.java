package rougelite.BusinessLogicLayer.logic;

import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.inventory.Pocket;
import rougelite.BusinessLogicLayer.message.GameMessages;
import rougelite.BusinessLogicLayer.objects.Portal;
import rougelite.BusinessLogicLayer.objects.PortalStone;

public class PocketEngine {

    private final Player player;
    private final Pocket pocket;

    public PocketEngine(Player player) {
        this.player = player;
        this.pocket = player.getPocket();
    }

    public String addStone(PortalStone stone) {
        if (stone == null) {
            return null;
        }
        pocket.addStone(stone);
        return stone.reportObject();
    }

    public String useStoneOnPortal(Portal portal) {
        if (portal == null) {
            return null;
        }

        PortalStone stone = pocket.getStone();
        if (stone == null) {
            return null;
        }

        if (!stone.canUseOnPortal(portal)) {
            return null;
        }

        String message = stone.getInsertStoneMessage();
        stone.useOnPortal(player, portal);

        if (portal.isFullyActivated()) {
            portal.portalOpen(true);
            return GameMessages.ACTIVATED_PORTAL;
        }

        return message;
    }

    public void clearPocket() {
        pocket.clearPocket();
    }

    public boolean canEnterPortal(Portal portal, int currentRoomIndex, int playerX, int playerY) {
        if (portal == null) return false;
        if (!portal.isOpen()) return false;
        if (currentRoomIndex != 4) return false;
        if (playerX != portal.getX() || playerY != portal.getY()) return false;
        return true;
    }

    public Pocket getPocket() {
        return pocket;
    }
}