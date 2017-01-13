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
    private final double UMBRAL_EMPATE = 5;
    private final int UMBRAL_COMBUSTIBLE = 75;
    private double[][] distancias;
    private double gradiente_muro_encontrado=Double.MAX_VALUE;
    private boolean siguiendo_muro=false;
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
    
    
    private boolean necesitaRepostar(EstadoAgente agente){
        if(agente.getFuelActual() < UMBRAL_COMBUSTIBLE)
            return true;
        else 
            return false;
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
    
    //Por si falla segun dice antonio deberia de devolver EstadoAgente agente_seleccionado
    private void calcularSiguienteMovimmiento(EstadoAgente agente_seleccionado,Pair<Integer,Integer> posicion_agente, Pair<Integer,Integer> posicion_destino){
        Pair<Acciones,Pair<Integer,Integer>> casilla = calcularMejorCasilla(posicion_agente,posicion_destino);
        int i=casilla.getValue().getValue();//Invertimos las variables para nuestro mapa
        int j=casilla.getValue().getKey();//Invertimos las variables para nuestro mapa
        int mapa [][] = this.linkbc.getMapa();
        Acciones direccion = casilla.getKey();
        if()
        
        //Si es un muro o es un agente
        //Si ya esabamos siguiendo el muro o es la primera vez que lo encontramos
        if(mapa[i][j]==1 || mapa[i][j]==2 || mapa[i][j]==4 || siguiendo_muro){
                siguiendo_muro=true;
                switch(direccion){
                    case moveNW:
                        if(distancias[0][0] < gradiente_muro_encontrado)
                            gradiente_muro_encontrado = distancias[0][0];
                        if(mapa[i][j])
                        agente_seleccionado.setNextAction(moveW);
                        break;
                    case moveN:
                        gradiente_muro_encontrado = distancias[0][1];
                        agente_seleccionado.setNextAction(moveNW);
                        break;
                    case moveNE:
                        gradiente_muro_encontrado = distancias[0][2];
                        agente_seleccionado.setNextAction(moveN);
                        break;
                    case moveW:
                        gradiente_muro_encontrado = distancias[1][0];
                        agente_seleccionado.setNextAction(moveSW);
                        agente_seleccionado.setNextAction(moveS);
                        break;
                    case moveE:
                        gradiente_muro_encontrado = distancias[1][2];
                        agente_seleccionado.setNextAction(moveNE);
                        agente_seleccionado.setNextAction(moveN);
                        break;
                    case moveSW:
                        gradiente_muro_encontrado = distancias[2][0];
                        agente_seleccionado.setNextAction(moveS);
                        break;
                    case moveS:
                        gradiente_muro_encontrado = distancias[2][1];
                        agente_seleccionado.setNextAction(moveSE);
                        break;
                    case moveSE:
                        gradiente_muro_encontrado = distancias[2][2];
                        agente_seleccionado.setNextAction(moveE);
                        break;
                }
            
        }
        
    }
    
    //Devolveremos la direccion (norte sur, este...) donde teoricamente nos deberiamos mover si no hay obstaculos y la casilla que es el mapa del profesor.
    private Pair<Acciones,Pair<Integer,Integer>> calcularMejorCasilla(Pair<Integer,Integer> posicion_agente, Pair<Integer,Integer> posicion_destino){
        Pair<Integer,Integer> posicion_objetivo = new Pair(posicion_destino.getValue(),posicion_destino.getKey());
        //Cambiamos los indices para que concuerden con los del profesor.
        int j = posicion_agente.getKey();
        int i = posicion_agente.getValue();
        double minimo;
        Pair<Integer,Integer> resultado = null;
        Acciones direccion = null;
        
        
        distancias = new double[3][3];
        
        
        distancias[0][0] = calcularDistanciaEuclidea(new Pair(i-1,j-1),posicion_objetivo);
        distancias[0][2] = calcularDistanciaEuclidea(new Pair(i-1,j+1),posicion_objetivo);
        distancias[0][1] = calcularDistanciaEuclidea(new Pair(i-1,j),posicion_objetivo);
        
        distancias[1][2] = calcularDistanciaEuclidea(new Pair(i,j+1),posicion_objetivo); 
        distancias[1][0] = calcularDistanciaEuclidea(new Pair(i,j-1),posicion_objetivo);
        
        distancias[2][2] = calcularDistanciaEuclidea(new Pair(i+1,j+1),posicion_objetivo);
        distancias[2][1] = calcularDistanciaEuclidea(new Pair(i+1,j),posicion_objetivo);
        distancias[2][0] = calcularDistanciaEuclidea(new Pair(i+1,j-1),posicion_objetivo);
        
        minimo = distancias[0][0];
        //Puede fallar la conversion de los indices
        for(int c=0; c < 3; c++){
            for(int h=0; h<3; h++){
                if((c!=1)&&(h!=1)){
                    if(distancias[c][h]<minimo)
                    {
                        minimo = distancias[c][h];
                        resultado = new Pair(i+(c-1),j+(h-1));
                        int indice_i = c;
                        int indice_j = h;

                        if((indice_i == 0)&&(indice_j == 0))
                            direccion = Acciones.moveNW;
                        if((indice_i == 0)&&(indice_j == 1))
                            direccion = Acciones.moveN;
                        if((indice_i == 0)&&(indice_j == 2))
                            direccion = Acciones.moveNE;
                        if((indice_i == 1)&&(indice_j == 0))
                            direccion = Acciones.moveW;
                        if((indice_i == 1)&&(indice_j == 2))
                            direccion = Acciones.moveE;
                        if((indice_i == 2)&&(indice_j == 0))
                            direccion = Acciones.moveSW;
                        if((indice_i == 2)&&(indice_j == 1))
                            direccion = Acciones.moveS;
                        if((indice_i == 2)&&(indice_j == 2))
                            direccion = Acciones.moveSE;
                    }
                }
            }
        }
        
        return new Pair(direccion,resultado);        
    }
    
    public EstadoAgente buscandoObjetivo(ArrayList<EstadoAgente> estados,boolean tenemosFuelEnElMundo){
        EstadoAgente agente_seleccionado;
        double minDistancia;
        ArrayList<Integer> indices_posibles = new ArrayList();
        int indice_agente_seleccionado = 0;
        
        
        
        if(this.subObjetivo == null){
            calcularSubObjetivo();//Analiza los cuadrantes y asigna un subObjetivo que sea un grupo de casillas inexploradas 3x3
        }
        ///Elegimos al agente que esté más cerca del objetivo
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
            if(distancia_aux <= minDistancia + this.UMBRAL_EMPATE ){
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
        
        agente_seleccionado = estados.get(indice_agente_seleccionado);
        
        //Una vez seleccionado el agente que queremos comprobamos si hay fuel en el mundo
        //Si hay combustible lo hacemos repostar
        if(tenemosFuelEnElMundo){
            if(necesitaRepostar(agente_seleccionado)){
                agente_seleccionado.setNextAction(Acciones.refuel);

                return agente_seleccionado;
            }
        }else
        {
            //Sino combrobamos si tiene combustible el agente
            if(agente_seleccionado.getFuelActual()>0){
                
            }else{
                //Si tenemos agentes en el array eliminamos el agente seleccionado 
                //porque no tiene fuel y no hay fuel en el mundo por lo que ya no nos sirve
                if(estados.size()>0){
                    estados.remove(agente_seleccionado);
                    return buscandoObjetivo(estados,false);
                }else{ //Sino tenemos más agentes que podamos mover devolvemos un EstadoAgente a null porque ya solo queda hacer el logout 
                    //CONDICIÓN DE PARADA SI NO HEMOS ENCONTRADO EL OBJETIVO
                    return null;
                }
            }
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
