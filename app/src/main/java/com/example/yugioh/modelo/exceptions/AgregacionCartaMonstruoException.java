package com.example.yugioh.modelo.exceptions;

public class AgregacionCartaMonstruoException extends  Exception{

    public AgregacionCartaMonstruoException(){
        super("Solo puede agregar una carta Monstruo por turno.");
    }
}
