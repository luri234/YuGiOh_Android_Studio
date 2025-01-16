package com.example.yugioh.modelo;

import android.content.res.AssetManager;

import java.io.IOException;
import java.util.ArrayList;
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
    public void cambiarTurno() {
        this.turno++;
        if (turno % 2 == 0) {
            this.jugadorActual = 2;
        } else {
            this.jugadorActual = 1;
        }
    }
    public void resetearEstadoCartasMonstruo() {
        //permite que en el siguiente turno el usuario y la maquina puedan jugar con todos sus monstruos
        //un monstruo solo puede atacar una vez por turno
        int idJ1 = tablero.getJugador1().getId();
        int idJ2 = tablero.getJugador2().getId();
        ArrayList<Carta> cartasMonstruoJ1 = tablero.getTableroCompartido().get(idJ1).get("CartasMonstruo");
        ArrayList<Carta> cartasMonstruoJ2 = tablero.getTableroCompartido().get(idJ2).get("CartasMonstruo");
        for (Carta carta : cartasMonstruoJ1) {
            CartaMonstruo carta_c = (CartaMonstruo)carta;
            carta_c.setPuedeAtacar(true);
        }
        for (Carta carta : cartasMonstruoJ2) {
            CartaMonstruo carta_c = (CartaMonstruo)carta;
            carta_c.setPuedeAtacar(true);
        }
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

    //logica para el juego, fase llenar tablero
}

