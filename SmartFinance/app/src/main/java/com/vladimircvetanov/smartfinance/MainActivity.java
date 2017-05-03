package com.vladimircvetanov.smartfinance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vladimircvetanov.smartfinance.accounts.AccountsFragment;
import com.vladimircvetanov.smartfinance.date.DatePickerFragment;
import com.vladimircvetanov.smartfinance.favourites.FavouritesFragment;
import com.vladimircvetanov.smartfinance.model.Category;
import com.vladimircvetanov.smartfinance.model.Manager;
import com.vladimircvetanov.smartfinance.transactionRelated.NoteInputFragment;
import com.vladimircvetanov.smartfinance.transactionRelated.TransactionFragment;

import org.joda.time.DateTime;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener, NoteInputFragment.NoteCommunicator {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private TextView userProfile;
    private View headerView;
    private View toolbarTitle;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments() == null || fragmentManager.getFragments().isEmpty()) {
            fragmentManager.beginTransaction()
                    .add(R.id.main_fragment_frame, new DiagramFragment(), getString(R.string.diagram_fragment_tag))
                    .commit();
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        headerView = navigationView.getHeaderView(0);
        userProfile = (TextView) headerView.findViewById(R.id.user_profile_link);
        userProfile.setText("Hi, " + Manager.getLoggedUser().getEmail());

        LinearLayout header = (LinearLayout) headerView.findViewById(R.id.header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        toolbarTitle = findViewById(R.id.diagram_fragment_link);
        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_frame, new DiagramFragment(), getString(R.string.diagram_fragment_tag))
                        .commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout:
                LogoutDialogFragment dialog = new LogoutDialogFragment();
                dialog.show(getSupportFragmentManager(), getString(R.string.logout_button));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_accounts:
                if (fragmentManager.getFragments() != null || !fragmentManager.getFragments().isEmpty()) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_frame, new AccountsFragment(), getString(R.string.accounts_fragment_tag))
                            .addToBackStack(getString(R.string.accounts_fragment_tag))
                            .commit();
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_favourites:
                if (fragmentManager.getFragments() != null || !fragmentManager.getFragments().isEmpty()) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_frame, new FavouritesFragment(), getString(R.string.favourites_fragment_tag))
                            .addToBackStack(getString(R.string.transaction_fragment_tag))
                            .commit();
                }
                drawer.closeDrawer(GravityCompat.START);
                return false;

            case R.id.nav_income:
                if (fragmentManager.getFragments() != null || !fragmentManager.getFragments().isEmpty()) {
                    TransactionFragment fragment = TransactionFragment.getNewInstance(Category.Type.INCOME);
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_frame, fragment, getString(R.string.transaction_fragment_tag))
                            .addToBackStack(getString(R.string.transaction_fragment_tag))
                            .commit();
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_expense:
                if (fragmentManager.getFragments() != null || !fragmentManager.getFragments().isEmpty()) {
                    TransactionFragment fragment = TransactionFragment.getNewInstance(Category.Type.EXPENSE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_frame, fragment, getString(R.string.transaction_fragment_tag))
                            .addToBackStack(getString(R.string.transaction_fragment_tag))
                            .commit();
                }
                drawer.closeDrawer(GravityCompat.START);
                return false;

            case R.id.nav_calendar:
                DialogFragment datePicker = DatePickerFragment.newInstance(this, DateTime.now());
                datePicker.show(getSupportFragmentManager(), getString(R.string.calendar_fragment_tag));
                drawer.closeDrawer(GravityCompat.START);
                return false;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {}

    @Override
    public void setNote(String note) {
        TransactionFragment t = (TransactionFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.transaction_fragment_tag));
        t.setNote(note);
    }
}