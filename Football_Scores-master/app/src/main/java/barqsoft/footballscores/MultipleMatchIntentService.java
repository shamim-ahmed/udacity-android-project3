package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.util.Constants;
import barqsoft.footballscores.util.Utilities;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MultipleMatchIntentService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor cursor = null;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (cursor != null) {
                    cursor.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                // initialize the new cursor
                SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
                String dateStr = dateFormatter.format(new Date());
                Uri searchUri = DatabaseContract.ScoresTable.buildScoreWithDate();

                String sortOrder = String.format("%s, %s", DatabaseContract.ScoresTable.DATE_COL, DatabaseContract.ScoresTable.TIME_COL);
                cursor = getContentResolver().query(searchUri, null, null, new String[]{dateStr}, sortOrder);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item);

                ContentValues values = Utilities.readCursor(cursor);
                Utilities.populateView(values, views, getApplicationContext());

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra("row_number", position);
                views.setOnClickFillInIntent(R.id.list_item_root, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (cursor != null && cursor.moveToPosition(position)) {
                    int index = cursor.getColumnIndex(DatabaseContract.ScoresTable.MATCH_ID);
                    return cursor.getLong(index);
                }

                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
