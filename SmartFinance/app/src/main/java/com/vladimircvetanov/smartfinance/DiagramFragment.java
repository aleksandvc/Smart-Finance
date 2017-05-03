package com.vladimircvetanov.smartfinance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.szugyi.circlemenu.view.CircleLayout;
import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.message.Message;
import com.vladimircvetanov.smartfinance.model.Category;
import com.vladimircvetanov.smartfinance.model.CategoryExpense;
import com.vladimircvetanov.smartfinance.model.Transaction;
import com.vladimircvetanov.smartfinance.transactionRelated.TransactionFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class DiagramFragment extends Fragment {

    private HashSet<CategoryExpense> displayedCategories;
    private HashMap<Integer, Integer> colors;

    private PieChart pieChart;
    private ArrayList<PieEntry> entries;
    private PieData pieData;
    private PieDataSet pieDataSet;
    private Button totalSumButton;

    private CircleLayout circleLayout;
    private DBAdapter adapter;

    private View rootView;
    private Transaction transaction;
    private Bundle args;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            entries = savedInstanceState.getParcelableArrayList("entriesList");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.fragment_diagram, container, false);
        } catch (InflateException e) {}

        circleLayout = (CircleLayout) rootView.findViewById(R.id.frame);
        pieChart = (PieChart) rootView.findViewById(R.id.pie_chart);

        entries = new ArrayList<>();
        pieDataSet = new PieDataSet(entries, "");

        colors = new HashMap<>();

        totalSumButton = (Button) rootView.findViewById(R.id.total_sum_btn);
        adapter = DBAdapter.getInstance(getActivity());

        args = getArguments();
        transaction = null;
        if (args != null && args.containsKey("TRANSACTION")){
            transaction = (Transaction) args.getSerializable("TRANSACTION");
        }

        drawDiagram();
        drawFavouriteIcons();
        displayTotal(totalSumButton);

        /* Animator for the Report Drawer */
        final LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.report_layout);
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
                        v.animate().setInterpolator(interpolator).translationY(translationY[0] < v.getHeight()/2 ? 0 : v.getHeight()-totalSumButton.getHeight());
                        return v.onTouchEvent(event);
                }
            }
        });

        totalSumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout.getTranslationY() != 0){
                    Interpolator interpolator = new AccelerateDecelerateInterpolator();
                    layout.animate().setInterpolator(interpolator).translationY(0);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayTotal(totalSumButton);
        if (transaction != null && colors.containsKey(transaction.getCategory().getIconId())) {
            addEntry(transaction);
            Message.message(getActivity(), "Entry added to the diagram");
        }
    }

    void addEntry(Transaction transaction) {
        if (args != null || !colors.containsKey(transaction.getCategory().getIconId())) {
            if (entries.size() == 1) {
                entries.clear();
                entries.add(new PieEntry(0));
            }
            float entrySum = (float) transaction.getSum();
            PieEntry entry = new PieEntry(entrySum);
            entries.add(entry);
            pieDataSet.addColor(colors.get(transaction.getCategory().getIconId()));

            pieChart.setData(pieData);
            pieChart.invalidate();
        }
    }

    void drawDiagram() {
        if (entries.isEmpty()) {
            entries.add(new PieEntry(100));
        }
        pieDataSet.setColor(R.color.colorGrey);
        if (args != null && args.containsKey("TRANSACTION")) {
            pieChart.setCenterText(String.format("Total:\n%.2f", getTotal()));
        }

        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new CustomPercentFormatter());

        pieChart.setUsePercentValues(true);
        pieChart.setDescription(null);
        pieChart.getLegend().setEnabled(false);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    void drawFavouriteIcons() {
        if (displayedCategories == null) displayedCategories = new HashSet<>();
        for (final CategoryExpense categoryExpense : adapter.getCachedFavCategories().values()) {
            if (!displayedCategories.contains(categoryExpense)) {
                displayedCategories.add(categoryExpense);
                final ImageView icon = new ImageView(getActivity());
                int iconId = categoryExpense.getIconId();
                icon.setImageResource(iconId);

                int color = getDominantColor(iconId);
                if (!colors.containsKey(iconId)) {
                    colors.put(iconId, color);
                }

                icon.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                CircleLayout.LayoutParams params = new CircleLayout.LayoutParams(120, 120);
                icon.setLayoutParams(params);

                icon.setPadding(20, 20, 20, 20);
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
                                .replace(R.id.main_fragment_frame, fragment, getString(R.string.transaction_fragment_tag))
                                .addToBackStack(getString(R.string.transaction_fragment_tag))
                                .commit();
                        icon.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.icon_background));
                    }
                });

                icon.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        pieChart.setCenterText(categoryExpense.getName() + "\n" + categoryExpense.getSum());
                        pieChart.setHoleColor(ContextCompat.getColor(getActivity(), R.color.colorGrey));
                        icon.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.icon_background));

                        return false;
                    }
                });
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("entriesList", entries);
    }

    private double getTotal() {
        double sum = 0.0d;
        for (LinkedList<Transaction> list : adapter.getCachedTransactions().values()) {
            for (Transaction t : list) {
                sum += t.getSum() * (t.getCategory().getType() == Category.Type.EXPENSE ? -1 : 1);
            }
        }
        return sum;
    }

    private double displayTotal(Button displayView) {
        getTotal();
        displayView.setText(String.format("Total: %.2f", getTotal()));

        if (getTotal() < 0)
            displayView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorDarkOrange));
        else
            displayView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGreen));

        return getTotal();
    }

    private int getDominantColor(int image) {
        Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(), image);
        List<Palette.Swatch> swatchesTemp = Palette.from(icon).generate().getSwatches();
        List<Palette.Swatch> swatches = new ArrayList<Palette.Swatch>(swatchesTemp);
        Collections.sort(swatches, new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch swatch1, Palette.Swatch swatch2) {
                return swatch2.getPopulation() - swatch1.getPopulation();
            }
        });
        return swatches.size() > 0 ? swatches.get(0).getRgb() : R.color.colorGreen;
    }

    private class CustomPercentFormatter implements IValueFormatter {
        private DecimalFormat mFormat;

        CustomPercentFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            if(value <= 0) return "";
            return mFormat.format(value) + " %";
        }
    }
}