package barqsoft.footballscores.util;

public class Constants {
    public static final long NUMBER_OF_MILLISECONDS_IN_DAY = 86400000;
    public static final String SELECTED_INDEX_ATTRIBUTE = "selected_index";

    public static final String LEAGUE_CODE_BUNDESLIGA1 = "394";
    public static final String LEAGUE_CODE_BUNDESLIGA2 = "395";
    public static final String LEAGUE_CODE_PREMIER = "398";
    public static final String LEAGUE_CODE_PRIMERA = "399";
    public static final String LEAGUE_CODE_SERIE_A = "401";

    public static final String TIMEFRAME_NEXT_TWO = "n2";
    public static final String TIMEFRAME_PREVIOUS_TWO = "p2";
    public static final String API_FIXTURES_BASE_URL = "http://api.football-data.org/alpha/fixtures/";
    public static final String API_SEASONS_BASE_URL = "http://api.football-data.org/alpha/soccerseasons/";
    public static final String TIMEFRAME_QUERY_PARAM = "timeFrame";
    public static final String HTTP_GET_METHOD = "GET";
    public static final String AUTH_TOKEN_HEADER_NAME = "X-Auth-Token";

    public static final String JSON_FIELD_FIXTURES = "fixtures";
    public static final String JSON_FIELD_LINKS = "_links";
    public static final String JSON_FIELD_SOCCER_SEASON = "soccerseason";
    public static final String JSON_FIELD_SELF = "self";
    public static final String JSON_FIELD_MATCH_DATE = "date";
    public static final String JSON_FIELD_HOME_TEAM_NAME = "homeTeamName";
    public static final String JSON_FIELD_AWAY_TEAM_NAME = "awayTeamName";
    public static final String JSON_FIELD_RESULT = "result";
    public static final String JSON_FIELD_HOME_GOALS = "goalsHomeTeam";
    public static final String JSON_FIELD_AWAY_GOALS = "goalsAwayTeam";
    public static final String JSON_FIELD_MATCH_DAY = "matchday";

    public static final String DATE_FORMAT_LONG_1 = "yyyy-MM-ddHH:mm:ss";
    public static final String DATE_FORMAT_LONG_2 = "yyyy-MM-dd:HH:mm";
    public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DAY_OF_WEEK = "EEEE";
    public static final String TIME_ZONE = "UTC";

    public static final String HREF_ATTRIBUTE_NAME = "href";
    public static final String INVALID_SCORE = "-1";
    public static final String TEXT_MIME_TYPE = "text/plain";
    public static final String ACTION_DATA_UPDATED = "barqsoft.footballscores.action.DATA_UPDATED";


    private Constants() {
    }
}
