package barqsoft.footballscores;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

public class SingleMatchWidgetProvider extends AppWidgetProvider {
    private static final String TAG = SingleMatchWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i(TAG, "onUpdate method called ...");

        context.startService(new Intent(context, SingleMatchIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Log.i(TAG, "onAppWidgetOptionsChanged method called ...");

        context.startService(new Intent(context, SingleMatchIntentService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        String actionDataUpdated = context.getString(R.string.action_data_updated);

        if (actionDataUpdated.equals(intent.getAction())) {
            Log.i(TAG, "onReceive method called ...");
            context.startService(new Intent(context, SingleMatchIntentService.class));
        }
    }
}
