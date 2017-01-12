/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica3;

import java.util.ArrayList;
import javafx.util.Pair;

/**
 *
 * @author Antonio Javier Benítez Guijarro
 * @author Emilio Manuel Chica Jiménez
 */
public class Heuristica {
    private Pair<Integer,Integer> subObjetivo;
    private BaseConocimiento linkbc;
    private int tamMapa;
    private int cuadrante_1_i_inicio;
    private int cuadrante_1_i_final;
    private int cuadrante_1_j_inicio;
    private int cuadrante_1_j_final;
    
    private int cuadrante_2_i_inicio;
    private int cuadrante_2_i_final;
    private int cuadrante_2_j_inicio;
    private int cuadrante_2_j_final;
    
    private int cuadrante_3_i_inicio;
    private int cuadrante_3_i_final;
    private int cuadrante_3_j_inicio;
    private int cuadrante_3_j_final;
    
    private int cuadrante_4_i_inicio;
    private int cuadrante_4_i_final;
    private int cuadrante_4_j_inicio;
    private int cuadrante_4_j_final;
    
    
    
    public Heuristica(){
        linkbc = BaseConocimiento.getInstance();
        
        tamMapa = linkbc.getTamMapa();
    }
    
    private void dividirCuadrantes(){
        this.cuadrante_1_i_inicio = 0;
        this.cuadrante_1_i_final = (tamMapa/2)-1;
        this.cuadrante_1_j_inicio = 0;
        this.cuadrante_1_j_final = (tamMapa/2)-1;
        
        
        this.cuadrante_2_i_inicio = tamMapa/2;
        this.cuadrante_2_i_final = tamMapa-1;
        this.cuadrante_2_j_inicio = 0;
        this.cuadrante_2_j_final = (tamMapa/2)-1;
        
        this.cuadrante_3_i_inicio = 0;
        this.cuadrante_3_i_final = (tamMapa/2)-1;
        this.cuadrante_3_j_inicio = tamMapa/2;
        this.cuadrante_3_j_final = tamMapa-1;
        
        this.cuadrante_4_i_inicio = tamMapa/2;
        this.cuadrante_4_i_final = tamMapa-1;
        this.cuadrante_4_j_inicio = tamMapa/2;
        this.cuadrante_4_j_final = tamMapa-1;
        
    }
    
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
    
    private void calcularSubObjetivo(){
    }
    
    
    private boolean comprobarCasilla(){
    
        return false;
    }
}
