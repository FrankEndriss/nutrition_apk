package com.happypeople.nutrition.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.android.ContextHolder;

/** Low-Level database interface for Nutrition app.
 * Package protected, could be an inner class to SQLiteDataRepository
 */
class NutritionSQLiteOpenHelper extends SQLiteOpenHelper {
    private final Context context;

    public NutritionSQLiteOpenHelper(final Context context) {
        super(context, "nutriDB", null, 1);
        this.context=context;
    }

    private void flywayMigrate(SQLiteDatabase database) {
        ContextHolder.setContext(context);
        Flyway flyway = new Flyway();
        final String dbPath = database.getPath();
        flyway.setDataSource("jdbc:sqlite:" + dbPath, "", "");
        flyway.migrate();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //flywayMigrate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        flywayMigrate(db);
    }
}
