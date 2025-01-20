package com.example.yugioh.modelo;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.example.yugioh.Utilitaria;
import com.example.yugioh.modelo.exceptions.AgregacionCartaMonstruoException;
import com.example.yugioh.modelo.exceptions.CartaMagicaAsociacionException;
import com.example.yugioh.modelo.exceptions.SinEspacioTableroException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import com.example.yugioh.MainActivity;
public class Jugador {
    public static int jugadores; //contador de instancias

    //atributos
    private int id;
    private int puntosVida;
    private String nombre;
    private Deck deck;
    private ArrayList<Carta> cartasEnMano;
    private boolean noAgregoMonstruo;  //indicador si en el turno agrego monstruo
    private boolean MaquinaCambiarViewUnico; //indica que la maquina debe cambiar de posicion su view(ya sea de vertical a horizontal,etc)
    private boolean isMaquinaDefensa;

    public Jugador(String nombre, Deck deck){
        jugadores += 1;
        this.id = jugadores;
        this.puntosVida = 4000;
        this.nombre = nombre;
        this.deck = deck;
        this.cartasEnMano = new ArrayList<>();
        this.noAgregoMonstruo = true;
    }

    //METODOS
    public Carta tomarCartaEnTurno(TextView deck) { //para que se actualce en el mainxml el numero del deck
        Carta cartaTomada = this.deck.robarCarta();
        this.cartasEnMano.add(cartaTomada);
        int num = Integer.parseInt(deck.getText().toString());
        int nuevo = num-1;
        deck.setText(String.valueOf(nuevo));
        return cartaTomada;
    }

    public void tomar5Cartas(TextView deck) {
        for (int i = 0; i < 5; i++) {
            tomarCartaEnTurno(deck);
        }
    }
    public void llenarTableroMaquina(Tablero tablero,Context context) {
        // mano_maquina sería la lista de cartas en mano del jugador
        ArrayList<Carta> manoMaquina = new ArrayList<>(tablero.getJugador2().getCartasEnMano());//debe ser una copiar para que no se modifique
        // Colocar cartas de monstruo

        for (Carta carta : manoMaquina) {
            if (carta instanceof CartaMonstruo) {
                if (this.noAgregoMonstruo) { // si no se ha agregado monstruo en ese turno
                    if (tablero.getTableroCompartido().get(2).get("CartasMonstruo").size() < 3) {
                        tablero.aniadirCartaTablero(carta, 2); // agrega la carta al tablero
                        setNoAgregoMonstruo(false); // ya tiene un monstruo agregado
                    }
                }
            } else { // si no es monstruo, será cualquier otra carta
                if (tablero.getTableroCompartido().get(2).get("CartasEspeciales").size() < 3) {
                    if (carta instanceof CartaMagica)
                        {
                            if(tablero.validarAgregacionCartaMagica((CartaMagica)carta, this)){
                                CartaMagica carta1 = (CartaMagica)carta;
                                for (Carta c:tablero.getTableroCompartido().get(2).get("CartasMonstruo")){
                                    CartaMonstruo c1 = (CartaMonstruo)c;
                                    if ((c1.getCartaMagica()==null) && (c1.getTipoMonstruo() == carta1.getTipoMonstruo()) ){
                                        tablero.aniadirCartaTablero(carta1, 2);
                                        c1.setCartaMagica(carta1);
                                        Utilitaria.crearToast(context,"La máquina ha enlazado '"+carta.getNombre()+"' al monstruo '"+c1.getNombre()+"'",false);
                                        if(carta1.getIncrementoAtaque()>0)
                                            Utilitaria.crearToast(context,c1.getNombre()+"aumenta su ataque: "+c1.getAtaque()+"--->" +(c1.getAtaque()+carta1.getIncrementoAtaque()),false);
                                        else
                                            Utilitaria.crearToast(context,c1.getNombre()+"aumenta su defensa: "+c1.getDefensa()+"--->" +(c1.getDefensa()+carta1.getIncrementoDefensa()),false);

                                        return;
                                    }}
                            }

                        }
                    else{
                        tablero.aniadirCartaTablero(carta,2);
                    }

                }
            }
        }
        //SalirDelModoDefensa(tablero);
    }




