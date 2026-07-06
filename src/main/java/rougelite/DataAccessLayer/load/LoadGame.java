package rougelite.DataAccessLayer.load;

import rougelite.BusinessLogicLayer.characters.Enemy;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.characters.behavior.*;
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior;
import rougelite.BusinessLogicLayer.inventory.Backpack;
import rougelite.BusinessLogicLayer.inventory.HiddenBackpack;
import rougelite.BusinessLogicLayer.inventory.Pocket;
import rougelite.BusinessLogicLayer.logic.MoveLogic;
import rougelite.BusinessLogicLayer.mapGenerator.*;
import rougelite.BusinessLogicLayer.objects.*;
import rougelite.DataAccessLayer.save.*;

import java.util.ArrayList;
import java.util.List;

public class LoadGame {

    // ==================== ВОССТАНОВЛЕНИЕ ИГРОКА ====================
    public static Player restorePlayer(PlayerSave playerSave) {
        if (playerSave == null) return null;

        Player player = new Player(
                playerSave.name,
                playerSave.maxHealth,
                playerSave.strength,
                playerSave.dexterity
        );
        player.setHealth(playerSave.health);
        player.setExperience(playerSave.experience);
        player.setCurrentPlayerLevel(playerSave.level);

        if (playerSave.position != null) {
            player.setLevelPosition(playerSave.position.x, playerSave.position.y);
            player.setRoomPosition(playerSave.position.x, playerSave.position.y);
        }

        // ==================== ВОССТАНОВЛЕНИЕ НАПРАВЛЕНИЯ ИГРОКА ====================
        if (playerSave.facing != null) {
            if (playerSave.facing.equals("RIGHT")) {
                player.setFacing(MoveLogic.Direction.RIGHT);
            } else {
                player.setFacing(MoveLogic.Direction.LEFT);
            }
        }

        return player;
    }

    // ==================== ВОССТАНОВЛЕНИЕ РУК ====================
    public static void restoreHands(Player player, HandsSave handsSave) {
        if (handsSave == null || player == null) return;
        HiddenBackpack hiddenBackpack = player.getHiddenBackpack();
        for (Hands hand : hiddenBackpack.getAllHands()) {
            if (hand.getType().name().equals(handsSave.type)) {
                hiddenBackpack.setEquippedHands(hand);
                break;
            }
        }
    }


    // ==================== ВОССТАНОВЛЕНИЕ ИНВЕНТАРЯ ====================
    public static void restoreInventory(Player player, InventorySave inventorySave) {
        if (player == null || inventorySave == null) return;

        Backpack backpack = player.getBackpack();

        // 1. Оружие
        if (inventorySave.weapons != null) {
            for (WeaponSave ws : inventorySave.weapons) {
                try {
                    Weapon.WeaponType type = Weapon.WeaponType.valueOf(ws.type);
                    Weapon weapon = new Weapon(type);
                    weapon.setCurrentAmmo(ws.currentAmmo);
                    if (ws.name != null && !ws.name.isEmpty()) {
                        weapon.setName(ws.name);
                    }
                    backpack.addWeapon(weapon);

                    if (inventorySave.equippedWeaponType != null &&
                            inventorySave.equippedWeaponType.equals(ws.type)) {
                        int currentIndex = backpack.getWeapons().size() - 1;
                        backpack.equipWeapon(currentIndex);
                        System.out.println("✅ Экипировано оружие: " + weapon.getName());
                    }

                } catch (IllegalArgumentException e) {
                    System.out.println("Неизвестный тип оружия: " + ws.type);
                }
            }
        }

        // 2. Амуниция
        if (inventorySave.ammo != null) {
            for (AmmoSave as : inventorySave.ammo) {
                try {
                    Ammo.AmmoType type = Ammo.AmmoType.valueOf(as.type);
                    Ammo ammo = new Ammo(type);
                    ammo.setAmount(as.amount);
                    backpack.addAmmo(ammo);
                } catch (IllegalArgumentException e) {
                    System.out.println("Неизвестный тип амуниции: " + as.type);
                }
            }
        }

        // 3. Зелья
        if (inventorySave.potions != null) {
            for (PotionSave ps : inventorySave.potions) {
                try {
                    HealthPotion.HealthPotionType type = HealthPotion.HealthPotionType.valueOf(ps.type);
                    HealthPotion potion = new HealthPotion(type);
                    backpack.addHealthPotion(potion);
                } catch (IllegalArgumentException e) {
                    System.out.println("Неизвестный тип зелья: " + ps.type);
                }
            }
        }

        // 4. Усиления
        if (inventorySave.reinforces != null) {
            for (ReinforceSave rs : inventorySave.reinforces) {
                try {
                    Reinforce.ReinforceType type = Reinforce.ReinforceType.valueOf(rs.type);
                    Reinforce reinforce = new Reinforce(type);
                    backpack.addReinforce(reinforce);
                } catch (IllegalArgumentException e) {
                    System.out.println("Неизвестный тип усиления: " + rs.type);
                }
            }
        }

        // 5. Сокровища
        backpack.addTreasure(inventorySave.treasure);
    }

