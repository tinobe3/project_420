package com.example.project_420;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

/**
 * Activity class for creating the week plan
 * @author Tino Behnen
 * @version 1.2
 */
public class PlanWeek extends AppCompatActivity
{
    private static final String TAG = "PlanWeek";
    private Week week;
    private Day day = Day.MON;
    private int movementCount = 1;
    private EditText nameEdit, setsEdit, repsEdit, weightEdit, durationEdit;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_week);

        Log.d(TAG, "onCreate() — called");

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.app_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        week = Week.getInstance();
        week.clear();
        week.save(this);
        week.load(this);

        nameEdit = findViewById(R.id.nameInput);
        setsEdit = findViewById(R.id.setsInput);
        repsEdit = findViewById(R.id.repsInput);
        weightEdit = findViewById(R.id.weightInput);
        durationEdit = findViewById(R.id.durationInput);

        refreshTitle();
    }

    /**
     *
     * @param view
     */
    public void endWeek(View view)
    {
        newMovement();
        backToMain();
    }

    /**
     *
     * @param view
     */
    public void endDay(View view)
    {
        newMovement();
        movementCount = 1;

        if (day == Day.SUN)
        {
            backToMain();
        }
        else
        {
            day = Day.values()[day.ordinal() + 1];
            refreshTitle();
        }
    }

    /**
     *
     * @param view
     */
    public void nextMove(View view)
    {
        if (isEmpty(nameEdit) || isEmpty(setsEdit) || isEmpty(repsEdit))
        {
            Toast.makeText(this, "Fill in required* fields!", Toast.LENGTH_LONG).show();
        }
        newMovement();
    }

    /**
     *
     */
    private void newMovement()
    {
        if (isEmpty(nameEdit) || isEmpty(setsEdit) || isEmpty(repsEdit))
        {
            return;
        }

        String name = nameEdit.getText().toString();
        int sets = Integer.parseInt(setsEdit.getText().toString());
        int reps = Integer.parseInt(repsEdit.getText().toString());

        int weight;
        if (isEmpty(weightEdit))
            weight = 0;
        else
            weight = Integer.parseInt(weightEdit.getText().toString());
        int duration;
        if (isEmpty(durationEdit))
            duration = 0;
        else
            duration = Integer.parseInt(durationEdit.getText().toString());

        week.addMovement(new Movement(day, name, sets, reps, weight, duration));

        movementCount++;
        refreshTitle();
        resetInputs();
    }

    /**
     *
     */
    @SuppressLint("SetTextI18n")
    private void refreshTitle()
    {
        ((TextView)findViewById(R.id.dayTitle))
                .setText(movementCount + "/" + dayLong());
    }

    /**
     *
     */
    private void resetInputs()
    {
        nameEdit.getText().clear();
        setsEdit.getText().clear();
        repsEdit.getText().clear();
        weightEdit.getText().clear();
        durationEdit.getText().clear();
    }

    /**
     *
     * @param editText
     * @return
     */
    private boolean isEmpty(EditText editText)
    {
        return editText.getText().toString().trim().length() <= 0;
    }

    /**
     *
     */
    private void backToMain()
    {
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
    }

    /**
     *
     * @return
     */
    private String dayLong()
    {
        String dayName = "Monday";
        switch (this.day)
        {
            case TUE:
                dayName = "Tuesday";
                break;
            case WED:
                dayName = "Wednesday";
                break;
            case THU:
                dayName = "Thursday";
                break;
            case FRI:
                dayName = "Friday";
                break;
            case SAT:
                dayName = "Saturday";
                break;
            case SUN:
                dayName = "Sunday";
                break;
        }
        return dayName;
    }

    /**
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() — called");

        week.save(this);
    }
}