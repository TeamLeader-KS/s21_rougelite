package rougelite.BusinessLogicLayer.logic;

public class ExperienceCalculator {
    private static final double BASE_EXP = 100.0;
    private static final double GROWTH_RATE = 1.5;  // ← МЕНЯЙТЕ ТОЛЬКО ЭТО


    // Автоматический расчёт (не трогать)
    private static final double SUM_DENOM = GROWTH_RATE - 1;
    private static final double INV_BASE = BASE_EXP / SUM_DENOM;  // для обратной формулы

    public static double expNextRaw(int level) {

        if (level < 1) return 0;
        return BASE_EXP * Math.pow(GROWTH_RATE, level - 1);
    }

    public static long expNext(int level) {
        return Math.round(expNextRaw(level));
    }

    public static double totalExpRaw(int level) {
        if (level <= 1) return 0;
        return INV_BASE * (Math.pow(GROWTH_RATE, level - 1) - 1);
    }

    public static long totalExp(int level) {
        return Math.round(totalExpRaw(level));
    }

    public static int getLevelByExp(long currentExp) {
        if (currentExp <= 0) return 1;

        double val = (double) currentExp / INV_BASE + 1.0;
        double logVal = Math.log(val);
        double logRate = Math.log(GROWTH_RATE);

        int level = (int) Math.floor(logVal / logRate) + 1;

        if (level < 1) level = 1;
        if (totalExp(level) > currentExp && level > 1) level--;

        return level;
    }

    public static long expToReach(int fromLevel, int toLevel) {
        if (toLevel <= fromLevel) return 0;
        return totalExp(toLevel) - totalExp(fromLevel);
    }

    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("GROWTH_RATE = " + GROWTH_RATE);
        System.out.println("Таблица опыта:");
        System.out.println("Ур. | Опыт до след. | Всего опыта");
        System.out.println("----|---------------|-------------");

        for (int level = 1; level <= 50; level++) {
            System.out.printf("%3d | %13d | %11d%n",
                    level, expNext(level), totalExp(level));
        }

        // Тест обратной формулы
        System.out.println("\n--- Проверка обратной формулы ---");
        for (int level = 1; level <= 20; level++) {
            long exp = totalExp(level);
            int calculatedLevel = getLevelByExp(exp);
            System.out.printf("Опыт %d -> уровень %d (должен быть %d) %s%n",
                    exp, calculatedLevel, level,
                    calculatedLevel == level ? "✓" : "✗");
        }
    }
}