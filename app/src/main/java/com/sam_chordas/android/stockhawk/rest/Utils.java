package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.util.Log;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Utils {
    private static String oneYearPrice;
    private static String EPSCurrentYearPice;
    private static String EPSCurrentYearPercent;
    private static String EPSNextYearPrice;
    private static String EPSNextYearPercent;
    private static String LOG_TAG = Utils.class.getSimpleName();
    private static String symbol;
    private static String bid;

    public static boolean showPercent = true;

    public static ArrayList quoteJsonToContentVals(String JSON) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject = null;
        JSONArray resultsArray = null;
        try {
            jsonObject = new JSONObject(JSON);
            if (jsonObject != null && jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject("query");
                int count = Integer.parseInt(jsonObject.getString("count"));
                if (count == 1) {
                    jsonObject = jsonObject.getJSONObject("results")
                            .getJSONObject("quote");
                    ContentProviderOperation contentProviderOperation = buildBatchOperation(jsonObject);
                    if (contentProviderOperation == null) {
                        return null;
                    }
                    batchOperations.add(contentProviderOperation);
                } else {
                    resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

                    if (resultsArray != null && resultsArray.length() != 0) {
                        for (int i = 0; i < resultsArray.length(); i++) {
                            jsonObject = resultsArray.getJSONObject(i);
                            ContentProviderOperation contentProviderOperation = buildBatchOperation(jsonObject);
                            if (contentProviderOperation == null) {
                                return null;
                            }
                            batchOperations.add(contentProviderOperation);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return batchOperations;
    }

    public static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format("%.2f", round);
        StringBuffer changeBuffer = new StringBuffer(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        try {
            String change = jsonObject.getString("Change");
            oneYearPrice = jsonObject.getString("OneyrTargetPrice");
            EPSCurrentYearPice = jsonObject.getString("PriceEPSEstimateCurrentYear");
            EPSCurrentYearPercent = jsonObject.getString("EPSEstimateCurrentYear");
            EPSNextYearPrice = jsonObject.getString("PriceEPSEstimateNextYear");
            EPSNextYearPercent = jsonObject.getString("EPSEstimateNextYear");
            symbol = jsonObject.getString("symbol");
            bid = jsonObject.getString("Bid");
            if (change.contains("null") || oneYearPrice.contains("null") || EPSCurrentYearPercent.contains("null") || EPSCurrentYearPice.contains("null") || EPSNextYearPrice.contains("null") || EPSNextYearPercent.contains("null") || symbol.contains("null") || bid.contains("null")) {
                return null;
            } else {


                builder.withValue(QuoteColumns.SYMBOL, symbol);
                builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(bid));
                builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                        jsonObject.getString("ChangeinPercent"), true));
                builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
                builder.withValue(QuoteColumns.ISCURRENT, 1);

                builder.withValue(QuoteColumns.ONEYEARPRICE, oneYearPrice);
                builder.withValue(QuoteColumns.EPSCURRENTYEAR, EPSCurrentYearPice);
                builder.withValue(QuoteColumns.EPSCURRENTYEARPERCENT, EPSCurrentYearPercent);
                builder.withValue(QuoteColumns.EPSNEXTYEAR, EPSNextYearPrice);
                builder.withValue(QuoteColumns.EPSNEXTYEARPERCENT, EPSNextYearPercent);

                if (change.charAt(0) == '-') {
                    builder.withValue(QuoteColumns.ISUP, 0);
                } else {
                    builder.withValue(QuoteColumns.ISUP, 1);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    public static String getEPSCurrentYearPice() {
        return EPSCurrentYearPice;
    }

    public static String getEPSCurrentYearPercent() {
        return EPSCurrentYearPercent;
    }

    public static String getEPSNextYearPrice() {
        return EPSNextYearPrice;
    }

    public static String getEPSNextYearPercent() {
        return EPSNextYearPercent;
    }

    public static String getOneYearPrice() {
        return oneYearPrice;
    }
}
