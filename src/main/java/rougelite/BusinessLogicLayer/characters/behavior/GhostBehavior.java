package rougelite.BusinessLogicLayer.characters.behavior;

import rougelite.BusinessLogicLayer.characters.Enemy;
import rougelite.BusinessLogicLayer.characters.Player;
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior;
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap;
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel;
import rougelite.BusinessLogicLayer.message.GameMessages;
import rougelite.BusinessLogicLayer.objects.Ammo;
import rougelite.BusinessLogicLayer.objects.GameObject;
import rougelite.BusinessLogicLayer.objects.Weapon;
import rougelite.PresentationLayer.ui.Messages;

import java.util.Random;


public class GhostBehavior implements EnemyBehavior{

        private static final boolean[][] AGRO_RADIUS = {

                {false, true, true, true, false},
                {true, true, true, true, true},
                {true, true, false, true, true},
                {true, true, true, true, true},
                {false, true, true, true, false},

        };

        private GameLevel currentLevel;
        private boolean ghostEnteredBattle = false;

        @Override
        public boolean[][] getAgroRadius() {
            return AGRO_RADIUS;
        }

        @Override
        public void enemyMovePattern(Enemy enemy, DrawRoomsMap room, Player player, GameLevel level) {
            this.currentLevel = level;
            Random random = new Random();
            int chanceVisible = random.nextInt(100);

            if (!ghostEnteredBattle)
            {
                if (chanceVisible < 70)
                {
                    enemy.setVisible(false);
                }
                else
                {
                    enemy.setVisible(true);
                }
            }

            if (this.isPlayerInAgroRadius(enemy, player, room)) {
                if (!enemy.isPlayerNoticed()) {
                    enemy.setPlayerNoticed(true);
                    Messages.show(GameMessages.ENEMY_NOTICED_YOU, 500);
                    ghostEnteredBattle = true;
                    enemy.setVisible(true);
                }

                if (attackOnPlayer(enemy, player)) {
                    return;
                }
            } else {
                enemy.setPlayerNoticed(false);
            }

            MoveDirection[] directions = {
                    MoveDirection.ENEMY_MOVE_RANDOM
            };
            MoveDirection direction = directions[random.nextInt(directions.length)];
            enemy.enemyMove(enemy, room, direction, player);
        }

        @Override
        public String pursuit(Enemy enemy, Player player, GameLevel level) {
            // Призрак не преследует, а телепортируется
            return null;
        }

        @Override
        public boolean attackOnPlayer(Enemy enemy, Player player)
        {
            boolean result = enemy.attackPlayer(player);
            if (result)
            {
                ghostEnteredBattle = true;
                enemy.setVisible(true);
            }
            return result;
        }

        @Override
        public void receivingDamage(Enemy enemy, int damage) {
            int healthBefore = enemy.getHealth();
            enemy.takeDamage(damage);
            System.out.println("💥 Призрак получил урон: " + damage + " (было: " + healthBefore + ", стало: " + enemy.getHealth() + ")");

            ghostEnteredBattle = true;
            enemy.setVisible(true);

            if (!enemy.isAlive()) {
                System.out.println("☠️ Призрак уничтожен!");
            }
        }

        @Override
        public GameObject dropGenerator(Enemy enemyType) {
            if (!enemyType.isAlive()) {
                Random random = new Random();
                int chance = random.nextInt(100);

                if (chance < 35) {

                    if (random.nextBoolean()) {
                        Weapon.WeaponType[] weaponTypes =
                                {
                                        Weapon.WeaponType.WARP_ANNIHILATOR
                                };
                        Weapon.WeaponType type = weaponTypes[random.nextInt(weaponTypes.length)];
                        Weapon weapon = new Weapon(type);
                        weapon.setPosition(enemyType.getX(), enemyType.getY());
                        return weapon;
                    }
                } else if (chance > 35 && chance < 70) {
                    if (random.nextBoolean()) {
                        Ammo.AmmoType[] ammoTypes =
                                {
                                        Ammo.AmmoType.WARP_ANNIHILATOR_AMMUNITION
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

