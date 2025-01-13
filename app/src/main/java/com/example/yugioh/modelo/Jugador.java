package com.example.yugioh.modelo;

import com.example.yugioh.modelo.exceptions.AgregacionCartaMonstruoException;
import com.example.yugioh.modelo.exceptions.CartaMagicaAsociacionException;
import com.example.yugioh.modelo.exceptions.SinEspacioTableroException;

import java.util.ArrayList;

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
