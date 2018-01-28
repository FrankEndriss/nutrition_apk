package com.happypeople.nutrition;

import android.content.Context;

import com.happypeople.nutrition.persistence.DataRepository;
import com.happypeople.nutrition.persistence.IDataRepository;

/**
 * Poor mans DI.
 * Use ((NutritionAppContext)getApplicationContext()).getXXX()
 * in any Activity of the app.
 */
public interface NutritionAppContext {
    /**
     * @return the persistence adapter used to find and store data.
     */
    IDataRepository getDataRepository(Context context);
}
