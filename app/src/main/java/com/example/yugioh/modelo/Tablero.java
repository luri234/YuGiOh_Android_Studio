package com.example.yugioh.modelo;

import android.content.res.AssetManager;

import com.example.yugioh.MainActivity;
import com.example.yugioh.modelo.exceptions.AgregacionCartaMonstruoException;
import com.example.yugioh.modelo.exceptions.CartaMagicaAsociacionException;
import com.example.yugioh.modelo.exceptions.SinEspacioTableroException;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
//7
public class Tablero {
    public int id;
    public Jugador jugador1;
    public Jugador jugador2;
    public HashMap<Integer, HashMap<String, ArrayList<Carta>>> tableroCompartido;


    public Tablero(AssetManager am, String archivoCartas) throws IOException {
        this.id = 1;
        this.jugador1 = aniadirJugador(am, archivoCartas);
        this.jugador2 = new Jugador("Maquina", new Deck(am, archivoCartas));


        //creacion del tablero compartido
        tableroCompartido = new HashMap<>();

        HashMap<String, ArrayList<Carta>> espacioJugador1 = new HashMap<>();
        espacioJugador1.put("CartasMonstruo", new ArrayList<>());
        espacioJugador1.put("CartasEspeciales", new ArrayList<>());

        HashMap<String, ArrayList<Carta>> espacioJugador2 = new HashMap<>();
        espacioJugador2.put("CartasMonstruo", new ArrayList<>());
        espacioJugador2.put("CartasEspeciales", new ArrayList<>());

        tableroCompartido.put(jugador1.getId(), espacioJugador1);
        tableroCompartido.put(jugador2.getId(), espacioJugador2);
    }

    public Jugador aniadirJugador(AssetManager am, String archivoCartas) throws IOException {
        return new Jugador("JugadorPrueba", new Deck(am, archivoCartas));
    }

    public void aniadirCartaTablero(Carta carta, int idJugador) {
        if (carta instanceof CartaMonstruo) {
            if (tableroCompartido.get(idJugador).get("CartasMonstruo").size() < 3) {
                tableroCompartido.get(idJugador).get("CartasMonstruo").add(carta);
                // quitado por el android
                // System.out.println("+----Carta " + carta.getNombre() + " añadida");
                quitarCartaMano(idJugador, carta);
            }// else {

            //System.out.println("No se pudo incluir esa carta, espacio lleno");
            // }
        } else if (carta instanceof CartaMagica || carta instanceof CartaTrampa) {
            if (tableroCompartido.get(idJugador).get("CartasEspeciales").size() < 3) {
                tableroCompartido.get(idJugador).get("CartasEspeciales").add(carta);
                //System.out.println("+----Carta " + carta.getNombre() + " añadida");
                quitarCartaMano(idJugador, carta);
            } //else {
            //System.out.println("No se pudo incluir esa carta, espacio lleno");
            //}
        }
    }

    public void quitarCartaTablero(Carta carta, int idJugador) {
        if (carta instanceof CartaMonstruo) {
            tableroCompartido.get(idJugador).get("CartasMonstruo").remove(carta);
            if(idJugador==1)
                MainActivity.quitarImagenTablero(carta,MainActivity.contenedorMonstruosUser);
            else
                MainActivity.quitarImagenTablero(carta,MainActivity.contenedorMontruosMaquina);
        } else {
            tableroCompartido.get(idJugador).get("CartasEspeciales").remove(carta);
            if(idJugador==1)
                MainActivity.quitarImagenTablero(carta,MainActivity.contenedorEspecialesUser);
            else
                MainActivity.quitarImagenTablero(carta,MainActivity.contenedorEspecialesMaquina);
        }
    }

    public void quitarCartaMano(int idJugador, Carta carta) {
        if (idJugador == 1) {
            jugador1.getCartasEnMano().remove(carta);
        } else if (idJugador == 2) {
            jugador2.getCartasEnMano().remove(carta);
        }
    }

    //Revisado
    public boolean hayCartasMonstruoBocaArriba(Jugador jugador) {
        ArrayList<Carta> espacioMonstruosJ = tableroCompartido.get(jugador.getId()).get("CartasMonstruo");
        int i = 0;
        for (Carta cartaMonstruo : espacioMonstruosJ) {
            if (((CartaMonstruo) cartaMonstruo).getIsBocaArriba()) {
                i++;
            }
        }
        return i > 0;
    }

