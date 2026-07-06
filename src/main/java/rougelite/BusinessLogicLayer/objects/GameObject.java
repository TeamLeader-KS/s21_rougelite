package rougelite.BusinessLogicLayer.objects;

public abstract class GameObject {

    private int x;
    private int y;

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX() {return x;}
    public int getY() {return y;}
}
