package com.happypeople.nutrition.persistence;

import com.happypeople.nutrition.model.Food;
import com.happypeople.nutrition.model.NutritionListEntry;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 */
public interface IDataRepository {
    void searchFoods(String query, ResultCallback<Food> result);

    Optional<Food> getFoodByName(String name);

    void createFood(Food food);

    void updateFood(Food food);

    List<NutritionListEntry> getNutritionListEntries(Date day) throws ParseException;

    void createNutritionListEntry(NutritionListEntry nut);

    void updateNutritionListEntry(NutritionListEntry nut);

    void deleteNutritionListEntry(NutritionListEntry ent);

    public interface ResultCallback<T> {
        void add(T resultObject);
    }
}
