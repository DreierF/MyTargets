package de.dreier.mytargets;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.parceler.Parcels;

import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.NotificationInfo$$Parcelable;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import de.dreier.mytargets.shared.utils.WearableUtils;

public class WearableListener extends WearableListenerService {

    public static final String TRAINING_STARTED = "training_started";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        // Transform byte[] to Bundle
        byte[] data = messageEvent.getData();

        Log.d("listener", messageEvent.getPath());
        if (messageEvent.getPath().equals(WearableUtils.STARTED_ROUND)) {
            if (data.length != 0) {
                NotificationInfo info = getNotificationInfo(data);
                showNotification(info);
                if (info != null) {
                    Intent intent = new Intent(TRAINING_STARTED);
                    intent.putExtra(MainActivity.EXTRA_ROUND, Parcels.wrap(info.round));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            }
        } else if (messageEvent.getPath().equals(WearableUtils.UPDATE_ROUND)) {
            NotificationInfo info = getNotificationInfo(data);
            showNotification(info);
        } else if (messageEvent.getPath().equals(WearableUtils.STOPPED_ROUND)) {
            cancelNotification();
        }
    }

    @Nullable
    private NotificationInfo getNotificationInfo(byte[] data) {
        return Parcels
                .unwrap(ParcelableUtil.unmarshall(data, NotificationInfo$$Parcelable.CREATOR));
    }

    private void showNotification(NotificationInfo info) {

        // Build the intent to display our custom notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(MainActivity.EXTRA_ROUND, Parcels.wrap(info.round));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Build activity page
        Notification page = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .extend(new Notification.WearableExtender()
                        .setCustomSizePreset(Notification.WearableExtender.SIZE_FULL_SCREEN)
                        .setDisplayIntent(pendingIntent))
                .build();

        // Create the ongoing notification
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.wear_bg);
        Notification.Builder notificationBuilder =
                new Notification.Builder(this)
                        .setContentTitle(info.title)
                        .setContentText(info.text)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setOngoing(true)
                        .extend(new Notification.WearableExtender()
                                .addPage(page).setBackground(image));

        // Build the notification and show it
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void cancelNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}