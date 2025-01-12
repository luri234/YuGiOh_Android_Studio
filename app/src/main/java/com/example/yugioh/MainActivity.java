package com.example.yugioh;


import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yugioh.modelo.Carta;
import com.example.yugioh.modelo.Jugador;
import com.example.yugioh.modelo.Partida;
import com.example.yugioh.modelo.Tablero;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    //declarando las instancias a utilizar del modelo
    Partida partida = null;
    Tablero tablero = null;
    Jugador j1 = null;
    Jugador j2 = null;
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


        //asignando valor a las instancias , manejando posibles errores en la creacion
        try {
            partida = new Partida(getAssets(), "archivocartas.txt");
            tablero = partida.getTablero();
            j1 = tablero.getJugador1();
            j2 = tablero.getJugador2();
        }catch (IOException e){
            crearDialog("ERROR", "Hubo un problema al abrir el archivo de cartas..");
        }catch (Exception e){
            crearDialog("ERROR", "Ha surgido un error desconocido");
        }

//        Jugador j1 = new Jugador("Usuario",1);
//        Jugador j2 = new Jugador("Máquina",2);
        MensajeInicio("Bienvenido al juego de Yu-Gi-Oh!","Para continuar con el sorteo inicial presiona Ok",partida,j1,j2);

        //FUNCION que reinicie el tablero , hace el sorteo logico y muestre las cartas tomadas
        repartirCartasIniciales();

        //CONTINUO AQUI

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

    public void cargarCartasEnMemoria(){

        try{
            ArrayList<Carta> cartas = Carta.leerCartas(getAssets(), "archivocartas.txt");
            //recorremos las cartas para irlas creando en memoria
            for(Carta c : cartas){
                Resources res = getResources(); //obtenemos la referencia a la carpeta resources

                int rId = res.getIdentifier(c.getRutaImagen(), "drawable", getPackageName());
                //creamos la instancia del recurso a crear en memoria
                ImageView imv = new ImageView(this);
                imv.setImageResource(rId);
                imv.getLayoutParams().width = 300; //ERROR
                imv.getLayoutParams().height = 300;

            }
        } catch (IOException e) {
            crearDialog("ERROR", "Hubo un problema al abrir el archivo de cartas..");
        }
    }

    public void repartirCartasIniciales(){
        //llamo al modelo para q reparta las cartas una por una


        for(int i = 0; i < 5; i++){
            //agregamos una a jugador 1
            Carta cJ1 = j1.tomarCartaEnTurno();
            //la aniadimos al scroll
            mostrarImagenScroll(cJ1, R.id.contenedorScrollRojo);

            //agregamos una a jugador 2
            Carta cJ2 = j2.tomarCartaEnTurno();
            mostrarImagenScroll(cJ2, R.id.contenedorScrollAzul);
        }

    }
    //crear una funcion para agregar cartas imagenes encima de la carta base
    //preguntar como hacer establecer los guidelines de la imagen por codigo.

    public void mostrarImagenScroll(Carta cartaAMostrar, int idLinearLayoutScroll){
        LinearLayout contenedor = findViewById(idLinearLayoutScroll);

        //para encontrar donde se dan los errores
        if(contenedor == null){
            crearDialog("Error en el programa", "No se encontro el id del LinearLayout");
            return;
        }

        Resources res = getResources();
        int rId = res.getIdentifier(cartaAMostrar.getRutaImagen(), "drawable", getPackageName());
        if(rId == 0){
            crearDialog("Error en el programa", "No se encontro la imagen: "+ cartaAMostrar.getRutaImagen());
            return;
        }
        ImageView imV = new ImageView(this);
        imV.setImageResource(rId);

        //configuramos los atributos de la imagen
        LinearLayout.LayoutParams atributos = new LinearLayout.LayoutParams(
                dpToPx(80), //ancho
                ViewGroup.LayoutParams.MATCH_PARENT  //alto
        );
        atributos.setMargins(0, 0, 2, 0);
        imV.setLayoutParams(atributos);
        imV.setScaleType(ImageView.ScaleType.CENTER_INSIDE); //centrado

        //agregamos la imagen al LinearLayout(Scroll)
        contenedor.addView(imV);
    }

    // Método para convertir dp a px (para garantizar consistencia en diferentes pantallas)
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

}
