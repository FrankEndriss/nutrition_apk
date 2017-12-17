package com.happypeople.nutrition;

import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.happypeople.nutrition.model.Food;
import com.happypeople.nutrition.model.FoodAmount;
import com.happypeople.nutrition.model.NutritionListEntry;
import com.happypeople.nutrition.persistence.DataRepository;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditNutritionEntryActivity extends AppCompatActivity {

    /** Adapter for List of search results while
     * entering a nutrition.
     */
    private ArrayAdapter<Food> nutritionSearchResultAdapter;

    /** Food entered/selected while current editing */
    private Food selectedFood;

    /** TextView für Menge-Eingabe */
    private TextView amountTextView;

    /** TextView für Food-Eingabe */
    private TextView nutritionTextView;

    /** saveButton, activ-state is switched */
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_nutrition_entry);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_time);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.hours_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);

        // initialize the list to display searched/found items while
        // editing the field nutrition
        final List<Food> listAdapterModel=new ArrayList<Food>();

        nutritionSearchResultAdapter = new ArrayAdapter<>(
                        this,
                        R.layout.nutrition_search_result_list_view_item,
                        R.id.nutrition_search_result_list_view_i,
                        listAdapterModel);

        final ListView listView=(ListView)findViewById(R.id.nutritionSearchResultListView);
        listView.setAdapter(nutritionSearchResultAdapter);

        final TextView foodEnterTextView=(TextView)findViewById(R.id.edit_food);
        // click listener for above list. On click, the clicked food is
        // set as the selected food
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedFood=nutritionSearchResultAdapter.getItem(position);
                foodEnterTextView.setText(selectedFood.getName());
                amountTextView.setText(""+selectedFood.getDefaultAmount());
                checkAndSetSaveButtonState();
            }
        });

        amountTextView=(TextView)findViewById(R.id.edit_menge);
        amountTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAndSetSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // ignore
            }
        });
        saveButton=(Button)findViewById(R.id.button_save);
        saveButton.setOnClickListener(new Button.OnClickListener()  {
            @Override
            public void onClick(View v) {
                onSaveClicked(v);
            }
        });
        final Button cancelButton=(Button)findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new Button.OnClickListener()  {
            @Override
            public void onClick(View v) {
                onCancelClicked(v);
            }
        });

        nutritionTextView=(TextView)findViewById(R.id.edit_food);
        nutritionTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                final String query=nutritionTextView.getText().toString();
                if(query==null || query.length()==0)
                    return;

                // TODO do this async
                listAdapterModel.clear();
                nutritionSearchResultAdapter.notifyDataSetChanged();
                getApp().getDataRepository().searchFoods(query, new DataRepository.ResultCallback<Food>() {
                    @Override
                    public void add(Food resultObject) {
                        listAdapterModel.add(resultObject);
                        nutritionSearchResultAdapter.notifyDataSetChanged();
                    }
                });

                checkAndSetSaveButtonState();
            }
        });

        checkAndSetSaveButtonState();
    }

    /**
     * @return the entered amount or null if nothing usefull available
     */
    private BigDecimal getAmount() {
        final String str=amountTextView.getText()
                .toString()
                .replace(',', '.');
        try {
            return new BigDecimal(str);
        }catch(NumberFormatException e) {
            // TODO show kind of error message...make field text red
            return null;
        }
    }

    private void checkAndSetSaveButtonState() {
        final boolean enabled=selectedFood!=null && getAmount()!=null;
        saveButton.setEnabled(enabled);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // set time to current hour
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_time);
        final int index=Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        spinner.setSelection(index);

        // all other fields empty
    }

    public void onSaveClicked(final View view) {
        final Food food=selectedFood;
        final BigDecimal amount=getAmount();
        if(food==null || amount==null)
            throw new IllegalStateException("some data is null: food="+food+" amount="+amount);

        final NutritionListEntry entry=new NutritionListEntry(
                new Date(), food, new FoodAmount(amount.intValue(), FoodAmount.FoodUnit.GRAM));

        // persist the data
        getApp().getDataRepository().createNutritionListEntry(entry);

        // and switch back to caller
        finish();
    }

    public void onCancelClicked(final View view) {
        // dont save data, simply switch back to caller
        finish();
    }

    private NutritionAppContext getApp() {
        return (NutritionAppContext)getApplicationContext();
    }
}
