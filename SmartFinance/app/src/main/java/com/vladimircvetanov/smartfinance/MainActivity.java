package com.vladimircvetanov.smartfinance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vladimircvetanov.smartfinance.model.Category;
import com.vladimircvetanov.smartfinance.model.User;

import java.util.ArrayList;

import static com.vladimircvetanov.smartfinance.model.User.favouriteCategories;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private PieChart pieChart;
    private ArrayList<PieEntry> percentages;
    private PieData entry;
    private PieDataSet pieDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        pieChart = (PieChart) findViewById(R.id.pie_chart);
        percentages = new ArrayList<>();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Temporarily created user so I can generate his favourite categories
        User user = new User("some mail", "some pass");

        drawDiagram();
        drawFavouriteIcons();

        //I've added buttons to currently extant activities for ease of navigation during development.
        //Add and remove buttons as needed.
        //                                      ~Simo

        Button toLogIn = (Button) findViewById(R.id.temp_to_login);
        Button toRegister = (Button) findViewById(R.id.temp_to_register);
        Button toTransaction = (Button) findViewById(R.id.temp_to_transaction);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            switch (v.getId()) {
                case R.id.temp_to_login:
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    break;
                case R.id.temp_to_register:
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                    break;
                case R.id.temp_to_transaction:
                    startActivity(new Intent(MainActivity.this, TransactionActivity.class));
                    break;
            }
            }
        };
        toLogIn.setOnClickListener(onClickListener);
        toRegister.setOnClickListener(onClickListener);
        toTransaction.setOnClickListener(onClickListener);
        //end of temp code
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_currency:
                return true;
            case R.id.item_settings:
                return true;
            case R.id.item_logout:
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void drawDiagram() {
        percentages.add(new PieEntry(25f));
        percentages.add(new PieEntry(15f));
        percentages.add(new PieEntry(5f));
        percentages.add(new PieEntry(19f));
        percentages.add(new PieEntry(2f));
        percentages.add(new PieEntry(13f));
        percentages.add(new PieEntry(21f));

        pieDataSet = new PieDataSet(percentages, "");
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        entry = new PieData(pieDataSet);
        entry.setValueFormatter(new PercentFormatter());

        pieChart.setUsePercentValues(true);
        pieChart.setData(entry);
        pieChart.setDescription(null);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
    }

    private void drawFavouriteIcons() {
        int counter = 0;
        for (final Category category : favouriteCategories) {
            final ImageView icon = new ImageView(MainActivity.this);
            icon.setImageResource(category.getIcon());
            icon.setPadding(30, 30, 30, 30);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(140, 140);
            lp.gravity = Gravity.CENTER;
            icon.setLayoutParams(lp);

            float angleDeg = counter++ * 360.0f / favouriteCategories.size() - 90.0f;
            float angleRad = (float) (angleDeg * Math.PI / 180.0f);
            // Calculate the position of the view, offset from center (300 px from center).
            icon.setTranslationX(450 * (float) Math.cos(angleRad));
            icon.setTranslationY(450 * (float) Math.sin(angleRad));
            frameLayout.addView(icon);

            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                    intent.putExtra("category", category.getName());
                    startActivity(intent);

                    pieChart.setCenterText(category.getName() + "\n" + category.getTotalAmount());
                    pieChart.setHoleColor(getResources().getColor(R.color.colorOrange));
                    //icon.setBorderColor(getResources().getColor(R.color.colorWhite));
                    icon.setBackground(getResources().getDrawable(R.drawable.icon_background));
                }
            });
        }
    }
}

