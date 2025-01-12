package com.example.yugioh.modelo;

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


    public boolean isNoAgregoMonstruo() {
        return noAgregoMonstruo;
    }


    public void setNoAgregoMonstruo(boolean noAgregoMonstruo) {
        this.noAgregoMonstruo = noAgregoMonstruo;
    }
}
