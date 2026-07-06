package rougelite.BusinessLogicLayer.message;

public class GameMessages {
    // === РЮКЗАК ===

    // добавление
    public static final String BACKPACK_ADD_WEAPON = "Оружие добавлено в рюкзак";
    public static final String BACKPACK_ADD_AMMO = "Амуниция добавлена в рюкзак";
    public static final String BACKPACK_ADD_POTION = "Зелье добавлено в рюкзак";
    public static final String BACKPACK_ADD_REINFORCE = "Предмет добавлен в рюкзак";
    // ремув
    public static final String BACKPACK_REMOVE_AMMO = "Амуниция удалена из рюкзака";
    public static final String BACKPACK_REMOVE_POTION = "Зелье удалено из рюкзака";
    // сотальное
    public static final String BACKPACK_FULL = "Рюкзак переполнен!";
    public static final String BACKPACK_HAS_WEAPON = "У тебя уже есть такое оружие!";
    public static final String BACKPACK_HAS_REINFORCE = "У тебя уже есть такой предмет!";

    // === экипировка оружия ===
    public static final String EQUIP_WEAPON = "В твоих руках %s";
    public static final String UNEQUIP_WEAPON = "Ты убрал %s";
    public static final String NO_WEAPON = "У тебя пока нет оружия!";

    // === экипировка рук ===
    public static final String EQUIP_FISTS = "Пока ты без оружия. Размахивай кулаками!";
    public static final String EQUIP_NAILS = "Пока ты без оружия. Кусайся и царапайся!";
    public static final String UNEQUIP_FISTS = "Ты спрятал кулаки!";
    public static final String UNEQUIP_NAILS = "Ты убрал когти!";
    public static final String NO_READY_FOR_FIGHT = "Тебе надо подготовиться к сражению!";

    // === ПОДБОР ПРЕДМЕТОВ ===
    public static final String PICKUP_WEAPON = "Ты нашел оружие!";
    public static final String PICKUP_AMMO = "Ты нашел амуницию!";
    public static final String PICKUP_POTION = "Ты нашел зелье здоровья!";
    public static final String PICKUP_REINFORCE = "Ты нашел предмет!";
    // корона
    public static final String PICKUP_CROWN = "Корона Тьмы твоя! Одень ее и стань нашим новым повелителем!";

    // === ПОРТАЛ И КАМНИ ===
    public static final String IMPOSSIBLE_USE_PORTAL_STONE = "Ты не можешь активировать портал из этого места!";
    public static final String ACTIVATED_PORTAL = "ПОРТАЛ АКТИВИРОВАН!";
    public static final String INSERT_PORTAL_STONE = "Камень установлен!"; // TODO возможно лишнее
    public static final String ENTER_PORTAL = "Ты готов войти в портал?";
    public static final String PORTAL_NEXT_LEVEL = "Ты переместился на новый уровень подземелья!";
    public static final String PICKUP_PORTAL_STONE = "Ты подобрал камень портала";

    // === КАРМАН === TODO возможно лишнее
    public static final String POCKET_ADD_PORTAL_STONE = "Камень установлен!";
    public static final String CLEAR_POCKET = "Ты использовал все камни! Карман свободен.";

    // ИСПОЛЬЗОВАНИЕ

    // === Стрельба ===
    public static final String NO_AMMUNITION = "Требуется перезарядка оружия!";
    // === Амуниция ===
    public static final String USE_AMMO = "Оружие перезаряжено!";
    public static final String FULL_AMMO = "Оружие полностью заряжено!";
    public static final String IMPOSSIBLE_USE_AMMO = "Не получится использовать с этим оружием!";
    public static final String BLADED_WEAPON = "Этому оружию не нужна амуниция!";
    public static final String NOT_EQUIP_WEAPON = "Нет экипированного оружия!";


    // === ЗЕЛЬЯ ===
    public static final String USE_HEALTH_POTION = "Ты выпил %s! Здоровье восстановлено!";
    public static final String FULL_HEALTH = "Здоровье уже полное!";

    // === РОСТ ХАРАКТЕРИСТИК ===

    // экспа
    public static final String EXPERIENCE_UP = "Получен опыт!";
    // сокровища
    public static final String TREASURE_UP = "Получены сокровища!";
    // новый уровень
    public static final String LEVEL_UP = "Получен новый уровень!";

    // сообщение для врагов
    public static final String ENEMY_NOTICED_YOU = "Враг тебя заметил!";
    public static final String ZOMBIE_ATTACKED_YOU = "Зомби укусил тебя!";
    public static final String VAMPIRE_ATTACKED_YOU = "Вампир вонзил клыки в твою шею!";
    public static final String OGRE_ATTACKED_YOU = "Огр ударил тебя своей дубиной!";
    public static final String GHOST_ATTACKED_YOU = "Призрак вогнал тебя в ужас!";
    public static final String SNAKE_MAGE_ATTACKED_YOU = "Змей-Маг наложил на тебя смертельное заклятье!";
    public static final String BOSS_ATTACKED_YOU = "Владыка Тьмы поразил тебя темным клинком!";
    public static final String BOSS_SCARES_YOU = "Ты умрешь, смертный!";

    public static final String[] BOSS_SCARES = {
            "Ты умрешь, смертный!",
            "Сдохни, червь!",
            "Ты слабак!",
            "Не смеши меня!",
            "Ты никогда не победишь!"
    };


    // === БОЙ ===
    public static final String BATTLE_START = "⚔️ БОЙ НАЧАЛСЯ: %s vs %s ⚔️";
    public static final String BATTLE_ENEMY_DEFEATED = "✅ %s повержен!";
    public static final String BATTLE_PLAYER_DEAD = "💀 %s погиб в бою...";
    public static final String BATTLE_ATTACK = "👉 %s атакует %s";
    public static final String BATTLE_HIT = "   Попадание! Урон: %d. У цели осталось HP: %d";
    public static final String BATTLE_MISS = "   Промах!";


    // ==================== МЕТОДЫ ДЛЯ ФОРМАТИРОВАНИЯ ====================

    public static String formatEquipWeapon(String weaponName) {
        return String.format(EQUIP_WEAPON, weaponName);
    }

    public static String formatUnequipWeapon(String weaponName) {
        return String.format(UNEQUIP_WEAPON, weaponName);
    }

    public static String formatUseReinforce(String reinforceName) {
        return String.format(USE_AMMO, reinforceName);
    }

    public static String formatUseHealthPotion(String potionName) {
        return String.format(USE_HEALTH_POTION, potionName);
    }

    public static String formatBattleStart(String playerName, String enemyName) {
        return String.format(BATTLE_START, playerName, enemyName);
    }

    public static String formatEnemyDefeated(String enemyName) {
        return String.format(BATTLE_ENEMY_DEFEATED, enemyName);
    }

    public static String formatPlayerDead(String playerName) {
        return String.format(BATTLE_PLAYER_DEAD, playerName);
    }

    public static String formatAttack(String attackerName, String targetName) {
        return String.format(BATTLE_ATTACK, attackerName, targetName);
    }

    public static String formatHit(int damage, int remainingHp) {
        return String.format(BATTLE_HIT, damage, remainingHp);
    }

}
