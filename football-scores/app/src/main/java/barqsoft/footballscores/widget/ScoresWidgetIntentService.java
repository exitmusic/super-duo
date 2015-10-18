package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import barqsoft.footballscores.DatabaseContract;

/**
 * Created by kchang on 10/17/15.
 */
public class ScoresWidgetIntentService extends IntentService {

    private static final String[] GAME_COLUMNS = {
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.TIME_COL
    };
    private static final int INDEX_HOME_COL = 0;
    private static final int INDEX_HOME_GOALS_COL = 1;
    private static final int INDEX_AWAY_COL = 2;
    private static final int INDEX_AWAY_GOALS_COL = 3;
    private static final int INDEX_TIME_COL = 4;

    public ScoresWidgetIntentService() {
        super("ScoresWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Get all scores widget ids
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ScoresWidgetProvider.class));

        // Get today's first game from content provider
        Uri todaysFirstGameUri = DatabaseContract.scores_table.buildScoreWithDate();
        Cursor data = getContentResolver().query(todaysFirstGameUri, GAME_COLUMNS, null, null,
                DatabaseContract.scores_table.MATCH_DAY + " ASC");

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        // Extract game data from cursor

    }
}
