package it.jaschke.alexandria.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.jaschke.alexandria.MainActivity;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.utils.Constants;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class BookService extends IntentService {

    private final String LOG_TAG = BookService.class.getSimpleName();

    public static final String FETCH_BOOK = "it.jaschke.alexandria.services.action.FETCH_BOOK";
    public static final String DELETE_BOOK = "it.jaschke.alexandria.services.action.DELETE_BOOK";

    public static final String EAN = "it.jaschke.alexandria.services.extra.EAN";

    public BookService() {
        super(Constants.APP_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (FETCH_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(EAN);
                fetchBook(ean);
            } else if (DELETE_BOOK.equals(action)) {
                final String ean = intent.getStringExtra(EAN);
                deleteBook(ean);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void deleteBook(String ean) {
        if(ean!=null) {
            getContentResolver().delete(AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ean)), null, null);
        }
    }

    /**
     * Handle action fetchBook in the provided background thread with the provided
     * parameters.
     */
    private void fetchBook(String ean) {

        if(ean.length() != Constants.ISBN13_EAN_LENGTH){
            return;
        }

        Cursor bookEntry = getContentResolver().query(
                AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ean)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if(bookEntry.getCount()>0){
            bookEntry.close();
            return;
        }

        bookEntry.close();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJsonString = null;

        try {
            final String FORECAST_BASE_URL = getString(R.string.api_base_url);
            final String QUERY_PARAM = getString(R.string.query_param_name);

            final String ISBN_PARAM = String.format("%s:%s", getString(R.string.isbn_param_name), ean);

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, ISBN_PARAM)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.i(LOG_TAG, "the URI is " + builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(getString(R.string.http_get_method));
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                return;
            }
            bookJsonString = buffer.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

        }

        final String ITEMS = getString(R.string.json_field_items);

        final String VOLUME_INFO = getString(R.string.json_field_volumeInfo);

        final String TITLE = getString(R.string.json_field_title);
        final String SUBTITLE = getString(R.string.json_field_subtitle);
        final String AUTHORS = getString(R.string.json_field_authors);
        final String DESC = getString(R.string.json_field_description);
        final String CATEGORIES = getString(R.string.json_field_categories);
        final String IMG_URL_PATH = getString(R.string.json_field_imageLinks);
        final String THUMBNAIL_URL = getString(R.string.json_field_thumbnail);

        try {
            JSONObject bookJson = new JSONObject(bookJsonString);
            JSONArray bookArray;
            if(bookJson.has(ITEMS)){
                bookArray = bookJson.getJSONArray(ITEMS);
            }else{
                final String messageEvent = getString(R.string.message_event);
                final String messageKey = getString(R.string.message_extra_key);

                Intent messageIntent = new Intent(messageEvent);
                messageIntent.putExtra(messageKey, getString(R.string.not_found));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
                return;
            }

            JSONObject bookInfo = ((JSONObject) bookArray.get(0)).getJSONObject(VOLUME_INFO);

            String title = bookInfo.getString(TITLE);

            String subtitle = Constants.EMPTY_STRING;
            if(bookInfo.has(SUBTITLE)) {
                subtitle = bookInfo.getString(SUBTITLE);
            }

            String desc = Constants.EMPTY_STRING;
            if(bookInfo.has(DESC)){
                desc = bookInfo.getString(DESC);
            }

            String imgUrl = Constants.EMPTY_STRING;
            if(bookInfo.has(IMG_URL_PATH) && bookInfo.getJSONObject(IMG_URL_PATH).has(THUMBNAIL_URL)) {
                imgUrl = bookInfo.getJSONObject(IMG_URL_PATH).getString(THUMBNAIL_URL);
            }

            writeBackBook(ean, title, subtitle, desc, imgUrl);

            if(bookInfo.has(AUTHORS)) {
                writeBackAuthors(ean, bookInfo.getJSONArray(AUTHORS));
            }
            if(bookInfo.has(CATEGORIES)){
                writeBackCategories(ean,bookInfo.getJSONArray(CATEGORIES) );
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
    }

    private void writeBackBook(String ean, String title, String subtitle, String desc, String imgUrl) {
        ContentValues values= new ContentValues();
        values.put(AlexandriaContract.BookEntry._ID, ean);
        values.put(AlexandriaContract.BookEntry.TITLE, title);
        values.put(AlexandriaContract.BookEntry.IMAGE_URL, imgUrl);
        values.put(AlexandriaContract.BookEntry.SUBTITLE, subtitle);
        values.put(AlexandriaContract.BookEntry.DESC, desc);
        getContentResolver().insert(AlexandriaContract.BookEntry.CONTENT_URI,values);
    }

    private void writeBackAuthors(String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values= new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(AlexandriaContract.AuthorEntry._ID, ean);
            values.put(AlexandriaContract.AuthorEntry.AUTHOR, jsonArray.getString(i));
            getContentResolver().insert(AlexandriaContract.AuthorEntry.CONTENT_URI, values);
            values= new ContentValues();
        }
    }

    private void writeBackCategories(String ean, JSONArray jsonArray) throws JSONException {
        ContentValues values= new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(AlexandriaContract.CategoryEntry._ID, ean);
            values.put(AlexandriaContract.CategoryEntry.CATEGORY, jsonArray.getString(i));
            getContentResolver().insert(AlexandriaContract.CategoryEntry.CONTENT_URI, values);
            values= new ContentValues();
        }
    }
 }