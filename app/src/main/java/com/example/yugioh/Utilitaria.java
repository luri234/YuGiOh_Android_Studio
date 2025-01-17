package com.example.yugioh;

import static android.app.ProgressDialog.show;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.function.Consumer;

public class Utilitaria {

    //La funciones de aqui no deben llamar en ningun caso  a las clases del modelo, solo deben
    //ser para implementar objetos visuales(y asi tener mas orden).

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


    //
    public static void crearDialogSetPosicionCarta(Consumer<Boolean> retorno,Context contexto){
        //Crea y muestra el dialogo para que el usuario defina la posicion de una carta Monstruo
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("Selecciona la posición de la carta");

        // botones
        builder.setPositiveButton("Modo Ataque", (dialog, which) -> {
            retorno.accept(true);
            dialog.dismiss();
        });

        builder.setNegativeButton("Modo Defensa", (dialog, which) -> {
            // Acciónes para "Modo Defensa"
            retorno.accept(false);
            dialog.dismiss();
        });

        // Crear y mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