    //Revisado
    public boolean hayCartasMonstruoEnAtaque(Jugador jugador) {
        ArrayList<Carta> espacioMonstruosJ = tableroCompartido.get(jugador.getId()).get("CartasMonstruo");
        int i = 0;
        for (Carta cartaMonstruo : espacioMonstruosJ) {
            if (((CartaMonstruo) cartaMonstruo).getIsInAtaque()) {
                i++;
            }
        }
        return i > 0;
    }

    //Revisado
    public CartaTrampa verificarCartaTrampa(Jugador enemigo, CartaMonstruo cartaAtacante) {
        ArrayList<Carta> espacioEspecialesJ = tableroCompartido.get(enemigo.getId()).get("CartasEspeciales");
        CartaTrampa cartaEncontrada = null;
        for (Carta cartaEspecial : espacioEspecialesJ) {
            if (cartaEspecial instanceof CartaTrampa) {
                TipoAtributo atributoCarta = ((CartaTrampa) cartaEspecial).getTipoAtributo();
                if (atributoCarta.equals(cartaAtacante.getTipoAtributo())) {
                    cartaEncontrada = (CartaTrampa) cartaEspecial;
                }
            }
        }
        return cartaEncontrada;
    }

    //Revisado
    public void eliminarCartaMagicaAsociada(Jugador jugador, CartaMonstruo cm) {

        if (cm.getCartaMagica() != null) {
            int id_cartaMagicaAsociada = cm.getCartaMagica().getId();
            ArrayList<Carta> espacioCartasEspecialesJ = tableroCompartido.get(jugador.getId()).get("CartasEspeciales");
            int i = 0;
            Integer indiceCarta = null;
            for (Carta cartaEspecial : espacioCartasEspecialesJ) {
                int id_cartaEspecial = cartaEspecial.getId();
                if (cartaEspecial instanceof CartaMagica && id_cartaEspecial == id_cartaMagicaAsociada) {
                    indiceCarta = i;
                }
                i += 1;
            }
            Carta cartaAEliminar = espacioCartasEspecialesJ.get(indiceCarta);
            quitarCartaTablero(cartaAEliminar, jugador.getId());

        }
    }

    //Revisado
    public boolean validarAgregacionCartaMagica(CartaMagica cm, Jugador jugador) {

        ArrayList<Carta> espacioMonstruosJ = tableroCompartido.get(jugador.getId()).get("CartasMonstruo");
        int i = 0;
        for (Carta cartaMonstruo : espacioMonstruosJ) {
            if (((CartaMonstruo) cartaMonstruo).getTipoMonstruo() == cm.getTipoMonstruo()) {
                i += 1;
            }
        }
        return i > 0;
    }


