package com.sam_chordas.android.stockhawk.ui;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DetailViewFragment extends Fragment {


    @InjectView(R.id.graph)
    GraphView graphView;
    @InjectView(R.id.oneYrTarget)
    TextView oneYearTarget;
    @InjectView(R.id.oneYrTargetPrice)
    TextView oneYearTargetPrice;
    @InjectView(R.id.EPSEstimateCurrentYear)
    TextView EPSEstimateCurrentYear;
    @InjectView(R.id.EPSEstimateCurrentYearPrice)
    TextView EPSEstimateCurrentYearPrice;
    @InjectView(R.id.EPSEstimateCurrentYearPercent)
    TextView EPSEstimateCurrentYearPercent;
    @InjectView(R.id.EPSEstimateNextYear)
    TextView EPSEstimateNextYear;
    @InjectView(R.id.EPSEstimateNextYearPrice)
    TextView EPSEstimateNextYearPrice;
    @InjectView(R.id.EPSEstimateNextYearPercent)
    TextView EPSEstimateNextYearPercent;

    public DetailViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_view, container, false);
        ButterKnife.inject(this, rootView);


        String name = getActivity().getIntent().getStringExtra(getResources().getString(R.string.name));


        Cursor c = getActivity().getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.ONEYEARPRICE, QuoteColumns.EPSCURRENTYEAR, QuoteColumns.EPSCURRENTYEARPERCENT, QuoteColumns.EPSNEXTYEAR, QuoteColumns.EPSNEXTYEARPERCENT}, QuoteColumns.SYMBOL + " = ?",
                new String[]{name},
                null);
        c.moveToNext();

        graphView.setTitle(getResources().getString(R.string.past_seven_days));
        graphView.getGridLabelRenderer().setHorizontalAxisTitle(getResources().getString(R.string.day));
        graphView.getGridLabelRenderer().setVerticalAxisTitle(getResources().getString(R.string.price));

        oneYearTarget.setText(getResources().getString(R.string.one_year_target));
        oneYearTarget.setContentDescription(getResources().getString(R.string.one_year_target) + c.getString(c.getColumnIndex(QuoteColumns.ONEYEARPRICE)));
        oneYearTargetPrice.setText(c.getString(c.getColumnIndex(QuoteColumns.ONEYEARPRICE)));

        EPSEstimateCurrentYear.setText(getResources().getString(R.string.eps_estimate_current_year));
        EPSEstimateCurrentYear.setContentDescription(getResources().getString(R.string.eps_estimate_current_year) + c.getString(c.getColumnIndex(QuoteColumns.EPSCURRENTYEAR)) + getResources().getString(R.string.and_percent) + c.getString(c.getColumnIndex(QuoteColumns.EPSCURRENTYEARPERCENT)));
        EPSEstimateCurrentYearPrice.setText(c.getString(c.getColumnIndex(QuoteColumns.EPSCURRENTYEAR)));
        EPSEstimateCurrentYearPercent.setText(c.getString(c.getColumnIndex(QuoteColumns.EPSCURRENTYEARPERCENT)));


        EPSEstimateNextYear.setText(getResources().getString(R.string.eps_estimate_next_year));
        EPSEstimateNextYear.setContentDescription(getResources().getString(R.string.eps_estimate_next_year) + c.getString(c.getColumnIndex(QuoteColumns.EPSNEXTYEAR)) + getResources().getString(R.string.and_percent) + c.getString(c.getColumnIndex(QuoteColumns.EPSNEXTYEARPERCENT)));
        EPSEstimateNextYearPrice.setText(c.getString(c.getColumnIndex(QuoteColumns.EPSNEXTYEAR)));
        EPSEstimateNextYearPercent.setText(c.getString(c.getColumnIndex(QuoteColumns.EPSNEXTYEARPERCENT)));
        FetchDetailStock fetchDetailStock = new FetchDetailStock(this);
        fetchDetailStock.execute(name);
        return rootView;
    }
}
