package s21_rougelite;

import java.util.Random;

public class Room {

    private static final int MIN_HEIGHT = 10;
    private static final int MAX_HEIGHT = 20;
    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 30;

            private int height;
            private int length;
            private int[][] room;


    public Room()
    {
        Random rand = new Random();

        this.height = MIN_HEIGHT + rand.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1);
        this.length = MIN_LENGTH + rand.nextInt(MAX_LENGTH - MIN_LENGTH + 1);

        this.room = new int[height][length];

        // 3. Заполняем комнату
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < length; j++)
            {
                if ((i == 0 || i == height - 1) || (j == 0 || j == length - 1))
                {
                    room[i][j] = 1;
                }
                else
                {
                    room[i][j] = 0;
                }
            }
        }
    }

    // Геттеры для размеров
    public int[][] getRoom() {
        return room;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }

}
