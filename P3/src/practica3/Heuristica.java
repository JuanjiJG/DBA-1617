/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica3;

import javafx.util.Pair;

/**
 *
 * @author Antonio Javier Benítez Guijarro
 * @author Emilio Manuel Chica Jiménez
 */
public class Heuristica {
    private Pair<Integer,Integer> subObjetivo;
    private BaseConocimiento linkbc;
    
    public EstadoAgente calcularSiguienteMovimiento(ArrayList<EstadoAgente> estados){
        EstadoAgente aux = new EstadoAgente();
        return aux;
    }
    
    //Puede que no este correcto. IMPORTANTE.
    private double calcularDistanciaEuclidea(Pair<Integer,Integer> posicion_posible){ 
        double d=0; 
        d = Math.sqrt(((subObjetivo.getValue() - posicion_posible.getValue())*(subObjetivo.getValue() - posicion_posible.getValue()))+((subObjetivo.getKey() - subObjetivo.getKey())*(subObjetivo.getKey() - subObjetivo.getKey()))); 
        return d; 
    }
    
    
    
    private boolean comprobarCasilla(){
    
        return false;
    }
}
