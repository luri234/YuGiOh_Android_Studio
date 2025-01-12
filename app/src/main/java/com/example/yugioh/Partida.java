package com.example.yugioh;
import java.util.Random;
public class Partida {
    private int turno;
    //private Tablero tablero;
    private int jugadorActual;
    private int ronda;
    //funcion cambiada para interaccion con el usuario
    public Jugador sorteoInicios(Jugador j1, Jugador j2) {
        Jugador[] jugadores = {j1, j2};
        Jugador jugadorEmpieza = jugadores[new Random().nextInt(jugadores.length)];
        this.turno = jugadorEmpieza.getId();
        this.jugadorActual = turno;
        return jugadorEmpieza;
    }

    public int getJugadorActual() {
        return jugadorActual;
    }
}
