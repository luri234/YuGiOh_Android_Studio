package com.example.yugioh.modelo;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Carta {
    //contador del numero de instancias
    public static int cartas = 0;

    private int id;
    private String nombre;
    private String descripcion;
    private String rutaImagen;

    //Constructor
    public Carta(String nombre, String descripcion) {
        cartas += 1;
        this.id = cartas;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.rutaImagen = "";
    }
    //metodos
    public static ArrayList<Carta> leerCartas(AssetManager am, String rutaArchivo) throws IOException {
        ArrayList<Carta> cartas = new ArrayList<Carta>();
        try(BufferedReader lector = new BufferedReader(new InputStreamReader(am.open(rutaArchivo)))){
            String line;
            while((line = lector.readLine())!= null){
                String[] datos = line.split(", "); //ojo puede dar error? pq hay lineas de diferentes tamnianos?
                String tipoCarta = datos[0];
                String nombreCarta = datos[1];
                //no olvidar parse int
                if(tipoCarta.equals("CartaMonstruo")){
                    TipoMonstruo tm = TipoMonstruo.valueOf(datos[2]);
                    TipoAtributo ta = TipoAtributo.valueOf(datos[3]);
                    int ataque = Integer.parseInt(datos[4]);
                    int defensa = Integer.parseInt(datos[5]);
                    String descripcion = datos[6];
                    String rutaImagen = datos[7];
                    CartaMonstruo cm = new CartaMonstruo(nombreCarta, descripcion, ataque, defensa, ta, tm);
                    cm.setRutaImagen(rutaImagen);
                    cartas.add(cm);

                } else if (tipoCarta.equals("CartaMagia")) {
                    String descripcion = datos[2];
                    int incAtk = Integer.parseInt(datos[3]);
                    int incDef = Integer.parseInt(datos[4]);
                    TipoMonstruo tm = TipoMonstruo.valueOf(datos[5]);
                    String rutaImagen = datos[6];
                    CartaMagica cm = new CartaMagica(nombreCarta, descripcion, incAtk, incDef, tm);
                    cm.setRutaImagen(rutaImagen);
                    cartas.add(cm);
                } else if (tipoCarta.equals("CartaTrampa")) {
                    String descripcion = datos[2];
                    TipoAtributo ta = TipoAtributo.valueOf(datos[3]);
                    String rutaImagen = datos[4];
                    CartaTrampa ct  = new CartaTrampa(nombreCarta, descripcion, ta);
                    ct.setRutaImagen(rutaImagen);
                    cartas.add(ct);
                }

            }
        }
        return cartas;
    }

    //getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }
}
