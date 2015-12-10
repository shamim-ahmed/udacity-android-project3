package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

public class FootballScoreWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        String homeTeamName = "Arsenal";
        String awayTeamName = "Liverpool";
        int homeIconResourceId = R.drawable.arsenal;
        int awayIconResourceId = R.drawable.liverpool;
        String score = "0 - 0";


        for (int widgetId : appWidgetIds) {
            int layoutId = R.layout.widget_list_item;
            RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
            views.setImageViewResource(R.id.home_crest, homeIconResourceId);
            views.setImageViewResource(R.id.away_crest, awayIconResourceId);
            views.setTextViewText(R.id.home_name, homeTeamName);
            views.setTextViewText(R.id.away_name, awayTeamName);
            views.setTextViewText(R.id.score_textview, score);

            Intent appIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_root_layout, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }
}