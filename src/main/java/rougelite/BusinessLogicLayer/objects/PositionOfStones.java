package rougelite.BusinessLogicLayer.objects;

public enum PositionOfStones {
    NONE(-1),
    UP_LEFT(0),
    UP_MIDDLE(1),
    UP_RIGHT(2),
    MIDDLE_LEFT(3),
    MIDDLE_CENTER(4), // «Корона Нокс» (Corona Nox)
    MIDDLE_RIGHT(5),
    DOWN_LEFT(6),
    DOWN_MIDDLE(7),
    DOWN_RIGHT(8);

    private final int roomIndex;

    PositionOfStones(int roomIndex)
    {
        this.roomIndex = roomIndex;
    }

    public int getRoomIndex() {
        return roomIndex;
    }

    public static PositionOfStones fromRoomIndex(int roomIndex)
    {
        for (PositionOfStones position : values())
        {
            if (position.roomIndex == roomIndex)
            {
                return position;
            }
        }
        return NONE;
    }

    public String getRussianName()
    {
        switch (this) {
            case UP_LEFT: return "верхний левый камень портала";
            case UP_MIDDLE: return "верхний центральный камень портала";
            case UP_RIGHT: return "верхний правый камень портала";
            case MIDDLE_LEFT: return "центральный левый камень портала";
            case MIDDLE_CENTER: return "АРТЕФАКТ Корона Ноктис";
            case MIDDLE_RIGHT: return "центральный правый камень портала";
            case DOWN_LEFT: return "нижний левый камень портала";
            case DOWN_MIDDLE: return "нижний центральный камень портала";
            case DOWN_RIGHT: return "нижний правый камень портала";
            default: return "неизвестный предмет";
        }
    }
}