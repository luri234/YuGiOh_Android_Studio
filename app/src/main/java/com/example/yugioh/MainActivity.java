package com.example.yugioh;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
import com.example.yugioh.modelo.Utilitaria;
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
    TextView deckuser;
    TextView deckmaq;
    public static ConstraintLayout contenedorMonstruosUser;
    public static ConstraintLayout contenedorEspecialesUser;
    public static ConstraintLayout contenedorMontruosMaquina;
    public static ConstraintLayout contenedorEspecialesMaquina;

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
        deckuser = findViewById(R.id.num_deck_user);
        deckmaq = findViewById(R.id.num_deck_maq);
        contenedorMonstruosUser = findViewById(R.id.CartasMonstruoJugador);


        //asignando valor a las instancias , manejando posibles errores en la creacion
        try {
            partida = new Partida(getAssets(), "archivocartas.txt");
            tablero = partida.getTablero();
            j1 = tablero.getJugador1();
            j2 = tablero.getJugador2();
        } catch (IOException e) {
            crearDialog("ERROR", "Hubo un problema al abrir el archivo de cartas..");
        } catch (Exception e) {
            crearDialog("ERROR", "Ha surgido un error desconocido");
        }
        //se da la bienvenida, sorteo inicio y se reparten las 5 cartas iniciales.
        inicioJuego("Bienvenido al juego de Yu-Gi-Oh!", "Presione Ok para empezar a jugar!", partida, j1, j2);


    }

    public void llenartableromaquina(Tablero tablero, Jugador j2) {
        j2.llenarTableroMaquina(tablero);
        ArrayList<Carta> esp_mons = tablero.getTableroCompartido().get(2).get("CartasMonstruo");
        ArrayList<Carta> esp_esp = tablero.getTableroCompartido().get(2).get("CartasEspeciales");
        int indice = 1;
        int cont = 1;
        for (Carta carta : esp_mons) {
            CartaMonstruo c = (CartaMonstruo) carta;
            if (cont == 1)
                agregarImagenTablero(c, R.id.mons_1_maq_carta, 2, false, true);
            else if (cont == 2)
                agregarImagenTablero(c, R.id.mons_2_maq_carta, 2, false, true);
            else if (cont == 3)
                agregarImagenTablero(c, R.id.mons_3_maq_carta, 2, false, true);

            cont++;
        }
        for (Carta carta : esp_esp) {
            if (indice == 1) {
                agregarImagenTablero(carta, R.id.esp_1_maq_carta, 2, false, true);
            } else if (indice == 2) {
                agregarImagenTablero(carta, R.id.esp_2_maq_carta, 2, false, true);
            } else if (indice == 3) {
                agregarImagenTablero(carta, R.id.esp_3_maq_carta, 2, false, true);
            }
            indice++;
        }
        Utilitaria.crearToast(this, "La máquina ya ha llenado su tablero", false);

    }


    //METODOS
    public void inicioJuego(String title, String mensaje, Partida partida, Jugador j1, Jugador j2) {
        //Antes se llamaba mostrar mensajeInicio.
        //Muestra una bienvenida -> realiza el sorteo de inicia -> y reparte las 5 cartas iniciales, tanto para el juego logico y virtual
        AlertDialog.Builder abd = new AlertDialog.Builder(this);
        abd.setTitle(title);
        abd.setMessage(mensaje);
        abd.setPositiveButton("OK", (dialog, b) -> {
                    Jugador jinicial = partida.sorteoInicios(j1, j2);
                    dialog.dismiss();
                    crearDialog("Sorteo inicial", "El jugador que comienza es: " + jinicial.getNombre() + "\nPara continuar da clic en 'Siguiente fase'");
                    repartirCartasIniciales();
                    restringir_movimientos(true, 1);
                    restringir_movimientos(true, 2);

                }
        );
        AlertDialog dialog = abd.create();
        dialog.show();

    }

    public void crearDialog(String title, String mensaje) {
        AlertDialog.Builder abd = new AlertDialog.Builder(this);
        abd.setTitle(title);
        abd.setMessage(mensaje);
        abd.setPositiveButton("OK", (dialog, b) -> {
                    dialog.dismiss();
                }
        );
        AlertDialog dialog = abd.create();
        dialog.show();

    }

    //ignorar
    public void cargarCartasEnMemoria() {

        try {
            ArrayList<Carta> cartas = Carta.leerCartas(getAssets(), "archivocartas.txt");
            //recorremos las cartas para irlas creando en memoria
            for (Carta c : cartas) {
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

    public void repartirCartasIniciales() {
        //llamo al modelo para q reparta las cartas una por una


        for (int i = 0; i < 5; i++) {
            //agregamos una a jugador 1
            Carta cJ1 = j1.tomarCartaEnTurno(deckuser);
            //la aniadimos al scroll
            mostrarImagenScroll(cJ1, R.id.contenedorScrollRojo);


            //agregamos una a jugador 2
            Carta cJ2 = j2.tomarCartaEnTurno(deckmaq);
            mostrarImagenScroll(cJ2, R.id.contenedorScrollAzul);
        }

    }


    public void quitarImagenMano(Carta c, int id_jugador) {
        if (id_jugador == 2) {
            LinearLayout azul = findViewById(R.id.contenedorScrollAzul);
            for (int i = 0; i < azul.getChildCount(); i++) {
                View cartita = azul.getChildAt(i);
                if (cartita.getTag().equals(c.getRutaImagen())) {
                    azul.removeView(cartita);
                }
            }
        } else {
            LinearLayout rojo = findViewById(R.id.contenedorScrollRojo);
            for (int i = 0; i < rojo.getChildCount(); i++) {
                View cartita = rojo.getChildAt(i);
                if (cartita.getTag().equals(c.getRutaImagen())) {
                    rojo.removeView(cartita);
                }
            }
        }
        tablero.quitarCartaMano(id_jugador, c);
    }
    public static void quitarImagenTablero(Carta carta, ConstraintLayout contenedor) {
        for (int i = contenedor.getChildCount() - 1; i >= 0; i--) {
            View cartita = contenedor.getChildAt(i);
            if (cartita.getTag() != null && cartita.getTag().equals(carta.getRutaImagen())) {
                ImageView v = (ImageView)cartita;
                int rId = R.drawable.imagen_base;
                v.setImageResource(rId);
                v.setTag("imagen_base.png");
                //se ha cambiado la imagen por la base (fondo blanco) y se ha guardado la ruta del view para poder compararla despues(setTag)
            }
        }
    }

    //}
    //preguntar como hacer establecer los guidelines de la imagen por codigo
    // .


    public void pasarFase(View v){
        //puede representar al metodo ronda de java
        //codigo que queremos que haga //v representa al boton
        //este es solo un ejemplo porque se necesita el cod de java

        if (partida.getRonda()==1 || partida.getRonda()==2) {
            faseLLenarTableros();
            partida.cambiarTurno();

        }
        else{
            //crearDialog("PRUEBA AVISO","a partir de esta ronda ya se puede declarar batalla");
            if ((partida.getRonda()%2) !=0) {
                if (partida.getRonda() ==3) {
                    Utilitaria.crearToast(this,"Desde este turno ya se puede declarar batalla",true);
                }
                faseLLenarTableros();
            }
            else {
                faseDeclararBatalla();
                partida.cambiarTurno();
                //partida.resetearEstadoCartasMonstruo(); //ya pueden jugar con todas sus monstruos
            }

        }
        //partida.cambiarTurno();
        partida.setRonda(partida.getRonda()+1);

        //para probar el metodo


    }

    public void faseLLenarTableros(){
        if (partida.getJugadorActual() == 2){
            j2.setNoAgregoMonstruo(true);
            restringir_movimientos(true,1);
            restringir_movimientos(false,2);
            crearDialog("Llenar Tablero","La Máquina ha robado una carta del deck y procede a llenar su tablero");
            Carta c = j2.tomarCartaEnTurno(deckmaq);
            llenartableromaquina(tablero,j2);
            mostrarImagenScroll(c,R.id.contenedorScrollAzul);
        }
        else{
            j1.setNoAgregoMonstruo(true);
            restringir_movimientos(false,1);
            restringir_movimientos(true,2);
            Carta c1 = j1.tomarCartaEnTurno(deckuser);
            crearDialog("Llenar Tablero","Has robado una carta del deck y es tu turno de llenar tu tablero");
            mostrarImagenScroll(c1,R.id.contenedorScrollRojo);
        }
    }
    public void faseDeclararBatalla(){
        //solo de prueba
        Utilitaria.mensajeLargo(findViewById(R.id.contenedorScrollAzul),"Testeto de los mensajes largos acá (interaccion con usuario), se puede utilizar esto o el toast",true);
        if (partida.getJugadorActual() ==1){
            crearDialog("DECLARAR BATALLA","Es tu turno de declarar batalla");
        }
        else
            j2.declararBatallaComoMaquina(tablero,j1,this);
    }
    //la funcion esta pensada para que el jugador no pueda hacer nada mientras no sea su turno
    //con true se restringen los movimientos
    public void restringir_movimientos(boolean val,int id_jugador){
        LinearLayout rojo = findViewById(R.id.contenedorScrollRojo);
        LinearLayout azul = findViewById(R.id.contenedorScrollAzul);
        if (id_jugador == 1) {
            for (int i = 0; i < rojo.getChildCount(); i++) {
                View cartita = rojo.getChildAt(i);
                cartita.setEnabled(!val);
            }
        }
        else{
            for (int i = 0; i < azul.getChildCount(); i++) {
                View cartita = azul.getChildAt(i);
                cartita.setEnabled(!val);
            }
        }
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
                crearDialogAgregarCarta(cartaAMostrar,idLinearLayoutScroll);


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


    public void crearDialogAgregarCarta(Carta carta, int id_contenedor){
        //obtenemos el numero de imagenes en el layout
        LinearLayout contenedor = findViewById(id_contenedor);

        int numImagenes = contenedor.getChildCount();
        //obtenemos el size de la lista cartasEnMano logico(que debe ser igual, solo para verificar q si lo sean)
        Jugador jj; //esto es solo para que funcione para el user y la maquina, evita problemas con usuario malo (preguntar a May de ser necesario)
        if (id_contenedor == R.id.contenedorScrollRojo)
            jj = j1;
        else
            jj = j2;
        int tamanio =  jj.getCartasEnMano().size();

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
            //se restringe para que solo se pueda colocar desde la mano del jugador y no desde la maquina
            if (id_contenedor == R.id.contenedorScrollRojo) {
                dialogView.findViewById(R.id.btn_accept).setOnClickListener(v -> {
                    //accciones al clicar 'colocar'
                    aniadirCartaTableroFisico(carta, j1);
                    dialog.dismiss();
                });
            }
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
                            buscarAgregarImagenTablero(c, findViewById(R.id.CartasMonstruoJugador), false, true);
                            Utilitaria.crearToast(this,"Carta en Modo Ataque añadida",false);
                            isInAtaque[0]= true;

                        }else {
                            //ojo si se agrega en defensa se debe dar el drawable de la carta boca abajo
                            buscarAgregarImagenTablero(c, findViewById(R.id.CartasMonstruoJugador), true, false);
                            Utilitaria.crearToast(this,"Carta en Modo Defensa Aniadida",false);
                            isInAtaque[0] = false;
                        }
                    });
                    j1.setNoAgregoMonstruo(false); //con esto se activa el dialog de que solo se puede agregar un monstruo por turno

                } else if ( c instanceof  CartaMagica) {
                    //mostramos en un dialog cartas a enlazar
                    crearDialogEnlazarCarta(c, findViewById(R.id.CartasEspecialesJugador));


                } else if ( c instanceof  CartaTrampa) {
                    //ojo se deben agregar boca abajo
                    //solo agregamos al tablero (ojala todas fueran asi xd)
                    buscarAgregarImagenTablero(c, findViewById(R.id.CartasEspecialesJugador), false, true);
                    Utilitaria.crearToast(this,"Carta Trampa Aniadida",false);
                }
            }

            //actualizamos el tablero logico de la carta a aniadir

            //hay problemas con jugarCarta
            //j.jugarCarta(partida, c, isInAtaque[0]);

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

    public void buscarAgregarImagenTablero(Carta cartaAAgregar, ConstraintLayout contenedorCartas, boolean vaGirada, boolean mostrar){
        //busca el espacio vacio donde pueda agregar la imagen en la seccion


        int totalHijos = contenedorCartas.getChildCount();

        for(int i = 0; i < totalHijos; i++){
            View hijo = contenedorCartas.getChildAt(i);
            if(hijo instanceof ImageView){
                ImageView imV = (ImageView)hijo;
                int idImage = imV.getId();
                if(imV.getContentDescription().equals("Casillero Disponible")){
                    agregarImagenTablero(cartaAAgregar, idImage, j1.getId(), vaGirada, mostrar);
                    tablero.aniadirCartaTablero(cartaAAgregar,j1.getId());
                    return;
                }

            }
        }

    }

    //funcion que agrega la imagen, trabaja con aniadircartatablero
    public void agregarImagenTablero(Carta c, int id_view,int id_jugador , boolean vaGirada, boolean mostrar){
        ImageView v = findViewById(id_view); //
        Resources res = getResources(); //
        int rId = R.drawable.carta_bocaabajo;

        //aniadido
        if(mostrar){
            rId = res.getIdentifier(c.getRutaImagen(), "drawable", getPackageName()); //
        }
        quitarImagenMano(c,id_jugador); //
        v.setImageResource(rId); //
        v.setTag(c.getRutaImagen());//necesito identificarla para borrarla luego
        //aniadido
        if(vaGirada) v.setRotation(-90f);
        //tablero.aniadirCartaTablero(c,id_jugador); se la puso en el metodo buscaragregarimagen por problemas con la maquina

        //cambiamos el estado a casillero ocupado
        v.setContentDescription("Casillero ocupado");
    }

    // Metodo para convertir dp a px (para garantizar consistencia en diferentes pantallas)
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    public void crearDialogEnlazarCarta(Carta cartaMagica, ConstraintLayout contenedorCartasEspeciales ){
        // Lista de nombres
        final String[] names = {"Carta1", "Carta2", "Carta3"};

        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona la carta a Enlazar")
                .setSingleChoiceItems(names, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Se puede capturar la selección aquí si es necesario
                    }
                })
                .setPositiveButton("Enlazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtener el nombre seleccionado
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if (selectedPosition != -1) {
                            String selectedName = names[selectedPosition];
                            // Realizar acción con el nombre seleccionado (por ejemplo, enlazar)
                            Utilitaria.crearToast(MainActivity.this,"Enlazado con " + selectedName,false);
                            buscarAgregarImagenTablero(cartaMagica, contenedorCartasEspeciales, false, true);
                        } else {
                            Utilitaria.crearToast(MainActivity.this,"No se seleccionó ninguna carta",false);
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Acción de cancelación
                        dialog.dismiss();
                        Utilitaria.crearToast(MainActivity.this, "Enlace cancelado",false);
                    }
                });

        // Crear y mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
