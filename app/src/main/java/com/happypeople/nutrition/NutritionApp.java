package com.happypeople.nutrition;

import android.app.Application;
import android.content.Context;

import com.happypeople.nutrition.persistence.IDataRepository;
import com.happypeople.nutrition.persistence.DataRepository;
import com.happypeople.nutrition.persistence.SQLiteDataRepository;


/**
 * This is the poor mans DI-Container implementation.
 * Just call one of the NutritionAppContext methods to find
 * the global/singleton Objects.
 * This pattern can easyly be extended to use String names instead
 * of method names, and/or one can send a scope to the get-methods,
 * and create objects on the fly and so on...
 */
public class NutritionApp extends Application implements NutritionAppContext {

    private IDataRepository dataRepository;

    @Override
    public IDataRepository getDataRepository(Context context) {
        if(dataRepository==null)
            dataRepository= new SQLiteDataRepository(context);

        return dataRepository;
    }
}
