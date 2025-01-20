package com.example.yugioh.modelo.exceptions;

import com.example.yugioh.modelo.Carta;

public class CartaMagicaAsociacionException extends Exception {
    public CartaMagicaAsociacionException(Carta c) {
        super("La carta Magica "+ "'"+ c.getNombre()+"'"+ " no puede asociarse a ningun monstruo en el tablero.");
    }
}
