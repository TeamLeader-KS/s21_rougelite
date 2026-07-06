package rougelite.BusinessLogicLayer.mapGenerator;


import rougelite.BusinessLogicLayer.characters.Enemy;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.characters.behavior.*;
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior;
import rougelite.BusinessLogicLayer.objects.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;


public class GameLevel {
    // Значения отступов
    private static final int VERTICAL_RANGE = 5;
    private static final int HORIZONTAL_RANGE = 20;
    // Значения комнат
    private static final int MAX_HEIGHT = 12;
    private static final int MAX_LENGTH = 30;

    private int dungeonLevel;

    public static DrawRoomsMap getRoomByIndex(int index) {
        for (DrawRoomsMap area : rooms) {
            if (area.getRoomIndex() == index) {
                return area;
            }
        }
        return null;
    }

    public int getDungeonLevel() {
        return dungeonLevel;
    }

    public void setDungeonLevel(int dungeonLevel) {
        this.dungeonLevel = dungeonLevel;
    }

    private static List<DrawRoomsMap> rooms;
    private List<Corridor> corridors;
    private List<GameObject> items;

    private int[][] levelMap;
    private Random random;
    private Portal portal;

    private boolean[] visitedRooms;
    private boolean[] openedCorridors;

    private List<Enemy> enemies = new ArrayList<>();

    private LastRoom lastRoom;


    // ========== ТУМАН ВОЙНЫ ==========
    public enum mapExploration {
        UNKNOWN, // НЕИЗВЕСТНЫЙ
        VISITED, // ПОСЕТИЛИ
        VISIBLE  // ВИДИМЫЙ
    }

    private mapExploration[][] explorationMap;

    private void createExplorationMap() {
        if (levelMap == null || levelMap.length == 0) return;
        int height = levelMap.length;
        int width = levelMap[0].length;
        explorationMap = new mapExploration[height][width];
        for (int i = 0; i < height; i++) {
            Arrays.fill(explorationMap[i], mapExploration.UNKNOWN);
        }
    }

    public void transferVisibleToVisited(int playerX, int playerY, int visionRadius) {

        for (int y = 0; y < explorationMap.length; y++) {
            for (int x = 0; x < explorationMap[0].length; x++) {
                if (explorationMap[y][x] == mapExploration.VISIBLE) {
                    explorationMap[y][x] = mapExploration.VISITED;
                }
            }
        }

        markVisibleArea(playerX, playerY, visionRadius);

        for (DrawRoomsMap area : rooms) {
            int xStart = area.getX();
            int yStart = area.getY();
            int xEnd = xStart + area.getLength();
            int yEnd = yStart + area.getHeight();

            if (playerX >= xStart && playerX < xEnd && playerY >= yStart && playerY < yEnd) {
                for (int y = yStart; y < yEnd; y++) {
                    for (int x = xStart; x < xEnd; x++) {
                        if (x >= 0 && x < levelMap[0].length && y >= 0 && y < levelMap.length) {
                            explorationMap[y][x] = mapExploration.VISITED;
                        }
                    }
                }
                break;
            }
        }
    }

