package com.vladimircvetanov.smartfinance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.szugyi.circlemenu.view.CircleLayout;
import com.vladimircvetanov.smartfinance.model.CategoryExpense;
import com.vladimircvetanov.smartfinance.model.User;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.vladimircvetanov.smartfinance.model.Manager.getLoggedUser;
import static com.vladimircvetanov.smartfinance.model.User.favouriteCategories;

public class DiagramFragment extends Fragment {

    private FrameLayout frameLayout;
    private PieChart pieChart;
    private ArrayList<PieEntry> entries;
    private PieData pieData;
    private PieDataSet pieDataSet;
    private Button totalSumButton;

    //Temp
    private EditText addValue;
    private Button addButton;
    private CircleLayout circleLayout;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("someVarA", someVarA);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //someVarA = savedInstanceState.getInt("someVarA");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diagram, container, false);

        circleLayout = (CircleLayout) rootView.findViewById(R.id.frame);
        pieChart = (PieChart) rootView.findViewById(R.id.pie_chart);
        entries = new ArrayList<>();
        pieDataSet = new PieDataSet(entries, "");
        totalSumButton = (Button) rootView.findViewById(R.id.total_sum_btn);

        //Temp button
        addButton = (Button) rootView.findViewById(R.id.add_value_btn);
        addValue = (EditText) rootView.findViewById(R.id.add_value);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addValue.getText().toString().isEmpty() && Integer.parseInt(addValue.getText().toString()) > 0) {
                    addEntry(Integer.parseInt(addValue.getText().toString()));
                }
            }
        });

        drawDiagram();
        drawFavouriteIcons();

        //I've added buttons to currently extant activities for ease of navigation during development.
        //Add and remove buttons as needed.
        //                                      ~Simo

        Button toLogIn = (Button) rootView.findViewById(R.id.temp_to_login);
        Button toRegister = (Button) rootView.findViewById(R.id.temp_to_register);
        Button toTransaction = (Button) rootView.findViewById(R.id.temp_to_transaction);
        Button toProfile = (Button) rootView.findViewById(R.id.temp_to_profile);
        Button toInquiry = (Button) rootView.findViewById(R.id.to_main_with_report);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.temp_to_login:
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        break;
                    case R.id.temp_to_register:
                        startActivity(new Intent(getActivity(), RegisterActivity.class));
                        break;
                    case R.id.temp_to_transaction:
                        //startActivity(new Intent(getActivity(), TransactionActivity.class));
                        break;
                    case R.id.temp_to_profile:
                        User u = (User) getActivity().getIntent().getSerializableExtra("user");
                        Intent i = new Intent(getActivity(), ProfileActivity.class);
                        i.putExtra("user",u);
                        startActivity(i);
                        break;
                    case R.id.to_main_with_report:
                       // startActivity(new Intent(getActivity(), MainWithReportActivity.class));
                        break;
                }
            }
        };
        toLogIn.setOnClickListener(onClickListener);
        toRegister.setOnClickListener(onClickListener);
        toTransaction.setOnClickListener(onClickListener);
        toProfile.setOnClickListener(onClickListener);
        toInquiry.setOnClickListener(onClickListener);

        return rootView;
    }

    private void addEntry(float entrySum) {
        if (entries.size() == 1) {
            entries.clear();
            entries.add(new PieEntry(0));
        }
        PieEntry entry = new PieEntry(entrySum);
        entries.add(entry);

        pieChart.setData(pieData);
        pieChart.invalidate();

        getLoggedUser().totalSum += entrySum;
        totalSumButton.setText(String.valueOf(getLoggedUser().totalSum));
    }

    void drawDiagram() {

        if (entries.isEmpty()) {
            entries.add(new PieEntry(100));
        }
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new CustomPercentFormatter());

        pieChart.setUsePercentValues(true);
        pieChart.setDescription(null);
        pieChart.getLegend().setEnabled(false);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    void drawFavouriteIcons() {
        for (final CategoryExpense categoryExpense : favouriteCategories) {
            final ImageView icon = new ImageView(getActivity());
            icon.setImageResource(categoryExpense.getIconId());

            icon.setScaleType(ImageButton.ScaleType.FIT_CENTER);
            CircleLayout.LayoutParams params = new CircleLayout.LayoutParams(120, 120);
            icon.setLayoutParams(params);

            icon.setPadding(30, 30, 30, 30);
            icon.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorTransparent));

            circleLayout.addView(icon);
            circleLayout.setRotating(false);

            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransactionFragment fragment = new TransactionFragment();
                    Bundle arguments = new Bundle();
                    arguments.putSerializable(getString(R.string.EXTRA_SECTION), categoryExpense);
                    fragment.setArguments(arguments);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.master_layout, fragment, getString(R.string.transaction_fragment_tag))
                            .addToBackStack(getString(R.string.transaction_fragment_tag))
                            .commit();

                    pieChart.setCenterText(categoryExpense.getName() + "\n" + categoryExpense.getSum());
                    pieChart.setHoleColor(ContextCompat.getColor(getActivity(), R.color.colorGrey));
                    icon.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.icon_background));
                }
            });
        }
    }

    class CustomPercentFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public CustomPercentFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            if(value <= 0) return "";
            return mFormat.format(value) + " %";
        }
    }
}


