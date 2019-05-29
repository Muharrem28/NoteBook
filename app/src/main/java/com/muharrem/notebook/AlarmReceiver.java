package com.muharrem.notebook;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManager notifManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        int noteId = intent.getIntExtra("noteId", -1);
        if (noteId != -1) {
            Log.d("+++++", "Receive noteId : " + noteId);
            AppUtils.initialize(context);
            AppUtils.loadAllNotes();
            Note note = AppUtils.findNoteById(noteId);
            AppUtils.notificationNote = note;

            createNotification(note, context);
//
//            NotificationChannel mChannel = new NotificationChannel(12345, name, importance);
//            builder = new NotificationCompat.Builder(context, id);
//
//            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                    .setSmallIcon(R.mipmap.notebook)
//                    .setContentTitle("Note Defterim")
//                    .setContentText(note.getTitle())
//                    .setOngoing(false)
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .setAutoCancel(true)
//                    .setChannelId("notebook_1");
//
//            Intent i = new Intent(context, NoteActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_ONE_SHOT);
//
//            builder.setContentIntent(pendingIntent);
//            manager.notify(12345, builder.build());
        }
    }

    private void createNotification(Note note, Context context) {
        final int NOTIFY_ID = 0;
        String id = "notebook_id"; // default_channel_id
        String title = "channel_title"; // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 200});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, NoteActivity.class);
            intent.putExtra("noteId", note.getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle("NOT DEFTERİM")                            // required
                    .setSmallIcon(R.mipmap.alarm)   // required           // required
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.notebook))   // required
                    .setContentText(note.getTitle()) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(note.getTitle())
                    .setVibrate(new long[]{100, 200, 300, 200});
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, NoteActivity.class);
            intent.putExtra("noteId", note.getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle("NOT DEFTERİM")                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(note.getTitle()) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(note.getTitle())
                    .setVibrate(new long[]{100, 200, 300, 200})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }
}