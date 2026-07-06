package rougelite.BusinessLogicLayer.logic;

import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior;
import rougelite.BusinessLogicLayer.inventory.Backpack;
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap;
import rougelite.BusinessLogicLayer.message.GameMessages;
import rougelite.BusinessLogicLayer.objects.GameObject;
import rougelite.BusinessLogicLayer.objects.Hands;
import rougelite.BusinessLogicLayer.objects.Weapon;
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel;
import rougelite.BusinessLogicLayer.logic.WeaponEngine;
import rougelite.BusinessLogicLayer.logic.MoveLogic.Direction;
import rougelite.BusinessLogicLayer.characters.Enemy;
import rougelite.BusinessLogicLayer.mapGenerator.Room;
import rougelite.PresentationLayer.ui.Messages;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WeaponEngine {

    private final Player player;
    private final List<Projectile> projectiles = new ArrayList<>();
    private Direction lastHorizontalDirection = Direction.RIGHT; // запоминаем последнее горизонтальное направление
    private GameLevel currentLevel;
    private final GameEngine gameEngine;

    public WeaponEngine(Player player, GameLevel level, GameEngine gameEngine) {
        this.player = player;
        this.currentLevel = level;
        this.gameEngine = gameEngine;
    }

    public void setCurrentLevel(GameLevel level) {
        this.currentLevel = level;
    }

    // Обновляем направление при движении влево/вправо
    public void updateDirection(Direction direction) {
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            lastHorizontalDirection = direction;
            player.setFacing(direction);
        }
    }

    // ======= СТРЕЛЬБА =========
    public boolean shoot() {
        System.out.println("=== SHOOT CALLED ===");

        rays.clear();
        waves.clear();


        Weapon weapon = player.getBackpack().getEquippedWeapon();
        if (weapon == null) {
            System.out.println("No weapon");
            return meleeAttack();
        }

        if (weapon.getMaxAmmo() > 0 && weapon.getCurrentAmmo() <= 0) {
            Messages.show(GameMessages.NO_AMMUNITION, 500);
            return false;
        }

        System.out.println("Weapon type: " + weapon.getType());
        System.out.println("Current ammo: " + weapon.getCurrentAmmo());


        Direction shootDirection = lastHorizontalDirection;
        System.out.println("Shoot direction: " + shootDirection);

        // ===== позиция +1 для оружия =====
        int startX = player.getLevelX();
        int startY = player.getLevelY();

        switch (shootDirection) {
            case LEFT:
                startX = player.getLevelX() - 1;
                break;
            case RIGHT:
                startX = player.getLevelX() + 1;
                break;
            default:
                break;
        }


        // ===== ЛУК, ДРБОВИК, МИНИГАН =====
        if (weapon != null && (weapon.getType() == Weapon.WeaponType.BOW ||
                weapon.getType() == Weapon.WeaponType.SHOTGUN ||
                weapon.getType() == Weapon.WeaponType.MINIGUN)) {

            weapon.fire();

            int damageFromDexterity = weapon.getWeaponPower() + player.getDexterity();
            System.out.println("🏹 Урон с ловкостью: " + damageFromDexterity);

            Projectile projectile = new Projectile(
                    startX,
                    startY,
                    shootDirection,
                    weapon,
                    damageFromDexterity,
                    false
            );
            projectiles.add(projectile);
            return true;

        }

        // ===== ЛАЗЕР И БЛАСТЕР - создаём луч (RAY) =====
        if (weapon.getType() == Weapon.WeaponType.LASER ||
                weapon.getType() == Weapon.WeaponType.BLASTER) {

            weapon.fire();

            int maxDistance = 55;
            int[][] levelMap = currentLevel.getLevelMap();

            int rayStartX = startX;
            switch (shootDirection) {
                case LEFT:  rayStartX = startX - 1; break;
                case RIGHT: rayStartX = startX + 1; break;
                default: break;
            }

            int endX = rayStartX;
            int damage = weapon.getWeaponPower();


            List<Enemy> enemiesToRemove = new ArrayList<>();

            for (int i = 0; i <= maxDistance; i++) {
                int checkX = rayStartX + (shootDirection == Direction.RIGHT ? i : -i);

                if (checkX < 0 || checkX >= levelMap[0].length) break;
                if (levelMap[startY][checkX] == 1) break;

                for (Enemy enemy : currentLevel.getEnemies()) {
                    if (enemy.isAlive() && enemy.getX() == checkX && enemy.getY() == startY) {
                        boolean killed = enemy.takeDamageAndCheckDeath(damage);
                        System.out.println("⚡ Луч поразил врага! Урон: " + damage);
                        if (killed) {
                            System.out.println("ENEMY DEFEATED!");

                            gameEngine.addExperienceFromEnemy(enemy);
                            gameEngine.addTreasuresFromEnemy(enemy);

                            if (enemy.getEnemyType() == Enemy.EnemyType.BOSS) {
                                Messages.show(GameMessages.PICKUP_CROWN, 5000);
                            }


                            EnemyBehavior behavior = enemy.getBehavior();
                            if (behavior != null) {
                                if (!currentLevel.checkPositionPortalStone(enemy.getX(), enemy.getY()))
                                {
                                    GameObject drop = behavior.dropGenerator(enemy);
                                    if (drop != null) {
                                        drop.setPosition(enemy.getX(), enemy.getY());
                                        currentLevel.addItem(drop);
                                        System.out.println("🎁 Враг оставил: " + drop);
                                    }
                                }
                            }
                            enemiesToRemove.add(enemy);
                        }
                    }
                }

                endX = checkX;
            }

            for (Enemy enemy : enemiesToRemove) {
                currentLevel.removeEnemy(enemy);
            }

            // Создаём луч
            Ray ray = new Ray(rayStartX, startY, endX, startY, weapon);
            rays.add(ray);
            System.out.println("Луч создан от " + rayStartX + " до " + endX);

            return true;
        }

        // ===== АННИГИЛЯТОР - создаём волну по комнате =====
        if (weapon.getType() == Weapon.WeaponType.WARP_ANNIHILATOR) {
            weapon.fire();
            Room currentRoom = null;

            for (DrawRoomsMap area : currentLevel.getRooms()) {
                if (area instanceof Room) {
                    Room room = (Room) area;
                    if (player.getLevelX() >= room.getX() && player.getLevelX() < room.getX() + room.getLength() &&
                            player.getLevelY() >= room.getY() && player.getLevelY() < room.getY() + room.getHeight()) {
                        currentRoom = room;
                        break;
                    }

                }
            }

            if (currentRoom != null) {
                // Создаём волну
                AnnihilationWave wave = new AnnihilationWave(currentRoom.getRoomIndex(), weapon);
                waves.add(wave);
                System.out.println("Annihilation wave created in room " + currentRoom.getRoomIndex());

                int damage = weapon.getWeaponPower();
                List<Enemy> enemiesToRemove = new ArrayList<>();


                for (Enemy enemy : currentLevel.getEnemies()) {
                    if (enemy.getX() >= currentRoom.getX() && enemy.getX() < currentRoom.getX() + currentRoom.getLength() &&
                            enemy.getY() >= currentRoom.getY() && enemy.getY() < currentRoom.getY() + currentRoom.getHeight()) {

                        boolean isKilledByThisHit = enemy.takeDamageAndCheckDeath(damage);
                        System.out.println("Enemy hit! Damage: " + damage);

                        if (isKilledByThisHit) {
                            System.out.println("ENEMY DEFEATED!");

                            gameEngine.addExperienceFromEnemy(enemy);
                            gameEngine.addTreasuresFromEnemy(enemy);

                            if (enemy.getEnemyType() == Enemy.EnemyType.BOSS) {
                                Messages.show(GameMessages.PICKUP_CROWN, 5000);
                            }

                            EnemyBehavior behavior = enemy.getBehavior();
                            if (behavior != null) {
                                if (!currentLevel.checkPositionPortalStone(enemy.getX(), enemy.getY()))
                                {
                                    GameObject drop = behavior.dropGenerator(enemy);
                                    if (drop != null) {
                                        drop.setPosition(enemy.getX(), enemy.getY());
                                        currentLevel.addItem(drop);
                                        System.out.println("🎁 Враг оставил: " + drop);
                                    }
                                }
                            }

                            enemiesToRemove.add(enemy);
                        }
                    }
                }

                for (Enemy enemy : enemiesToRemove) {
                    currentLevel.getEnemies().remove(enemy);
                }
            }

            return true;
        }

        // ===== ОСТАЛЬНОЕ ОРУЖИЕ =====
        if (weapon.getType() != Weapon.WeaponType.KNIFE &&
                weapon.getType() != Weapon.WeaponType.SWORD &&
                weapon.getType() != Weapon.WeaponType.WARHAMMER) {

            weapon.fire();

            int damage = weapon.getWeaponPower();

            // ===== ОБЫЧНЫЕ СНАРЯДЫ (всё остальное оружие) =====
            Projectile projectile = new Projectile(
                    startX,
                    startY,
                    shootDirection,
                    weapon,
                    damage,
                    false
            );
            projectiles.add(projectile);

            System.out.println("Projectile added! Total: " + projectiles.size());
            System.out.println("Projectile at: x=" + projectile.x + ", y=" + projectile.y);

        } else {
            // Холодное оружие — используем meleeAttack
            return meleeAttack();

        }

        return true;
    }



    public List<Projectile> getProjectiles() {
        return new ArrayList<>(projectiles);
    }

    public Direction getLastHorizontalDirection() {
        return lastHorizontalDirection;
    }

    // ==================== ВНУТРЕННИЙ КЛАСС СНАРЯДА ====================

    public static class Projectile {
        public int x, y;
        public final int startX, startY;
        public final Direction direction;
        public final Weapon weapon;
        public final int damage;
        public final boolean isLaser;
        private int distance = 0;
        public static final int MAX_DISTANCE = 30;

        public Projectile(int x, int y, Direction direction, Weapon weapon, int damage, boolean isLaser) {
            this.startX = x;
            this.startY = y;
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.weapon = weapon;
            this.damage = damage;
            this.isLaser = isLaser;
        }

        public void move() {
            switch (direction) {
                case LEFT:
                    x--;
                    break;
                case RIGHT:
                    x++;
                    break;
                default:
                    break;
            }
        }

        public void increaseDistance() {
            distance++;
        }

        public boolean isOutOfBounds() {
            return distance >= MAX_DISTANCE;
        }
    }


    public void update() {

        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();

            p.move();
            p.increaseDistance();

            // Проверка границ
            if (p.x < 0 || p.x >= currentLevel.getLevelMap()[0].length ||
                    p.y < 0 || p.y >= currentLevel.getLevelMap().length) {
                System.out.println("OUT OF BOUNDS! Removing");
                iterator.remove();
                continue;
            }

            // Проверка стены
            int tile = currentLevel.getLevelMap()[p.y][p.x];
            if (tile == 1) {
                System.out.println("HIT WALL! Removing at x=" + p.x + ", y=" + p.y);
                iterator.remove();
                continue;
            }

            // ===== ПРОВЕРКА ВРАГОВ =====
            boolean hitEnemy = false;
            Enemy enemyToRemove = null;


            for (Enemy enemy : currentLevel.getEnemies()) {
                if (enemy.isAlive() && enemy.getX() == p.x && enemy.getY() == p.y) {

                    boolean isKilledByThisHit = enemy.takeDamageAndCheckDeath(p.damage);

                    System.out.println("HIT ENEMY! Damage: " + p.damage);

                    if (isKilledByThisHit) {
                        System.out.println("ENEMY DEFEATED!");

                        gameEngine.addExperienceFromEnemy(enemy);
                        gameEngine.addTreasuresFromEnemy(enemy);

                        if (enemy.getEnemyType() == Enemy.EnemyType.BOSS) {
                            Messages.show(GameMessages.PICKUP_CROWN, 5000);
                        }

                        EnemyBehavior behavior = enemy.getBehavior();
                        if (behavior != null) {
                            if (!currentLevel.checkPositionPortalStone(enemy.getX(), enemy.getY()))
                            {
                                GameObject drop = behavior.dropGenerator(enemy);
                                if (drop != null) {
                                    drop.setPosition(enemy.getX(), enemy.getY());
                                    currentLevel.addItem(drop);
                                    System.out.println("🎁 Враг оставил: " + drop);
                                }
                            }
                        }
                        enemyToRemove = enemy;
                    }
                    hitEnemy = true;
                    break;
                }
            }

            if (enemyToRemove != null) {
                currentLevel.removeEnemy(enemyToRemove);
            }

            if (hitEnemy) {
                iterator.remove();
                continue;
            }

            if (p.distance >= p.MAX_DISTANCE) {
                System.out.println("MAX DISTANCE! Removing");
                iterator.remove();
            }
        }
    }

    // лучи
    public static class Ray {
        public int startRayX;
        public int startRayY;
        public int endRayX;
        public int endRayY;
        public Weapon weapon;

        public Ray(int startRayX, int startRayY, int endRayX, int endRayY, Weapon weapon) {
            this.startRayX = startRayX;
            this.startRayY = startRayY;
            this.endRayX = endRayX;
            this.endRayY = endRayY;
            this.weapon = weapon;
        }
    }

    private final List<Ray> rays = new ArrayList<>();

    public List<Ray> getRays() {
        return new ArrayList<>(rays);
    }

    public void clearRays() {
        rays.clear();
    }

    // волна

    public static class AnnihilationWave {
        public int roomIndex;
        public Weapon weapon;

        public AnnihilationWave(int roomIndex, Weapon weapon) {
            this.roomIndex = roomIndex;
            this.weapon = weapon;
        }
    }

    private final List<AnnihilationWave> waves = new ArrayList<>();

    public List<AnnihilationWave> getWaves() {
        return new ArrayList<>(waves);
    }

    public void clearWaves() {
        waves.clear();
    }

    public boolean meleeAttack() {
        Weapon weapon = player.getBackpack().getEquippedWeapon();
        Hands hands = player.getEquippedHands(); // Всегда не null (FISTS или NAILS)

        if (weapon == null && hands == null)
        {
            Messages.show(GameMessages.NO_READY_FOR_FIGHT, 500);
            return false;
        }
        int damageFromStrength;
        String attackName;

        if (weapon != null && (weapon.getType() == Weapon.WeaponType.KNIFE ||
                weapon.getType() == Weapon.WeaponType.SWORD ||
                weapon.getType() == Weapon.WeaponType.WARHAMMER)) {
            damageFromStrength = weapon.getWeaponPower();
            attackName = weapon.getName();
            System.out.println("⚔️ Удар " + attackName + "! Урон: " + damageFromStrength);
        } else {
            damageFromStrength = hands.getHandsPower();
            attackName = hands.getName();
            System.out.println("👊 Удар " + attackName + "! Урон: " + damageFromStrength);
        }

        int totalDamage = damageFromStrength + player.getStrength();
        System.out.println("💥 Итоговый урон: " + damageFromStrength + " (база) + " + player.getStrength() + " (сила) = " + totalDamage);

        int targetX = player.getLevelX();
        int targetY = player.getLevelY();
        Direction facing = player.getFacing();

        switch (facing) {
            case LEFT:
                targetX -= 2;
                break;
            case RIGHT:
                targetX += 2;
                break;
            default:
                return false;
        }

        for (Enemy enemy : currentLevel.getEnemies()) {
            if (enemy.isAlive() && enemy.getX() == targetX && enemy.getY() == targetY) {
                enemy.takeDamage(totalDamage);
                System.out.println("💥 Попадание! Урон: " + totalDamage);

                if (!enemy.isAlive()) {
                    System.out.println("ENEMY DEFEATED!");

                    gameEngine.addExperienceFromEnemy(enemy);
                    gameEngine.addTreasuresFromEnemy(enemy);

                    if (enemy.getEnemyType() == Enemy.EnemyType.BOSS) {
                        Messages.show(GameMessages.PICKUP_CROWN, 5000);
                    }

                    EnemyBehavior behavior = enemy.getBehavior();
                    if (behavior != null) {
                        if (!currentLevel.checkPositionPortalStone(enemy.getX(), enemy.getY()))
                        {
                            GameObject drop = behavior.dropGenerator(enemy);
                            if (drop != null) {
                                drop.setPosition(enemy.getX(), enemy.getY());
                                currentLevel.addItem(drop);
                                System.out.println("🎁 Враг оставил: " + drop);
                            }
                        }
                    }
                    currentLevel.removeEnemy(enemy);
                }
                return true;
            }
        }

        System.out.println("💨 Взмах! Никого нет.");
        return false;
    }


}