package com.vladimircvetanov.smartfinance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

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
    static User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_diagram, container, false);

        frameLayout = (FrameLayout) rootView.findViewById(R.id.frame);
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
        //Temp user
        user = new User("pesho@gmail.com", "pesho123");

        drawDiagram();
        drawFavouriteIcons();

        return rootView;
    }

    private void addEntry(float entrySum) {
        PieEntry entry = new PieEntry(entrySum);
        entries.add(entry);
        pieChart.setData(pieData);
        pieChart.invalidate();

        user.totalSum += entrySum;
        totalSumButton.setText(String.valueOf(user.totalSum));
    }

    void drawDiagram() {
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);;

        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());

        pieChart.setUsePercentValues(true);
        pieChart.setDescription(null);
        pieChart.getLegend().setEnabled(false);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    void drawFavouriteIcons() {
        int counter = 0;
        for (final Section section : favouriteCategories) {
            final ImageButton icon = new ImageButton(getActivity());
            icon.setImageResource(section.getIconID());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(150, 150);
            lp.gravity = Gravity.CENTER;
            icon.setLayoutParams(lp);
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            icon.setBackgroundColor(getActivity().getColor(R.color.colorTransparent));

            float angleDeg = counter++ * 360.0f / favouriteCategories.size() - 90.0f;
            float angleRad = (float) (angleDeg * Math.PI / 180.0f);
            // Calculate the position of the view, offset from center (300 px from center).
            icon.setTranslationX(450 * (float) Math.cos(angleRad));
            icon.setTranslationY(450 * (float) Math.sin(angleRad));
            frameLayout.addView(icon);

            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransactionFragment fragment = new TransactionFragment();
                    Bundle arguments = new Bundle();
                    arguments.putString(getString(R.string.EXTRA_SECTION), String.valueOf(section));
                    fragment.setArguments(arguments);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.master_layout, new TransactionFragment(), "Transaction Fragment")
                            .commit();

                    //Intent intent = new Intent(getActivity(), TransactionActivity.class);
                    //intent.putExtra(getString(R.string.EXTRA_SECTION), section);
                    //startActivity(intent);

                    pieChart.setCenterText(section.getName() + "\n" + section.getSum());
                    pieChart.setHoleColor(getActivity().getColor(R.color.colorGrey));

                    icon.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.icon_background));
                }
            });
        }
    }
}
