package com.example.tetris;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    GridLayout gridLayout;
    LinearLayout linearLayout;
    Chronometer chronometer;

    public static final int NUM_COLUMNAS = 10;
    public static final int NUM_FILAS = 16;

    // para obtener las coordenadas del imageView en el grid se usa:
    // col = indice mod 10
    // row = indice // 10
    ArrayList<Tile> listaImageViews;
    int[][] matrizControl = new int[NUM_FILAS][NUM_COLUMNAS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        /*DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.e("test", String.format("Alto: %s, Ancho: %s",String.valueOf(height), String.valueOf(width)));*/


        gridLayout = findViewById(R.id.tableroGrid);
        linearLayout = findViewById(R.id.infoLayout);
        chronometer = findViewById(R.id.chronometer);
        listaImageViews = new ArrayList<>();

        gridLayout.setColumnCount(10);
        gridLayout.setRowCount(20);

        poblarTablero();
        imprimirMatrizControl();

        chronometer.start();
        handler.post(gameLoop);

    }

    /*
    * Función para dibujar en pantalla las condiciones iniciales del
    * tablero de juego. Se llena un ArrayList con los imageView para un mejor manejo
    * */
    public void poblarTablero(){
        int tag = 0;
        for (int row = 0; row < NUM_FILAS; row++){
            for (int col = 0; col < NUM_COLUMNAS; col++){
                Tile img = new Tile(this);
                /*img.setTag(tag);
                tag++;*/


                // If para llenar las paredes laterales e inferior
                if (col == 0 || col ==9 || row == 15){

                    // params indica como se va a ubicar un elemento en el gridView
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();

                    // Se indica la fila y columna donde se va a ubicar
                    params.rowSpec = GridLayout.spec(row);
                    params.columnSpec = GridLayout.spec(col);

                    // se agrega la imagen al imageview
                    img.setImageResource(R.drawable.cuadrogris32);
                    gridLayout.addView(img, params);

                    // Se actualiza la matriz de control
                    matrizControl[row][col] = 1;
                    img.setEsObstaculo(true);

                // Se llena sección de la pantalla donde se van a mover las piezas
                } else{

                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.rowSpec = GridLayout.spec(row);
                    params.columnSpec = GridLayout.spec(col);
                    img.setImageResource(R.drawable.cuadrogris32);
                    img.setAlpha(0.35f);
                    gridLayout.addView(img, params);
                    matrizControl[row][col] = 0;
                    img.setEsObstaculo(false);
                }
                listaImageViews.add(img);
            }
        }
    }

    public void imprimirMatrizControl(){
        System.out.println();
        for (int row = 0; row < NUM_FILAS; row++) {
            for (int col = 0; col < NUM_COLUMNAS; col++) {
                System.out.print(String.valueOf(matrizControl[row][col]) + " " );
            }
            System.out.println();
        }
    }

    Handler handler = new Handler();
    Runnable gameLoop = new Runnable() {
        int i = 0;
        @Override
        public void run() {
            // Revisar si hay que poner una pieza
                // no: mover pieza
                // si: colocar pieza, nuevaPieza = false
            // Verificar si pieza tiene una pieza obstaculo por debajo
                // si: marcar pieza como obstaculo, nuevaPieza = true
                //     verificar si hay fila completa
                //         si: Eliminar fila y desplazar las de arriba para abajo

            handler.postDelayed(this, 1000);
        }
    };
    
}
