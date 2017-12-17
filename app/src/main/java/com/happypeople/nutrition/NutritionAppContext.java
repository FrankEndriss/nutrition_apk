package com.happypeople.nutrition;

import com.happypeople.nutrition.persistence.DataRepository;

/** Poor mans DI.
 * Use ((NutritionAppContext)getApplicationContext()).getXXX()
 * in any Activity of the app.
 */
public interface NutritionAppContext {
    /**
     * @return the persistence adapter used to find and store data.
     */
    DataRepository getDataRepository();
}
