package rougelite.BusinessLogicLayer.characters.behavior;

import rougelite.BusinessLogicLayer.characters.Enemy;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior;
import rougelite.BusinessLogicLayer.mapGenerator.*;
import rougelite.BusinessLogicLayer.message.GameMessages;
import rougelite.BusinessLogicLayer.objects.Ammo;
import rougelite.BusinessLogicLayer.objects.GameObject;
import rougelite.BusinessLogicLayer.objects.Weapon;
import rougelite.PresentationLayer.ui.Messages;
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel;
import rougelite.BusinessLogicLayer.objects.*;

import java.util.Random;
import java.util.*;



public class SnakeMageBehavior implements EnemyBehavior {

        private static final boolean[][] AGRO_RADIUS = {
                {false, false, true, true, true, true, true, false, false},
                {false, true, true, true, true, true, true, true, false},
                {true, true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true, true},
                {true, true, true, true, false, true, true, true, true},
                {true, true, true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true, true, true},
                {false, true, true, true, true, true, true, true, false},
                {false, false, true, true, true, true, true, false, false},
        };

        private GameLevel currentLevel;

        @Override
        public boolean[][] getAgroRadius() {
            return AGRO_RADIUS;
        }

        @Override
        public void enemyMovePattern(Enemy enemy, DrawRoomsMap room, Player player, GameLevel level) {
            this.currentLevel = level;
            Random random = new Random();

            if (this.isPlayerInAgroRadius(enemy, player, room)) {
                if (!enemy.isPlayerNoticed()) {
                    enemy.setPlayerNoticed(true);
                    Messages.show(GameMessages.ENEMY_NOTICED_YOU, 500);
                }

                if (attackOnPlayer(enemy, player)) {
                    return;
                }
            } else {
                enemy.setPlayerNoticed(false);
            }

            MoveDirection[] directions = {
                    MoveDirection.ENEMY_MOVE_UP_LEFT,
                    MoveDirection.ENEMY_MOVE_UP_RIGHT,
                    MoveDirection.ENEMY_MOVE_DOWN_LEFT,
                    MoveDirection.ENEMY_MOVE_DOWN_RIGHT,
            };
            MoveDirection direction = directions[random.nextInt(directions.length)];
            enemy.enemyMove(enemy, room, direction, player);
            enemy.enemyMove(enemy, room, direction, player);
        }

        @Override
        public String pursuit(Enemy enemy, Player player, GameLevel level) {
            int startX = enemy.getX();
            int startY = enemy.getY();
            int targetX = player.getLevelX();
            int targetY = player.getLevelY();

            int[][] map = level.getLevelMap();
            int height = map.length;
            int width = map[0].length;

            Set<String> occupied = new HashSet<>();
            for (Enemy e : level.getEnemies()) {
                if (e != enemy) occupied.add(e.getX() + "," + e.getY());
            }
            for (GameObject item : level.getItems()) {
                occupied.add(item.getX() + "," + item.getY());
            }

            boolean[][] visited = new boolean[height][width];
            int[][] parentX = new int[height][width];
            int[][] parentY = new int[height][width];
            MoveDirection[][] parentDir = new MoveDirection[height][width];

            Queue<int[]> queue = new ArrayDeque<>();
            queue.add(new int[]{startX, startY});
            visited[startY][startX] = true;

            int[] dx = {0, 0, -1, 1, -1, 1, -1, 1};
            int[] dy = {-1, 1, 0, 0, -1, -1, 1, 1};
            MoveDirection[] dirs = {
                    MoveDirection.ENEMY_MOVE_UP,
                    MoveDirection.ENEMY_MOVE_DOWN,
                    MoveDirection.ENEMY_MOVE_LEFT,
                    MoveDirection.ENEMY_MOVE_RIGHT,
                    MoveDirection.ENEMY_MOVE_UP_LEFT,
                    MoveDirection.ENEMY_MOVE_UP_RIGHT,
                    MoveDirection.ENEMY_MOVE_DOWN_LEFT,
                    MoveDirection.ENEMY_MOVE_DOWN_RIGHT
            };

            while (!queue.isEmpty()) {
                int[] cur = queue.poll();
                int x = cur[0], y = cur[1];

                boolean isAdjacentToPlayer = (Math.abs(x - targetX) + Math.abs(y - targetY) == 1);
                if (isAdjacentToPlayer) {
                    if (map[y][x] != 1 && !occupied.contains(x + "," + y)) {
                        while (parentX[y][x] != startX || parentY[y][x] != startY) {
                            int px = parentX[y][x];
                            int py = parentY[y][x];
                            x = px;
                            y = py;
                        }
                        return parentDir[y][x].name();
                    }
                }

                for (int i = 0; i < 8; i++) {
                    int nx = x + dx[i];
                    int ny = y + dy[i];
                    if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                    if (visited[ny][nx]) continue;
                    if (map[ny][nx] == 1) continue;
                    if (occupied.contains(nx + "," + ny)) continue;

                    visited[ny][nx] = true;
                    parentX[ny][nx] = x;
                    parentY[ny][nx] = y;
                    parentDir[ny][nx] = dirs[i];
                    queue.add(new int[]{nx, ny});
                }
            }

            return null;
        }

        @Override
        public boolean attackOnPlayer(Enemy enemy, Player player)
        {
            return enemy.attackPlayer(player);
        }

        @Override
        public void receivingDamage(Enemy enemy, int damage) {
            int healthBefore = enemy.getHealth();
            enemy.takeDamage(damage);
            System.out.println("💥 Призрак получил урон: " + damage + " (было: " + healthBefore + ", стало: " + enemy.getHealth() + ")");
            if (!enemy.isAlive()) {
                System.out.println("☠️ Призрак уничтожен!");
            }
        }

        @Override
        public GameObject dropGenerator(Enemy enemyType) {
            if (!enemyType.isAlive()) {
                Random random = new Random();
                int chance = random.nextInt(100);

                if (chance < 40) {

                    if (random.nextBoolean()) {
                        Weapon.WeaponType[] weaponTypes =
                                {
                                        Weapon.WeaponType.LASER,
                                        Weapon.WeaponType.BLASTER
                                };
                        Weapon.WeaponType type = weaponTypes[random.nextInt(weaponTypes.length)];
                        Weapon weapon = new Weapon(type);
                        weapon.setPosition(enemyType.getX(), enemyType.getY());
                        return weapon;
                    }
                } else if (chance > 40 && chance < 80) {
                    if (random.nextBoolean()) {
                        Ammo.AmmoType[] ammoTypes =
                                {
                                        Ammo.AmmoType.LASER_AMMUNITION,
                                        Ammo.AmmoType.BLASTER_AMMUNITION
                                };
                        Ammo.AmmoType type = ammoTypes[random.nextInt(ammoTypes.length)];
                        Ammo ammo = new Ammo(type);
                        ammo.setPosition(enemyType.getX(), enemyType.getY());
                        return ammo;
                    }
                } else {
                    return null;
                }
                return null;
            }
            return null;
        }
    }


