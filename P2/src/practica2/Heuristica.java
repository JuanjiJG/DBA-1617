/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

import javafx.util.Pair;

/**
 *
 * @author NeN
 */
public class Heuristica {
    private double calcularDistanciaEuclidea(){
        return 0;
    }
    public static acciones calcularSiguienteMovimiento(Mapa mapa,Pair<Integer,Integer> posicion_coche){
        acciones accion=acciones.MovE;
        return accion;
        
    }
    public static acciones[] comprobarAccionesPosibles(Mapa mapa,Pair<Integer,Integer> posicion_coche){
        acciones[] actions={acciones.MovE};
        
        return actions;
    }
}