    public void jugarCarta(Partida partida, Carta cartaSeleccionada, Boolean isInAtaque, Integer id_monstruoAEnlazar) {
        //se encarga de hacer los respectivos ajustes de cada carta jugada y aniadirla al tablero logico.

        Tablero tablero = partida.getTablero();
        //seteamos las reglas del juego y aniadimos la carta al tablero logico

        // Si la carta es un monstruo
        if (cartaSeleccionada instanceof CartaMonstruo) {
            //convertimos la seleccion en una carta monstruo
            CartaMonstruo cartaMonstruoSelec = (CartaMonstruo)cartaSeleccionada;

            if (isInAtaque) {
                cartaMonstruoSelec.setisInAtaque(true);
                cartaMonstruoSelec.setBocaArriba(true);
            } else {
                cartaMonstruoSelec.setisInAtaque(false);
                cartaMonstruoSelec.setBocaArriba(false);
            }
            tablero.aniadirCartaTablero(cartaSeleccionada, this.id);
            setNoAgregoMonstruo(false);
        } else if (cartaSeleccionada instanceof CartaMagica) {
            //en este else se setea la carta magica a la carta del jugador
            CartaMagica cartaMagicaCasteada = (CartaMagica)cartaSeleccionada;
            tablero.buscarCartaMonstruo(id_monstruoAEnlazar, this).setCartaMagica(cartaMagicaCasteada); //devuelve la cartaMonstruoEncontrada y le setea la carta magica elegida
            tablero.aniadirCartaTablero(cartaSeleccionada, this.id);
        } else if (cartaSeleccionada instanceof CartaTrampa) {
            tablero.aniadirCartaTablero(cartaSeleccionada, this.id);
        }
    }
    public void declararBatallaComoMaquina(Tablero tablero, Jugador oponente, Context context, TextView vidauser,TextView vidamaq) {
        //se incluyo context para poder enviar mensajes directos cuando se llame a la funcion en el main
        ArrayList<Carta> MonstruosTablero = tablero.getTableroCompartido().get(this.id).get("CartasMonstruo");
        ArrayList<CartaMonstruo> monstruosAtacantes = new ArrayList<>();
        ArrayList<Carta> monstruosOponente = tablero.getTableroCompartido().get(oponente.getId()).get("CartasMonstruo");

        //valida si hay algún monstruo en el tablero(sin importar si está en defensa o ataque)
        if (MonstruosTablero.isEmpty()){
            Utilitaria.crearToast(context,"La Máquina no tiene monstruos en el tablero para atacar",false);
            return;
        }
        Utilitaria.crearToast(context,"La " + this.getNombre() + " podría declararte batalla!! ",true);
        for (Carta c:MonstruosTablero) {
            CartaMonstruo mons = (CartaMonstruo)c;
            if (mons.getisInAtaque() && mons.getPuedeAtacar())
                monstruosAtacantes.add(mons);
        }
        //se guarda el tamaño inicial para verificar si la máquina debe ponerse en modo defensa
        int sizeMaquina = new ArrayList<>(monstruosAtacantes).size(); //size de la copia(por medio a cuando se actualice)
        int sizeUser = new ArrayList<>(monstruosOponente).size(); // si funciona, no le muevas .Att: Mayra
        int contador=0;//modo defensa

        // Función de carta monstruo y carta trampa
        for (CartaMonstruo cartaAtacante : monstruosAtacantes) {
            CartaTrampa cartaTrampa = tablero.verificarCartaTrampa(oponente, cartaAtacante);
            Integer incAtk;
            Tupla incrementos = cartaAtacante.obtenerIncrementosATKyDEF();
            incAtk = (Integer)incrementos.obj1;

            if (cartaTrampa != null) {
                Utilitaria.crearToast(context,oponente.getNombre() + "niega el ataque con '" + cartaTrampa.getNombre() ,false);
                tablero.quitarCartaTablero(cartaTrampa, oponente.getId());
                cartaAtacante.setPuedeAtacar(false);//ejecutó un ataque directo, entonces ya no puede atacar
                continue; //pasa a la siguiente iteracion
            }

            if (monstruosOponente.isEmpty()) {
                Utilitaria.crearToast(context,cartaAtacante.getNombre() + " realiza un ataque directo",false);
                oponente.setPuntosVida(oponente.getPuntosVida() - cartaAtacante.getAtaque()-incAtk,vidauser);
                cartaAtacante.setPuedeAtacar(false);
            } else {
                //uso de for clasico para evitar errores.
                for(int i = 0; i < monstruosOponente.size(); i++){
                    CartaMonstruo cartaMonstruoRival = (CartaMonstruo)monstruosOponente.get(i);
                    if (convieneAtacar(cartaAtacante,cartaMonstruoRival)) {
                        //verifica que no haya atacado antes
                        if(cartaAtacante.getPuedeAtacar()) {
                            Utilitaria.crearToast(context, cartaAtacante.getNombre() + " ataca a " + cartaMonstruoRival.getNombre() + "!", false);
                            Tupla tupla = tablero.ataqueEntreCartas(cartaAtacante, cartaMonstruoRival, this, oponente, context);
                            Integer danioAEnemigo = (Integer) (tupla.obj1);
                            Integer danioAJugador = (Integer) (tupla.obj2);
                            //
                            this.setPuntosVida(this.getPuntosVida() - danioAJugador, vidamaq);//con este metodo lo actualizo en la interfaz
                            oponente.setPuntosVida(oponente.getPuntosVida() - danioAEnemigo, vidauser);
                            //
                            cartaAtacante.setPuedeAtacar(false);
                        }

                    } else {
                        Utilitaria.crearToast(context,"La Máquina no va usar " + cartaAtacante.getNombre() + " para atacar a " + cartaMonstruoRival.getNombre() + " porque no le conviene.",true);
                        contador++;
                    }
                }
                //verifica que ninguna carta de la maquina haya atacada aljugador

                if(contador==(sizeUser*sizeMaquina)){
                    Utilitaria.crearToast(context,"La máquina no ha podido declararte batalla y se pone en modo defensa",false);
                    this.setMaquinaCambiarViewUnico(true);
                    for (Carta cartita:MonstruosTablero){
                        CartaMonstruo monstruo = (CartaMonstruo) cartita;
                        monstruo.setisInAtaque(false);
                            //monstruo.setBocaArriba(true);
                            // }

                    //si ninguna carta pudo atacar significa que todas se deben poner en modo defensa
                    }


                }
            }
        }
    }
    //logica de la maquina (le conviene o no atacar)
    //metodo solo para la máquina
    public static boolean convieneAtacar(CartaMonstruo cartamaquina,CartaMonstruo cartauser){
        Integer incAtkMaq =0;
        Integer incAtkUser =0;
        Integer incDefMaq =0;
        Integer incDefUser =0;
        //verificamos si tienen carta magica
        if(cartamaquina.getCartaMagica()!=null)
        {
            Tupla incr = cartamaquina.obtenerIncrementosATKyDEF();
            incAtkMaq = (Integer)incr.obj1;
            incDefMaq =(Integer)incr.obj2;
        }
        if(cartauser.getCartaMagica()!=null)
        {
            Tupla incr = cartauser.obtenerIncrementosATKyDEF();
            incAtkUser = (Integer)incr.obj1;
            incDefUser =(Integer)incr.obj2;
        }
        if (cartauser.getisInAtaque()){
            return cartamaquina.getAtaque()+incAtkMaq >= cartauser.getAtaque()+incAtkUser;
        }
        else{
            return cartamaquina.getAtaque() + incAtkMaq >= cartauser.getDefensa() + incDefMaq;

        }
    }
    // le conviene o no a la maquina cambiar su carta de defensa (boca arriba) a ataque
    //se usa en fase llenar tablero
    //metodo solo para la maquina
    public static void SalirDelModoDefensa(Tablero tablero){
        //me devuelve las cartas que pueden atacar
        ArrayList<Carta> MonstruosUser = tablero.getTableroCompartido().get(1).get("CartasMonstruo");
        ArrayList<Carta> MonstruosMaq = tablero.getTableroCompartido().get(2).get("CartasMonstruo");
        for(Carta car:MonstruosMaq){
            CartaMonstruo mons = (CartaMonstruo) car;
            for(Carta oponente:MonstruosUser){
                CartaMonstruo mons_op = (CartaMonstruo) oponente;
                if(mons.getIsBocaArriba() && convieneAtacar(mons,mons_op)){ //por si acaso se verifica que esté boca arriba xd
                    mons.setisInAtaque(true);
            }
        }}
    }

