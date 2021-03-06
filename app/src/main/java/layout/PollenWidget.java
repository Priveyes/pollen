package layout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import java.util.Date;
import ua.edu.zsmu.mfi.biology.pollen.pollen.NormalPollenConcentrationDataProvider;
import ua.edu.zsmu.mfi.biology.pollen.pollen.NormalConcentration;
import ua.edu.zsmu.mfi.biology.pollen.R;
import ua.edu.zsmu.mfi.biology.pollen.pollen.PollenForecastAsyncTask;

import static android.content.Context.ALARM_SERVICE;

public class PollenWidget extends AppWidgetProvider {

    private static final String UPD_CLICKED = "automaticWidgetSyncButtonClick";

    // 1 hour (ms)
    private static final long REPEAT = 3600000;

    private NormalConcentration normalConcentrationStorage;

    @Override
    public void onEnabled(final Context context) {
        Log.i("onEnabled", ""+new Date());
        startWidget(context);
    }

    /**
     * Frequent alarms are bad for battery life. As of API 22, the AlarmManager will override near-future and high-frequency alarm requests,
     * delaying the alarm at least 5 seconds into the future and ensuring that the repeat interval is at least 60 seconds.
     *
     * @param context
     */
    // Enter relevant functionality for when the first widget is created
    private void startWidget(Context context) {
        NormalPollenConcentrationDataProvider provider = new NormalPollenConcentrationDataProvider();
        //this.normalConcentrationStorage = provider.getDataFromGoogleDrive();
        this.normalConcentrationStorage = provider.getDataFromLocalFile(context);
        Log.i("normal:", this.normalConcentrationStorage.toString());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 5000, REPEAT,
                getPendingSelfIntent(context, UPD_CLICKED));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // TODO delete
        NormalPollenConcentrationDataProvider provider = new NormalPollenConcentrationDataProvider();
        this.normalConcentrationStorage = provider.getDataFromLocalFile(context);


        if (UPD_CLICKED.equals(intent.getAction())) {
            updateForecast(context);
        }
    }

    private void updateForecast(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.pollen_widget);
        PollenForecastAsyncTask pollenForecastAsyncTask =
                new PollenForecastAsyncTask(views, context, normalConcentrationStorage);
        pollenForecastAsyncTask.execute();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.pollen_widget);
        views.setOnClickPendingIntent(R.id.updateImageButton, getPendingSelfIntent(context, UPD_CLICKED));
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

}

