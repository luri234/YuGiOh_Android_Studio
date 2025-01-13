package com.example.yugioh;


import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yugioh.modelo.Carta;
import com.example.yugioh.modelo.CartaMagica;
import com.example.yugioh.modelo.CartaMonstruo;
import com.example.yugioh.modelo.CartaTrampa;
import com.example.yugioh.modelo.Jugador;
import com.example.yugioh.modelo.Partida;
import com.example.yugioh.modelo.Tablero;
import com.example.yugioh.modelo.exceptions.AgregacionCartaMonstruoException;
import com.example.yugioh.modelo.exceptions.CartaMagicaAsociacionException;
import com.example.yugioh.modelo.exceptions.SinEspacioTableroException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

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

        //se da la bienvenida, sorteo inicio y se reparten las 5 cartas iniciales.
        inicioJuego("Bienvenido al juego de Yu-Gi-Oh!","Presione Ok para empezar a jugar!",partida,j1,j2);




    }




    //METODOS
    public void inicioJuego(String title,String mensaje,Partida partida,Jugador j1, Jugador j2){
        //Antes se llamaba mostrar mensajeInicio.
        //Muestra una bienvenida -> realiza el sorteo de inicia -> y reparte las 5 cartas iniciales, tanto para el juego logico y virtual
        AlertDialog.Builder abd = new AlertDialog.Builder(this);
        abd.setTitle(title);
        abd.setMessage(mensaje);
        abd.setPositiveButton("OK",(dialog,b)->{
            Jugador jinicial = partida.sorteoInicios(j1,j2);
            dialog.dismiss();
            crearDialog("Sorteo inicial","El jugador que comienza es: "+jinicial.getNombre()+"\nPara continuar da clic en 'Siguiente fase'");
            repartirCartasIniciales();

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

    //ignorar
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


    //funcion que agrega la imagen, trabaja con aniadircartatablero
    public void agregarImagenTablero(Carta c, int id_view,int id_jugador){
        ImageView v = findViewById(id_view);
        Resources res = getResources();
        int rId = res.getIdentifier(c.getRutaImagen(), "drawable", getPackageName());
        quitarImagenMano(c,id_jugador);
        v.setImageResource(rId);
        tablero.aniadirCartaTablero(c,id_jugador);
    }

    public void quitarImagenMano(Carta c, int id_jugador){
        if (id_jugador == 2) {
            LinearLayout azul = findViewById(R.id.contenedorScrollAzul);
            for (int i = 0; i < azul.getChildCount(); i++) {
                View cartita = azul.getChildAt(i);
                if (cartita.getTag().equals(c.getRutaImagen())){
                    azul.removeView(cartita);
                }
            }
        }else{
            LinearLayout rojo = findViewById(R.id.contenedorScrollRojo);
            for (int i = 0; i < rojo.getChildCount(); i ++){
                View cartita = rojo.getChildAt(i);
                if(cartita.getTag().equals(c.getRutaImagen())){
                    rojo.removeView(cartita);
                }
            }
        }
        tablero.quitarCartaMano(id_jugador,c);
    }
    //metodo todavia en proceso -----------
    //public void quitarImagenTablero(Carta c, int id_jugador, int id_view){
    //    ImageView v = findViewById(id_view);

    //}
    //preguntar como hacer establecer los guidelines de la imagen por codigo
    // .


    public void pasarFase(View v){
        //codigo que queremos que haga //v representa al boton
        //este es solo un ejemplo porque se necesita el cod de java
        crearDialog("Llenar Tablero","es turno del jugador (necesito getters y codigo) para que llene su tablero");
        //para probar el metodo
        agregarImagenTablero(j1.getCartasEnMano().get(0),R.id.esp_1_maq_carta,1);

    }




    public void mostrarImagenScroll(Carta cartaAMostrar, int idLinearLayoutScroll){
        LinearLayout contenedor = findViewById(idLinearLayoutScroll);

        //para encontrar donde se dan los errores
        if(contenedor == null){
            crearDialog("Error en el programa", "No se encontro el id del LinearLayout");
            return;
        }

        Resources res = getResources();
        int rId = res.getIdentifier(cartaAMostrar.getRutaImagen(), "drawable", getPackageName());
        //para mostrar en caso de error
        if(rId == 0){
            crearDialog("Error en el programa", "No se encontro la imagen: "+ cartaAMostrar.getRutaImagen());
            return;
        }
        ImageView imV = new ImageView(this);
        imV.setImageResource(rId);
        //setTag agregado para poder identificar la carta para eliminarla despues
        imV.setTag(cartaAMostrar.getRutaImagen());

        //configuramos los atributos de la imagen
        LinearLayout.LayoutParams atributos = new LinearLayout.LayoutParams(
                dpToPx(80), //ancho
                ViewGroup.LayoutParams.MATCH_PARENT  //alto
        );
        atributos.setMargins(0, 0, 2, 0);
        imV.setLayoutParams(atributos);
        imV.setScaleType(ImageView.ScaleType.CENTER_INSIDE); //centrado

        //definimos su onCLick Listener para que cuando se toque aparezca sus datos
        imV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción al hacer clic en la imagen
//                Toast.makeText(getApplicationContext(),
//                        "Clic en la carta: " + cartaAMostrar.getNombre(),
//                        Toast.LENGTH_SHORT).show();
                crearDialogAgregarCarta(cartaAMostrar);


            }
        });

        //agregamos la imagen al LinearLayout(Scroll)
        contenedor.addView(imV);
    }

    //ignorar
    public void mostrarTurno(int idJugador){
        //crea un dialogo que muestra el turno de quien de jugar una carta
        String nombreJ = "Error de asignacion de nombre";
        if(idJugador == 1){
            nombreJ = j1.getNombre();
        } else if (idJugador == 2) {
            nombreJ = j2.getNombre();
        }
        crearDialog("Atencion", "Turno del jugador: " + nombreJ);
    }


    public void crearDialogAgregarCarta(Carta carta){
        //obtenemos el numero de imagenes en el layout
        LinearLayout contenedor = findViewById(R.id.contenedorScrollRojo);

        int numImagenes = contenedor.getChildCount();
        //obtenemos el size de la lista cartasEnMano logico(que debe ser igual, solo para verificar q si lo sean)
        int tamanio =  j1.getCartasEnMano().size();

        //creamos el dialogo personalizado
        // Crear un Inflater para el diseño personalizado
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.datos_colocar_carta, null);
        TextView nombre = dialogView.findViewById(R.id.c_nombre);
        TextView descripcion = dialogView.findViewById(R.id.c_desc);
        if( numImagenes == tamanio){
            //validamos que instancia de carta es
            if( carta instanceof CartaMonstruo){
                CartaMonstruo cm = (CartaMonstruo)carta;
                // Configurar los valores dinámicos a mostrar
                TextView ataque = dialogView.findViewById(R.id.c_atk);
                TextView defensa = dialogView.findViewById(R.id.c_def);
                TextView atributoCarta = dialogView.findViewById(R.id.c_atributo);
                TextView tipoCarta = dialogView.findViewById(R.id.c_tipo);
                //damos los nuevos valores

                ataque.setText("ATK: "+ cm.getAtaque());
                defensa.setText("DEF: "+cm.getDefensa());
                atributoCarta.setText("Atributo: " + cm.getTipoAtributo());
                tipoCarta.setText("Tipo de Monstruo: " + cm.getTipoMonstruoString());
            } else if (carta instanceof CartaMagica) {
                CartaMagica cm = (CartaMagica)carta;
                TextView incAtk = dialogView.findViewById(R.id.c_atk);
                TextView incDef = dialogView.findViewById(R.id.c_def);
                TextView tipoMonstruo = dialogView.findViewById(R.id.c_tipo);
                //asignacion valores
                incAtk.setText("INC ATK: " + cm.getIncrementoAtaque());
                incDef.setText("INC DEF: " + cm.getIncrementoDefensa());
                tipoMonstruo.setText("Tipo Monstruo: " + cm.getTipoMonstruoString());

                //setteamos los demas campos no utilizados para q no aparezcan
                dialogView.findViewById(R.id.c_atributo).setVisibility(View.GONE);

            } else if (carta instanceof CartaTrampa) {
                CartaTrampa ct = (CartaTrampa)carta;
                TextView atributo = dialogView.findViewById(R.id.c_atributo);
                atributo.setText("Atributo: "+ ct.getTipoAtributo());
                dialogView.findViewById(R.id.c_tipo).setVisibility(View.GONE);
                dialogView.findViewById(R.id.c_atk).setVisibility(View.GONE);
                dialogView.findViewById(R.id.c_def).setVisibility(View.GONE);

            }

            //setteamos los valores que toda carta tiene
            nombre.setText(carta.getNombre());
            descripcion.setText("Descripcion: " + carta.getDescripcion());

            // Crear el AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);

            // Obtener referencia a los botones y configurar listeners
            AlertDialog dialog = builder.create();
            dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
                dialog.dismiss(); // Cierrar el dialogo
            });
            dialogView.findViewById(R.id.btn_accept).setOnClickListener(v -> {
                //accciones al clicar 'colocar'
                Toast.makeText(this, "*procede a colocar la carta en tablero ", Toast.LENGTH_SHORT).show();
                //funcio que valida le agregacion de una carta y dentro llame agregarcartaTablero
                dialog.dismiss();
            });
            // Mostrar el diálogo
            dialog.show();

        }else{
            crearDialog("Error de programa", "El tamanio del scroll j1 no coincide con el logico");
        }
    }

    public void aniadirCartaTableroFisico(Carta c, Jugador j ){
        //variables para actualizar el tablero logico
        Boolean[] isInAtaque = {null} ;
        CartaMonstruo cartaAAsociar = null;
        //valida si el jugador puede aniadir una carta y procede a aniadirla
        try {
            if(tablero.sePuedeAgregarCarta(c, j)){
                //validamos para cada caso de carta
                if(c instanceof CartaMonstruo){
                    //preguntamos si en ataque o defensa
                    crearDialogSetPosicionCarta(resultado -> {
                        //agregamos la carta al tablero definiendola si va girada o no de acuerdo al modo
                        if(resultado){

                            Toast.makeText(this, "Carta en Modo Ataque aniadida", Toast.LENGTH_SHORT).show();
                            isInAtaque[0]= true;

                        }else {
                            Toast.makeText(this, "Carta en Modo Defensa Aniadida", Toast.LENGTH_SHORT).show();
                            isInAtaque[0] = false;
                        }
                    });

                } else if ( c instanceof  CartaMagica) {
                    //mostramos cartas a enlazar
                } else if ( c instanceof  CartaTrampa) {
                    //solo agregamos al tablero (ojala todas fueran asi)
                }
            }
            //actualizamos el tablero logico de la carta a aniadir
            j.jugarCarta(partida, c, isInAtaque[0],cartaAAsociar );

        } catch (AgregacionCartaMonstruoException | SinEspacioTableroException  | CartaMagicaAsociacionException e){
            crearDialog("Error",  e.getMessage());
        }



    }

    //
    public void crearDialogSetPosicionCarta(Consumer<Boolean> retorno){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public void buscarAgregarImagenTablero(){
        //busca el espacio vacio donde pueda agregar la imagen en la seccion

    }


    // Metodo para convertir dp a px (para garantizar consistencia en diferentes pantallas)
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

}
