package barqsoft.footballscores;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.util.Constants;
import barqsoft.footballscores.util.Utilities;

public class FootballScoreWidget extends AppWidgetProvider {
    private static final String TAG = FootballScoreWidget.class.getSimpleName();
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String INVALID_SCORE = "-1";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "onUpdate method called ...");

        for (int widgetId : appWidgetIds) {
            int layoutId = R.layout.widget_list_item;
            RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
            updateView(context, views);

            Intent appIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_root_layout, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        if (Constants.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            Log.i(TAG, "onReceive method called ...");
        }
    }

    private void updateView(Context context, RemoteViews views) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
        String dateStr = dateFormatter.format(new Date());
        Uri searchUri = DatabaseContract.ScoresTable.buildScoreWithDate();

        String sortOrder = String.format("%s, %s", DatabaseContract.ScoresTable.DATE_COL, DatabaseContract.ScoresTable.TIME_COL);
        Cursor cursor = context.getContentResolver().query(searchUri, null, null, new String[]{dateStr}, sortOrder);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues values = readCursor(cursor);

            String homeGoals = values.getAsString(DatabaseContract.ScoresTable.HOME_GOALS_COL);

            if (INVALID_SCORE.equals(homeGoals)) {
                homeGoals = "";
            }

            String awayGoals = values.getAsString(DatabaseContract.ScoresTable.AWAY_GOALS_COL);

            if (INVALID_SCORE.equals(awayGoals)) {
                awayGoals = "";
            }

            String scoreStr = String.format("%s - %s", homeGoals, awayGoals);
            int homeIconResourceId = Utilities.getTeamCrestByTeamName(values.getAsString(DatabaseContract.ScoresTable.HOME_COL));
            int awayIconResourceId = Utilities.getTeamCrestByTeamName(values.getAsString(DatabaseContract.ScoresTable.AWAY_COL));

            views.setImageViewResource(R.id.home_crest, homeIconResourceId);
            views.setImageViewResource(R.id.away_crest, awayIconResourceId);
            views.setTextViewText(R.id.home_name, values.getAsString(DatabaseContract.ScoresTable.HOME_COL));
            views.setTextViewText(R.id.away_name, values.getAsString(DatabaseContract.ScoresTable.AWAY_COL));
            views.setTextViewText(R.id.score_textview, scoreStr);
            views.setTextViewText(R.id.data_textview, values.getAsString(DatabaseContract.ScoresTable.TIME_COL));
        }

        cursor.close();
    }

    private ContentValues readCursor(Cursor cursor) {
        ContentValues values = new ContentValues();
        int homeColumnIndex = cursor.getColumnIndex(DatabaseContract.ScoresTable.HOME_COL);
        int awayColumnIndex = cursor.getColumnIndex(DatabaseContract.ScoresTable.AWAY_COL);
        int homeGoalsColumnIndex = cursor.getColumnIndex(DatabaseContract.ScoresTable.HOME_GOALS_COL);
        int awayGoalsColumnIndex = cursor.getColumnIndex(DatabaseContract.ScoresTable.AWAY_GOALS_COL);
        int timeColumnIndex = cursor.getColumnIndex(DatabaseContract.ScoresTable.TIME_COL);

        values.put(DatabaseContract.ScoresTable.HOME_COL, cursor.getString(homeColumnIndex));
        values.put(DatabaseContract.ScoresTable.AWAY_COL, cursor.getString(awayColumnIndex));
        values.put(DatabaseContract.ScoresTable.HOME_GOALS_COL, cursor.getString(homeGoalsColumnIndex));
        values.put(DatabaseContract.ScoresTable.AWAY_GOALS_COL, cursor.getString(awayGoalsColumnIndex));
        values.put(DatabaseContract.ScoresTable.TIME_COL, cursor.getString(timeColumnIndex));

        return values;
    }
}
