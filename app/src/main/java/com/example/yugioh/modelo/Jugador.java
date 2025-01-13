package com.example.yugioh.modelo;

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
    public Carta tomarCartaEnTurno() {
        Carta cartaTomada = this.deck.robarCarta();
        this.cartasEnMano.add(cartaTomada);
        return cartaTomada;
    }

    public void tomar5Cartas() {
        for (int i = 0; i < 5; i++) {
            tomarCartaEnTurno();
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
                    tablero.aniadirCartaTablero(carta, 2);

                }
            }
        }
    }

    //metodo nuevo para android


    public void jugarCarta(Partida partida, Carta cartaSeleccionada, boolean isInAtaque, CartaMonstruo cartaAAsociar) throws AgregacionCartaMonstruoException, CartaMagicaAsociacionException, SinEspacioTableroException {
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

                tablero.aniadirCartaTablero(cartaSeleccionada, this.id);
                setNoAgregoMonstruo(false);

            }

            // Si la carta es trampa
            else if (cartaSeleccionada instanceof CartaTrampa) {
                tablero.aniadirCartaTablero(cartaSeleccionada, this.id);
            }
            // Si la carta es mágica
            else if (cartaSeleccionada instanceof CartaMagica) {
                cartaAAsociar.setCartaMagica((CartaMagica) cartaSeleccionada);
                tablero.aniadirCartaTablero(cartaSeleccionada, this.id);
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
