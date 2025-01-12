package com.example.yugioh;

public class Jugador {
    private String nombre;
    //momentaneamente hasta que se revise el códido en el sonar
    private int id;
    //metodo improvisado para avanzar con el android
    public Jugador(String nombre, int id){
        this.nombre = nombre;
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
