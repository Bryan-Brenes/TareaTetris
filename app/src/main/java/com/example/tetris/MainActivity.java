package com.example.tetris;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.tetris.R.drawable.cuadronaranja;

public class MainActivity extends Activity {

    GridLayout gridLayout;
    LinearLayout linearLayout;

    // para obtener las coordenadas del imageView en el grid se usa:
    // col = indice mod 10
    // row = indice // 10
    ArrayList<ImageView> listaImageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.e("test", String.format("Alto: %s, Ancho: %s",String.valueOf(height), String.valueOf(width)));


        gridLayout = findViewById(R.id.tableroGrid);
        linearLayout = findViewById(R.id.infoLayout);
        listaImageViews = new ArrayList<>();

        gridLayout.setColumnCount(10);
        gridLayout.setRowCount(20);

        poblarTablero();

    }

    public void poblarTablero(){
        int tag = 0;
        for (int row = 0; row <= 15; row++){
            for (int col = 0; col < 10; col++){
                ImageView img = new ImageView(this);
                /*img.setTag(tag);
                tag++;*/
                listaImageViews.add(img);
                if (col == 0 || col ==9 || row == 15){

                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.rowSpec = GridLayout.spec(row);
                    params.columnSpec = GridLayout.spec(col);
                    img.setImageResource(R.drawable.cuadrogris32);
                    gridLayout.addView(img, params);
                } else{

                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.rowSpec = GridLayout.spec(row);
                    params.columnSpec = GridLayout.spec(col);
                    img.setImageResource(R.drawable.cuadrogris32);
                    img.setAlpha(0.35f);
                    gridLayout.addView(img, params);
                }
            }
        }
    }
    
}
