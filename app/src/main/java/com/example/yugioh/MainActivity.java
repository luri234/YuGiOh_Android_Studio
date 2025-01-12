package com.example.yugioh;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button sgtfase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sgtfase = findViewById(R.id.button_fases);
        Jugador j1 = new Jugador("Usuario",1);
        Jugador j2 = new Jugador("Máquina",2);
        Partida partida = new Partida();
        MensajeInicio("Bienvenido al juego de Yu-Gi-Oh!","Para continuar con el sorteo inicial presiona Ok",partida,j1,j2);
        sgtfase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //codigo que queremos que haga //v representa al boton
                //este es solo un ejemplo porque se necesita el cod de java
                crearDialog("Llenar Tablero","es turno del jugador (necesito getters y codigo) para que llene su tablero");

            }
        });
    }
    public void MensajeInicio(String title,String mensaje,Partida partida,Jugador j1, Jugador j2){
        AlertDialog.Builder abd = new AlertDialog.Builder(this);
        abd.setTitle(title);
        abd.setMessage(mensaje);
        abd.setPositiveButton("OK",(dialog,b)->{
            Jugador jinicial = partida.sorteoInicios(j1,j2);
            dialog.dismiss();
            crearDialog("Sorteo inicial","El jugador que comienza es: "+jinicial.getNombre()+"\nPara continuar da clic en 'Siguiente fase'");
        }
        );
        AlertDialog dialog = abd.create();
        dialog.show();
    }
    public void crearDialog(String title, String mensaje){
        AlertDialog.Builder abd = new AlertDialog.Builder(this);
        abd.setTitle(title);
        abd.setMessage(mensaje);
        abd.setPositiveButton("OK",(dialog,b)->{
            dialog.dismiss();}
        );
        AlertDialog dialog = abd.create();
        dialog.show();

    }
}