    //todo una funcion para ataque directos
    public void ataqueDirecto(Jugador enemigo, Partida partida, CartaMonstruo cartaMonstruoSelec, Context context, TextView vidaMaquina) {
        Tablero tablero = partida.getTablero();
        // Ataque sin trampa
        //obtenemos los INC de ataque de la carta monstruo
        Integer incAtkJugador;
        Tupla tIncrementos =  cartaMonstruoSelec.obtenerIncrementosATKyDEF();
        incAtkJugador = (Integer)tIncrementos.obj1;

        int danio = cartaMonstruoSelec.getAtaque() + incAtkJugador;
        Utilitaria.crearToast(context, "Ataque directo a maquina! con "+  cartaMonstruoSelec.getNombre(), false);
        enemigo.setPuntosVida(enemigo.getPuntosVida() - danio, vidaMaquina);
        cartaMonstruoSelec.setPuedeAtacar(false);
    }

    public void declararBatalla(Jugador enemigo, Partida partida, CartaMonstruo cartaMonstruoSelec, CartaMonstruo cartaASerAtacada, Context context, TextView vidaMaquina, TextView vidaUser) {
        Log.w("declararBatalla()", "inicio de la funcion");
        Tablero tablero = partida.getTablero();
        ArrayList<Carta> espacioCartasMonstruoJ = tablero.getTableroCompartido().get(this.id).get("CartasMonstruo");
        ArrayList<Carta> espacioEnemigo = tablero.getTableroCompartido().get(enemigo.id).get("CartasMonstruo");

        //Verificar cartas Trampa ya esta en el ataqueEntreCartas()

        // Ejecutar el ataque
        Integer danioAEnemigo = 0;
        Integer danioAJugador = 0;
        //se devuelve una tupla
        Tupla tupla = tablero.ataqueEntreCartas(cartaMonstruoSelec, cartaASerAtacada, this, enemigo, context);
        //descomprimimos la tupla
        danioAEnemigo =(Integer)tupla.getObj1();
        danioAJugador = (Integer)tupla.getObj2();

        // Actualizar vida de los jugadores
        this.setPuntosVida(this.getPuntosVida() - danioAJugador,vidaUser );
        enemigo.setPuntosVida(enemigo.getPuntosVida() - danioAEnemigo, vidaMaquina);
        cartaMonstruoSelec.setPuedeAtacar(false);

        Log.w("declararBatalla()", "fin de la funcion");
    }





