package com.example.tetris;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
import java.util.Collections;
import java.util.Random;

public class MainActivity extends Activity implements GestureDetector.OnGestureListener {

    GestureDetector gestureDetector;

    GridLayout gridLayout;
    LinearLayout linearLayout;
    Chronometer chronometer;
    TextView scoreTextView;

    public static final int NUM_COLUMNAS = 10;
    public static final int NUM_FILAS = 16;
    public static final int NUM_TOTAL_PIEZAS = 5;

    int score;

    // para obtener las coordenadas del imageView en el grid se usa:
    // col = indice mod 10
    // row = indice // 10
    ArrayList<Tile> listaImageViews;
    int[][] matrizControl = new int[NUM_FILAS][NUM_COLUMNAS];
    private ArrayList<Integer> indicesPiezaActiva;
    private ArrayList<Integer> indicesPiezaViejos;

    private boolean colocarNuevaPieza;
    private int imagenActual;

    boolean juegoActivo;

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

        gestureDetector = new GestureDetector(this, this);
        score = 0;


        gridLayout = findViewById(R.id.tableroGrid);
        linearLayout = findViewById(R.id.infoLayout);
        chronometer = findViewById(R.id.chronometer);
        scoreTextView = findViewById(R.id.scoreTextView);
        listaImageViews = new ArrayList<>();

        gridLayout.setColumnCount(10);
        gridLayout.setRowCount(20);

        colocarNuevaPieza = true;
        indicesPiezaActiva = null;
        juegoActivo = true;

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
            if (colocarNuevaPieza){
                indicesPiezaActiva = colocarPiezaAleatoria();
                colocarNuevaPieza = false;
                juegoActivo = !determinarSiPerdio();

                if (!juegoActivo){
                    // hacer animacion pare restablecer el juego
                    resetTablero();
                    System.out.println("reset Tablero");
                    juegoActivo = true;
                }

            } else {
                //mover pieza, debe determinar si la posicion siguiente en válida
                moverPieza(Direccion.ABAJO);

            }
                // no: mover pieza
                // si: colocar pieza, nuevaPieza = false
            // Verificar si pieza tiene una pieza obstaculo por debajo
                // si: marcar pieza como obstaculo, nuevaPieza = true
                //     verificar si hay fila completa
                //         si: Eliminar fila y desplazar las de arriba para abajo

            //moverPieza(Direccion.ABAJO);

