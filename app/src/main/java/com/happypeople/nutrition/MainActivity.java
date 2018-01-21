package com.happypeople.nutrition;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.happypeople.nutrition.model.NutritionListEntry;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.android.ContextHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * App zur Anzeige von Ernährungspositionen.
 * Sinn der Sache ist es schnell und einfach nachzuvollziehen was am Tag/Woche bereits gegessen wurde,
 * um daraus zu schliessen was noch gegessen werden darf/kann/soll.
 * <p>
 * Hauptansicht ist eine Tabelle mit Mahlzeiten pro Zeile. Felder:
 * Zeitstempel:    Date/Time
 * Was:            Text/Choice
 * KCal:           Int
 * ggf. KCal Anteile    Int/Int/Int
 * - +-Button für Neueingabe
 * - Möglichkeit Edit / Delete
 * - Filter für Tabelle: Alles / Letzte X Tage / Kalenderwoche / Kalendermonat
 * Summen:
 * Tagessumme
 * ggf letzte drei Tage (Averages?)
 * ggf letzte sieben Tage (Averages?)
 * ggf alles aktuell angezeigte
 * -Summmen einfärben nach Zielen, zB grün für "Tageskalorien kleiner als Ziel".
 * -Anpassung von "Zielen" per "Notiz", zB "Laufen 1h / 500 KCal"
 * -ggf Erweiterung der Notiz als Training-Daybook
 * <p>
 * Persistenz:
 * Lokal / sqLite database
 * Cloud:
 * -Goole Play ???
 * -happypeople.com
 * -github
 * -verschlüsselt
 */
public class MainActivity extends AppCompatActivity {

    private Calendar currentDate = Calendar.getInstance();
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Adapter for List of nutrition entries.
     */
    private ArrayAdapter<NutritionListEntry> nutritionListAdapter;
    private List<NutritionListEntry> listAdapterModel;

    /**
     * Initializes the local database by applying the Flyway migration.
     */
    private void initDB() {
        final SQLiteDatabase db = openOrCreateDatabase("nutriDB", 0, null);
        ContextHolder.setContext(this);
        Flyway flyway = new Flyway();
        final String dbPath = db.getPath();
        flyway.setDataSource("jdbc:sqlite:" + dbPath, "", "");
        flyway.migrate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String uuid = UUID.randomUUID().toString();

        // setup the database
        initDB();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // init nutritionList view
        // initialize the list to display searched/found items while
        // editing the field nutrition

        listAdapterModel = new ArrayList<>();

        nutritionListAdapter = new ArrayAdapter<NutritionListEntry>(this, 0, listAdapterModel) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return createNutritionListView(position, convertView, parent);
            }
        };

        final ListView listView = (ListView) findViewById(R.id.nutritionEntryList);
        listView.setAdapter(nutritionListAdapter);
        // initial display
        currentDate = Calendar.getInstance();
        onDataChange();
    }

    private View createNutritionListView(int pos, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            convertView = getLayoutInflater().inflate(R.layout.nutrition_entry_list_row, parent, false);
        }

        // Get the data item for this position
        final NutritionListEntry entry = nutritionListAdapter.getItem(pos);
        final DateFormat format = new SimpleDateFormat("HH:mm");

        ((TextView) convertView.findViewById(R.id.nleTs))
                .setText(format.format(entry.getTs()));
        ((TextView) convertView.findViewById(R.id.nleName))
                .setText(entry.getFood().getName());
        ((TextView) convertView.findViewById(R.id.nleAmount))
                .setText("" + entry.getGrams() + "g");
        ((TextView) convertView.findViewById(R.id.nleKcal))
                .setText("" + entry.getKcal() + "kCal");
        ((TextView) convertView.findViewById(R.id.nleFat))
                .setText(String.format("%.1f", entry.getFatGrams()) + "g");
        ((TextView) convertView.findViewById(R.id.nleCarbo))
                .setText(String.format("%.1f", entry.getCarboGrams()) + "g");
        ((TextView) convertView.findViewById(R.id.nleProtein))
                .setText(String.format("%.1f", entry.getProteinGrams()) + "g");
        return convertView;
    }

    protected void onResume() {
        super.onResume();
        onDataChange();
    }

    private NutritionAppContext getApp() {
        return (NutritionAppContext) getApplication();
    }

    /**
     * Call this method after any data change.
     * It reloads/redisplays the dynamic data fields.
     */
    private void onDataChange() {
        updateCurrentDateField();
        updateNutritionEntryList();
    }

    private void updateCurrentDateField() {
        final TextView dateField = (TextView) findViewById(R.id.text_date);
        dateField.setText(dateFormat.format(currentDate.getTime()));
    }

    private void updateNutritionEntryList() {
        listAdapterModel.clear();
        listAdapterModel.addAll(getApp().getDataRepository().getNutritionListEntries(currentDate.getTime()));
        nutritionListAdapter.notifyDataSetChanged();
        updateSumFields(listAdapterModel);
    }

    private void updateSumFields(final List<NutritionListEntry> nutritionListEntries) {
        int count = 0;
        int sumCkal = 0;
        double sumFat = 0.0;
        double sumCarbo = 0.0;
        double sumSugar = 0.0;
        double sumProtein = 0.0;
        for (NutritionListEntry entry : nutritionListEntries) {
            count++;
            sumCkal += entry.getKcal();
            sumFat += entry.getFatGrams();
            sumCarbo += entry.getCarboGrams();
            sumProtein += entry.getProteinGrams();
            sumSugar += entry.getSugarGrams();
        }
        final TextView sumKcalField = (TextView) findViewById(R.id.sumkcal);
        final TextView sumFatField = (TextView) findViewById(R.id.sumfat);
        final TextView sumCarboField = (TextView) findViewById(R.id.sumcarbo);
        final TextView sumProteinField = (TextView) findViewById(R.id.sumprotein);
        if (count > 0) {
            sumKcalField.setText("" + sumCkal + "kCal");
            sumFatField.setText(String.format("%.1f", sumFat) + "g");
            sumCarboField.setText(String.format("%.1f", sumCarbo) + "g");
            sumProteinField.setText(String.format("%.1f", sumProtein) + "g");
        } else {
            sumKcalField.setText("");
            sumFatField.setText("");
            sumCarboField.setText("");
            sumProteinField.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Snackbar.make(view, "Settings was clicked!", Snackbar.LENGTH_LONG)
            //Toast.makeText(this, "Settings was clicked", Toast.LENGTH_SHORT).show();
            final ListView nutritionList = (ListView) findViewById(R.id.nutritionList);
            Snackbar.make(nutritionList, "Settings was clicked", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addFoodClicked(View view) {
        // should switch to editNutritionEntry-Activity
    }

    public void addNutritionEntryClicked(View view) {
        final Intent intent = new Intent(this, EditNutritionEntryActivity.class);
        intent.putExtra("date", new Date());
        startActivity(intent);
    }

    public void showSaldoClicked(View view) {
        // should switch to showSaldo-Activity
    }

    public void addActivityClicked(View view) {
        // should switch to addActivity-Activity
    }

    public void incDateClicked(View view) {
        currentDate.add(Calendar.DAY_OF_MONTH, 1);
        //currentDate.roll(Calendar.DAY_OF_MONTH, 1);
        onDataChange();
    }

    public void decDateClicked(View view) {
        currentDate.add(Calendar.DAY_OF_MONTH, -1);
        onDataChange();
    }
}
