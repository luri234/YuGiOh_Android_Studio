package com.example.yugioh.modelo.exceptions;

import com.example.yugioh.modelo.CartaMonstruo;

public class MonstruoNoEnAtaqueException extends Exception {
    public MonstruoNoEnAtaqueException(CartaMonstruo cm) {
        super("El monstruo "+"'"+cm.getNombre()+"'"+" no esta en posicion de ataque");
    }
}