            handler.postDelayed(this, 1000);
        }
    };

    public void resetTablero(){
        for (int row = 0; row < NUM_FILAS; row++){
            for (int col = 0; col < NUM_COLUMNAS; col++){
                int indice = getIndice(row, col);
                Tile tile = listaImageViews.get(indice);
                if (col == 0 || col == 9 || row == 15){
                    tile.setEsObstaculo(true);
                    tile.setImageResource(R.drawable.cuadrogris32);
                    tile.setAlpha(1f);
                    matrizControl[row][col] = 1;
                } else {
                    tile.setEsObstaculo(false);
                    tile.setImageResource(R.drawable.cuadrogris32);
                    tile.setAlpha(0.35f);
                    matrizControl[row][col] = 0;
                }
            }
        }
        score = 0;
        scoreTextView.setText("0000");
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    public int getIndice(int row, int col){
        int indice = NUM_COLUMNAS * row + col;
        return indice;
    }

    public boolean determinarSiPerdio(){
        /*boolean perdio = false;
        for (Integer indice : indicesPiezaActiva){
            if (listaImageViews.get(indice).getEsObstaculo() && indice < 19){
                perdio = true;
            }
        }

        return perdio;*/
        boolean perdio = false;
        for (int row = 0; row < 2; row++){
            for (int col = 1; col < 9; col++){
                int indice = getIndice(row, col);
                Tile tile = listaImageViews.get(indice);
                tile.setImageResource(R.drawable.cuadrorosado32);
                tile.setAlpha(0.35f);
                if (tile.getEsObstaculo()){
                    perdio = true;
                }
            }
        }
        return perdio;
    }

    public ArrayList<Integer> colocarPiezaAleatoria(){

        Random rm = new Random();
        int tipoPieza = rm.nextInt(NUM_TOTAL_PIEZAS) + 1;

        int indiceInicial = 1;
        ArrayList<Integer> indicesPieza = new ArrayList<>();
        switch (tipoPieza){

            // caso de pieza en zeta
            //Posible indices iniciales: 1 a 6
            case 1:
                indiceInicial = rm.nextInt(6) + 1;
                indicesPieza.add(indiceInicial);
                indicesPieza.add(indiceInicial + 1);
                indicesPieza.add(indiceInicial + 1 + 10);
                indicesPieza.add(indiceInicial + 1 + 10 + 1);

                break;

            // caso de pieza en T
            //Posible indices iniciales: 1 a 6
            case 2:
                indiceInicial = rm.nextInt(6) + 1;
                indicesPieza.add(indiceInicial);
                indicesPieza.add(indiceInicial + 1);
                indicesPieza.add(indiceInicial + 2);
                indicesPieza.add(indiceInicial + 1 + 10);

                break;

            // caso de pieza en línea horizontal
            //Posible indices iniciales: 1 a 5
            case 3:
                indiceInicial = rm.nextInt(4) + 1;
                indicesPieza.add(indiceInicial);
                indicesPieza.add(indiceInicial + 1);
                indicesPieza.add(indiceInicial + 2);
                indicesPieza.add(indiceInicial + 3);

                break;

            // caso de pieza en L
            //Posible indices iniciales: 1 a 6
            case 4:
                indiceInicial = rm.nextInt(5) + 1;
                indicesPieza.add(indiceInicial);
                indicesPieza.add(indiceInicial + 1);
                indicesPieza.add(indiceInicial + 2);
                indicesPieza.add(indiceInicial + 2 + 10);

                break;

            // caso de pieza en cuadrado
            //Posible indices iniciales: 1 a 7
            case 5:
                indiceInicial = rm.nextInt(7) + 1;
                indicesPieza.add(indiceInicial);
                indicesPieza.add(indiceInicial + 1);
                indicesPieza.add(indiceInicial + 10);
                indicesPieza.add(indiceInicial + 1 + 10);

                break;
        }

        // modificar los image view de los indices donde va a estar la pieza

        // determinar cual imagen se va a utiliza para la nueva pieza
        int imagenSeleccionada = rm.nextInt(3);
        for (Integer i: indicesPieza){
            Tile tile = listaImageViews.get(i);
            tile.setAlpha(1f);

            // 0: imagen naranja
            if (imagenSeleccionada == 0){
                tile.setImageResource(R.drawable.cuadronaranja32);
                imagenActual = R.drawable.cuadronaranja32;
            } else if (imagenSeleccionada == 1){
                tile.setImageResource(R.drawable.cuadrorosado32);
                imagenActual = R.drawable.cuadrorosado32;
            } else {
                tile.setImageResource(R.drawable.cuadroverde32);
                imagenActual = R.drawable.cuadroverde32;
            }

        }
        return indicesPieza;

    }

    public void moverPieza(int direccion){

        boolean movimientoValido = true;

        if (direccion == Direccion.ABAJO){

            // Determinar si en una posicion mas abajo hay un obtaculo
            // lo que se considera como un movimiento invalido
            for (Integer indice : indicesPiezaActiva){
                int indicePosicionSiguiente = indice + 10;
                Tile tile = listaImageViews.get(indicePosicionSiguiente);
                if (tile.getEsObstaculo()){
                    movimientoValido = false;
                }
            }
            //System.out.println(movimientoValido);
            if (movimientoValido){

                // se colocan grises las posiciones viejas
                for (Integer indice : indicesPiezaActiva){
                    Tile tile = listaImageViews.get(indice);
                    tile.setAlpha(0.35f);
                    tile.setImageResource(R.drawable.cuadrogris32);
                }

                ArrayList<Integer> nuevosIndices = new ArrayList<>();

                for (Integer indice : indicesPiezaActiva){
                    int indiceN = indice + 10;
                    Tile tile = listaImageViews.get(indiceN);
                    tile.setAlpha(1f);
                    tile.setImageResource(imagenActual);
                    nuevosIndices.add(indiceN);
                }

                indicesPiezaViejos = indicesPiezaActiva;
                indicesPiezaActiva = nuevosIndices;

            } else {
                for (Integer indice : indicesPiezaActiva){
                    Tile tile = listaImageViews.get(indice);
                    tile.setEsObstaculo(true);

                    int col = indice % NUM_COLUMNAS;
                    int row = indice / NUM_COLUMNAS;
                    //System.out.println(String.format("ROW: %s, COL: %s", row, col));
                    matrizControl[row][col] = 1;
                    imprimirMatrizControl();
                    verificarSiCompletoFila();

                }
                colocarNuevaPieza = true;
            }
        } else if (direccion == Direccion.DERECHA){

            for (Integer indice : indicesPiezaActiva){
                int indicePosicionSiguiente = indice + 1;
                Tile tile = listaImageViews.get(indicePosicionSiguiente);
                if (tile.getEsObstaculo()){
                    movimientoValido = false;
                }
            }
            if (movimientoValido){
                // se colocan grises las posiciones viejas
                for (Integer indice : indicesPiezaActiva){
                    Tile tile = listaImageViews.get(indice);
                    tile.setAlpha(0.35f);
                    tile.setImageResource(R.drawable.cuadrogris32);
                }

                ArrayList<Integer> nuevosIndices = new ArrayList<>();

                for (Integer indice : indicesPiezaActiva){
                    int indiceN = indice + 1;
                    Tile tile = listaImageViews.get(indiceN);
                    tile.setAlpha(1f);
                    tile.setImageResource(imagenActual);
                    nuevosIndices.add(indiceN);
                }
                indicesPiezaViejos = indicesPiezaActiva;
                indicesPiezaActiva = nuevosIndices;

            }

        } else {
            for (Integer indice : indicesPiezaActiva){
                int indicePosicionSiguiente = indice - 1;
                Tile tile = listaImageViews.get(indicePosicionSiguiente);
                if (tile.getEsObstaculo()){
                    movimientoValido = false;
                }
            }
            if (movimientoValido){
                // se colocan grises las posiciones viejas
                for (Integer indice : indicesPiezaActiva){
                    Tile tile = listaImageViews.get(indice);
                    tile.setAlpha(0.35f);
                    tile.setImageResource(R.drawable.cuadrogris32);
                }

                ArrayList<Integer> nuevosIndices = new ArrayList<>();

                for (Integer indice : indicesPiezaActiva){
                    int indiceN = indice - 1;
                    Tile tile = listaImageViews.get(indiceN);
                    tile.setAlpha(1f);
                    tile.setImageResource(imagenActual);
                    nuevosIndices.add(indiceN);
                }
                indicesPiezaViejos = indicesPiezaActiva;
                indicesPiezaActiva = nuevosIndices;

            }
        }
    }

    public void desplazarhastaAbajo(){
        boolean movimientoValido = true;

        while (movimientoValido){
            // Determinar si en una posicion mas abajo hay un obtaculo
            // lo que se considera como un movimiento invalido
            for (Integer indice : indicesPiezaActiva){
                int indicePosicionSiguiente = indice + 10;
                Tile tile = listaImageViews.get(indicePosicionSiguiente);
                if (tile.getEsObstaculo()){
                    movimientoValido = false;
                }
            }

            //System.out.println(movimientoValido);
            if (movimientoValido){

                // se colocan grises las posiciones viejas
                for (Integer indice : indicesPiezaActiva){
                    Tile tile = listaImageViews.get(indice);
                    tile.setAlpha(0.35f);
                    tile.setImageResource(R.drawable.cuadrogris32);
                }

                ArrayList<Integer> nuevosIndices = new ArrayList<>();

                for (Integer indice : indicesPiezaActiva){
                    int indiceN = indice + 10;
                    Tile tile = listaImageViews.get(indiceN);
                    tile.setAlpha(1f);
                    tile.setImageResource(imagenActual);
                    nuevosIndices.add(indiceN);
                }
                indicesPiezaViejos = indicesPiezaActiva;
                indicesPiezaActiva = nuevosIndices;

            } else {
                for (Integer indice : indicesPiezaActiva){
                    Tile tile = listaImageViews.get(indice);
                    tile.setEsObstaculo(true);
                    int col = indice % NUM_COLUMNAS;
                    int row = indice / NUM_COLUMNAS;
                    //System.out.println(String.format("ROW: %s, COL: %s", row, col));
                    matrizControl[row][col] = 1;
                    imprimirMatrizControl();
                    verificarSiCompletoFila();

                }
                colocarNuevaPieza = true;
            }
        }


    }

    public void verificarSiCompletoFila(){
        ArrayList<Integer> indicesFilasCompletas = new ArrayList<>();
        for (Integer pos : indicesPiezaActiva){
            System.out.println(String.format("Pos: %s", pos));
            int row = pos / NUM_COLUMNAS;
            int sumaFila = 0;
            for (int col = 0; col < NUM_COLUMNAS; col++){
                sumaFila += matrizControl[row][col];
            }
            if (sumaFila == 10){
                indicesFilasCompletas.add(row);
                break;
            }
        }

        if (indicesFilasCompletas.size() == 0){
            return;
        }

        System.out.println(String.format("Indices de filas: %s", indicesFilasCompletas.size()));
        for (Integer i : indicesFilasCompletas){
            System.out.println(i);
        }

        score += indicesFilasCompletas.size()*100;
        scoreTextView.setText(String.format("%04d", score));

        // se quitan cmo obstaculos los elementos de las filas completadas
        for (Integer row : indicesFilasCompletas){
            for (int col = 1; col < 9; col++){
                int indice = getIndice(row, col);
                Tile tile = listaImageViews.get(indice);
                tile.setAlpha(0.35f);
                tile.setImageResource(R.drawable.cuadrogris32);
                tile.setEsObstaculo(false);
                matrizControl[row][col] = 0;
            }
        }

        Collections.sort(indicesFilasCompletas);
        int FilaMayor = indicesFilasCompletas.get(indicesFilasCompletas.size()-1);
        //A partir de fila mayor para atrás se deben desplazar par abajo

        for (int fila = FilaMayor; fila >= 0; fila--){
            for (int col = 1; col < 9; col++){
                int indice = getIndice(fila, col);
                Tile tileActual = listaImageViews.get(indice);
                if (tileActual.getEsObstaculo()){
                    desplazarTileHaciaAbajo(indice, fila, col);
                }
            }
        }



    }

    public void desplazarTileHaciaAbajo(int indice, int fila, int columna){

        boolean movimientoValido = true;

        while (movimientoValido){
            // Determinar si en una posicion mas abajo hay un obtaculo
            // lo que se considera como un movimiento invalido

            int indicePosicionSiguiente = indice + 10;
            Tile tileSig = listaImageViews.get(indicePosicionSiguiente);
            if (tileSig.getEsObstaculo()){
                movimientoValido = false;
            }


            //System.out.println(movimientoValido);
            if (movimientoValido){

                // se coloca en gris la posición vieja
                Tile tile = listaImageViews.get(indice);
                tile.setAlpha(0.35f);
                tile.setEsObstaculo(false);
                tile.setImageResource(R.drawable.cuadrogris32);
                matrizControl[fila][columna] = 0;

                Tile sig = listaImageViews.get(indice + 10);
                sig.setAlpha(1f);
                sig.setImageResource(R.drawable.cuadrorosado32);
                sig.setEsObstaculo(true);
                matrizControl[fila + 1][columna] = 1;

                indice += 10;
                fila += 1;

            } /*else {

                Tile tile = listaImageViews.get(indice);
                tile.setEsObstaculo(true);
                int col = indice % NUM_COLUMNAS;
                int row = indice / NUM_COLUMNAS;
                //System.out.println(String.format("ROW: %s, COL: %s", row, col));
                matrizControl[row][col] = 1;

            }*/
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        System.out.println("se le dio tap");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        /*System.out.println(String.format("Velocidad en X: %s", String.valueOf(velocityX)));
        System.out.println(String.format("Velocidad en Y: %s", String.valueOf(velocityY)));*/

        // movimiento swipe left
        if((velocityX < -750 && velocityY < 300 && velocityY > -300) || velocityX < -2500){
            moverPieza(Direccion.IZQUIERDA);
        // movimiento swipe right
        } else if ((velocityX > 750 && velocityY < 300 && velocityY > -300) || velocityX > 2500){
            moverPieza(Direccion.DERECHA);
        }
        // movimiento swipe down
        if ((velocityY > 850 && velocityX < 300 && velocityX > -300) || velocityY > 2500){
            desplazarhastaAbajo();
            //moverPieza(Direccion.ABAJO);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return gestureDetector.onTouchEvent(event);
    }
}
