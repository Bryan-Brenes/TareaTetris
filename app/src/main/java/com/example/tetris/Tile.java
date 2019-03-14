package com.example.tetris;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;

public class Tile extends AppCompatImageView {

    private boolean esObstaculo;

    public Tile(Context context, boolean isObs){
        super(context);
        this.esObstaculo = isObs;
    }

    public Tile(Context context){
        super(context);
        this.esObstaculo = true;
    }

    public void setEsObstaculo(boolean esObs){
        this.esObstaculo = esObs;
    }

    public boolean getEsObstaculo(){
        return this.esObstaculo;
    }
}