    // ==================== ВОССТАНОВЛЕНИЕ КАРМАНА ====================
    public static Pocket restorePocket(PocketSave pocketSave, GameLevel gameLevel) {
        Pocket pocket = new Pocket();
        if (pocketSave != null && pocketSave.stones != null) {
            for (Integer roomIndex : pocketSave.stones) {
                for (DrawRoomsMap area : gameLevel.getRooms()) {
                    if (area instanceof Room) {
                        Room room = (Room) area;
                        if (room.getRoomIndex() == roomIndex) {
                            PortalStone stone = new PortalStone(roomIndex);
                            pocket.addStone(stone);
                            break;
                        }
                    } else if (area instanceof LastRoom) {
                        LastRoom lastRoom = (LastRoom) area;
                        if (lastRoom.getRoomIndex() == roomIndex) {
                            PortalStone stone = new PortalStone(roomIndex);
                            pocket.addStone(stone);
                            break;
                        }
                    }
                }
            }
        }
        return pocket;
    }

    // ==================== ВОССТАНОВЛЕНИЕ ПОРТАЛА ====================
    public static Portal restorePortal(PortalSave portalSave) {
        if (portalSave == null) return null;
        Portal portal = new Portal(portalSave.x, portalSave.y);
        if (portalSave.stonesInserted != null) {
            for (Integer stoneIndex : portalSave.stonesInserted) {
                portal.insertStone(stoneIndex);
            }
        }
        if (portalSave.isOpen) {
            portal.portalOpen(true);
        }
        return portal;
    }

