package com.vladimircvetanov.smartfinance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vladimircvetanov.smartfinance.model.Manager;
import com.vladimircvetanov.smartfinance.model.Section;
import com.vladimircvetanov.smartfinance.model.User;

import java.util.ArrayList;

import static com.vladimircvetanov.smartfinance.model.User.favouriteCategories;

public class MainWithReportActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private PieChart pieChart;
    private ArrayList<PieEntry> percentages;
    private PieData entry;
    private PieDataSet pieDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_report);

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

        final LinearLayout layout = (LinearLayout) findViewById(R.id.report_layout);
        final Button bt = (Button) findViewById(R.id.report_sum);

        frameLayout.setPadding(50,50,50,bt.getHeight()+10);

        final float[] startY = new float[1];
        final float[] translationY = new float[1];
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startY[0] = event.getY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float newY = event.getY();
                        float deltaY = startY[0] - newY;
                        translationY[0] = v.getTranslationY();
                        translationY[0] -= deltaY;
                        if (translationY[0] < 0)
                            translationY[0] = 0;
                        if (translationY[0] >= v.getHeight()-150)
                            translationY[0] = v.getHeight()-150;
                        v.setTranslationY(translationY[0]);
                        return true;
                    default:
                        Interpolator interpolator = new AccelerateDecelerateInterpolator();
                        v.animate().setInterpolator(interpolator).translationY(translationY[0] < 350 ? 0 : v.getHeight()-bt.getHeight());
                        return v.onTouchEvent(event);
                }
            }
        });

        bt.setText("" + Manager.getSum());
        bt.setBackgroundColor(ContextCompat.getColor(this,Manager.getSum() >= 0 ? R.color.colorGreen : R.color.colorOrange));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout.getTranslationY() != 0){
                    Interpolator interpolator = new OvershootInterpolator(3);
                    layout.animate().setInterpolator(interpolator).translationY(0);
                }
            }
        });
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
                startActivity(new Intent(MainWithReportActivity.this,LoginActivity.class));
                MainWithReportActivity.this.finish();
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
        for (final Section section : favouriteCategories) {
            final ImageView icon = new ImageView(MainWithReportActivity.this);
            icon.setImageResource(section.getIconID());
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
                    Intent intent = new Intent(MainWithReportActivity.this, TransactionActivity.class);
                    intent.putExtra(getString(R.string.EXTRA_SECTION), section);
                    startActivity(intent);

                    pieChart.setCenterText(section.getName() + "\n" + section.getSum());
                    pieChart.setHoleColor(getResources().getColor(R.color.colorOrange));
                    icon.setBackground(getResources().getDrawable(R.drawable.icon_background));
                }
            });
        }
    }
}

