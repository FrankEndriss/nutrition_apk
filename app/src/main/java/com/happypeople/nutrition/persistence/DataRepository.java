package com.happypeople.nutrition.persistence;

import com.happypeople.nutrition.model.Food;
import com.happypeople.nutrition.model.FoodAmount.FoodUnit;
import com.happypeople.nutrition.model.NutritionListEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Simple persistence/datastore interface/implementation.
 */
//@Component(value="dataRepository")
//@ManagedBean(name="dataRepository")
//@ApplicationScoped
public class DataRepository {
    private final static Comparator<Food> foodNameComparator = new Comparator<Food>() {
        @Override
        public int compare(final Food o1, final Food o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
    /**
     * SampleData for testing.
     */
    private final SampleData sampleData = new SampleData();
    private Object searchLock = new Object();
    private volatile boolean stopSearch = false;

    public List<FoodUnit> getAmountUnits() {
        return Arrays.asList(FoodUnit.values());
    }

    /**
     * Searches for Food and places the search results in result.
     *
     * @param query  Query string
     * @param result target
     */
    public void searchFoods(String query, ResultCallback<Food> result) {

        stopSearch = true;
        synchronized (searchLock) {
            stopSearch = false;
            searchFoods_woSync(query, result);
        }
    }

    /**
     * searches for foods matching query, placing the result
     * in result. Ther query can be stopped by setting stopSearch to true.
     *
     * @param query
     * @param result
     */
    private void searchFoods_woSync(String query, ResultCallback<Food> result) {
        final Set<String> resultSet = new HashSet<String>();

        // search for startsWith(...)
        for (Food food : getFoods()) {
            if (stopSearch)
                return;

            if (food.getName().startsWith(query)) {
                resultSet.add(food.getName());
                result.add(food);
            }
        }

        // search for contains(...)
        for (Food food : getFoods()) {
            if (stopSearch)
                return;
            if (food.getName().contains(query) && !resultSet.contains(food.getName())) {
                resultSet.add(food.getName());
                result.add(food);
            }
        }

        // search for containsAll(...) and report
        // sorted by match count
        final String[] qArr = query.split(" ");
        if (qArr.length > 1) {
            List<Food>[] moreResults = new ArrayList[qArr.length];
            for (int i = 0; i < moreResults.length; i++)
                moreResults[i] = new ArrayList<Food>();

            for (Food food : getFoods()) {
                if (stopSearch)
                    return;

                int matchCount = 0;
                for (String str : qArr)
                    if (food.getName().contains(str))
                        matchCount++;
                if (matchCount > 0)
                    moreResults[matchCount - 1].add(food);
            }

            // add sorted by match count
            for (int i = moreResults.length - 1; i >= 0; i--) {
                if (stopSearch)
                    return;

                for (Food food : moreResults[i])
                    if (resultSet.add(food.getName()))
                        result.add(food);
            }
        }
    }

    /**
     * @return All existing foods, sorted by name.
     */
    public List<Food> getFoods() {
        final List<Food> ret = new ArrayList<Food>();
        ret.addAll(sampleData.foods);
        ret.sort(foodNameComparator);
        return ret;
    }

    public Optional<Food> getFoodByName(final String name) {
        for (final Food food : sampleData.foods) {
            if (food.getName().equals(name))
                return Optional.of(food);
        }
        return Optional.empty();
    }

    public void createFood(final Food food) {
        sampleData.foods.add(food);
    }

    public void updateFood(final Food food) {
        throw new RuntimeException("not implemented yet");
    }

    public List<NutritionListEntry> getNutritionListEntries(final Date day) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        final String dayStr = format.format(day);

        final List<NutritionListEntry> ret = new ArrayList<>();
        for (NutritionListEntry ent : sampleData.sampleNutritions)
            if (format.format(ent.getTs()).equals(dayStr))
                ret.add(ent);

        return ret;
    }

    public void createNutritionListEntry(final NutritionListEntry nut) {
        sampleData.sampleNutritions.add(nut);
    }

    public void updateNutritionListEntry(final NutritionListEntry nut) {
        throw new RuntimeException("not implemented yet");
    }

    public void deleteNutritionListEntry(final NutritionListEntry ent) {
        throw new RuntimeException("not implemented yet");
    }

    public interface ResultCallback<T> {
        void add(T resultObject);
    }

}
