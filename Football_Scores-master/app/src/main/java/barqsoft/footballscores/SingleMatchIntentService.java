package barqsoft.footballscores;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.util.Constants;
import barqsoft.footballscores.util.Utilities;

public class SingleMatchIntentService extends IntentService {
    private static final String TAG = SingleMatchIntentService.class.getSimpleName();

    public SingleMatchIntentService() {
        super(SingleMatchIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context appContext = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(appContext);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(appContext, SingleMatchWidgetProvider.class));

        if (appWidgetIds == null || appWidgetIds.length == 0) {
            Log.i(TAG, "onHandleIntent called, but no widgets were updated");
            return;
        }

        for (int widgetId : appWidgetIds) {
            int layoutId = R.layout.widget_single_match;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);
            updateView(appContext, views);

            // configure what happens when the widget is clicked
            Intent appIntent = new Intent(appContext, MainActivity.class);
            appIntent.putExtra(Constants.SELECTED_INDEX_ATTRIBUTE, 0);
            PendingIntent pendingIntent = PendingIntent.getActivity(appContext, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_root_layout, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, views);
        }

        Log.i(TAG, String.format("widgets with the following ids were updated: %s", Arrays.toString(appWidgetIds)));
    }

    private void updateView(Context context, RemoteViews views) {
        // a potential problem was fixed here related to Locale. For searching
        // in database, US locale needs to be used, irrespective of the user's preferred locale.
        SimpleDateFormat dateFormatter = new SimpleDateFormat(getString(R.string.date_format_short), Locale.US);
        String dateStr = dateFormatter.format(new Date());
        Uri searchUri = DatabaseContract.ScoresTable.buildScoreWithDate();

        String sortOrder = String.format("%s, %s", DatabaseContract.ScoresTable.DATE_COL, DatabaseContract.ScoresTable.TIME_COL);
        Cursor cursor = context.getContentResolver().query(searchUri, null, null, new String[]{dateStr}, sortOrder);

        if (cursor == null) {
            Log.i(TAG, "No data found");
            return;
        }

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            Log.i(TAG, "Retrieving data to be shown on the widget...");

            ContentValues values = Utilities.readCursor(cursor);
            Utilities.populateView(values, views, context);
        }

        cursor.close();
    }
}
