package com.example.yugioh.modelo;
import android.content.res.AssetManager;

import com.example.yugioh.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    public static int decks = 0; //contador del numero de instancias

    //atributos
    private int id;
    private ArrayList<Carta> baraja;


    //constructor modificado porque necesita el assets
    public Deck(AssetManager am, String archivoCartas) throws IOException {
        decks += 1;
        this.id = decks;
        //comentando para probar otro metodo
        //this.baraja = (ArrayList<Carta>) cargarCartas(am);

        this.baraja = asignacionCartas(am, archivoCartas);
    }

    public void barajear(ArrayList<Carta> baraja2) {
        Collections.shuffle(baraja2);
    }
    //metodo cambiar cargas un poco cambiado por el android

    //metodo agregado por Android studio
    public ArrayList<Carta> asignacionCartas(AssetManager am, String nombreArchivo) throws IOException {
        //se encarga de asignar 10 cartas monstruo, 5 magicas y 5 trampa para la baraja principal

        ArrayList<Carta> barajaFinal = new ArrayList<>();
        int cantMonstruos = 0;
        int cantMagicas = 0;
        int cantTrampas = 0;

        ArrayList<Carta> listaPrincipal = Carta.leerCartas(am, nombreArchivo);
        barajear(listaPrincipal);

        for (Carta carta : listaPrincipal) {
            if (carta instanceof CartaMonstruo && cantMonstruos < 10) {
                barajaFinal.add(carta);
                cantMonstruos++;
            } else if (carta instanceof CartaMagica && cantMagicas < 5) {
                barajaFinal.add(carta);
                cantMagicas++;
            } else if (carta instanceof CartaTrampa && cantTrampas < 5) {
                barajaFinal.add(carta);
                cantTrampas++;
            }
        }
        barajear(barajaFinal);
        return barajaFinal;
    }

    public Carta robarCarta() {
        if (!baraja.isEmpty()) {
            return baraja.remove(0); // Elimina y devuelve la primera carta
        } else {
            return null; // Si la baraja está vacía, retorna null
        }
    }

    public ArrayList<Carta> getBaraja() {
        return baraja;
    }
}


