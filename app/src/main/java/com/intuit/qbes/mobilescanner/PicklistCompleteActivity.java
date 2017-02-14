package com.intuit.qbes.mobilescanner;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


public class PicklistCompleteActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Full screen is set for the Window
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_picklist_complete);

        mGoBack = (TextView)findViewById(R.id.go_back);

        mGoBack.setPaintFlags(mGoBack.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mGoBack.setOnClickListener(this);



    }


    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.go_back:
            {
                Intent mainactivity = new Intent(this, MainActivity.class);
                startActivity(mainactivity);

            }

        }
    }
}
