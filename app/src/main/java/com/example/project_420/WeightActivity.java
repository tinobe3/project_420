package com.example.project_420;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author matiasnäppä
 * versio 1.2
 */
public class WeightActivity extends AppCompatActivity {

    private float weight, previousWeight;
    private int weightPoints, i;
    private static final String weightPref = "weightPref", weightPointsPref = "weightPointsPref";
    private ArrayList<Float> weightList;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.app_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        weightList = new ArrayList<>();

        SharedPreferences gymlogPrefs = getSharedPreferences("gymlogPrefs", Context.MODE_PRIVATE);
        previousWeight = gymlogPrefs.getFloat(weightPref, 0);
        weightPoints = gymlogPrefs.getInt(weightPointsPref, 0);

        SharedPreferences weightPrefs = getSharedPreferences("weightPrefs", Context.MODE_PRIVATE);
        int weightIndex = weightPrefs.getInt("size", 0);
        for (i = 0; i < weightIndex; i++) {
            weightList.add(weightPrefs.getFloat(Integer.toString(i), 0));
        }

        TextView tvPreviousWeight = findViewById(R.id.tvPreviousWeight);
        tvPreviousWeight.setText("Your previous weight was: " + (previousWeight) + " kg.");
    }

    public void saveWeight(View view) {
        EditText weightInput = findViewById(R.id.weightInput);
        SwitchCompat toggleLossGain = findViewById(R.id.toggleLossGain);

        if (!weightInput.getText().toString().equals("")) {
            weight = Float.parseFloat(weightInput.getText().toString());

            if (previousWeight == 0) {
                weightPoints = 0;
                previousWeight = weight;
            } else if ((weight < previousWeight) && toggleLossGain.isChecked()) {
                weightPoints += (int) (previousWeight - weight);
                previousWeight = weight;
            } else if ((weight < previousWeight) && !toggleLossGain.isChecked()) {
                weightPoints -= (int) (previousWeight - weight);
                previousWeight = weight;
            } else if ((weight > previousWeight) && toggleLossGain.isChecked()) {
                weightPoints -= (int) (weight - previousWeight);
                previousWeight = weight;
            } else if ((weight > previousWeight) && !toggleLossGain.isChecked()) {
                weightPoints += (int) (weight - previousWeight);
                previousWeight = weight;
            }

            weightList.add(weight);
            SharedPreferences weightPrefs = getSharedPreferences("weightPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = weightPrefs.edit();
            for (i = 0; i < weightList.size(); i++) {
                editor.putFloat(Integer.toString(i), weightList.get(i));
            }
            editor.putInt("size", weightList.size());
            editor.apply();

            SharedPreferences gymlogPrefs = getSharedPreferences("gymlogPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor gymlogPrefsEditor = gymlogPrefs.edit();
            gymlogPrefsEditor.putFloat(weightPref, previousWeight);
            gymlogPrefsEditor.putInt(weightPointsPref, weightPoints);
            gymlogPrefsEditor.apply();

            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
        }
    }

    /**
     * Ask user if they are sure they want to delete all data.
     * if yes: Deletes all weight data from sharedpreferences.
     * Refreshes the textviews.
     * if no: nothing happens.
     */
    public void deleteWeight(View view) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete all data?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences weightPrefs = getSharedPreferences("weightPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor weightPrefsEditor = weightPrefs.edit();
                        weightPrefsEditor.clear();
                        weightPrefsEditor.apply();

                        SharedPreferences gymlogPrefs = getSharedPreferences("gymlogPrefs",MODE_PRIVATE);
                        SharedPreferences.Editor gymlogPrefsEditor = gymlogPrefs.edit();
                        gymlogPrefsEditor.putFloat(weightPref,0);
                        gymlogPrefsEditor.putInt(weightPointsPref,0);
                        gymlogPrefsEditor.apply();

                        TextView tvPreviousWeight = findViewById(R.id.tvPreviousWeight);
                        tvPreviousWeight.setText("Your previous weight was: " + (0.0) + " kg.");

                        previousWeight = 0;
                        weightPoints = 0;
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    public float getPreviousWeight() {
        return previousWeight;
    }

    public void setPreviousWeight(float previousWeight) {
        this.previousWeight = previousWeight;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getWeightPoints() {
        return weightPoints;
    }

    public void setWeightPoints(int weightPoints) {
        this.weightPoints = weightPoints;
    }
}