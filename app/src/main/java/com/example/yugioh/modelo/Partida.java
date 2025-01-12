package com.example.yugioh.modelo;

import android.content.res.AssetManager;

import java.io.IOException;
import java.util.Random;
public class Partida {
    private int turno;
    private Tablero tablero;
    private int jugadorActual;
    private int ronda;

    //CONSTRUCTOR
    public Partida(AssetManager am, String archivoCartas) throws IOException {
        this.turno = 1;
        this.ronda = 1; //1 ronda es 2 turnos
        this.jugadorActual = 1; //por defecto
        this.tablero = new Tablero(am, archivoCartas);
    }
    //funcion cambiada para interaccion con el usuario
    public Jugador sorteoInicios(Jugador j1, Jugador j2) {
        Jugador[] jugadores = {j1, j2};
        Jugador jugadorEmpieza = jugadores[new Random().nextInt(jugadores.length)];
        this.turno = jugadorEmpieza.getId();
        this.jugadorActual = turno;
        return jugadorEmpieza;
    }

    //getter and setters
    public int getTurno() {
        return turno;
    }

    public void setTurno(int turno) {
        this.turno = turno;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    public int getJugadorActual() {
        return jugadorActual;
    }

    public void setJugadorActual(int jugadorActual) {
        this.jugadorActual = jugadorActual;
    }

    public int getRonda() {
        return ronda;
    }

    public void setRonda(int ronda) {
        this.ronda = ronda;
    }
}
