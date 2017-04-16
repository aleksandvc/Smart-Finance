package com.vladimircvetanov.smartfinance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vladimircvetanov.smartfinance.model.Section;
import com.vladimircvetanov.smartfinance.model.User;

import java.util.ArrayList;

import static com.vladimircvetanov.smartfinance.model.User.favouriteCategories;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private PieChart pieChart;
    private ArrayList<PieEntry> entries;
    private PieData pieData;
    private PieDataSet pieDataSet;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private TextView userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        pieChart = (PieChart) findViewById(R.id.pie_chart);
        entries = new ArrayList<>();
        pieDataSet = new PieDataSet(entries, "");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        FragmentManager fm = getSupportFragmentManager();
        if(fm.getFragments() != null || !fm.getFragments().isEmpty()) {
            fm.beginTransaction()
                    .add(R.id.diagram_fragment, new Fragment(), "Diagram")
                    .add(R.id.categories_list_fragment, new Fragment(), "Transactions list")
                    .commit();
        }
        // Temp code
        final Button addButton = (Button) findViewById(R.id.add_value_btn);
        final EditText addValue = (EditText) findViewById(R.id.add_value);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addValue.getText().toString().isEmpty() && Integer.parseInt(addValue.getText().toString()) > 0) {
                    addEntry(Integer.parseInt(addValue.getText().toString()));
                }
            }
        });
        //End of temp code

        //Temp user
        User user = new User("pesho@gmail.com", "pesho123");

        drawDiagram();
        drawFavouriteIcons();
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
                LogoutDialogFragment dialog = LogoutDialogFragment.newInstance();
                dialog.show(getSupportFragmentManager(), "Log out");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_accounts:
                return true;
            case R.id.nav_favourites:
                return true;
            case R.id.nav_income:
                return true;
            case R.id.nav_calculator:
                return true;
            case R.id.nav_calendar:
                return true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addEntry(float entrySum) {
        PieEntry entry = new PieEntry(entrySum);
        entries.add(entry);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void drawDiagram() {
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);;

        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());

        pieChart.setUsePercentValues(true);
        pieChart.setDescription(null);
        pieChart.getLegend().setEnabled(false);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void drawFavouriteIcons() {
        int counter = 0;
        for (final Section section : favouriteCategories) {
            final ImageButton icon = new ImageButton(MainActivity.this);
            icon.setImageResource(section.getIconID());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(150, 150);
            lp.gravity = Gravity.CENTER;
            icon.setLayoutParams(lp);
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            icon.setBackgroundColor(getColor(R.color.colorTransparent));

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
                    intent.putExtra(getString(R.string.EXTRA_SECTION), section);
                    startActivity(intent);

                    pieChart.setCenterText(section.getName() + "\n" + section.getSum());
                    pieChart.setHoleColor(getColor(R.color.colorGrey));

                    icon.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.icon_background));
                }
            });
        }
    }
}

