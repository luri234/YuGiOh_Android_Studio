package com.example.yugioh.modelo;

import static android.app.ProgressDialog.show;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class Utilitaria {
    public static void crearToast(Context context, String mensaje,boolean duramucho){
        if(duramucho)
            Toast.makeText(context,mensaje,Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context,mensaje,Toast.LENGTH_SHORT).show();

    }
    public static void mensajeLargo(View view,String mensaje,boolean duramucho){
        if(duramucho)
            Snackbar.make(view, mensaje,Snackbar.LENGTH_LONG).show();
        else
            Snackbar.make(view,mensaje,Snackbar.LENGTH_SHORT).show();
    }
}
