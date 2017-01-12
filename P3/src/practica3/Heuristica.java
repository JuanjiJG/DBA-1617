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
    private final double UMBRAL = 5;
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
    
    public EstadoAgente buscandoObjetivo(ArrayList<EstadoAgente> estados){
        EstadoAgente agente_seleccionado;
        double minDistancia;
        ArrayList<Integer> indices_posibles = new ArrayList();
        int indice_agente_seleccionado = 0;
        
        
        
        if(this.subObjetivo == null){
            calcularSubObjetivo();//Analiza los cuadrantes y asigna un subObjetivo que sea un grupo de casillas inexploradas 3x3
        }
        
        minDistancia = calcularDistanciaEuclidea(estados.get(0).getPosicion(),this.subObjetivo);
        //Sacamos el agente que este mas cerca del objetivo, es decir, que su distancia hacia el objetivo sea la minima o que haya empatado con otro.
        for(int i = 1; i < estados.size(); i++){
            double distancia_aux = calcularDistanciaEuclidea(estados.get(i).getPosicion(),this.subObjetivo);
            
            if(distancia_aux < minDistancia){
                minDistancia = distancia_aux;
                indice_agente_seleccionado = i;
            }
        }
        
            
        for(int i = 0; i < estados.size(); i++){
            double distancia_aux = calcularDistanciaEuclidea(estados.get(i).getPosicion(),this.subObjetivo);
            if(distancia_aux <= minDistancia + this.UMBRAL ){
                indices_posibles.add(i);
            }            
        }  
        
        //Preferencia CAMION, AVION, COCHE.
        if(indices_posibles.size() > 0){
            int agente_seleccionado_empate = indices_posibles.get(0);
            for(int i =0; i < indices_posibles.size(); i++){
                switch(estados.get(indices_posibles.get(i)).getTipo()){
                    case TipoAgentes.CAMION :
                        if(estados.get(agente_seleccionado_empate).getTipo() != TipoAgentes.CAMION)
                            agente_seleccionado_empate = indices_posibles.get(i);
                        break;
                    case TipoAgentes.AVION :
                        if(estados.get(agente_seleccionado_empate).getTipo() == TipoAgentes.COCHE)
                            agente_seleccionado_empate = indices_posibles.get(i);                        
                        break;
                }
            }
            indice_agente_seleccionado = agente_seleccionado_empate;            
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        return aux;
    }
    
    //Puede que no este correcto. IMPORTANTE.
    private double calcularDistanciaEuclidea(Pair<Integer,Integer> posicion_objetivo,Pair<Integer,Integer> posicion_agente){ 
        double d=0; 
        d = Math.sqrt(((posicion_objetivo.getValue() - posicion_agente.getValue())*(posicion_objetivo.getValue() - posicion_agente.getValue()))+((posicion_objetivo.getKey() - posicion_agente.getKey())*(posicion_objetivo.getKey() - posicion_agente.getKey()))); 
        return d; 
    }
    
    //Calcula el numero de casillas inexploradas de cada cuadrante y elegimos el cuadrante con mas inexploradas y asignamos el nuevo objetivo.
    private void calcularSubObjetivo(){
        int mapa [][] = this.linkbc.getMapa();
        int cont_cuadrante_1 = 0;
        int cont_cuadrante_2 = 0;
        int cont_cuadrante_3 = 0;
        int cont_cuadrante_4 = 0;
        int maxCuadrante = 0;
        int cuadrante = 1;
        
        for(int i = 5; i < tamMapa; i++){
            for(int j = 5; j < tamMapa; j++){
                //Contamos el numero de casillas inexploras que hay en cada cuadrante. Los if filtran si los indices estan dentro de cada cuadrante.
                if((i>=this.cuadrante_1_i_inicio)&&(i<= this.cuadrante_1_i_final)&&(j>=this.cuadrante_1_j_inicio)&&(j<=this.cuadrante_1_j_final)){
                    if(mapa[i][j]==5)
                        cont_cuadrante_1++;
                }
                if((i>=this.cuadrante_2_i_inicio)&&(i<= this.cuadrante_2_i_final)&&(j>=this.cuadrante_2_j_inicio)&&(j<=this.cuadrante_2_j_final)){
                    if(mapa[i][j]==5)
                        cont_cuadrante_2++;
                }
                if((i>=this.cuadrante_3_i_inicio)&&(i<= this.cuadrante_3_i_final)&&(j>=this.cuadrante_3_j_inicio)&&(j<=this.cuadrante_3_j_final)){
                    if(mapa[i][j]==5)
                        cont_cuadrante_3++;
                }
                if((i>=this.cuadrante_4_i_inicio)&&(i<= this.cuadrante_4_i_final)&&(j>=this.cuadrante_4_j_inicio)&&(j<=this.cuadrante_4_j_final)){
                    if(mapa[i][j]==5)
                        cont_cuadrante_4++;
                }
            }
        }
        
        //Sacamos el cuadrante con mas ceros
        maxCuadrante = cont_cuadrante_1;
        
        
        if(maxCuadrante<cont_cuadrante_2){
            maxCuadrante = cont_cuadrante_2;
            cuadrante = 2;
        }
        
        if(maxCuadrante<cont_cuadrante_3){
            maxCuadrante = cont_cuadrante_3;
            cuadrante = 3;
        }
        
        if(maxCuadrante<cont_cuadrante_4){
            maxCuadrante = cont_cuadrante_4;
            cuadrante = 4;
        }
        
        int indice_i_inicio=0,indice_i_final=0,indice_j_inicio=0,indice_j_final=0;
        
        //Vamos a quitarle 6 a los limites porque la matriz es mas grande y no nos interesa asignar como objetivo los bordes del mapa.
        switch(cuadrante){
            case 1:
                indice_i_inicio = this.cuadrante_1_i_inicio + 6;
                indice_i_final = this.cuadrante_1_i_final;
                
                indice_j_inicio = this.cuadrante_1_j_inicio + 6;
                indice_j_final = this.cuadrante_1_j_final;
                
                break;
            case 2:
                indice_i_inicio = this.cuadrante_2_i_inicio;
                indice_i_final = this.cuadrante_2_i_final - 6;
                
                indice_j_inicio = this.cuadrante_2_j_inicio + 6;
                indice_j_final = this.cuadrante_2_j_final;
                break;
            case 3:
                indice_i_inicio = this.cuadrante_3_i_inicio + 6;
                indice_i_final = this.cuadrante_3_i_final;
                
                indice_j_inicio = this.cuadrante_3_j_inicio;
                indice_j_final = this.cuadrante_3_j_final -6;
                break;
            case 4:
                indice_i_inicio = this.cuadrante_4_i_inicio;
                indice_i_final = this.cuadrante_4_i_final -6;
                
                indice_j_inicio = this.cuadrante_4_j_inicio;
                indice_j_final = this.cuadrante_4_j_final -6;
                break;
        }
        
        //Se acabara el bucle cuando no haya un grupo de casillas inexploradas 3x3 o se haya encontrado un subObjetivo
        for(int i = indice_i_inicio; (i < indice_i_final)&&(this.subObjetivo != null); i++){
            for(int j = indice_j_inicio; (j < indice_j_final)&&(this.subObjetivo != null); j++){
                int contador = 0;
                if(mapa[i-1][j-1]==5)
                    contador++;
                if(mapa[i-1][j+1]==5)
                    contador++;
                if(mapa[i-1][j]==5)
                    contador++;                
                if(mapa[i][j-1]==5)
                    contador++;
                if(mapa[i][j]==5)
                    contador++;
                if(mapa[i][j+1]==5)
                    contador++;
                if(mapa[i+1][j]==5)
                    contador++;
                if(mapa[i+1][j-1]==5)
                    contador++;
                if(mapa[i+1][j+1]==5)
                    contador++;
                
                if(contador == 9)
                    this.subObjetivo = new Pair<Integer,Integer>(i,j);
            }
        }
    }
    
    
    private boolean comprobarCasilla(){
    
        return false;
    }
}
