package com.example.tetris;

import android.content.Context;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.logging.Handler;

public class GameManager {

    private ArrayList<Tile> listaImageViews;
    private int[][] matrizControl;
    private GridLayout tableroGridLayout;
    private boolean juegoActivo;

    //private TextView clockTextView = find

    public GameManager(GridLayout grid, ArrayList<Tile> listaTiles, int[][] matriz){
        this.tableroGridLayout = grid;
        this.listaImageViews = listaTiles;
        this.matrizControl = matriz;
        this.juegoActivo = true;
    }

    public GridLayout getTableroGridLayout(){
        return this.tableroGridLayout;
    }

    public void cambiarValorItemMatriz(int row, int col, int valor){
        this.matrizControl[row][col] = valor;
    }

    public void invertirValorItemMatriz(int row, int col){
        if (this.matrizControl[row][col] == 1){
            this.matrizControl[row][col] = 0;
        } else {
            this.matrizControl[row][col] = 1;
        }
    }

    public int getValorItemMatriz(int row, int col){
        return this.matrizControl[row][col];
    }

    public Tile getTile(int index){
        return this.listaImageViews.get(index);
    }

    public Tile getTile(int row, int col){
        int index = (MainActivity.NUM_COLUMNAS * row) + col;
        return this.listaImageViews.get(index);
    }
}
