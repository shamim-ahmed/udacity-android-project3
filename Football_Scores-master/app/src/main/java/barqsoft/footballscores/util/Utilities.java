package barqsoft.footballscores.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.RemoteViews;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities {
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;

    public static String getLeague(int league_num, Context context) {
        Resources resources = context.getResources();

        switch (league_num) {
            case SERIE_A:
                return resources.getString(R.string.league_name_serie_a);
            case PREMIER_LEGAUE:
                return resources.getString(R.string.league_name_premier);
            case CHAMPIONS_LEAGUE:
                return resources.getString(R.string.league_name_uefa_champions);
            case PRIMERA_DIVISION:
                return resources.getString(R.string.league_name_primera_division);
            case BUNDESLIGA:
                return resources.getString(R.string.league_name_bundesliga);
            default:
                return resources.getString(R.string.league_name_unknown);
        }
    }

    public static String getMatchDay(int match_day, int league_num, Context context) {
        Resources resources = context.getResources();

        if (league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return resources.getString(R.string.match_day_cl_group_stages);
            } else if (match_day == 7 || match_day == 8) {
                return resources.getString(R.string.match_day_cl_first_knockout);
            } else if (match_day == 9 || match_day == 10) {
                return resources.getString(R.string.match_day_cl_quarter_final);
            } else if (match_day == 11 || match_day == 12) {
                return resources.getString(R.string.match_day_cl_semi_final);
            } else {
                return resources.getString(R.string.match_day_cl_final);
            }
        } else {
            return resources.getString(R.string.match_day_generic_prefix) + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals, int awaygoals) {
        if (home_goals < 0 || awaygoals < 0) {
            return " - ";
        } else {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName(String teamname, Context context) {
        Resources resources = context.getResources();

        if (teamname == null) {
            return R.drawable.no_icon;
        }

        if (teamname.equals(resources.getString(R.string.team_name_arsenal))) {
            return R.drawable.arsenal;
        }

        if (teamname.equals(resources.getString(R.string.team_name_manchester))) {
            return R.drawable.manchester_united;
        }

        if (teamname.equals(resources.getString(R.string.team_name_swansea))) {
            return R.drawable.swansea_city_afc;
        }

        if (teamname.equals(resources.getString(R.string.team_name_leicester))) {
            return R.drawable.leicester_city_fc_hd_logo;
        }

        if (teamname.equals(resources.getString(R.string.team_name_everton))) {
            return R.drawable.everton_fc_logo1;
        }

        if (teamname.equals(resources.getString(R.string.team_name_west_ham_united))) {
            return R.drawable.west_ham;
        }

        if (teamname.equals(resources.getString(R.string.team_name_tottenham))) {
            return R.drawable.tottenham_hotspur;
        }

        if (teamname.equals(resources.getString(R.string.team_name_west_bromwich))) {
            return R.drawable.west_bromwich_albion_hd_logo;
        }

        if (teamname.equals(resources.getString(R.string.team_name_sutherland))) {
            return R.drawable.sunderland;
        }

        if (teamname.equals(resources.getString(R.string.team_name_stoke_city))) {
            return R.drawable.stoke_city;
        }

        return R.drawable.no_icon;
    }

    public static ContentValues readCursor(Cursor cursor) {
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

    public static void populateView(ContentValues values, RemoteViews views, Context context) {
        String homeGoals = values.getAsString(DatabaseContract.ScoresTable.HOME_GOALS_COL);

        if (Constants.INVALID_SCORE.equals(homeGoals)) {
            homeGoals = "";
        }

        String awayGoals = values.getAsString(DatabaseContract.ScoresTable.AWAY_GOALS_COL);

        if (Constants.INVALID_SCORE.equals(awayGoals)) {
            awayGoals = "";
        }

        String scoreStr = String.format("%s - %s", homeGoals, awayGoals);
        int homeIconResourceId = getTeamCrestByTeamName(values.getAsString(DatabaseContract.ScoresTable.HOME_COL), context);
        int awayIconResourceId = getTeamCrestByTeamName(values.getAsString(DatabaseContract.ScoresTable.AWAY_COL), context);

        views.setImageViewResource(R.id.home_crest, homeIconResourceId);
        views.setImageViewResource(R.id.away_crest, awayIconResourceId);
        views.setTextViewText(R.id.home_name, values.getAsString(DatabaseContract.ScoresTable.HOME_COL));
        views.setTextViewText(R.id.away_name, values.getAsString(DatabaseContract.ScoresTable.AWAY_COL));
        views.setTextViewText(R.id.score_textview, scoreStr);
        views.setTextViewText(R.id.data_textview, values.getAsString(DatabaseContract.ScoresTable.TIME_COL));

        views.setTextColor(R.id.home_name, Color.BLACK);
        views.setTextColor(R.id.away_name, Color.BLACK);
        views.setTextColor(R.id.score_textview, Color.BLACK);
        views.setTextColor(R.id.data_textview, Color.BLACK);
    }
}
