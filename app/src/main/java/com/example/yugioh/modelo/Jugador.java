package com.example.yugioh.modelo;

import android.content.Context;
import android.text.Editable;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.yugioh.modelo.exceptions.AgregacionCartaMonstruoException;
import com.example.yugioh.modelo.exceptions.CartaMagicaAsociacionException;
import com.example.yugioh.modelo.exceptions.SinEspacioTableroException;

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
    public void llenarTableroMaquina(Tablero tablero) {
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
    }

    //metodo nuevo para android


    public void jugarCarta(Partida partida, Carta cartaSeleccionada, boolean isInAtaque) throws AgregacionCartaMonstruoException, CartaMagicaAsociacionException, SinEspacioTableroException {
        Tablero tablero = partida.getTablero();

        //VALIDAMOS LA AGREGACION DE LA CARTA
        if (tablero.sePuedeAgregarCarta(cartaSeleccionada, this)){
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

                setNoAgregoMonstruo(false);

            }
        }


    }
    public void declararBatallaComoMaquina(Tablero tablero, Jugador oponente, Context context) {
        //se incluyo context para poder enviar mensajes directos cuando se llame a la funcion en el main
        Utilitaria.crearToast(context,"La " + this.getNombre() + " te va a declarar batalla!! " + oponente.getNombre() + " más vale que estés preparadx ",true);

        // Obtener monstruos en ataque que pueden atacar
        ArrayList<CartaMonstruo> monstruosAtacantes = new ArrayList<>();
        ArrayList<CartaMagica> cartasMagicas = new ArrayList<>();
        ArrayList<CartaTrampa> cartasTrampa = new ArrayList<>();

        for (Carta carta : tablero.getTableroCompartido().get(this.getId()).get("CartasMonstruo")) {
            CartaMonstruo carta_c = (CartaMonstruo)carta;
            if (carta_c.getisInAtaque() && carta_c.getPuedeAtacar()) {
                monstruosAtacantes.add(carta_c);
            }
        }

        // Si no hay monstruos en ataque, cambiar a modo defensa
        if (monstruosAtacantes.isEmpty()) {
            Utilitaria.crearToast(context,"La " + this.getNombre() + " no tiene monstruos disponibles para atacar. Se coloca a la defensiva.",true);
            for (Carta carta : tablero.getTableroCompartido().get(this.getId()).get("CartasMonstruo")) {
                CartaMonstruo carta_c = (CartaMonstruo)carta;
                if (carta_c.getisInAtaque()) {
                    carta_c.setisInAtaque(noAgregoMonstruo);
                    carta_c.setBocaArriba(false);
                    Utilitaria.crearToast(context,this.getNombre() + " pone al monstruo " + carta.getNombre() + " en defensa.",false);
                }
            }
            return;
        }

        // Obtener monstruos del oponente
        ArrayList<Carta> monstruosOponente = tablero.getTableroCompartido().get(oponente.getId()).get("CartasMonstruo");

        // Obtener cartas mágicas y trampa
        for (Carta carta : tablero.getTableroCompartido().get(this.getId()).get("CartasEspeciales")) {
            if (carta instanceof CartaTrampa) {
                cartasTrampa.add((CartaTrampa) carta);
            } else if (carta instanceof CartaMagica) {
                cartasMagicas.add((CartaMagica) carta);
            }
        }

        // Función de carta mágica
        for (CartaMagica cartaMagica : cartasMagicas) {
            //if (tablero.validarAgregacionCartaMagica(cartaMagica, this)) {//esto ya ha sido validado en llenartableromaquina
                for (Carta monstruo : tablero.getTableroCompartido().get(this.getId()).get("CartasMonstruo")) {
                    CartaMonstruo monstruo_c = (CartaMonstruo)monstruo; //casting
                    if (monstruo_c.getTipoMonstruo() == cartaMagica.getTipoMonstruo()) {
                        if (cartaMagica.getIncrementoAtaque() > 0) {
                            //no se debe alterar el ataque de la carta base
                            //monstruo_c.setAtaque(monstruo_c.getAtaque() + cartaMagica.getIncrementoAtaque());
                            //en que momento se hace el incremento de ataque entonces?
                            Utilitaria.crearToast(context,this.getNombre() + " equipa '" + cartaMagica.getNombre() + "' a '" + monstruo_c.getNombre() + "', aumentando su ataque en " + cartaMagica.getIncrementoAtaque() + ".",false);
                            //monstruo_c.setCartaMagica(cartaMagica); en la lógica de llenar tablero ya se ha enlazado
                        } else if (cartaMagica.getIncrementoDefensa() > 0) {
                            //monstruo_c.setDefensa(monstruo_c.getDefensa() + cartaMagica.getIncrementoDefensa());
                            Utilitaria.crearToast(context,this.getNombre() + " equipa '" + cartaMagica.getNombre() + "' a '" + monstruo_c.getNombre() + "', aumentando su defensa en " + cartaMagica.getIncrementoDefensa() + ".",true);
                            //monstruo_c.setCartaMagica(cartaMagica);

                        }
                        //si se equipa no se elimina del tablero, solo se elimina si el monstruo q la equipo es eliminado
                        //tablero.quitarCartaTablero(cartaMagica, this.getId());
                        break;
                    }
                }

        }

        // Función de carta monstruo y carta trampa
        for (CartaMonstruo cartaAtacante : monstruosAtacantes) {
            CartaTrampa cartaTrampa = tablero.verificarCartaTrampa(oponente, cartaAtacante);
            if (cartaTrampa != null) {
                Utilitaria.crearToast(context,oponente.getNombre() + " activa la carta trampa '" + cartaTrampa.getNombre() + "' y detiene el ataque de '" + cartaAtacante.getNombre() + "'.",false);
                tablero.quitarCartaTablero(cartaTrampa, oponente.getId());
                MainActivity.quitarImagenTablero(cartaTrampa,MainActivity.contenedorEspecialesUser);

                continue;
            }

            if (monstruosOponente.isEmpty()) {
                Utilitaria.crearToast(context,cartaAtacante.getNombre() + " realiza un ataque directo",false);
                oponente.setPuntosVida(oponente.getPuntosVida() - cartaAtacante.getAtaque());
                cartaAtacante.setPuedeAtacar(false);
                Utilitaria.crearToast(context,this.getNombre() + " ha terminado su fase de batalla.",false);
                //System.out.println(this.getNombre() + " ha terminado su fase de batalla.");
            } else {
                //uso de for clasico para evitar errores.
                for(int i = 0; i < monstruosOponente.size(); i++){
                    CartaMonstruo cartaMonstruoRival = (CartaMonstruo)monstruosOponente.get(i);
                    //if
                    //obtenemos el incremento de ataque de la carta rival y de la propia

                    Integer incAtaqueCartaEleg =  (Integer)(cartaAtacante.obtenerIncrementosATKyDEF().obj1);
                    Integer incAtaqueCartaRival = (Integer)(cartaMonstruoRival.obtenerIncrementosATKyDEF().obj1);
                    if ((cartaMonstruoRival.getAtaque() + incAtaqueCartaRival) < (cartaAtacante.getAtaque() + incAtaqueCartaEleg)) {
                        Utilitaria.crearToast(context,cartaAtacante.getNombre() + " ataca a " + cartaMonstruoRival.getNombre() + "!",false);
                        //System.out.println("---+ " + cartaAtacante.getNombre() + " ataca a " + cartaMonstruoRival.getNombre() + "!");
                        Tupla tupla = tablero.ataqueEntreCartas(cartaAtacante, cartaMonstruoRival, this, oponente);
                        Integer danioAEnemigo = (Integer)(tupla.obj1);
                        Integer danioAJugador = (Integer)(tupla.obj2);
                        this.puntosVida -= danioAJugador;
                        oponente.puntosVida -= danioAEnemigo;
                        cartaAtacante.setPuedeAtacar(false);

                    } else {
                        Utilitaria.crearToast(context,"La Maquina ha decidido no usar a " + cartaAtacante.getNombre() + " para atacar a tu carta " + cartaMonstruoRival.getNombre() + " porque no le conviene.",true);
                    }
                }
            }
        }
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


    public void setPuntosVida(int puntosVida) {
        this.puntosVida = puntosVida;
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
}