    // ==================== ВОССТАНОВЛЕНИЕ УРОВНЯ ====================
    public static GameLevel restoreLevel(LevelSave levelSave, Portal portal, int windowWidth, int windowHeight) {
        if (levelSave == null) return null;

        GameLevel gameLevel = new GameLevel(levelSave.dungeonLevel);

        if (portal != null)
        {
            gameLevel.setPortal(portal);
            System.out.println("✅ Портал привязан к уровню при создании");
        }

        // 1. Комнаты
        List<DrawRoomsMap> restoredRooms = new ArrayList<>();
        if (levelSave.rooms != null) {
            for (RoomSave roomSave : levelSave.rooms) {
                // Определяем тип комнаты
                boolean isLastRoom = (roomSave.height == 11 && roomSave.length == 31 && roomSave.index == 4);

                DrawRoomsMap room;
                {
                    if (isLastRoom)
                    {
                        LastRoom lastRoom = new LastRoom(roomSave.index, roomSave.isCenterRoom);
                        lastRoom.setPosition(roomSave.x, roomSave.y);
                        room = lastRoom;
                        System.out.println("✅ Восстановлена LastRoom");
                    } else {
                        Room firstRoom = new Room(roomSave.index, roomSave.isCenterRoom);
                        firstRoom.setPosition(roomSave.x, roomSave.y);
                        firstRoom.setHeight(roomSave.height);
                        firstRoom.setLength(roomSave.length);
                        firstRoom.setTiles(roomSave.tiles);
                        room = firstRoom;
                    }

                    if (roomSave.hasPortalStone)
                    {
                        PortalStone stone = new PortalStone(
                                roomSave.portalStoneIndex != null ? roomSave.portalStoneIndex : roomSave.index,
                                roomSave.stoneX, roomSave.stoneY
                        );
                        if (room instanceof Room)
                        {
                            ((Room) room).setPortalStone(stone);
                        } else if (room instanceof LastRoom)
                        {
                            ((LastRoom) room).setPortalStone(stone);
                        }
                    }
                }

                if (roomSave.portalX != -1 && roomSave.portalY != -1) {
                    if (portal != null) {
                        if (room instanceof Room) {
                            ((Room) room).setPortal(portal);
                        } else if (room instanceof LastRoom) {
                            ((LastRoom) room).setPortal(portal);
                        }
                        System.out.println("✅ Портал привязан к комнате " + roomSave.index);
                    }
                }

                restoredRooms.add(room);
            }
        }
        gameLevel.setRooms(restoredRooms);

        // 2. Коридоры
        List<Corridor> restoredCorridors = new ArrayList<>();
        if (levelSave.corridors != null) {
            for (CorridorSave corridorSave : levelSave.corridors) {
                Corridor corridor = new Corridor(corridorSave.fromRoom, corridorSave.toRoom, corridorSave.path);
                restoredCorridors.add(corridor);
            }
        }
        gameLevel.setCorridors(restoredCorridors);

        // 3. Предметы
        List<GameObject> restoredItems = new ArrayList<>();
        if (levelSave.itemsOnFloor != null) {
            for (ItemSave itemSave : levelSave.itemsOnFloor) {
                GameObject obj = null;
                try {
                    switch (itemSave.type) {
                        case WEAPON:
                            Weapon w = new Weapon(Weapon.WeaponType.valueOf(itemSave.subtype));
                            w.setPosition(itemSave.x, itemSave.y);
                            if (itemSave.varietyName != null) w.setName(itemSave.varietyName);
                            w.setCurrentAmmo(itemSave.currentAmmo);
                            obj = w;
                            break;
                        case AMMO:
                            Ammo a = new Ammo(Ammo.AmmoType.valueOf(itemSave.subtype));
                            a.setAmount(itemSave.amount);
                            a.setPosition(itemSave.x, itemSave.y);
                            obj = a;
                            break;
                        case HEALTH_POTION:
                            HealthPotion p = new HealthPotion(HealthPotion.HealthPotionType.valueOf(itemSave.subtype));
                            p.setPosition(itemSave.x, itemSave.y);
                            obj = p;
                            break;
                        case REINFORCE:
                            Reinforce r = new Reinforce(Reinforce.ReinforceType.valueOf(itemSave.subtype));
                            r.setPosition(itemSave.x, itemSave.y);
                            obj = r;
                            break;
                        case DARK_CROWN:
                            DarkCrown crown = new DarkCrown(DarkCrown.DarkCrownType.DARK_CROWN);
                            crown.setPosition(itemSave.x, itemSave.y);
                            obj = crown;
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка восстановления предмета: " + e.getMessage());
                }
                if (obj != null) restoredItems.add(obj);
            }
        }
        gameLevel.setItems(restoredItems);

        // 4. Враги
        List<Enemy> restoredEnemies = new ArrayList<>();
        if (levelSave.enemies != null) {
            for (EnemySave enemySave : levelSave.enemies) {
                try {
                    Enemy.EnemyType type = Enemy.EnemyType.valueOf(enemySave.type);
                    EnemyBehavior behavior = createBehavior(type);
                    Enemy enemy = new Enemy(type, enemySave.x, enemySave.y, enemySave.level, behavior);
                    enemy.setHealth(enemySave.health);
                    enemy.setMaxHealth(enemySave.maxHealth);
                    enemy.setVisible(enemySave.isVisible);
                    if (!enemySave.isAlive) enemy.setHealth(0);
                    restoredEnemies.add(enemy);
                } catch (Exception e) {
                    System.out.println("Ошибка восстановления врага: " + e.getMessage());
                }
            }
        }
        gameLevel.setEnemies(restoredEnemies);

        // 5. Карты
        if (levelSave.levelMap != null) {
            gameLevel.setLevelMap(levelSave.levelMap);
            System.out.println("✅ Установлена карта уровня");
        }
        if (levelSave.explorationMap != null) {
            gameLevel.setExplorationMap(levelSave.explorationMap);
            System.out.println("✅ Установлена карта разведки");
        }

        // 5. Решаем, нужно ли перестраивать уровень
        if (levelSave.levelMap == null) {
            System.out.println("⚠️ Нет сохранённой карты, создаём новую...");
            gameLevel.buildLevel(windowWidth, windowHeight);
        } else {
            System.out.println("✅ Карта загружена из сохранения, buildLevel пропущен");
        }

        return gameLevel;
    }

    // ==================== СОЗДАНИЕ ПОВЕДЕНИЯ ====================
    private static EnemyBehavior createBehavior(Enemy.EnemyType type) {
        switch (type) {
            case ZOMBIE: return new ZombieBehavior();
            case VAMPIRE: return new VampireBehavior();
            case GHOST: return new GhostBehavior();
            case OGRE: return new OgreBehavior();
            case SNAKE_MAGE: return new SnakeMageBehavior();
            case BOSS: return new BossBehavior();
            default: return new ZombieBehavior();
        }
    }
}