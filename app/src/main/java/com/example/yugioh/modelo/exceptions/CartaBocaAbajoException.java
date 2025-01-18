package com.example.yugioh.modelo.exceptions;

public class CartaBocaAbajoException extends Exception {
    public CartaBocaAbajoException() {

        super("Una carta boca abajo no se puede cambiar de posición");
    }
}
