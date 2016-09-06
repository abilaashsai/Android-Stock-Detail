package com.sam_chordas.android.stockhawk.ui;

import com.sam_chordas.android.stockhawk.detailStockGson.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;



public interface StockUrl {
    @GET("yql")
    Call<Example> getUser(
            @Query("q") String q,
            @Query("format") String format,
            @Query("diagnostics") String dia,
            @Query("env") String env,
            @Query("callback") String callback
    );
}
