package com.example.yugioh.modelo.exceptions;

import com.example.yugioh.modelo.Carta;

public class SinEspacioTableroException extends Exception{

    public SinEspacioTableroException(Carta c){
        super("No hay espacio en el tablero para agregar:  "+ c.getNombre());
    }
}
