package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by kchang on 10/13/15.
 */
public class ScoresWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = ScoresWidgetProvider.class.getSimpleName();

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

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // Get today's first game from content provider
        String[] dateSelection = new String[1];
        Date todaysDate = new Date(System.currentTimeMillis()+(86400000));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateSelection[0] = dateFormat.format(todaysDate);
        Uri todaysFirstGameUri = DatabaseContract.scores_table.buildScoreWithDate();
        Cursor data = context.getContentResolver().query(todaysFirstGameUri, GAME_COLUMNS, null, dateSelection, null);

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        // Extract game data from cursor
        String homeTeam = data.getString(INDEX_HOME_COL);
        String homeGoals = data.getString(INDEX_HOME_GOALS_COL);
        String awayTeam = data.getString(INDEX_AWAY_COL);
        String awayGoals = data.getString(INDEX_AWAY_GOALS_COL);
        String gameTime = data.getString(INDEX_TIME_COL);
        data.close();

        int weatherArtResourceId = R.drawable.abc_ic_clear_mtrl_alpha;
        String description = "Clear";
        double maxTemp = 24;
        String formattedMaxTemperature = "24";

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_scores_small;
            RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);

            // Add the data to the RemoteViews
            //views.setImageViewResource(R.id.home_crest, weatherArtResourceId);
            //views.setImageViewResource(R.id.away_crest, weatherArtResourceId);
            // Content Descriptions for RemoteViews were only added in ICS MR1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(views, description);
            }
            views.setTextViewText(R.id.home_name, homeTeam);
            views.setTextViewText(R.id.away_name, awayTeam);
            views.setTextViewText(R.id.score_textview, "VS");
            views.setTextViewText(R.id.data_textview, gameTime);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.home_crest, description);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {

        context.startService(new Intent(context, ScoresWidgetIntentService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // Check for SyncAdapter update action?
    }
}
