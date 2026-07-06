package rougelite.DataAccessLayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rougelite.BusinessLogicLayer.characters.Enemy;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.inventory.Backpack;
import rougelite.BusinessLogicLayer.logic.GameEngine;
import rougelite.BusinessLogicLayer.logic.MoveLogic;
import rougelite.BusinessLogicLayer.mapGenerator.*;
import rougelite.BusinessLogicLayer.objects.*;
import rougelite.DataAccessLayer.save.*;
import rougelite.PresentationLayer.ui.Leaderboard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SaveLoadManager {

    private static final String RESOURCE_DIR = "src/main/java/rougelite/DataAccessLayer/files/";
    private static final String SAVE_FILE = RESOURCE_DIR + "savegame.json";
    private static final String LEADERBOARD_FILE = RESOURCE_DIR + "leaderboard.json";

    // ==================== СОХРАНЕНИЕ ====================
    public static void saveGame(GameEngine gameEngine, Player player, GameLevel level) {
        try {
            System.out.println("🔍 Сохранение уровня: " + gameEngine.getCurrentDungeonLevel());
            createSaveLoadFilesDir();
            SaveGame save = new SaveGame();

            // 1. Уровень подземелья
            save.dungeonLevel = gameEngine.getCurrentDungeonLevel();

            // 2. Игрок
            save.player = savePlayer(player);

            save.hands = saveHands(player);

            // 3. Инвентарь
            save.inventory = saveInventory(player);

            // 4. Карман с камнями
            save.pocket = savePocket(player);

            // 5. Портал
            save.portal = savePortal(level);

            // 6. Уровень (комнаты, коридоры, предметы, враги, туман)
            save.level = saveLevel(level);

            // 7. Сохраняем в JSON
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            String json = gson.toJson(save);
            Files.writeString(Path.of(SAVE_FILE), json);

            System.out.println("✅ Игра сохранена!");

        } catch (Exception e) {
            System.err.println("❌ Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== СОХРАНЕНИЕ ИГРОКА ====================
    private static PlayerSave savePlayer(Player player) {
        PlayerSave ps = new PlayerSave();
        ps.name = player.getName();
        ps.health = player.getHealth();
        ps.maxHealth = player.getMaxHealth();
        ps.strength = player.getStrength();
        ps.dexterity = player.getDexterity();
        ps.experience = player.getExperience();
        ps.level = player.getCurrentPlayerLevel();
        ps.position = new PositionSave(player.getLevelX(), player.getLevelY());
        ps.facing = player.getFacing() == MoveLogic.Direction.RIGHT ? "RIGHT" : "LEFT";
        return ps;
    }

    // ==================== СОХРАНЕНИЕ РУК ====================
    private static HandsSave saveHands(Player player) {
        Hands equipped = player.getEquippedHands();
        if (equipped == null) return null;
        HandsSave hs = new HandsSave();
        hs.type = equipped.getType().name();
        return hs;
    }

    // ==================== СОХРАНЕНИЕ ИНВЕНТАРЯ ====================
    private static InventorySave saveInventory(Player player) {
        Backpack backpack = player.getBackpack();
        InventorySave inv = new InventorySave();

        // Оружие
        inv.weapons = new ArrayList<>();
        for (Weapon w : backpack.getWeapons()) {
            WeaponSave ws = new WeaponSave();
            ws.type = w.getType().name();
            ws.currentAmmo = w.getCurrentAmmo();
            ws.maxAmmo = w.getMaxAmmo();
            ws.name = w.getName();
            inv.weapons.add(ws);
        }

        // Амуниция
        inv.ammo = new ArrayList<>();
        for (Ammo a : backpack.getAmmo()) {
            AmmoSave as = new AmmoSave();
            as.type = a.getType().name();
            as.amount = a.getAmountAmmo();
            inv.ammo.add(as);
        }

        // Зелья
        inv.potions = new ArrayList<>();
        for (HealthPotion p : backpack.getHealthPotion()) {
            PotionSave ps = new PotionSave();
            ps.type = p.getType().name();
            inv.potions.add(ps);
        }

        // Усиления
        inv.reinforces = new ArrayList<>();
        for (Reinforce r : backpack.getReinforces()) {
            ReinforceSave rs = new ReinforceSave();
            rs.type = r.getType().name();
            inv.reinforces.add(rs);
        }

        // Сокровища
        inv.treasure = backpack.getTreasure();

        // Экипированное оружие
        Weapon equipped = backpack.getEquippedWeapon();
        if (equipped != null) {
            inv.equippedWeaponType = equipped.getType().name();
        }

        return inv;
    }

    // ==================== СОХРАНЕНИЕ КАРМАНА ====================
    private static PocketSave savePocket(Player player) {
        PocketSave ps = new PocketSave();
        ps.stones = new ArrayList<>();
        for (PortalStone stone : player.getPocket().getAllStones()) {
            ps.stones.add(stone.getSourceRoomIndex());
        }
        return ps;
    }

    // ==================== СОХРАНЕНИЕ ПОРТАЛА ====================
    private static PortalSave savePortal(GameLevel level) {
        Portal portal = level.getPortal();
        if (portal == null) return null;

        PortalSave ps = new PortalSave();
        ps.x = portal.getX();
        ps.y = portal.getY();
        ps.stonesInserted = new ArrayList<>(portal.getInsertedStones());
        ps.isOpen = portal.isOpen();
        return ps;
    }

    // ==================== СОХРАНЕНИЕ УРОВНЯ ====================
    private static LevelSave saveLevel(GameLevel level) {
        LevelSave ls = new LevelSave();
        ls.dungeonLevel = level.getDungeonLevel();

        // 1. Комнаты
        ls.rooms = new ArrayList<>();
        for (DrawRoomsMap area : level.getRooms()) {
            RoomSave rs = new RoomSave();

            if (area instanceof Room) {
                Room room = (Room) area;
                rs.index = room.getRoomIndex();
                rs.x = room.getX();
                rs.y = room.getY();
                rs.height = room.getHeight();
                rs.length = room.getLength();
                rs.tiles = room.getTiles();
                rs.isCenterRoom = room.isCenterRoom();


                PortalStone stone = room.getPortalStone();
                rs.hasPortalStone = stone != null;
                if (stone != null) {
                    rs.portalStoneIndex = stone.getSourceRoomIndex();
                    rs.stoneX = stone.getStoneX();
                    rs.stoneY = stone.getStoneY();
                } else {
                    rs.stoneX = -1;
                    rs.stoneY = -1;
                }

                Portal portal = room.getPortal();
                if (portal != null) {
                    rs.portalX = portal.getX();
                    rs.portalY = portal.getY();
                } else {
                    rs.portalX = -1;
                    rs.portalY = -1;
                }

                ls.rooms.add(rs);

            } else if (area instanceof LastRoom) {
                LastRoom lastRoom = (LastRoom) area;
                rs.index = lastRoom.getRoomIndex();
                rs.x = lastRoom.getX();
                rs.y = lastRoom.getY();
                rs.height = lastRoom.getHeight();
                rs.length = lastRoom.getLength();
                rs.tiles = lastRoom.getTiles();
                rs.isCenterRoom = lastRoom.isCenterRoom();

                PortalStone stone = lastRoom.getPortalStone();
                rs.hasPortalStone = stone != null;
                if (stone != null) {
                    rs.portalStoneIndex = stone.getSourceRoomIndex();
                }

                Portal portal = lastRoom.getPortal();
                if (portal != null) {
                    rs.portalX = portal.getX();
                    rs.portalY = portal.getY();
                } else {
                    rs.portalX = -1;
                    rs.portalY = -1;
                }

                ls.rooms.add(rs);
                System.out.println("✅ Сохранена LastRoom (индекс: " + lastRoom.getRoomIndex() + ")");
            }
        }

        // 2. Коридоры
        ls.corridors = new ArrayList<>();
        for (Corridor c : level.getCorridors()) {
            CorridorSave cs = new CorridorSave();
            cs.fromRoom = c.getFromRoom();
            cs.toRoom = c.getToRoom();
            cs.path = c.getPath();
            ls.corridors.add(cs);
        }

        // 3. Предметы на полу
        ls.itemsOnFloor = new ArrayList<>();
        for (GameObject item : level.getItems()) {
            ItemSave is = new ItemSave();
            is.x = item.getX();
            is.y = item.getY();

            if (item instanceof Weapon) {
                Weapon w = (Weapon) item;
                is.type = ItemSave.ItemType.WEAPON;
                is.subtype = w.getType().name();
                is.currentAmmo = w.getCurrentAmmo();
                is.varietyName = w.getName();
            } else if (item instanceof Ammo) {
                Ammo a = (Ammo) item;
                is.type = ItemSave.ItemType.AMMO;
                is.subtype = a.getType().name();
                is.amount = a.getAmountAmmo();
                is.varietyName = a.getName();
            } else if (item instanceof HealthPotion) {
                HealthPotion p = (HealthPotion) item;
                is.type = ItemSave.ItemType.HEALTH_POTION;
                is.subtype = p.getType().name();
                is.varietyName = p.getName();
            } else if (item instanceof Reinforce) {
                Reinforce r = (Reinforce) item;
                is.type = ItemSave.ItemType.REINFORCE;
                is.subtype = r.getType().name();
                is.varietyName = r.getName();
            } else if (item instanceof DarkCrown) {
                is.type = ItemSave.ItemType.DARK_CROWN;
                is.varietyName = "Корона Тьмы";
            }
            ls.itemsOnFloor.add(is);
        }

        // 4. Враги
        ls.enemies = new ArrayList<>();
        for (Enemy enemy : level.getEnemies()) {
            EnemySave es = new EnemySave();
            es.type = enemy.getEnemyType().name();
            es.x = enemy.getX();
            es.y = enemy.getY();
            es.level = enemy.getLevel();
            es.health = enemy.getHealth();
            es.maxHealth = enemy.getMaxHealth();
            es.isAlive = enemy.isAlive();
            es.isVisible = enemy.isVisible();
            es.isDisguised = enemy.isDisguised();
            ls.enemies.add(es);
        }

        // 5. Туман войны (explorationMap)
        int height = level.getLevelMap().length;
        int width = level.getLevelMap()[0].length;
        ls.explorationMap = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ls.explorationMap[y][x] = level.getExploration(x, y).ordinal();
            }
        }

        // 6. Карта уровня
        ls.levelMap = level.getLevelMap();

        return ls;
    }

    // ==================== ЗАГРУЗКА ====================
    public static SaveGame loadGame() throws Exception {
        String json = Files.readString(Path.of(SAVE_FILE));
        Gson gson = new Gson();
        return gson.fromJson(json, SaveGame.class);
    }

    public static boolean hasSave() {
        return Files.exists(Path.of(SAVE_FILE));
    }

    public static void deleteSave() {
        try {
            Files.deleteIfExists(Path.of(SAVE_FILE));
            System.out.println("🗑️ Файл сохранения удалён.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void saveLeaderboard(List<Leaderboard.LeaderboardEntry> recording) {
        try {
            createSaveLoadFilesDir();
            LeaderBoardSave save = new LeaderBoardSave();
            save.recording = new ArrayList<>(recording);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(save);
            Files.writeString(Path.of(LEADERBOARD_FILE), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Leaderboard.LeaderboardEntry> loadLeaderboard() {
        try {
            String json = Files.readString(Path.of(LEADERBOARD_FILE));
            LeaderBoardSave save = new Gson().fromJson(json, LeaderBoardSave.class);
            return save.recording != null ? save.recording : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static void createSaveLoadFilesDir() throws IOException {
        Path dir = Path.of(RESOURCE_DIR);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
            System.out.println("📁 Создана папка: " + RESOURCE_DIR);
        }
    }

}