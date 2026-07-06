package rougelite.BusinessLogicLayer.objects;

import rougelite.BusinessLogicLayer.interfaces.ObjectInfo;
import rougelite.BusinessLogicLayer.message.GameMessages;


public class DarkCrown extends GameObject implements ObjectInfo {
    private String name;
    private DarkCrownType type;
    private int x;
    private int y;

    public void setPosition(int x, int y) {
        super.setPosition(x, y);
    }

    public enum DarkCrownType
    {
        DARK_CROWN("Корона Тьмы");

        private final String russianDarkCrownName;

        DarkCrownType(String russianDarkCrownName)
        {
            this.russianDarkCrownName = russianDarkCrownName;
        }

        public String getRussianDarkCrownName() {
            return russianDarkCrownName;
        }
    }

    public DarkCrown(DarkCrownType type)
    {
        this.type = type;
        this.name = type.getRussianDarkCrownName();
    }

    public String getName() {
        return name;
    }

    public DarkCrownType getType() {
        return type;
    }

    @Override
    public String reportObject() {
        return GameMessages.PICKUP_CROWN;
    }
}