    public boolean esDerrotado() {
        boolean sinCartasEnMano = getCartasEnMano().isEmpty();
        boolean sinCartasEnDeck = getDeck().getBaraja().isEmpty();
        boolean sinVida = getPuntosVida() <= 0;

        return sinVida || (sinCartasEnMano && sinCartasEnDeck);
    }



    //getters and setters
    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public int getPuntosVida() {
        return puntosVida;
    }


    public void setPuntosVida(int puntosVida,TextView puntosVidaJugador) {
        //modificada para que también actualice el xml
        this.puntosVida = puntosVida;
        String n = "PUNTOS DE VIDA: "+puntosVida;
        puntosVidaJugador.setText(n);


    }


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public Deck getDeck() {
        return deck;
    }


    public void setDeck(Deck deck) {
        this.deck = deck;
    }


    public ArrayList<Carta> getCartasEnMano() {
        return cartasEnMano;
    }


    public void setCartasEnMano(ArrayList<Carta> cartasEnMano) {
        this.cartasEnMano = cartasEnMano;
    }


    public boolean getNoAgregoMonstruo() {
        return noAgregoMonstruo;
    }


    public void setNoAgregoMonstruo(boolean noAgregoMonstruo) {
        this.noAgregoMonstruo = noAgregoMonstruo;
    }
    public void setMaquinaCambiarViewUnico(boolean HayQueCambiarPosicion){
        this.MaquinaCambiarViewUnico = HayQueCambiarPosicion;
    }
    public boolean getMaquinaCambiarViewUnico(){
        return this.MaquinaCambiarViewUnico;
    }

    public boolean getisMaquinaDefensa() {
        return isMaquinaDefensa;
    }
    public void setisMaquinaDefensa(boolean estaEnDefensa){
        this.isMaquinaDefensa = estaEnDefensa;
    }

}
