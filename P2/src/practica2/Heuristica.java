/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import javafx.util.Pair;

/**
 *
 * @author Emilio Chica Jiménez
 * @author Miguel Angel Torres López
 */
public class Heuristica {
    
    private static double calcularDistanciaEuclidea(Pair<Integer,Integer> posicion_objetivo,Pair<Integer,Integer> posicion_posible){
        double d=0;
        d = Math.sqrt(((posicion_objetivo.getValue() - posicion_posible.getValue())*(posicion_objetivo.getValue() - posicion_posible.getValue()))+((posicion_objetivo.getKey() - posicion_objetivo.getKey())*(posicion_objetivo.getKey() - posicion_objetivo.getKey())));
        return d;
    }
    
    public static Acciones calcularSiguientemoveimiento(Mapa mapa,Pair<Integer,Integer> posicion_coche){
        
        Acciones accion = Acciones.moveE;
        ArrayList<Acciones> acciones_posibles = comprobarAccionesPosibles(mapa,posicion_coche);
        
        if(acciones_posibles.size()>1){
            
            ArrayList<Double> distancias = new ArrayList();
            
            for(int i=0;i<acciones_posibles.size();++i){
                
                Pair<Integer,Integer> posicion_posible =  new Pair(0,0);
                
                switch(acciones_posibles.get(i)){
                    case moveSW:
                        posicion_posible= new Pair(posicion_coche.getKey()+1,posicion_coche.getValue()-1);
                        break;
                    case moveS:
                        posicion_posible= new Pair(posicion_coche.getKey()+1,posicion_coche.getValue());
                        break;
                    case moveW:
                        posicion_posible= new Pair(posicion_coche.getKey(),posicion_coche.getValue()-1);
                        break;
                    case moveNW:
                        posicion_posible= new Pair(posicion_coche.getKey()-1,posicion_coche.getValue()-1);
                        break;
                    case moveN:
                        posicion_posible= new Pair(posicion_coche.getKey()-1,posicion_coche.getValue());
                        break;
                    case moveNE:
                        posicion_posible= new Pair(posicion_coche.getKey()-1,posicion_coche.getValue()+1);
                        break;
                    case moveE:
                        posicion_posible= new Pair(posicion_coche.getKey(),posicion_coche.getValue()+1);
                        break;
                    case moveSE:
                        posicion_posible= new Pair(posicion_coche.getKey()+1,posicion_coche.getValue()+1);
                        break;
                }
                
                distancias.add(calcularDistanciaEuclidea(mapa.getPosicionObjetivo(),posicion_posible));
            }
            
            int indice_mejor_accion=-1;
            double min_distancia=distancias.get(0);
            
            for(int j=0;j<distancias.size();++j){
                
                if(min_distancia>distancias.get(j)){
                    
                    min_distancia = distancias.get(j);
                    indice_mejor_accion = j;
                }
            }
            
            accion = acciones_posibles.get(indice_mejor_accion);
        }
        else{
            accion=acciones_posibles.get(0);
        }
        
        return accion;
    }
    
    public static ArrayList<Acciones> comprobarAccionesPosibles(Mapa mapa,Pair<Integer,Integer> posicion_coche){
        
        ArrayList<Acciones> actions=new ArrayList();
        Acciones[] acciones_posibles ={Acciones.moveSW,Acciones.moveS,Acciones.moveW,Acciones.moveNW,Acciones.moveN,Acciones.moveNE,Acciones.moveE,Acciones.moveSE}; 
        int[][] mapa_actual = mapa.devolverMapa();
        
        int []casillas = new int[8];
        casillas[0] = mapa_actual[posicion_coche.getKey()+1][posicion_coche.getValue()-1];
        casillas[1]=mapa_actual[posicion_coche.getKey()+1][posicion_coche.getValue()];
        casillas[2]=mapa_actual[posicion_coche.getKey()][posicion_coche.getValue()-1];;
        casillas[3]=mapa_actual[posicion_coche.getKey()-1][posicion_coche.getValue()-1];
        casillas[4]=mapa_actual[posicion_coche.getKey()-1][posicion_coche.getValue()];
        casillas[5]=mapa_actual[posicion_coche.getKey()-1][posicion_coche.getValue()+1];
        casillas[6]=mapa_actual[posicion_coche.getKey()][posicion_coche.getValue()+1];
        casillas[7]=mapa_actual[posicion_coche.getKey()+1][posicion_coche.getValue()+1];
        
        if(posicion_coche.getKey() != 500 && posicion_coche.getValue() != 0 && (casillas[0]==0 || casillas[0]==2)){
                actions.add(Acciones.moveSW);
        }
        if(posicion_coche.getKey() != 500 && (casillas[1]==0 || casillas[1]==2)){
                 actions.add(Acciones.moveS);
        }
        if(posicion_coche.getValue() != 0 && (casillas[2]==0 || casillas[2]==2)){
                 actions.add(Acciones.moveW);
        }
        if(posicion_coche.getKey() != 0 && posicion_coche.getValue() != 0 && (casillas[3]==0 || casillas[3]==2)){
                 actions.add(Acciones.moveNW);
        }   
        if(posicion_coche.getKey() != 0 && (casillas[4]==0 || casillas[4]==2)){
                 actions.add(Acciones.moveN);
        }
        if(posicion_coche.getKey() != 0 && posicion_coche.getValue() != 500 && (casillas[5]==0 || casillas[5]==2)){
                 actions.add(Acciones.moveNE);
        }
        if(posicion_coche.getValue() != 500 && (casillas[6]==0 || casillas[6]==2)){
                 actions.add(Acciones.moveE);
        }
        if(posicion_coche.getKey() != 500 && posicion_coche.getValue() != 500 && (casillas[7]==0 || casillas[7]==2)){
                 actions.add(Acciones.moveSE);
        }
        
        //Solo cogemos las acciones con casilla negativa en el caso en el que no tengamos otra opción
        if(actions.size()==0){
            int indice=-1;
            int max = Integer.MIN_VALUE;
            for(int i=0;i<casillas.length;++i){
                 if(casillas[i]!=1){
                     if(max<casillas[i]){
                         max = casillas[i];
                         indice = i;
                     }
                 }
            }
            
            actions.add(acciones_posibles[indice]);
        }
       
        return actions;
    }
}
