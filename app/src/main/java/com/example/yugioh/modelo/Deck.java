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
    public ArrayList<Carta> cargarCartas(AssetManager am) throws IOException { //es el throw de problemas con el canal
        ArrayList<Carta> baraja = new ArrayList<>();
        //procesar cartasmonstruo
        ArrayList<Carta> cartasMonstruo = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(am.open("monstruos.txt")))) {
            String line;
            bf.readLine();
            while ((line = bf.readLine()) != null) {
                String[] lstDatos = line.split(", ");
                String nombre = lstDatos[0];
                String tipoMonstruo = lstDatos[1];
                String tipoAtributo = lstDatos[2];
                int ataque = Integer.parseInt(lstDatos[3]);
                int defensa = Integer.parseInt(lstDatos[4]);
                String descripcion = lstDatos[5];
                cartasMonstruo.add(new CartaMonstruo(nombre, descripcion, ataque, defensa, TipoAtributo.valueOf(tipoAtributo), TipoMonstruo.valueOf(tipoMonstruo)));
            }
        }
        barajear(cartasMonstruo);
        for (int i = 0; i < 10; i++) {
            baraja.add(cartasMonstruo.get(i));
        }


        //procesar cartasmagicas
        ArrayList<Carta> cartasMagicas = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(am.open("magicas.txt")))) {
            String linea;
            bf.readLine();
            while ((linea = bf.readLine()) != null) {
                String[] lstDatos = linea.split(", ");
                String nombre = lstDatos[0];
                String descripcion = lstDatos[1];
                int incrementoAtaque = Integer.parseInt(lstDatos[2]);
                int incrementoDefensa = Integer.parseInt(lstDatos[3]);
                String tipoMonstruo = lstDatos[4].strip();
                cartasMagicas.add(new CartaMagica(nombre, descripcion, incrementoAtaque, incrementoDefensa, TipoMonstruo.valueOf(tipoMonstruo)));

            }
        }
        barajear(cartasMagicas);
        for (int i = 0; i < 5; i++) {
            baraja.add(cartasMagicas.get(i));
        }

        //procesar cartastrampa
        ArrayList<Carta> cartasTrampa = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(am.open("trampa.txt")))) {
            bf.readLine(); // Salta la primera línea
            String linea;
            while ((linea = bf.readLine()) != null) {
                String[] lstDatos = linea.split(", ");
                String nombre = lstDatos[0];
                String descripcion = lstDatos[1];
                String atributo = lstDatos[2].strip();
                cartasTrampa.add(new CartaTrampa(nombre, descripcion, TipoAtributo.valueOf(atributo)));
            }
        }
        barajear(cartasTrampa);
        for (int i = 0; i < 5; i++) {
            baraja.add(cartasTrampa.get(i));
        }
        barajear(baraja);
        return baraja;
    }


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


