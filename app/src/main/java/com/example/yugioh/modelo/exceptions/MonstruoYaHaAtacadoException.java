package com.example.yugioh.modelo.exceptions;

import com.example.yugioh.modelo.Carta;
import com.example.yugioh.modelo.CartaMonstruo;

public class MonstruoYaHaAtacadoException extends Exception {
    public MonstruoYaHaAtacadoException(CartaMonstruo c) {
        super("El monstruo "+"'"+c.getNombre()+"'"+" ya ha atacado en este turno");
    }
}
