package com.sam_chordas.android.stockhawk.ui;

import android.os.AsyncTask;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.sam_chordas.android.stockhawk.detailStockGson.Example;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FetchDetailStock extends AsyncTask {
    DetailViewFragment detailViewFragment;
    Call<Example> trailerFetchCall;
    List<Float> average = new ArrayList<>();

    public FetchDetailStock(DetailViewFragment detailViewFragment) {
        this.detailViewFragment = detailViewFragment;
    }

    @Override
    protected Void doInBackground(Object[] params) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            String endDate = df.format(c.getTime());
            c.add(Calendar.DAY_OF_YEAR, -1);
            int day = 0;
            int inc = 1;
            while (day <= 10) {
                c.add(Calendar.DAY_OF_YEAR, -1);
                if (c.get(Calendar.DAY_OF_YEAR) != Calendar.SATURDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    day++;
                }
            }
            String startDate = df.format(c.getTime());
            String start = "https://query.yahooapis.com/v1/public/";
            StringBuilder sss = new StringBuilder();
            sss.append("select * from yahoo.finance.historicaldata where symbol = \"" + params[0] + "\" and startDate = \"" + startDate + "\" and endDate = \"" + endDate + "\" ");
            sss.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");
            Retrofit trailer_retrofit = new Retrofit.Builder()
                    .baseUrl(start)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            StockUrl trailer_service = trailer_retrofit.create(StockUrl.class);
            String qq = "select * from yahoo.finance.historicaldata where symbol = \"" + params[0] + "\" and startDate = \"" + startDate + "\" and endDate = \"" + endDate + "\" ";
            String format = "json";
            String dia = "true";
            String env = "store://datatables.org/alltableswithkeys";
            String callb = "";
            trailerFetchCall = trailer_service.getUser(qq, format, dia, env, callb);
            Response<Example> response = trailerFetchCall.execute();
            for (int i = 0; i < response.body().getQuery().getResults().getQuote().size(); i++) {
                float avg = (Float.parseFloat(response.body().getQuery().getResults().getQuote().get(i).getClose().toString()));
                average.add(avg);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (average.size() != 0) {
            PointsGraphSeries<DataPoint> series = new PointsGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint(1, average.get(0)),
                    new DataPoint(2, average.get(1)),
                    new DataPoint(3, average.get(2)),
                    new DataPoint(4, average.get(3)),
                    new DataPoint(5, average.get(4)),
                    new DataPoint(6, average.get(5)),
                    new DataPoint(7, average.get(6))
            });


            detailViewFragment.graphView.addSeries(series);
            series.setShape(PointsGraphSeries.Shape.POINT);
            LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[]{
                    new DataPoint(1, average.get(0)),
                    new DataPoint(2, average.get(1)),
                    new DataPoint(3, average.get(2)),
                    new DataPoint(4, average.get(3)),
                    new DataPoint(5, average.get(4)),
                    new DataPoint(6, average.get(5)),
                    new DataPoint(7, average.get(6))
            });
            detailViewFragment.graphView.setContentDescription("Graph showing past seven days " + average.get(0).toString() + " " + average.get(1).toString() + " " + average.get(2).toString() + " " + average.get(3).toString() + " " + average.get(4).toString() + " " + average.get(5).toString() + " " + average.get(6).toString());
            detailViewFragment.graphView.addSeries(series1);
        }
    }

    private int getWorkingDaysBetweenTwoDates(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        int workDays = 0;

        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            return 0;
        }

        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }

        do {
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                ++workDays;
            }
        } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

        return workDays;
    }
}
