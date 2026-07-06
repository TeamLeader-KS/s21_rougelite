package rougelite.BusinessLogicLayer.mapGenerator;

import rougelite.BusinessLogicLayer.objects.Portal;
import rougelite.BusinessLogicLayer.objects.PortalStone;

public interface DrawRoomsMap {
    int getX();
    int getY();
    int getHeight();
    int getLength();
    int[][] getTiles();
    int getTileAt(int x, int y);

    int getRoomIndex();
    boolean isCenterRoom();

    Portal getPortal();
    void setPortal(Portal portal);

    PortalStone getPortalStone();
    void setPortalStone(PortalStone stone);
}