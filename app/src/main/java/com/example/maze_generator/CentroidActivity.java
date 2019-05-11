package com.example.maze_generator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CentroidActivity extends AppCompatActivity {

    static int enterX,enterY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centroid);
        Intent i = getIntent();
        enterX = i.getIntExtra("enterX",0);
        enterY = i.getIntExtra("enterY", 0);
    }
}
