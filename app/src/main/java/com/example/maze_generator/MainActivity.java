package com.example.maze_generator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonGener;
    Button button_add;
    Button button_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameView view=(GameView)findViewById(R.id.game_view);


        buttonGener = (Button) findViewById(R.id.button);
        buttonGener.setOnClickListener(this);

        button_add = findViewById(R.id.button_add);
        button_start = findViewById(R.id.button_start);


    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.button:
                Intent intent = new Intent(this,CentroidActivity.class);
                startActivity(intent);
                break;
            case R.id.button_start:
                Intent i = new Intent(this,CentroidActivity.class);
                i.putExtra("drawway",);

                startActivity(i);
                CentroidDecomposition.findexit(MazeGener.Vertex[GameView.enterY][GameView.enterX]);
                break;

            default:
                break;
        }

    }


}
