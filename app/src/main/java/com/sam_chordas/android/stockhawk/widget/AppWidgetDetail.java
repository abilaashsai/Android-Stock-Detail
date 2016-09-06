package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AppWidgetDetail extends RemoteViewsService {
    public final String LOG_TAG = AppWidgetDetail.class.getSimpleName();

    private static final String[] WIDGET_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE, QuoteColumns.BIDPRICE,
            QuoteColumns.CREATED,
            QuoteColumns.ISUP,
            QuoteColumns.ISCURRENT
    };
    static final int INDEX_STOCK_ID = 0;
    static final int INDEX_STOCK_SYMBOL = 1;
    static final int INDEX_STOCK_CHANGE = 3;
    static final int INDEX_STOCK_BIDPRICE = 4;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();


                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        WIDGET_COLUMNS, null,
                        null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.app_widget);

                String symbol = data.getString(INDEX_STOCK_SYMBOL);
                String bid_price = data.getString(INDEX_STOCK_BIDPRICE);
                String change = data.getString(INDEX_STOCK_CHANGE);
                views.setTextViewText(R.id.stock_symbol_widget, symbol);
                views.setTextViewText(R.id.bid_price_widget, bid_price);
                views.setTextViewText(R.id.change_widget, change);

                final Intent fillInIntent = new Intent();
                Uri stockUri = QuoteProvider.Quotes.CONTENT_URI;
                fillInIntent.setData(stockUri);
                views.setOnClickFillInIntent(R.id.widget, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.app_widget);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_STOCK_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
