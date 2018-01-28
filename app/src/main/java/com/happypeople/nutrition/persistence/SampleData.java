package com.happypeople.nutrition.persistence;

import com.happypeople.nutrition.model.Food;
import com.happypeople.nutrition.model.FoodAmount;
import com.happypeople.nutrition.model.FoodAmount.FoodUnit;
import com.happypeople.nutrition.model.NutritionListEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Some sample data for testing.
 */
class SampleData {

    static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public final List<Food> foods = mkList(
            mkFood("Milch 3,5%", 66, 3.5, 4.7, 4.7, 3.4, 200.0),
            mkFood("Milch 1,5%", 46, 1.5, 4.7, 4.7, 3.4, 200.0),
            mkFood("Milch 0,5%", 36, 0.5, 4.7, 4.7, 3.4, 200.0),
            mkFood("Hansano Bulgaria Joghurt 0,1%", 39, 0.1, 5.4, 4.8, 4.2, 250.0),
            mkFood("Elite Körniger Frischkäse 0,4%", 72, 0.4, 4.0, 3.8, 12.7, 200.0),
            mkFood("Elite Kräuterquark 2,4%", 75, 2.4, 4.0, 3.8, 8.8, 200.0),
            mkFood("Kräuterquark 40%", 141, 10.1, 3.6, 2.5, 8.5, 200.0),
            mkFood("Penny Basismüsli 5-Korn-Mix", 366, 7.6, 56.0, 8.0, 12.0, 50.0),
            mkFood("Granola Knuspermüsli", 446, 15.5, 63.5, 38.0, 9.6, 50.0),
            mkFood("Thunfisch Dose", 118, 1.5, 0.0, 0.0, 26.0, 150.0),
            mkFood("Rosinen", 320, 0.5, 73.0, 54.0, 2.8, 20.0),
            mkFood("Öl Raps", 900, 100.0, 0.0, 0.0, 0.0, 10.0),
            mkFood("ELF Energy Cake Joghurt", 448, 19.7, 58.2, 26.0, 6.4, 125.0),
            mkFood("ELF Energy Cake Original", 437, 18.7, 58.0, 26.0, 6.4, 125.0),
            mkFood("ELF Energy Cake Toffee", 444, 18.4, 62.9, 26.0, 5.7, 125.0)
    );
    /**
     * some sample nutrition for testing
     */
    public final List<NutritionListEntry> sampleNutritions = mkList(
            mkNutri("2017-12-18 09:30", foods.get(0), 250, FoodUnit.GRAM),
            mkNutri("2017-12-18 11:30", foods.get(5), 20, FoodUnit.GRAM),
            mkNutri("2017-12-18 12:00", foods.get(2), 150, FoodUnit.GRAM),
            mkNutri("2017-12-18 18:00", foods.get(7), 125, FoodUnit.GRAM),
            mkNutri("2017-12-17 08:00", foods.get(4), 250, FoodUnit.GRAM),
            mkNutri("2017-12-17 18:00", foods.get(7), 125, FoodUnit.GRAM),
            mkNutri("2017-12-17 18:00", foods.get(3), 200, FoodUnit.GRAM)
    );

    private static <T> List<T> mkList(final T... objects) {
        final List<T> list = new ArrayList<T>();
        for (final T t : objects)
            list.add(t);
        return list;
    }

    private static Date d(final String dateStr) {
        try {
            return df.parse(dateStr);
        } catch (final ParseException e) {
            throw new RuntimeException("should not happen", e);
        }
    }

    private static NutritionListEntry mkNutri(final String date, final Food food, final int amount, final FoodUnit unit) {
        return new NutritionListEntry(d(date), food, new FoodAmount(amount, unit));
    }

    private static Food mkFood(final String name, final int kcalPer100g,
                               final double fatGrams, final double carboGrams, final double sugarGrams, final double proteinGrams, final double defaultAmount) {

        return new Food(UUID.randomUUID().toString(), name, kcalPer100g, fatGrams, carboGrams, sugarGrams, proteinGrams, defaultAmount);
    }
}
