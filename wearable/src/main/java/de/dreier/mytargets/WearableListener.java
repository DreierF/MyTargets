package de.dreier.mytargets;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.IOException;

import de.dreier.mytargets.models.Bow;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.WearableConst;

public class WearableListener extends WearableListenerService {

    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        // Transform byte[] to Bundle
        byte[] data = messageEvent.getData();

        Log.e("listener", "data.l:" + data.length);
        Bundle bundle = new Bundle();
        if (data.length != 0) {
            try {
                bundle = WearableConst.deserializeToBundle(data);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (messageEvent.getPath().equals(WearableConst.STARTED_ROUND)) {
            Round r = (Round) bundle.getSerializable(WearableConst.BUNDLE_ROUND);
            boolean m = bundle.getBoolean(WearableConst.BUNDLE_MODE);
            Bow b = (Bow) bundle.getSerializable(WearableConst.BUNDLE_BOW);
            showNotification(r, m, b);
        } else if (messageEvent.getPath().equals(WearableConst.STOPPED_ROUND)) {
            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
            hideNotification();
        }
    }

    public void showNotification(Round round, boolean mode, Bow bow) {
        // Build the intent to display our custom notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(MainActivity.EXTRA_ROUND, round);
        notificationIntent.putExtra(MainActivity.EXTRA_MODE, mode);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build activity page
        Notification page = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .extend(new Notification.WearableExtender()
                        .setCustomSizePreset(Notification.WearableExtender.SIZE_FULL_SCREEN)
                        .setDisplayIntent(pendingIntent))
                .build();

        // Create the ongoing notification
        Notification.Builder notificationBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(bow != null ? bow.image : null)
                        .setOngoing(true)
                        .extend(new Notification.WearableExtender()
                                .addPage(page));

        // Build the notification and show it
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(
                NOTIFICATION_ID, notificationBuilder.build());
    }

    private void hideNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}