    //revisada
    public Tupla ataqueEntreCartas(CartaMonstruo cartaJugador, CartaMonstruo cartaEnemigo, Jugador jugador, Jugador enemigo) {
        int jugadorID = jugador.getId();
        int enemigoID = enemigo.getId();


        //Existen cartas Trampa enemigas que impidan el ataque?
        if (verificarCartaTrampa(enemigo, cartaJugador) == null) {
            //No existen ->

            //1 Calculamos incrementos de ataque y defensa asociados
            int incAtkJugador = 0;
            //int incDefJugador = 0; //no se está utikizando
            int incAtkEnemigo = 0;
            int incDefEnemigo = 0;

            Integer danioRealAEnemigo = 0;  //inicializar con null?
            Integer danioRealAJugador = 0;

            //La carta enemiga tiene una carta magica asociada?
            if(cartaEnemigo.getCartaMagica() != null){
                //si tiene asociada -> se guarda sus incrementos de ATK y DEF
                incAtkEnemigo = cartaEnemigo.getCartaMagica().getIncrementoAtaque();
                incDefEnemigo = cartaEnemigo.getCartaMagica().getIncrementoDefensa();
            }
            //La carta del jugador tiene una carta magica asociada?
            if(cartaJugador.getCartaMagica() != null){
                incAtkJugador = cartaJugador.getCartaMagica().getIncrementoAtaque();
                //incDefJugador = cartaJugador.getCartaMagica().getIncrementoDefensa();
            }


            //Comprobamos si la carta enemigo esta en ataque
            if (cartaEnemigo.getIsInAtaque()) {

                //la carta de jugador es mas fuerte que la del enemigo?
                if ((cartaJugador.getAtaque() + incAtkJugador) > (cartaEnemigo.getAtaque() + incAtkEnemigo)) {
                    danioRealAEnemigo = (cartaJugador.getAtaque() + incAtkJugador) - (cartaEnemigo.getAtaque() + incAtkEnemigo);
                    danioRealAJugador = 0;
                    //formato salida
                    System.out.println("|| Choque de ataques ||     ||" + cartaJugador.getNombre() + " vs " + cartaEnemigo.getNombre() + "||");
                    System.out.println("\t " + cartaJugador.getAtaque() + " + " + incAtkJugador + "  -->  <--  " + cartaEnemigo.getAtaque() + " + " + incAtkEnemigo);
                    System.out.println("| " + cartaJugador.getNombre() + " destruyó a " + cartaEnemigo.getNombre() + " en batalla!");
                    quitarCartaTablero(cartaEnemigo, enemigoID);

                    eliminarCartaMagicaAsociada(enemigo, cartaEnemigo);
                    //tuve que tener una instancia de Main para poder llamar a los métodos acá

                    return new Tupla(danioRealAEnemigo, danioRealAJugador);
                }
                //la carta jugador y enemigo son iguales en fuerza:
                else if((cartaJugador.getAtaque() + incAtkJugador) == (cartaEnemigo.getAtaque() + incAtkEnemigo)){
                    danioRealAEnemigo = 0;
                    danioRealAJugador = 0;
                    System.out.println("|| Choque de ataques ||     ||" + cartaJugador.getNombre() + " vs " + cartaEnemigo.getNombre() + "||");
                    System.out.println("\t " + cartaJugador.getAtaque() + " + " + incAtkJugador + "  -->  <--  " + cartaEnemigo.getAtaque() + " + " + incAtkEnemigo);
                    System.out.println("| " + cartaJugador.getNombre() + " destruyó a " + cartaEnemigo.getNombre() + " en batalla!");
                    System.out.println("| " + cartaEnemigo.getNombre() + " destruyó a " + cartaJugador.getNombre() + " en batalla!");

                    quitarCartaTablero(cartaEnemigo, enemigoID);
                    eliminarCartaMagicaAsociada(enemigo, cartaEnemigo);
                    quitarCartaTablero(cartaJugador,jugadorID);

                    eliminarCartaMagicaAsociada(jugador, cartaJugador);

                    return new Tupla(danioRealAEnemigo, danioRealAJugador);

                }
                // la carta enemigo es mayor en fuerza a la del jugador:
                else{
                    danioRealAEnemigo = 0;
                    danioRealAJugador = (cartaEnemigo.getAtaque() + incAtkEnemigo) - (cartaJugador.getAtaque()+ incAtkJugador);
                    System.out.println("|| Choque de ataques ||     ||" + cartaJugador.getNombre() + " vs " + cartaEnemigo.getNombre() + "||");
                    System.out.println("\t " + cartaJugador.getAtaque() + " + " + incAtkJugador + "  -->  <--  " + cartaEnemigo.getAtaque() + " + " + incAtkEnemigo);
                    System.out.println("| " + cartaEnemigo.getNombre() + " destruyó a " + cartaJugador.getNombre() + " en batalla!");
                    quitarCartaTablero(cartaJugador, jugadorID);
                    eliminarCartaMagicaAsociada(jugador, cartaJugador);

                    return new Tupla(danioRealAEnemigo, danioRealAJugador);
                }
             //si la carta enemigo esta en defensa
            }else{
                if((cartaJugador.getAtaque() + incAtkJugador) >= (cartaEnemigo.getDefensa() + incDefEnemigo)){
                    //si el ataque del jugador es mayor a la defensa del enemigo
                    System.out.println("|| Ataque y defensa ||     ||" + cartaJugador.getNombre() + " vs " + cartaEnemigo.getNombre() + "||");
                    System.out.println("\t " + cartaJugador.getAtaque() + " + " + incAtkJugador + "  -->  <--  " + cartaEnemigo.getDefensa() + " + " + incDefEnemigo);
                    System.out.println("| " + cartaJugador.getNombre() + " destruyó a " + cartaEnemigo.getNombre() + " en batalla!");
                    quitarCartaTablero(cartaEnemigo, enemigoID);
                    eliminarCartaMagicaAsociada(enemigo, cartaEnemigo);
                    return new Tupla(0, 0);
                }else{
                    //la defensa del enemigo fue mayor
                    danioRealAJugador = (cartaEnemigo.getDefensa()+ incDefEnemigo) - (cartaJugador.getAtaque()+ incAtkJugador);
                    System.out.println("|| Ataque y defensa ||     ||" + cartaJugador.getNombre() + " vs " + cartaEnemigo.getNombre() + "||");
                    System.out.println("\t " + cartaJugador.getAtaque() + " + " + incAtkJugador + "  -->  <--  " + cartaEnemigo.getDefensa() + " + " + incDefEnemigo);
                    danioRealAEnemigo = 0;

                    //si la carta en defensa estaba boca abajo se cambia
                    if(!cartaEnemigo.getIsBocaArriba()) cartaEnemigo.setBocaArriba(false);
                    //no se destruye ningun monstruo
                    return new Tupla(danioRealAEnemigo, danioRealAJugador);
                }
            }
        }else{
            //si existe una carta trampa q impida el ataque
            CartaTrampa cartaTrampa = verificarCartaTrampa(enemigo, cartaJugador);
            System.out.println("|   Se detuvo el ataque! la carta Trampa " + "'"+cartaTrampa.getNombre()+"'"+ "se interpuso");
            quitarCartaTablero(cartaTrampa, enemigoID);
            if (enemigoID==1){MainActivity.quitarImagenTablero(cartaTrampa,MainActivity.contenedorEspecialesUser);}
            else
                MainActivity.quitarImagenTablero(cartaTrampa,MainActivity.contenedorEspecialesMaquina);
            return new Tupla(0, 0);
        }
   }

