package Khack.Q.Kkakkumi.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import Khack.Q.Kkakkumi.R;
import Khack.Q.Kkakkumi.recordActivity;

public class RecordService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startForegroundService();
    }

    void startForegroundService() {
        Intent notificationIntent = new Intent(this, recordActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_service);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "Record_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Record Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent);
        //.setContent(remoteViews)

        startForeground(1, builder.build());
    }
}
