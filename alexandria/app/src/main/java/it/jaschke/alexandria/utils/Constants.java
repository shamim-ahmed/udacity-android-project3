package it.jaschke.alexandria.utils;

public class Constants {
    public static final String APP_NAME = "Alexandria";
    public static final String SELECTED_BOOK_EAN = "it.jaschke.alexandria.book.ean";
    public static final int ISBN13_EAN_LENGTH = 13;
    public static final int ISBN10_EAN_LENGTH = 10;
    public static final String EMPTY_STRING = "";
    public static final String COMMA_SEPARATOR = ",";
    public static final String NEWLINE = "\n";
    public static final int LOADER_ID = 10;
    public static final String SQL_PERCENTAGE_STRING = "%";
    public static final int BARCODE_SCAN_REQUEST_CODE = 101;
    public static final String SCANNED_BARCODE_KEY = "scanned_barcode";

    public static final String API_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    public static final String QUERY_PARAM_NAME = "q";
    public static final String ISBN_PARAM_NAME = "isbn";
    public static final String HTTP_GET_METHOD = "GET";
    public static final String ISBN10_PREFIX = "978";
    public static final String TEXT_PLAIN_MIME_TYPE = "text/plain";
    public static final String PREF_START_FRAGMENT = "pref_startFragment";
    public static final String PREF_DEFAULT_FRAGMENT = "0";
    public static final String BACK_STACK_NAME = "Book Detail";
    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_EXTRA_KEY = "MESSAGE_EXTRA";

    public static final String JSON_FIELD_ITEMS = "items";
    public static final String JSON_FIELD_VOLUMEINFO = "volumeInfo";
    public static final String JSON_FIELD_TITLE = "title";
    public static final String JSON_FIELD_SUBTITLE = "subtitle";
    public static final String JSON_FIELD_AUTHORS = "authors";
    public static final String JSON_FIELD_DESCRIPTION = "description";
    public static final String JSON_FIELD_CATEGORIES = "categories";
    public static final String JSON_FIELD_IMAGELINKS = "imageLinks";
    public static final String JSON_FIELD_THUMBNAIL = "thumbnail";


    // private constructor to prevent instantiation
    private Constants() {
    }
}