    //Metodo nuevo para android

    public boolean sePuedeAgregarCarta(Carta carta, Jugador j) throws AgregacionCartaMonstruoException, SinEspacioTableroException, CartaMagicaAsociacionException {
        //verifica si el jugador puede agregar cierta carta a su espacio y tambien si es que ya no ha agregado una carta mosntruo
        //verifica si la carta Magica se puede asociar a un mosntruo en el tablero
        int idJugador = j.getId();
        ArrayList<Carta> espacioCartasMonstruo = tableroCompartido.get(idJugador).get("CartasMonstruo");
        ArrayList<Carta> espacioCartasEspeciales = tableroCompartido.get(idJugador).get("CartasEspeciales");

        if (carta instanceof CartaMonstruo) {
            if (espacioCartasMonstruo.size() < 3 && j.getNoAgregoMonstruo()) {
                return true;
            } else if (!j.getNoAgregoMonstruo()) {
                throw new AgregacionCartaMonstruoException();
            } else if (!(espacioCartasMonstruo.size() < 3)) {
                throw new SinEspacioTableroException(carta);
            }

        } else if (carta instanceof CartaMagica) {
            CartaMagica cm = (CartaMagica) carta;
            if (!validarAgregacionCartaMagica(cm, j)) {
                throw new CartaMagicaAsociacionException(cm);
            } else if (!(espacioCartasEspeciales.size() < 3)) {
                throw new SinEspacioTableroException(cm);
            } else if (validarAgregacionCartaMagica(cm, j) && espacioCartasEspeciales.size() < 3) {
                return true;
            }
        } else {
            if (!(espacioCartasEspeciales.size() < 3)) {
                throw new SinEspacioTableroException(carta);
            } else {
                return true;
            }
        }
        return false;
    }
    
    //getters and setters
    public int getId() {
        return id;
    }
    
    public Jugador getJugador1() {
        return jugador1;
    }

    public void setJugador1(Jugador jugador1) {
        this.jugador1 = jugador1;
    }
    
    public Jugador getJugador2() {
        return jugador2;
    }
    
    public void setJugador2(Jugador jugador2) {
        this.jugador2 = jugador2;
    }

    public HashMap<Integer, HashMap<String, ArrayList<Carta>>> getTableroCompartido() {
        return tableroCompartido;
    }

    public void setTableroCompartido(HashMap<Integer, HashMap<String, ArrayList<Carta>>> tableroCompartido) {
        this.tableroCompartido = tableroCompartido;
    }
    
    
}