    private void markVisibleArea(int centerX, int centerY, int radius) {
        setVisible(centerX, centerY);

        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (Math.abs(dx) == radius || Math.abs(dy) == radius) {
                    castRay(centerX, centerY, centerX + dx, centerY + dy);
                }
            }
        }
    }

    private void castRay(int startX, int startY, int endX, int endY) {
        int dx = Math.abs(endX - startX);
        int dy = -Math.abs(endY - startY);
        int sx = startX < endX ? 1 : -1;
        int sy = startY < endY ? 1 : -1;
        int err = dx + dy;

        while (true) {
            setVisible(startX, startY);
            if (startX == endX && startY == endY) break;

            int e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                startX += sx;
            }
            if (e2 <= dx) {
                err += dx;
                startY += sy;
            }

            if (startX < 0 || startX >= levelMap[0].length ||
                    startY < 0 || startY >= levelMap.length ||
                    levelMap[startY][startX] != -1) {
                break;
            }
        }
    }

    private void setVisible(int x, int y) {
        if (x >= 0 && x < levelMap[0].length && y >= 0 && y < levelMap.length) {
            explorationMap[y][x] = mapExploration.VISIBLE;
        }
    }

    public void openCellsInRadius(int x, int y, int radius) {
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < levelMap[0].length && ny >= 0 && ny < levelMap.length) {
                    explorationMap[ny][nx] = mapExploration.VISIBLE;
                }
            }
        }
    }

    public void openRoom(int roomIndex) {
        if (roomIndex < 0 || roomIndex >= rooms.size()) {
            return;
        }

        DrawRoomsMap area = rooms.get(roomIndex);

        if (area instanceof Room) {
            Room room = (Room) area;
            for (int y = room.getY(); y < room.getY() + room.getHeight(); y++) {
                for (int x = room.getX(); x < room.getX() + room.getLength(); x++) {
                    if (x >= 0 && x < levelMap[0].length && y >= 0 && y < levelMap.length) {
                        explorationMap[y][x] = mapExploration.VISITED;
                    }
                }
            }
        }
    }

    public boolean isExplored(int x, int y) {
        if (x < 0 || x >= levelMap[0].length || y < 0 || y >= levelMap.length) return false;
        mapExploration e = explorationMap[y][x];
        return e == mapExploration.VISITED || e == mapExploration.VISIBLE;
    }

    public mapExploration getExploration(int x, int y) {
        if (x < 0 || x >= levelMap[0].length || y < 0 || y >= levelMap.length) {
            return mapExploration.UNKNOWN;
        }
        return explorationMap[y][x];
    }

    public boolean isVisible(int x, int y) {
        return getExploration(x, y) == mapExploration.VISIBLE;
    }

    public boolean isVisited(int x, int y) {
        mapExploration e = getExploration(x, y);
        return e == mapExploration.VISITED || e == mapExploration.VISIBLE;
    }

    public void clearExploration() {
        createExplorationMap();
    }

    public GameLevel(int dungeonLevel) {
        this.random = new Random();
        this.rooms = new ArrayList<>();
        this.corridors = new ArrayList<>();
        this.items = new ArrayList<>();
        DoorCoordinates.clear();
        generateRooms();
        this.visitedRooms = new boolean[9];
        this.openedCorridors = new boolean[12];
        this.dungeonLevel = dungeonLevel;
    }

    private void generateRooms() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col;
                boolean isCenter = (row == 1 && col == 1);
                rooms.add(new Room(index, isCenter));
            }
        }
    }

    public void buildLevel(int windowWidth, int windowHeight) {

        DoorCoordinates.clear();

        rooms = new ArrayList<>();
        corridors = new ArrayList<>();
        items = new ArrayList<>();

        if (dungeonLevel == 21)
        {
            LastRoom lastRoom = new LastRoom(4, true);
            lastRoom.setPosition(windowWidth / 2 - 15, windowHeight / 2 - 5);
            rooms.add(lastRoom);

            Portal portal = new Portal(
                    lastRoom.getX() + lastRoom.getLength() / 2 - 10,
                    lastRoom.getY() + lastRoom.getHeight() / 2);
            this.portal = portal;
            lastRoom.setPortal(portal);

            buildLevelMapForLastRoom(lastRoom);

        } else {
            generateRooms();
            calculateAndSetRoomPositions(windowWidth, windowHeight);
            generateDoors();
            generateCorridorsBetweenDoors();
            setupPortalAndStone();
            buildLevelMap();
        }

        List<int[]> occupiedPositions = new ArrayList<>();
            for (DrawRoomsMap area : rooms)
            {
                if (area instanceof Room)
                {
                    Room room = (Room) area;
                    PortalStone stone = room.getPortalStone();
                    if (stone != null)
                    {
                        occupiedPositions.add(new int[]{stone.getStoneX(), stone.getStoneY()});
                    }
                }

            }

        allocationWeapon(occupiedPositions);
        allocationAmmo(occupiedPositions);
        allocationHealthPotion(occupiedPositions);
        allocationReinforce(occupiedPositions);
        createExplorationMap();
    }

    public void calculateAndSetRoomPositions(int windowWidth, int windowHeight) {
        System.out.println("Terminal size: " + windowWidth + " x " + windowHeight);
        Random rand = new Random();

        int col1_1 = (windowWidth / 10) + (rand.nextInt(5)) - (rand.nextInt(20)) + HORIZONTAL_RANGE;
        int row1_1 = rand.nextInt(5) + VERTICAL_RANGE;
        int col2_2 = (int) Math.round(windowWidth / 2.5) + (rand.nextInt(10)) - (rand.nextInt(10));
        int row2_2 = rand.nextInt(5) + VERTICAL_RANGE;
        int col3_3 = (windowWidth + (rand.nextInt(15)) - (rand.nextInt(25))) - (HORIZONTAL_RANGE + MAX_LENGTH);
        int row3_3 = rand.nextInt(5) + VERTICAL_RANGE;
        int col4_1 = (windowWidth / 10) + (rand.nextInt(5)) - (rand.nextInt(20)) + HORIZONTAL_RANGE;
        int row4_1 = (int) Math.round(windowHeight / 2.5) + (rand.nextInt(3) - rand.nextInt(3));
        int col5_2 = (int) Math.round(windowWidth / 2.5);
        int row5_2 = (int) Math.round(windowHeight / 2.5);
        int col6_3 = (windowWidth + (rand.nextInt(15)) - (rand.nextInt(25))) - (HORIZONTAL_RANGE + MAX_LENGTH);
        int row6_3 = (int) Math.round(windowHeight / 2.5) + (rand.nextInt(3) - rand.nextInt(3));
        int col7_1 = (windowWidth / 10) + (rand.nextInt(5)) - (rand.nextInt(20)) + HORIZONTAL_RANGE;
        int row7_1 = windowHeight - rand.nextInt(3) - VERTICAL_RANGE - MAX_HEIGHT;
        int col8_2 = (int) Math.round(windowWidth / 2.5) + (rand.nextInt(10)) - (rand.nextInt(10));
        int row8_2 = windowHeight - rand.nextInt(3) - VERTICAL_RANGE - MAX_HEIGHT;
        int col9_3 = (windowWidth + (rand.nextInt(15)) - (rand.nextInt(25))) - (HORIZONTAL_RANGE + MAX_LENGTH);
        int row9_3 = windowHeight - (VERTICAL_RANGE + MAX_HEIGHT);

        for (DrawRoomsMap area : rooms) {
            if (area instanceof Room) {
                Room room = (Room) area;

                int index = room.getRoomIndex();

                switch (index) {
                    case 0:
                        room.setPosition(col1_1, row1_1);
                        break;
                    case 1:
                        room.setPosition(col2_2, row2_2);
                        break;
                    case 2:
                        room.setPosition(col3_3, row3_3);
                        break;
                    case 3:
                        room.setPosition(col4_1, row4_1);
                        break;
                    case 4:
                        room.setPosition(col5_2, row5_2);
                        break;
                    case 5:
                        room.setPosition(col6_3, row6_3);
                        break;
                    case 6:
                        room.setPosition(col7_1, row7_1);
                        break;
                    case 7:
                        room.setPosition(col8_2, row8_2);
                        break;
                    case 8:
                        room.setPosition(col9_3, row9_3);
                        break;
                }
            }
        }

        for (int i = 0; i < rooms.size(); i++) {
            DrawRoomsMap area = rooms.get(i);

            if (area instanceof Room) {
                Room room = (Room) area;
                System.out.println("Комната " + i + ": (" + room.getX() + ", " + room.getY() +
                        ") размер " + room.getLength() + "x" + room.getHeight());
            } else {
                // Если это не Room (например, LastRoom), выводим информацию по-другому
                System.out.println("Область " + i + ": (" + area.getX() + ", " + area.getY() +
                        ") размер " + area.getLength() + "x" + area.getHeight());
            }
        }
    }

    private void generateDoors() {

        for (DrawRoomsMap area : rooms) {
            if (area instanceof Room) {
                Room room = (Room) area;
                if (room.getRoomIndex() == 4) {
                    room.generateCenterDoors();
                    break;
                }
            }
        }

        for (DrawRoomsMap area : rooms) {
            if (area instanceof Room) {
                Room room = (Room) area;
                if (room.getRoomIndex() != 4) {
                    room.generateNonCenterDoors();
                }
            }
        }
                System.out.println("=== ДВЕРИ ПОСЛЕ ГЕНЕРАЦИИ ===");
        for (DoorCoordinates.Door door : DoorCoordinates.getDoors()){
            System.out.println("Комната " + door.getRoomIndex() + " | сторона " + door.getSide() + " | (" + door.getX() + ", " + door.getY() + ")");
        }
    }

    private void generateCorridorsBetweenDoors() {
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                if (areRoomsAdjacent(i, j)) {
                    DoorCoordinates.Door door1 = findDoorFacingRoom(i, j);
                    DoorCoordinates.Door door2 = findDoorFacingRoom(j, i);
                    if (door1 != null && door2 != null) {
                        List<int[]> path = buildCorridorBetweenDoors(door1, door2);
                        corridors.add(new Corridor(i, j, path));
                    }
                }
            }
        }
    }

    private boolean areRoomsAdjacent(int roomA, int roomB) {
        int ax = roomA % 3, ay = roomA / 3;
        int bx = roomB % 3, by = roomB / 3;
        return (Math.abs(ax - bx) + Math.abs(ay - by)) == 1;
    }

    private DoorCoordinates.Door findDoorFacingRoom(int fromRoom, int toRoom) {
        int ax = fromRoom % 3, ay = fromRoom / 3;
        int bx = toRoom % 3, by = toRoom / 3;
        DoorCoordinates.Direction direction = null;
        if (bx > ax) direction = DoorCoordinates.Direction.EAST;
        else if (bx < ax) direction = DoorCoordinates.Direction.WEST;
        else if (by > ay) direction = DoorCoordinates.Direction.SOUTH;
        else if (by < ay) direction = DoorCoordinates.Direction.NORTH;
        if (direction != null) {
            for (DoorCoordinates.Door door : DoorCoordinates.getDoors()) {
                if (door.getRoomIndex() == fromRoom && door.getSide() == direction) {
                    return door;
                }
            }
        }
        return null;
    }

    private List<int[]> buildCorridorBetweenDoors(DoorCoordinates.Door door1, DoorCoordinates.Door door2) {
        List<int[]> path = new ArrayList<>();
        int x1 = door1.getX(), y1 = door1.getY();
        int x2 = door2.getX(), y2 = door2.getY();
        DoorCoordinates.Direction side1 = door1.getSide();
        int currentX = x1, currentY = y1;
        switch (side1) {
            case NORTH: currentY--; break;
            case SOUTH: currentY++; break;
            case EAST:  currentX++; break;
            case WEST:  currentX--; break;
        }
        path.add(new int[]{currentX, currentY});
        int midX = (x1 + x2) / 2, midY = (y1 + y2) / 2;
        if (side1 == DoorCoordinates.Direction.EAST || side1 == DoorCoordinates.Direction.WEST) {
            while (currentX != midX) {
                currentX += (currentX < midX) ? 1 : -1;
                path.add(new int[]{currentX, currentY});
            }
            while (currentY != midY) {
                currentY += (currentY < midY) ? 1 : -1;
                path.add(new int[]{currentX, currentY});
            }
        } else {
            while (currentY != midY) {
                currentY += (currentY < midY) ? 1 : -1;
                path.add(new int[]{currentX, currentY});
            }
            while (currentX != midX) {
                currentX += (currentX < midX) ? 1 : -1;
                path.add(new int[]{currentX, currentY});
            }
        }
        if (side1 == DoorCoordinates.Direction.EAST || side1 == DoorCoordinates.Direction.WEST) {
            while (currentY != y2) {
                currentY += (currentY < y2) ? 1 : -1;
                path.add(new int[]{currentX, currentY});
            }
        } else {
            while (currentX != x2) {
                currentX += (currentX < x2) ? 1 : -1;
                path.add(new int[]{currentX, currentY});
            }
        }
        while (currentX != x2 || currentY != y2) {
            if (currentX != x2) currentX += (currentX < x2) ? 1 : -1;
            if (currentY != y2) currentY += (currentY < y2) ? 1 : -1;
            path.add(new int[]{currentX, currentY});
        }
        if (!path.isEmpty()) {
            int[] last = path.get(path.size() - 1);
            if (last[0] == x2 && last[1] == y2) {
                path.remove(path.size() - 1);
            }
        }
        return path;
    }

    private void buildLevelMap() {
        int maxX = 0, maxY = 0;
        for (DrawRoomsMap room : rooms) {
            maxX = Math.max(maxX, room.getX() + room.getLength());
            maxY = Math.max(maxY, room.getY() + room.getHeight());
        }
        for (Corridor c : corridors) {
            for (int[] point : c.getPath()) {
                maxX = Math.max(maxX, point[0]);
                maxY = Math.max(maxY, point[1]);
            }
        }
        levelMap = new int[maxY + VERTICAL_RANGE][maxX + HORIZONTAL_RANGE];
        for (int i = 0; i < levelMap.length; i++) {
            Arrays.fill(levelMap[i], -1);
        }
        for (DrawRoomsMap room : rooms) {
            int[][] tiles = room.getTiles();
            for (int i = 0; i < room.getHeight(); i++) {
                for (int j = 0; j < room.getLength(); j++) {
                    levelMap[room.getY() + i][room.getX() + j] = tiles[i][j];
                }
            }
        }
        for (Corridor c : corridors) {
            for (int[] point : c.getPath()) {
                if (point[1] >= 0 && point[1] < levelMap.length &&
                        point[0] >= 0 && point[0] < levelMap[0].length) {
                    if (levelMap[point[1]][point[0]] != 2 && levelMap[point[1]][point[0]] != 1) {
                        levelMap[point[1]][point[0]] = 3;
                    }
                }
            }
        }
    }

    private void setupPortalAndStone() {

        Room centerRoom = null;
        for (DrawRoomsMap area : rooms) {
            if (area instanceof Room) {
                Room room = (Room) area;
                if (room.getRoomIndex() == 4) {
                    centerRoom = room;
                    break;
                }
            }
        }

        if (centerRoom != null) {
            Portal newPortal = new Portal(
                    centerRoom.getX() + centerRoom.getLength() / 2,
                    centerRoom.getY() + centerRoom.getHeight() / 2
            );
            centerRoom.setPortal(newPortal);
            this.portal = newPortal;
        }

        for (DrawRoomsMap area : rooms) {
            if (area instanceof Room) {
                Room room = (Room) area;
                if (room.getRoomIndex() != 4) {
                    PortalStone stone = new PortalStone(
                            room.getRoomIndex(),
                            room.getX(), room.getY(),
                            room.getLength(), room.getHeight()
                    );
                    room.setPortalStone(stone);
                }
            } else if (area instanceof LastRoom) {
                LastRoom lastRoom = (LastRoom) area;
                PortalStone stone = new PortalStone(
                        lastRoom.getRoomIndex(),
                        lastRoom.getX(), lastRoom.getY(),
                        lastRoom.getLength(), lastRoom.getHeight()
                );
                lastRoom.setPortalStone(stone);
            }
        }
    }

    private void allocationWeapon(List<int[]> occupiedPositions) {
        Random rand = new Random();
        Weapon.WeaponType[] weaponTypes = Weapon.WeaponType.values();

        int randomCount = rand.nextInt(2); // TODO тест оружия
        for (int i = 0; i < randomCount; i++) {
            DrawRoomsMap area = getRandomNonCenterRoom(rand);

            if (area != null)
            {
                if (area instanceof Room)
                {
                    Room room = (Room) area;
                    Weapon.WeaponType type = weaponTypes[rand.nextInt(weaponTypes.length)];
                    Weapon weapon = new Weapon(type);
                    checkingItemAllocation(weapon, room, occupiedPositions);
                }
            }
        }
    }

    private void allocationAmmo(List<int[]> occupiedPositions) {
        Random rand = new Random();
        Ammo.AmmoType[] ammoTypes = Ammo.AmmoType.values();

        int randomCount = rand.nextInt(3) + 1;
        for (int i = 0; i < randomCount; i++) {
            DrawRoomsMap area = getRandomNonCenterRoom(rand);

            if (area != null) {
                if (area instanceof Room) {
                    Room room = (Room) area;
                    Ammo.AmmoType type = ammoTypes[rand.nextInt(ammoTypes.length)];
                    Ammo ammo = new Ammo(type);
                    checkingItemAllocation(ammo, room, occupiedPositions);
                }
            }
        }
    }

    private void allocationHealthPotion(List<int[]> occupiedPositions) {
        Random rand = new Random();
        HealthPotion.HealthPotionType[] healthPotionTypes = HealthPotion.HealthPotionType.values();

        int randomCount = rand.nextInt(3) + 1;
        for (int i = 0; i < randomCount; i++) {
            DrawRoomsMap area = getRandomNonCenterRoom(rand);

            if (area != null) {
                if (area instanceof Room) {
                    Room room = (Room) area;
                    HealthPotion.HealthPotionType type = healthPotionTypes[rand.nextInt(healthPotionTypes.length)];
                    HealthPotion potion = new HealthPotion(type);
                    checkingItemAllocation(potion, room, occupiedPositions);
                }
            }
        }
    }

    private void allocationReinforce(List<int[]> occupiedPositions) {
        Random rand = new Random();
        Reinforce.ReinforceType[] reinforceTypes = Reinforce.ReinforceType.values();

        int randomCount = rand.nextInt(2);
        for (int i = 0; i < randomCount; i++) {
            DrawRoomsMap area = getRandomNonCenterRoom(rand);

            if (area != null) {
                if (area instanceof Room) {
                    Room room = (Room) area;
                    Reinforce.ReinforceType type = reinforceTypes[rand.nextInt(reinforceTypes.length)];
                    Reinforce reinforce = new Reinforce(type);
                    checkingItemAllocation(reinforce, room, occupiedPositions);
                }
            }
        }
    }

    public List<DrawRoomsMap> getRooms() { return rooms; }
    public List<Corridor> getCorridors() { return corridors; }
    public int[][] getLevelMap() { return levelMap; }
    public Portal getPortal() { return portal; }
    public List<GameObject> getItems() { return items; }
    public void addItem(GameObject item) { items.add(item); }
    public void removeItem(GameObject item) { items.remove(item); }

    public DrawRoomsMap getRoomAt(int y, int x) {
        for (DrawRoomsMap area : rooms) {
            if (x >= area.getX() && x < area.getX() + area.getLength() &&
                    y >= area.getY() && y < area.getY() + area.getHeight()) {
                return area;
            }
        }
        return null;
    }

    public GameObject getItemAt(int x, int y) {
        for (GameObject item : items) {
            if (item.getX() == x && item.getY() == y) {
                return item;
            }
        }
        return null;
    }

    public void visitRoom(int roomIndex) {
        if (roomIndex >= 0 && roomIndex < visitedRooms.length) {
            visitedRooms[roomIndex] = true;
        }
    }

    public boolean isRoomVisited(int roomIndex) {
        return roomIndex >= 0 && roomIndex < visitedRooms.length && visitedRooms[roomIndex];
    }

    public void openCorridor(int corridorIndex) {
        if (corridorIndex >= 0 && corridorIndex < openedCorridors.length) {
            openedCorridors[corridorIndex] = true;
        }
    }

    public boolean isCorridorOpened(int corridorIndex) {
        return corridorIndex >= 0 && corridorIndex < openedCorridors.length && openedCorridors[corridorIndex];
    }

    public int getCurrentRoomIndex(Player player) {
        int x = player.getLevelX();
        int y = player.getLevelY();

        for (int i = 0; i < rooms.size(); i++) {
            // Получаем элемент как DrawRoomsMap
            DrawRoomsMap area = rooms.get(i);

            if (area instanceof Room) {
                Room room = (Room) area;
                if (x >= room.getX() && x < room.getX() + room.getLength() &&
                        y >= room.getY() && y < room.getY() + room.getHeight()) {
                    return i;
                }
            }
        }
        return -1;
    }

        public int getCurrentRoomIndexByCoordinates(int x, int y) {
            for (int i = 0; i < rooms.size(); i++) {
                DrawRoomsMap area = rooms.get(i);
                if (area instanceof Room)
                {
                    Room room = (Room) area;
                    if (x >= room.getX() && x < room.getX() + room.getLength() &&
                            y >= room.getY() && y < room.getY() + room.getHeight()) {
                        return i;
                    }
                }
            }
            return -1;
        }

    public void clearVisibility() {
        for (int i = 0; i < visitedRooms.length; i++) {
            visitedRooms[i] = false;
        }
        for (int j = 0; j < openedCorridors.length; j++) {
            openedCorridors[j] = false;
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void removeEnemy(Enemy enemy) {
        System.out.println("removeEnemy: удаляем " + enemy);
        boolean removed = enemies.remove(enemy);
        System.out.println("Удалён: " + removed + ", осталось: " + enemies.size());
    }



    private EnemyBehavior createBehavior(Enemy.EnemyType type)
    {
            return switch (type)
            {
                case ZOMBIE -> new ZombieBehavior();
                case VAMPIRE -> new VampireBehavior();
                case GHOST -> new GhostBehavior();
                case OGRE -> new OgreBehavior();
                case SNAKE_MAGE -> new SnakeMageBehavior();
                default -> new ZombieBehavior();
            };
    }
    class RisingEnemies<T>
    {
        private T type;
        private int chance;

        public RisingEnemies(T type, int chance)
        {
            this.type = type;
            this.chance = chance;
        }

        public T getType() {
            return type;
        }

        public int getChance() {
            return chance;
        }
    }


    private Enemy.EnemyType getRandomEnemyType(int dungeonLevel, Random random) {

        int zombieChance = 90 - dungeonLevel * 5;
        int vampireChance;
            if (dungeonLevel < 10)
            {
                vampireChance = 50 + dungeonLevel * 5;
            }
            else
            {
                vampireChance = 65 - dungeonLevel * 2;
            }
        int ogreChance = -15 + dungeonLevel * 3;
        int ghostChance = -25 + dungeonLevel * 4;
        int snakeMageChance = -35 + dungeonLevel * 5;

        List<RisingEnemies<Enemy.EnemyType>> risingEnemies = new ArrayList<>();
        risingEnemies.add(new RisingEnemies<>(Enemy.EnemyType.ZOMBIE, zombieChance));
        risingEnemies.add(new RisingEnemies<>(Enemy.EnemyType.VAMPIRE, vampireChance));
        risingEnemies.add(new RisingEnemies<>(Enemy.EnemyType.GHOST, ghostChance));
        risingEnemies.add(new RisingEnemies<>(Enemy.EnemyType.OGRE, ogreChance));
        risingEnemies.add(new RisingEnemies<>(Enemy.EnemyType.SNAKE_MAGE, snakeMageChance));


        return chooseRisingEnemies(risingEnemies, random);
    }

    private Enemy.EnemyType chooseRisingEnemies(List<RisingEnemies<Enemy.EnemyType>> risingEnemies, Random random)
    {
        if (risingEnemies == null || risingEnemies.isEmpty())
        {
            return Enemy.EnemyType.ZOMBIE;
        }
        // challenger
        int totalChance = 0;

        for (RisingEnemies<Enemy.EnemyType> challenger : risingEnemies)
        {
            totalChance += challenger.getChance();
        }

        if (totalChance == 0) {
            return risingEnemies.get(0).getType();
        }

        int roll = random.nextInt(totalChance);

        for (RisingEnemies<Enemy.EnemyType> challenger : risingEnemies) {
            roll -= challenger.getChance();
            if (roll < 0) {
                return challenger.getType();
            }
        }
            return risingEnemies.get(risingEnemies.size() - 1).getType();
    }

    public void generateEnemies(int dungeonLevel) {
        Random random = new Random();

        if (dungeonLevel == 21) {
            DrawRoomsMap area = rooms.get(0);
            if (area instanceof LastRoom) {
                LastRoom lastRoom = (LastRoom) area;
                int bossX = lastRoom.getBossPositionX();
                int bossY = lastRoom.getBossPositionY();
                Enemy boss = new Enemy(
                        Enemy.EnemyType.BOSS,
                        bossX, bossY,
                        dungeonLevel,
                        new BossBehavior()
                );
                addEnemy(boss);
            }
            return;
        }

        for (int i  = 0; i < rooms.size(); i++) {
            DrawRoomsMap area = rooms.get(i);
            if (area instanceof Room)
            {
                Room room = (Room) area;
                if (room.getRoomIndex() == 4) {
                    continue;
                }
                int enemyCount = random.nextInt(3) + 2; // TODO количество врагов

                for (int j = 0; j < enemyCount; j++) {
                    int x = room.getX() + 1 + random.nextInt(room.getLength() - 2);
                    int y = room.getY() + 1 + random.nextInt(room.getHeight() - 2);

                    Enemy.EnemyType type = getRandomEnemyType(dungeonLevel, random);
                    EnemyBehavior behavior = createBehavior(type);
                    Enemy enemy = new Enemy(
                            type,
                            x, y,
                            dungeonLevel,
                            behavior
                    );
                    addEnemy(enemy);
                }
            }
        }
    }

    private boolean isOccupied(int x, int y, List<int[]> occupiedPositions) {
        for (int[] pos : occupiedPositions) {
            if (pos[0] == x && pos[1] == y) {
                return true;
            }
        }
        return false;
    }

    private boolean checkingItemAllocation(GameObject item, Room room, List<int[]> occupiedPositions) {
        Random rand = new Random();
        for (int attempt = 0; attempt < 50; attempt++) {
            int x = room.getX() + 1 + rand.nextInt(room.getLength() - 2);
            int y = room.getY() + 1 + rand.nextInt(room.getHeight() - 2);
            if (!isOccupied(x, y, occupiedPositions)) {
                item.setPosition(x, y);
                items.add(item);
                occupiedPositions.add(new int[]{x, y});
                return true;
            }
        }
        return false;
    }

    private DrawRoomsMap getRandomNonCenterRoom(Random rand) {
        List<Room> validRooms = new ArrayList<>();

        for (DrawRoomsMap area : rooms) {
            if (area instanceof Room) {
                Room room = (Room) area;
                if (room.getRoomIndex() != 4) {
                    validRooms.add(room);
                }
            }
        }

        if (validRooms.isEmpty()) {
            return null;
        }

        int randomIndex = rand.nextInt(validRooms.size());

        return validRooms.get(randomIndex);
    }

    public boolean checkPositionPortalStone(int x, int y)
    {
        for ( DrawRoomsMap area : rooms)
        {
            if (area instanceof Room)
            {
                Room room = (Room) area;
                PortalStone stone = room.getPortalStone();
                if (stone != null && stone.getStoneX() == x && stone.getStoneY() == y)
                {
                    return true;
                }
            }

        }
        return false;
    }

    private void buildLevelMapForLastRoom(LastRoom lastRoom) {
        int startX = lastRoom.getX();
        int startY = lastRoom.getY();
        int roomHeight = lastRoom.getHeight();
        int roomLength = lastRoom.getLength();

        int mapHeight = startY + roomHeight + VERTICAL_RANGE;
        int mapWidth = startX + roomLength + HORIZONTAL_RANGE;

        levelMap = new int[mapHeight][mapWidth];
        for (int i = 0; i < mapHeight; i++) {
            Arrays.fill(levelMap[i], -1);
        }

        int[][] tiles = lastRoom.getTiles();
        for (int i = 0; i < roomHeight; i++) {
            for (int j = 0; j < roomLength; j++) {
                levelMap[startY + i][startX + j] = tiles[i][j];
            }
        }
    }

    public void setRooms(List<DrawRoomsMap> rooms) {
        this.rooms = rooms;
    }

    public void setCorridors(List<Corridor> corridors) {
        this.corridors = corridors;
    }

    public void setItems(List<GameObject> items) {
        this.items = items; // Убедись, что поле 'items' объявлено в классе
    }

    public void setPortal(Portal portal) {
        this.portal = portal;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = new ArrayList<>(enemies);
    }

    public int[][] getExplorationMapForSave() {
        if (explorationMap == null) return null;
        int height = explorationMap.length;
        int width = explorationMap[0].length;
        int[][] result = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result[y][x] = explorationMap[y][x].ordinal();
            }
        }
        return result;
    }

    public void setExplorationMap(int[][] explorationMap) {
        if (explorationMap == null) return;
        int height = explorationMap.length;
        int width = explorationMap[0].length;
        this.explorationMap = new mapExploration[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int value = explorationMap[y][x];
                if (value >= 0 && value < mapExploration.values().length) {
                    this.explorationMap[y][x] = mapExploration.values()[value];
                } else {
                    this.explorationMap[y][x] = mapExploration.UNKNOWN;
                }
            }
        }
    }

    public void setLevelMap(int[][] levelMap) {
        this.levelMap = levelMap;
    }


}