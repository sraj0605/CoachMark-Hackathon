package com.hackathon.coachmarks;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private CoachMarks mBubbleCoachMark;
    private CoachMarks mHighlightCoachMark;
    Context context=getApplication();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        mHighlightCoachMark = new HighlightCoachMark.HighlightCoachMarkBuilder(
                context, fab).build();
        mBubbleCoachMark = new BubbleCoachMark.BubbleCoachMarkBuilder(
                context, fab, "Click the below button get started with the app!")
                .setTargetOffset(0.25f)
                .setShowBelowAnchor(true)
                .setPadding(10)
                .setOnShowListener(new CoachMarks.OnShowListener() {
                    @Override
                    public void onShow() {

                    }
                })
                .setOnDismissListener(new CoachMarks.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .build();
        getWindow().getDecorView().getRootView().post(new Runnable() {
            @Override
            public void run() {
                mBubbleCoachMark.show();
                mHighlightCoachMark.show();

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
