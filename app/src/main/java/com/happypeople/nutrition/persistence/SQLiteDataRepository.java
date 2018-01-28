package com.happypeople.nutrition.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.happypeople.nutrition.MainActivity;
import com.happypeople.nutrition.model.Food;
import com.happypeople.nutrition.model.FoodAmount;
import com.happypeople.nutrition.model.NutritionListEntry;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.android.ContextHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 */
public class SQLiteDataRepository
        implements IDataRepository
{
    private final SQLiteOpenHelper dbHelper;

    public SQLiteDataRepository(final Context context) {
        dbHelper=new NutritionSQLiteOpenHelper(context);
    }

    @Override
    public void searchFoods(String query, ResultCallback<Food> result) {

    }

    @Override
    public Optional<Food> getFoodByName(String name) {
        return null;
    }

    @Override
    public void createFood(Food food) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void updateFood(Food food) {
        throw new RuntimeException("not implemented yet");
    }

    private final DateFormat tsFormat=new SimpleDateFormat("YYYY-MM-DD HH:mm");

    @Override
    public List<NutritionListEntry> getNutritionListEntries(Date day) throws ParseException {
        final DateFormat dayFormat=new SimpleDateFormat("YYYY-MM-dd");
        final String dayLikeString=dayFormat.format(day)+"%";
        final String sql="SELECT * FROM nutrition, food WHERE nutrition.food_id = food.id AND nutrition.ts LIKE ? ORDER BY nutrition.ts DESC";
        final SQLiteDatabase db=dbHelper.getReadableDatabase();

        final List<NutritionListEntry> list=new ArrayList<NutritionListEntry>();
        final Cursor cursor = db.rawQuery(sql, new String[] { dayLikeString });
        if(!cursor.moveToFirst())
            return list; // empty return list

        final int idxN_id=cursor.getColumnIndex("nutrition.id");
        final int idxN_food_id=cursor.getColumnIndex("nutrition.food_id");
        final int idxN_ts=cursor.getColumnIndex("nutrition.ts");
        final int idxN_amount_g=cursor.getColumnIndex("nutrition.amount_g");

        final int idxF_id=cursor.getColumnIndex("food.id");
        final int idxF_name=cursor.getColumnIndex("food.name");
        final int idxF_kcalper100g=cursor.getColumnIndex("food.kcalper100g");
        final int idxF_fatper100g=cursor.getColumnIndex("food.fatper100g");
        final int idxF_carboper100g=cursor.getColumnIndex("food.carboper100g");
        final int idxF_sugarper100g=cursor.getColumnIndex("food.sugarper100g");
        final int idxF_proteinper100g=cursor.getColumnIndex("food.proteinper100g");
        final int idxF_default_amount_g=cursor.getColumnIndex("food.default_amount_g");

        final DateFormat tsFormat=new SimpleDateFormat("YYYY-MM-DD HH:mm");
        final Map<String, Food> foodMap=new HashMap<String, Food>();

        while(!cursor.isAfterLast()) {
            final String foodId=cursor.getString(idxN_food_id);
            Food food=null;
            if(foodMap.containsKey(foodId))
                food=foodMap.get(foodId);
            else {
                food = new Food(
                        cursor.getString(idxF_id),
                        cursor.getString(idxF_name),
                        cursor.getInt(idxF_kcalper100g),
                        cursor.getDouble(idxF_fatper100g),
                        cursor.getDouble(idxF_carboper100g),
                        cursor.getDouble(idxF_sugarper100g),
                        cursor.getDouble(idxF_proteinper100g),
                        cursor.getDouble(idxF_default_amount_g)
                );
                foodMap.put(foodId, food);
            }

            final Date ts=tsFormat.parse(cursor.getString(idxN_ts));
            final FoodAmount foodAmount=new FoodAmount(cursor.getInt(idxN_amount_g), FoodAmount.FoodUnit.GRAM);
            list.add(new NutritionListEntry(ts, food, foodAmount));

            cursor.moveToNext();
        }
        return list;
    }

    @Override
    public void createNutritionListEntry(NutritionListEntry ent) {
        final ContentValues map=new ContentValues();
        map.put("id", UUID.randomUUID().toString());
        map.put("food_id", ent.getFood().getId());
        map.put("ts", tsFormat.format(ent.getTs()));
        map.put("amount_g", ent.getGrams());

        dbHelper.getWritableDatabase().insert("nutrition", null, map);
    }

    @Override
    public void updateNutritionListEntry(NutritionListEntry ent) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void deleteNutritionListEntry(NutritionListEntry ent) {
        throw new RuntimeException("not implemented yet");
    }
}
