package de.dreier.mytargets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class PasseActivity extends Activity implements TargetView.OnTargetSetListener {

    public static final String ROUND_ID = "round_id";
    public static final String PASSE_IND = "passe_ind";
    private static final String EXTRA_VOICE_REPLY = "voice_input";
    private TargetView target;
    private Button next, prev;
    private int curPasse = 1;
    private int savedPasses = 0;
    private long mRound, mTraining;
    private TargetOpenHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_targets);

        //updateNotification();

        db = new TargetOpenHelper(this);

        Intent i = getIntent();
        if(i!=null && i.hasExtra(ROUND_ID)) {
            mRound = i.getLongExtra(ROUND_ID,-1);
            savedPasses = db.getPasses(mRound).getCount();
            if(i.hasExtra(PASSE_IND)) {
                curPasse = i.getIntExtra(PASSE_IND, -1)-1;
            } else {
                curPasse = savedPasses+1;
            }
        }

        target = (TargetView)findViewById(R.id.targetview);
        target.setOnTargetSetListener(this);
        next = (Button)findViewById(R.id.next_button);
        prev = (Button)findViewById(R.id.prev_button);

        TargetOpenHelper.Round r = db.getRound(mRound);
        mTraining = r.training;
        target.setTargetRound(r.target);
        target.setPPP(r.ppp);
/*
        Bundle remoteInput = RemoteInput.getResultsFromIntent(i);
        if (remoteInput != null) {
            String voiceInput = remoteInput.getCharSequence(EXTRA_VOICE_REPLY).toString();
            createPasseFromVoiceInput(voiceInput,r);
        }*/

        if(savedInstanceState!=null) {
            target.restoreState(savedInstanceState);
            curPasse = savedInstanceState.getInt("curPasse");
            updatePasse();
        } else {
            setPasse(curPasse);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPasse(curPasse + 1);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPasse(curPasse - 1);
            }
        });
    }

    /*private void createPasseFromVoiceInput(String voiceInput, TargetOpenHelper.Round r) {
        String[] inp = voiceInput.split("( |.)");
        int[] passe_zone = new int[r.ppp];
        int biggestPoints = TargetView.target_points[r.target][0];
        int cur = 0;
        int color = -1, zone = -1;
        for(int i=0;i<inp.length;i++) {
            try {
                int points = Integer.parseInt(inp[i]);
                while(points>biggestPoints) {
                    int firstPoints;
                    if(biggestPoints==10) {
                        firstPoints = Integer.parseInt(inp[i].substring(0, 2));
                        if (firstPoints != 10) {
                            firstPoints = Integer.parseInt(inp[i].substring(0, 1));
                            inp[i] = inp[i].substring(1);
                        } else {
                            inp[i] = inp[i].substring(2);
                        }
                    } else {
                        firstPoints = Integer.parseInt(inp[i].substring(0, 1));
                    }
                    passe_zone[cur] = firstPoints;
                    cur++;
                    points = Integer.parseInt(inp[i]);
                }
                passe_zone[cur] = points;
                cur++;
            } catch (NumberFormatException e) {
                String p = inp[i].toLowerCase();
                if(p==getString(R.string.yellow)) {
                    color = 0;
                } else if(p==getString(R.string.blue)) {
                    color = 1;
                } else if(p==getString(R.string.red)) {
                    color = 2;
                } else if(p==getString(R.string.black)) {
                    color = 3;
                } else if(p==getString(R.string.white)) {
                    color = 4;
                } else if(p==getString(R.string.inner)) {
                    zone = 0;
                } else if(p==getString(R.string.outer)) {
                    zone = 1;
                } else if(p=="x") {
                    passe_zone[cur] = 0;
                    cur++;
                }

                if(color!=-1 && zone!=-1) {
                    passe_zone[cur] = 1+color*2+zone;
                    cur++;
                    color = -1;
                    zone = -1;
                }
            }
        }
        if(cur==r.ppp) {
            OnTargetSet(passe_zone);
        }
    }*/

    public void setPasse(int passe) {
        if (passe <= savedPasses) {
            int[] points = db.getPasse(mRound, passe);
            if (points != null)
                target.setZones(points);
            else
                target.reset();
        } else if (passe != curPasse) {
            target.reset();
        }
        curPasse = passe;
        updatePasse();
    }

    public void updatePasse() {
        setTitle("Passe "+curPasse);
        prev.setEnabled(curPasse > 1);
        next.setEnabled(curPasse<=savedPasses);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.passe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            /*case R.id.action_settings:
                i = new Intent(this,SettingsActivity.class);
                startActivity(i);
                return true;*/
            case R.id.action_new_runde:
                i = new Intent(this,NewRoundActivity.class);
                i.putExtra(NewRoundActivity.TRAINING_ID,mTraining);
                i.putExtra(NewRoundActivity.FROM_PASSE,true);
                startActivity(i);
                overridePendingTransition(R.anim.left_in_complete, R.anim.right_out_half);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curPasse",curPasse);
        target.saveState(outState);
    }

    @Override
    public void OnTargetSet(int[] zones) {
        TargetOpenHelper db = new TargetOpenHelper(this);

        if(curPasse>savedPasses) {
            savedPasses++;
            db.addPasseToRound(mRound, zones);
        } else {
            db.updatePasse(mRound, curPasse, zones);
        }
        db.close();
        updatePasse();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    /*public void updateNotification() {
        int notificationId = 1;

        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel("Was hast du getroffen?")
                .build();

        Intent replyIntent = new Intent(this, PasseActivity.class);
        replyIntent.putExtra(ROUND_ID,mRound);
        PendingIntent replyPendingIntent =
                PendingIntent.getActivity(this, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);//PendingIntent.FLAG_UPDATE_CURRENT

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.drawable.wear_action_search, null, replyPendingIntent).build();


        /*NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_action_location_found,
                        "Passe", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();*

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("MyTargets")
                        .setContentText("Neue Passe")
                        .extend(new NotificationCompat.WearableExtender().addAction(action));
                                //.setContentAction(0));


      /*
        // Create the reply action and add the remote input
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.wear_action_search,
                        "Passe", replyPendingIntent)
                       // .addRemoteInput(remoteInput)
                        .build();


        // This notification will be shown only on watch
        final NotificationCompat.Builder wearableNotificationBuilder = new NotificationCompat.Builder(this)
                 .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("MyTargets")
                .setContentText("Wische nach links")
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .setGroup("GROUP")
                .setGroupSummary(false)
                .addAction(action);*

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }*/
}
