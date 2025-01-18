package com.example.yugioh;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.example.yugioh.modelo.exceptions.MonstruoNoEnAtaqueException;
import com.example.yugioh.modelo.exceptions.MonstruoYaHaAtacadoException;
import com.example.yugioh.modelo.exceptions.SinEspacioTableroException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    TextView vidaUser;
    TextView vidaMaq;
    public static ConstraintLayout contenedorMonstruosUser ;
    public static ConstraintLayout contenedorEspecialesUser;
    public static ConstraintLayout contenedorMontruosMaquina;
    public static ConstraintLayout contenedorEspecialesMaquina;
    View mensajes;
    String nombreUsuario;

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
        contenedorEspecialesUser = findViewById(R.id.CartasEspecialesJugador);
        contenedorEspecialesMaquina = findViewById(R.id.CartasEspecialesMaquina);
        contenedorMontruosMaquina = findViewById(R.id.CartasMonstruoMaquina);
        mensajes = findViewById(R.id.MensajeBatalla);
        vidaMaq = findViewById(R.id.vidaMaq);
        vidaUser = findViewById(R.id.vidaUser);



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
        j2.llenarTableroMaquina(tablero,this);
        ArrayList<Carta> esp_mons = tablero.getTableroCompartido().get(2).get("CartasMonstruo");
        ArrayList<Carta> esp_esp = tablero.getTableroCompartido().get(2).get("CartasEspeciales");
        int indice = 1;
        int cont = 1;
        for (Carta carta : esp_mons) {
            CartaMonstruo c = (CartaMonstruo) carta;
            if (cont == 1)
                agregarImagenTablero(c, R.id.mons_1_maq_carta, 2, !c.getisInAtaque(), c.getIsBocaArriba());
            else if (cont == 2)
                agregarImagenTablero(c, R.id.mons_2_maq_carta, 2, !c.getisInAtaque(), c.getIsBocaArriba());
            else if (cont == 3)
                agregarImagenTablero(c, R.id.mons_3_maq_carta, 2, !c.getisInAtaque(), c.getIsBocaArriba());

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
        if(partida.getRonda()%2 !=0) //para evitar hacer otra funcion, preguntar de ser necesario
            Utilitaria.crearToast(this, "La máquina ya ha llenado su tablero", false);

    }





    //METODOS
    public void inicioJuego(String title, String mensaje, Partida partida, Jugador j1, Jugador j2) {
        //Muestra una bienvenida -> realiza el sorteo de inicia -> y reparte las 5 cartas iniciales, tanto para el juego logico y virtual
        AlertDialog.Builder abd = new AlertDialog.Builder(this);
        abd.setTitle(title);
        abd.setMessage(mensaje);
        abd.setPositiveButton("OK", (dialog, b) -> {
            DialogoNombre();
        }
        );
        AlertDialog dialog = abd.create();
        dialog.show();

    }

    public void crearDialog(String title, String mensaje) {
        //Crea un dialogo normal
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
    public void DialogoNombre(){
            // Crear un EditText para la entrada del nombre
            EditText input = new EditText(this);
            input.setHint("Escribe tu nombre aquí");

            // Crear el cuadro de diálogo
            AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
            dialogo.setTitle("Añadiendo Jugador");
            dialogo.setMessage("Por favor, ingresa tu nombre:");
            dialogo.setView(input);

            // Botón "Aceptar"
            dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nombreUsuario = input.getText().toString().trim(); // Guarda el nombre
                    if (!nombreUsuario.isEmpty()) {
                        Utilitaria.crearToast(MainActivity.this, "¡Hola, " + nombreUsuario + "!",false);
                    } else {
                        Utilitaria.crearToast(MainActivity.this,"No ingresaste ningún nombre.",false);
                        nombreUsuario = "User_001";

                    }

                    j1.setNombre(nombreUsuario);
                    Jugador jinicial = partida.sorteoInicios(j1, j2);
                    String n = "" + j1.getDeck().getBaraja().size();
                    String m = "" + j2.getDeck().getBaraja().size();
                    deckuser.setText(n); //aparecen las cartas que tiene el dec "20"
                    deckmaq.setText(m);
                    dialog.dismiss();
                    crearDialog("Sorteo inicial", "El jugador que comienza es:\n-->" + jinicial.getNombre() + "\nPara continuar da clic en 'Iniciar Juego'");
                    repartirCartasIniciales();
                    restringir_movimientos(true, 1);
                    restringir_movimientos(true, 2);
                    dialog.dismiss();

                }
            });//no hay botón cancelar >:< lo obligamos a ponerse un nombre
            // Mostrar el diálogo
            dialogo.show();

    }

    public void repartirCartasIniciales() {
        //llamo al modelo para q reparta las cartas una por una en el tablero logico
        //y llama al metodo mostraImagenScroll paraq las cartas repartidas se muestren en la vista.

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
        // dependiendo del jugador y carta recibida, quita la carta visual de la mano del jugador
        // y llama al modelo para quitar la carta de la mano logica.
        tablero.quitarCartaMano(id_jugador, c);
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

    }

    public static void quitarImagenTablero(Carta carta, ConstraintLayout contenedor) {
        //Recibe un Constraint Layout y una carta , de acuerdo a eso quita la carta del constraint layout y
        //la deja en su forma por defecto.
        for (int i = contenedor.getChildCount() - 1; i >= 0; i--) {
            View cartita = contenedor.getChildAt(i);
            if (cartita.getTag() != null && cartita.getTag().equals(carta.getRutaImagen())) {
                ImageView v = (ImageView)cartita;
                int rId = R.drawable.imagen_base;
                v.setImageResource(rId);
                v.setTag("imagen_base.png");
                if (v.getRotation() !=0)
                    v.setRotation(0);//si está girada ya se puso correctamente
                v.setContentDescription("Casillero Disponible");
                    //se ha cambiado la imagen por la base (fondo blanco) y se ha guardado la ruta del view para poder compararla despues(setTag)
            }}

    }

    //}
    //preguntar como hacer establecer los guidelines de la imagen por codigo
    //

    public void pasarFase(View v){
        //puede representar al metodo ronda de java

        if (partida.getRonda()==1 || partida.getRonda()==2) {
            faseLLenarTableros();
            partida.cambiarTurno();

        }
        else{
            if(!j1.esDerrotado()&&!j2.esDerrotado()) {
                if ((partida.getRonda() % 2) != 0) {
                    if (partida.getRonda() == 3) {
                        Utilitaria.crearToast(this, "Desde este turno ya se puede declarar batalla", true);

                    }
                    faseLLenarTableros();
                    cambiarBotonFases("Fase Batalla","#FFCCCC",true);
                } else {
                    faseDeclararBatalla();
                    partida.cambiarTurno();
                    partida.resetearEstadoCartasMonstruo();
                    //ya pueden jugar con todas sus monstruos
                    if(!j1.esDerrotado()&&!j2.esDerrotado()) //valida el cambio de boton a 'Game Over'
                        cambiarBotonFases("Fase Principal","#CCCCFF",true);
                    else
                        cambiarBotonFases("Game Over","#FFCC99",true);

                }
            }
            else{
                if (j1.esDerrotado())

                    crearDialog("Game Over","Has perdido contra la máquina\nInténtalo de nuevo");
                else
                    //crearDialog("GAME OVER",j1.getNombre()+" ha ganado contra la máquina.\nFelicidades");
                    crearDialog("GAME OVER",j1.getNombre()+" ha ganado contra la máquina.\nFelicidades");
            }

        }
        partida.setRonda(partida.getRonda()+1);



    }

    public void faseLLenarTableros(){

        if (partida.getJugadorActual() == 2){
            if (partida.getRonda() <=2)
                cambiarBotonFases("Robar carta","#CCCCFF",true);
            j2.setNoAgregoMonstruo(true);
            restringir_movimientos(true,1);
            restringir_movimientos(false,2);
            crearDialog("Llenar Tablero","La Máquina ha robado una carta del deck y procede a llenar su tablero");
            Carta c = j2.tomarCartaEnTurno(deckmaq); //actualiza por detras al numero(view) de cartas del jugador, retorna y agrega la carta tomada a la mano logica
            llenartableromaquina(tablero,j2);
            mostrarImagenScroll(c,R.id.contenedorScrollAzul);

        }
        else{
            if (partida.getRonda() <=2 )
                cambiarBotonFases("Cambiar Turno","#CCCCFF",true);
            j1.setNoAgregoMonstruo(true);
            restringir_movimientos(false,1);
            restringir_movimientos(true,2);
            Carta c1 = j1.tomarCartaEnTurno(deckuser);
            crearDialog("Llenar Tablero",j1.getNombre()+" ha tomado una carta del deck y es su turno de llenar el tablero");
            mostrarImagenScroll(c1,R.id.contenedorScrollRojo);
        }


    }

    public void faseDeclararBatalla(){
        //solo de prueba
        Utilitaria.mensajeLargo(mensajes,"Testeto de los mensajes largos acá (interaccion con usuario), se puede utilizar esto o el toast",true);
        if (partida.getJugadorActual() ==1){
            crearDialog("DECLARAR BATALLA","Es tu turno de declarar batalla");
            setearMonstruosParaAtacar(j1, true); ///agregado Alan
        }
        else{
            j2.declararBatallaComoMaquina(tablero,j1,this,vidaUser,vidaMaq);
            llenartableromaquina(tablero,j2); //para que las cartas se pongan en modo defensa (cuando sea necesario)
            setearMonstruosParaAtacar(j1, false); //agregado Alan
        }
    }

    public void restringir_movimientos(boolean val,int id_jugador){
        //restringe la habilidad de dar 'click' del jugador recibido
        // no pueda hacer nada mientras no sea su turno
        //con true se restringen los movimientos
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
        //Recibe el linearLayout de un Scroll y una carta, crea la vista de la carta en el Scroll
        //ademas crea un onClick en la carta para luego poder ver su info o colocarla en el tablero visual  //OJO luego reparar para que las de la maquina no salgan el boton 'colocar'
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
        //Crea un dialogo personalizado para mostrar la info de la carta y poderla colocar en el tablero

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
        // Uso de un Inflater para el diseño personalizado
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
                aniadirCartaTableroFisico(carta, j1);
                dialog.dismiss();
            });
            //Desaparece el boton 'colocar' para las cartas del scroll de la maquina
            if(id_contenedor != R.id.contenedorScrollRojo) {
                dialogView.findViewById(R.id.btn_accept).setVisibility(View.GONE);
            }
            // Mostrar el diálogo
            dialog.show();

        }else{
            crearDialog("Error de programa", "El tamanio del scroll  no coincide con el logico");
        }
    }

    public void aniadirCartaTableroFisico(Carta c, Jugador j ){
        // valida si cierta carta se puede aniadir al tablero del jugador,
        // de acuerdo al tipo de carta realiza el proceso de aniadirla y crea la vista en el tablero


        //variables para actualizar el tablero logico
        Boolean[] isInAtaque = {null} ;
        Integer id_cartaMosntruoAEnlazar = null;
        try {
            if(tablero.sePuedeAgregarCarta(c, j)){  //funcion q valida si el jugador puede agregar la carta, basandose en las reglas del juego
                //validamos para cada caso de carta
                if(c instanceof CartaMonstruo){
                    //preguntamos si en ataque o defensa
                    Utilitaria.crearDialogSetPosicionCarta(resultado -> {
                        //agregamos la carta al tablero definiendola si va girada o no de acuerdo al modo
                        if(resultado){
                            buscarAgregarImagenTablero(c, findViewById(R.id.CartasMonstruoJugador), false, true);
                            Utilitaria.crearToast(this,"Carta en Modo Ataque añadida",false);
                            isInAtaque[0]= true;
                            //jugarCarta ya por detras setea PuedeAgregarMonstruo(false)
                            j.jugarCarta(partida, c, isInAtaque[0],null); //setea los atributos de la carta y la aniade al tablero logico

                        }else {
                            buscarAgregarImagenTablero(c, findViewById(R.id.CartasMonstruoJugador), true, false); //por detras aniade la carta al tablero logico
                            Utilitaria.crearToast(this,"Carta en Modo Defensa Aniadida",false);
                            isInAtaque[0] = false;
                            j.jugarCarta(partida, c, isInAtaque[0], null); //agrega la carta al tablero logico y la quita de la mano logica del jugador
                        }
                    }, this);

                } else if ( c instanceof  CartaMagica) {
                    //mostramos en un dialog las cartas disponibles a enlazar
                    crearDialogEnlazarCarta(c,j, findViewById(R.id.CartasEspecialesJugador)); //ya agrega por detras al tablero logico,etc


                } else if ( c instanceof  CartaTrampa) {
                    //ojo se deben agregar boca abajo?
                    buscarAgregarImagenTablero(c, findViewById(R.id.CartasEspecialesJugador), false, true);
                    j.jugarCarta(partida, c, null, null); //agrega la carta al tablero logico y la quita de la mano logica del jugador
                    Utilitaria.crearToast(this,"Carta Trampa Aniadida",false);
                }
            }


        } catch (AgregacionCartaMonstruoException | SinEspacioTableroException  | CartaMagicaAsociacionException e){
            crearDialog("Error",  e.getMessage());
        }



    }


    public void buscarAgregarImagenTablero(Carta cartaAAgregar, ConstraintLayout contenedorCartas, boolean vaGirada, boolean mostrar){
        //itera sobre los hijos del ConstraintLayout
        //y busca el espacio vacio(de acuerdo al contentDescription) donde pueda agregar la imagen y usar la funcion 'agregarImagenTablero()'
        //de la carta en el ConstraintLayout dado(tablero del jugador)
        //tambien recibe parametros para definir el giro y si la carta va boca abajo

        int totalHijos = contenedorCartas.getChildCount();

        for(int i = 0; i < totalHijos; i++){
            View hijo = contenedorCartas.getChildAt(i);
            if(hijo instanceof ImageView){
                ImageView imV = (ImageView)hijo;
                int idImage = imV.getId();
                if(imV.getContentDescription().equals("Casillero Disponible")){
                    agregarImagenTablero(cartaAAgregar, idImage, j1.getId(), vaGirada, mostrar);
                    return;
                }

            }
        }

    }

    public void agregarImagenTablero(Carta c, int id_view,int id_jugador , boolean vaGirada, boolean mostrar){
        // agrega la imagen de la carta recibida al View con el id recibido y define su posicion, quita la carta  de la mano visual del jugador.
        quitarImagenMano(c,id_jugador);
        ImageView v = findViewById(id_view); //
        Resources res = getResources(); //
        int rId = R.drawable.carta_bocaabajo;

        if(mostrar){
            rId = res.getIdentifier(c.getRutaImagen(), "drawable", getPackageName()); //
        }
        //
        v.setImageResource(rId); //
        v.setTag(c.getRutaImagen());//necesito identificarla para borrarla luego

        if(vaGirada) v.setRotation(-90f);
        //tablero.aniadirCartaTablero(c,id_jugador); se la puso en el metodo buscaragregarimagen por problemas con la maquina
        //cambiamos el estado a casillero ocupado
        v.setContentDescription("Casillero ocupado");
        Log.w("* AgregarImagenTablero", "se procedera a entrar a la funcion");
        //definimos su onCLick Listener para que cuando se toque aparezca sus datos
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearVentanaDescCarta(c, id_view);
            }
        });

    }

    public void crearVentanaDescCarta(Carta carta, int id_view){

        Log.w("* CrearVentanaDescCarta"," se obtendra el id del contenedor padre de cada imagen");
        //obtenemos el id del layout al que pertenece la view para luego validar y borrar ciertos botones
        ImageView imgCarta = findViewById(id_view);
        ConstraintLayout contenedorPadre =(ConstraintLayout)(imgCarta.getParent());
        int id_contenedor = contenedorPadre.getId();


        Log.w("* CrearVentanaDescCarta",getResources().getResourceName(id_contenedor));

        //creamos el dialogo personalizado
        // Uso de un Inflater para el diseño personalizado
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.datos_colocar_carta, null);
        TextView nombre = dialogView.findViewById(R.id.c_nombre);
        TextView descripcion = dialogView.findViewById(R.id.c_desc);

        Log.w("* CrearVentanaDescCarta"," validacion de cada instancia");

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
        Log.w("* CrearVentanaDescCarta"," seteamos los valores de toda carta");

        //setteamos los valores que toda carta tiene
        nombre.setText(carta.getNombre());
        descripcion.setText("Descripcion: " + carta.getDescripcion());

        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        Log.w("* CrearVentanaDescCarta"," cambiamos el texto del boton y listeners");

        //cambiamos el texto del boton a "Cambiar Posicion"
        Button btnAccept = dialogView.findViewById(R.id.btn_accept);
        btnAccept.setText("Cambiar Posicion");


        // Obtener referencia a los botones y configurar listeners
        AlertDialog dialog = builder.create();
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            dialog.dismiss(); // Cierrar el dialogo
        });

        dialogView.findViewById(R.id.btn_accept).setOnClickListener(v -> {
            //accciones al clicar 'Cambiar Posicion'

            dialog.dismiss();
        });

        Log.w("* CrearVentanaDescCarta"," validacion de contenedor padre y desapereciendo el boton");

        //Desaparece el boton 'colocar' para las cartas de los layouts de la maquina
        int idConstraintMonstruosJ = findViewById(R.id.CartasMonstruoJugador).getId();
        int idConstraintEspecialesJ = findViewById(R.id.CartasEspecialesJugador).getId();

        if(id_contenedor != idConstraintMonstruosJ && id_contenedor != idConstraintEspecialesJ) {
            dialogView.findViewById(R.id.btn_accept).setVisibility(View.GONE);
        } else if (carta instanceof CartaTrampa || carta instanceof CartaMagica) {
            dialogView.findViewById(R.id.btn_accept).setVisibility(View.GONE);
        }
        // Mostrar el diálogo
        dialog.show();

    }

    private int dpToPx(int dp) {
        // Metodo para convertir dp a px (para garantizar consistencia en diferentes pantallas)
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    public void crearDialogEnlazarCarta(Carta cartaMagica, Jugador jQEnlaza, ConstraintLayout contenedorCartasEspeciales ){

        CartaMagica cartaMagicaCasteada = (CartaMagica)cartaMagica;

        //de todas los mosntruos q hay en el tablero, clasifico solo las que coinciden con el tipoMonstruo de cartaMagica
        HashMap<String, Integer> dic = tablero.filtrarCartasMonstruoPorAtributo(cartaMagicaCasteada, jQEnlaza);

        //creo una array con las claves del dic(los nombres de las cartasMonstruo)
        String[] nombresMonstruos = dic.keySet().toArray(new String[0]);

        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enlazar carta monstruo:")
                .setSingleChoiceItems(nombresMonstruos, -1, new DialogInterface.OnClickListener() {
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
                            String nombreMonstruoSeleccion = nombresMonstruos[selectedPosition];
                            // Realizar acción con el nombre seleccionado (por ejemplo, enlazar)
                            Utilitaria.crearToast(MainActivity.this,"Enlazado con " + nombreMonstruoSeleccion,false);
                            buscarAgregarImagenTablero(cartaMagica, contenedorCartasEspeciales, false, true);  //OJO
                            //buscamos en el dic, el monstruo que eligio a enlazar y sacamos su id y actualizamos el tablero logico
                            int id_monstruoElegido = dic.get(nombreMonstruoSeleccion);
                            jQEnlaza.jugarCarta(partida, cartaMagica, null, id_monstruoElegido); //agrega por detras la carta al tablero logico y quita de la mano logica, ya setee la carta magica al mosntruo
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

    public void cambiarBotonFases(String texto, String colorEnHexa, boolean visible){

        Log.w("* cambiarBotonFases()", "Se ha ingresado a la funcion");
        //cambia el color, texto del boton de fases y su estado si se muestra o no
        Button btnFases = findViewById(R.id.button_fases);

        btnFases.setBackgroundColor(Color.parseColor(colorEnHexa));
        btnFases.setText(texto);

        if(!visible) btnFases.setVisibility(View.INVISIBLE);

    }

    public void setearMonstruosParaAtacar(Jugador j, boolean esFaseBatalla){
        //setea el onclick de los mosntruos en el tablero del jugador  dependiendo del booleano muestra el boton 'atacar' o no
        //para to do Monstruo en el tablero del jugador se les debe actualizar el onclick
        //iteramos en los elementos
        ConstraintLayout contenedorMonstruosJ = (ConstraintLayout)findViewById(R.id.CartasMonstruoJugador);
        int totalHijos = contenedorMonstruosJ.getChildCount();
        Log.w("* setearMonstruosParaAtacar", "hijos del constraintLayout: "+ totalHijos);


        for(int i = 0; i < totalHijos; i++){
            View hijo = contenedorMonstruosJ.getChildAt(i);
            if(hijo instanceof ImageView && hijo.getContentDescription().equals("Casillero ocupado")){
                ImageView imV = (ImageView) hijo;
                if(esFaseBatalla){
                    //actualizamos el onclick de los casilleros ocupados, para mostrar el boton de 'atacar'
                    imV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Nuevo comportamiento
                            Log.w("* setearMonstruosParaAtacar", "se ha cambiado el onClick del mosntruo para poder atacar");
                            crearDialogAtaque(v, j); //todo

                        }
                    });
                }
                else{
                    //acciones faseBatalla false

                    //se sobreescribe para que se muestre la ventana de solo para cambiar posicion y ya no atacar
                    imV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.w("* setearMonstruosParaAtacar()", "se setean la cartas para un onclick de cambiar posicion ");
                            String rutaCartaImagen = (String)v.getTag();
                            CartaMonstruo cm = tablero.buscarCartaMonstruoPorRuta(rutaCartaImagen, j); //devuelve la carta monstruo del tablero
                            crearVentanaDescCarta(cm, v.getId());
                        }
                    });
                }
            }
        }

        Log.w("* setearMonstruosParaAtacar()", "Fin de la funcion.");
    }

    public void crearDialogAtaque(View v, Jugador j){
        Log.w("* crearDialogAtaque()","Se ha ingresado en la funcion");
        String rutaCartaImagen = (String)v.getTag();
        Log.w("* crearDialogAtaque()", "ruta carta en la view imagen: "+ rutaCartaImagen);

        CartaMonstruo cm = tablero.buscarCartaMonstruoPorRuta(rutaCartaImagen, j); //devuelve la carta mosntruo del tablero

        //creamos el dialogo personalizado
        // Uso de un Inflater para el diseño personalizado
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.datos_colocar_carta, null);
        TextView nombre = dialogView.findViewById(R.id.c_nombre);
        TextView descripcion = dialogView.findViewById(R.id.c_desc);

        Log.w("* crearDialogAtaque"," seteando los datos..");
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
        nombre.setText(cm.getNombre());
        descripcion.setText("Descripcion: " + cm.getDescripcion());

        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        Log.w("* crearDialogAtaque"," cambiamos el texto del boton y listeners");

        //cambiamos el texto del boton a "Atacar"
        Button btnAccept = dialogView.findViewById(R.id.btn_accept);
        btnAccept.setText("Ejecutar ataque");


        // Obtener referencia a los botones y configurar listeners
        AlertDialog dialog = builder.create();
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(vi -> {
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_accept).setOnClickListener(vi -> {
            Log.w("* crearDialogAtaque","creando el dialogo de lista de ataque");
            //accciones al clicar 'Atacar'
            try {
                if(cm.validarAtaqueCarta()){
                    ArrayList<Carta> espacioEnemigo = tablero.getTableroCompartido().get(j2.getId()).get("CartasMonstruo");
                    if(espacioEnemigo.isEmpty()){
                        // Ataque directo
                        if (tablero.verificarCartaTrampa(j2, cm) != null) {
                            Utilitaria.crearToast(this, "Una carta Trampa detuvo el ataque de "+ cm.getNombre(), false);
                            CartaTrampa cartaTrampa = tablero.verificarCartaTrampa(j2, cm);
                            tablero.quitarCartaTablero(cartaTrampa, j2.getId());
                            cm.setPuedeAtacar(false);
                            quitarImagenTablero(cartaTrampa, contenedorEspecialesMaquina);
                        } else {
                            j.ataqueDirecto(j2, partida, cm, this, vidaMaq);
                        }
                    }
                    else {
                        mostrarListaParaAtacar(j2, cm, this);
                    }

                }
            } catch (MonstruoNoEnAtaqueException | MonstruoYaHaAtacadoException e) {
                crearDialog("Error", e.getMessage());
            }

            dialog.dismiss();
        });

        // Mostrar el diálogo
        dialog.show();

        Log.w("* crearDialogAtaque()"," fin de la funcion");

    }

    public void mostrarListaParaAtacar(Jugador maquina, CartaMonstruo monstruoAtacante, Context context){

        Log.w("* mostrarListaParaAtacar()","proceso de crear la lista de opciones de ataque");
        //accedo a las cartas Monstruo de maquina en el tablero
        ArrayList<Carta> monstruosTableroM = tablero.getTableroCompartido().get(maquina.getId()).get("CartasMonstruo");

        String[] nomMonstruos = new String[monstruosTableroM.size()];
        //itero en los mosntruos y lo agrego a un array de nombres
        for(int i = 0; i < nomMonstruos.length; i++){
            CartaMonstruo cm = (CartaMonstruo) monstruosTableroM.get(i);
            if(cm.getisBocaArriba()){
                nomMonstruos[i] = cm.getNombre();
            }else{
                nomMonstruos[i] = "Monstruo boca abajo";
            }
        }

        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atacar carta monstruo:")
                .setSingleChoiceItems(nomMonstruos, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Se puede capturar la selección aquí si es necesario
                    }
                })
                .setPositiveButton("Atacar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtener el nombre seleccionado
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if (selectedPosition != -1) {
                            String nombreMonstruoSeleccion = nomMonstruos[selectedPosition];
                            // Realizar acción con el nombre seleccionado
                            Utilitaria.crearToast(MainActivity.this,"Se ataco a: " + nombreMonstruoSeleccion,false);
                            CartaMonstruo cartaQSeAtaca = tablero.buscarCartaMonstruoPorNombre(nombreMonstruoSeleccion, j2);
                            Log.w("* mostrarListaParaAtacar()","llamando a la funcion declararbatalla()");
                            //actualizamos el tablero logico y por dentro eliminamos las vistas correpondientes
                            j1.declararBatalla(j2, partida, monstruoAtacante, cartaQSeAtaca, context, vidaMaq, vidaUser);

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
                        Utilitaria.crearToast(MainActivity.this, "Ataque cancelado",false);
                    }
                });

        // Crear y mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
        Log.w("* mostrarListaParaAtacar()","fin de la funcion");

    }






}
