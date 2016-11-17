/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

import javafx.util.Pair;

/**
 * Esta clase contiene los atributos y métodos para el mapa de conocimiento del mundo
 * @author Emilio Chica Jiménez
 * @author Miguel Angel Torres López
 * @author Juan José Jiménez García
 * @author Antonio Javier Benitez Guijarro
 */
public class Mapa {
    
    private static final int TAMANIO_MAPA = 504;
    private static final int TAMANIO_RADAR = 5;
    
    private int[][] matriz_mapa;
    private int[][] matriz_radar;
    private double[][] matriz_scanner;
    //Si la posicion es null significa que no hemos encontrado el objetivo aún.
    private Pair<Integer, Integer> posicion_objetivo;
    private int antiguedad;
    
    /**
     * Constructor de la clase Mapa
     * @author Juan José Jiménez García
     */
    public Mapa() {
        
        this.matriz_mapa = new int[TAMANIO_MAPA][TAMANIO_MAPA];
        this.matriz_radar = new int[TAMANIO_RADAR][TAMANIO_RADAR];
        this.posicion_objetivo = null;
        antiguedad=-1;
        this.inicializarMapa();
    }
    
    /**
     * Método para obtener la posición del objetivo
     * @return La posición del objetivo
     * @author Juan José Jiménez García
     */
    public Pair<Integer,Integer> getPosicionObjetivo() {
        return this.posicion_objetivo;
    }
    
    /**
     * Método para actualizar la matriz mapa de conocimiento del mundo
     * @param posicion La posición en la que se encuentra el agente
     * @author Miguel Ángel Torres López
     * @author Antonio Javier Benítez Guijarro
     */
    public void actualizarMapa(Pair<Integer, Integer> posicion) {
        //System.out.println("Debug: he entrado en actualizar mapa");
        int posicion_inicial_x=posicion.getKey();
        int posicion_inicial_y=posicion.getValue();
        
        
        //System.out.println("Debug: antes del primer for de actualizar mapa");
        
        for(int i=0;i<TAMANIO_RADAR;i++)
        {
            for(int j=0;j<TAMANIO_RADAR;j++)
            {
                //System.out.println("Debug: He entrado en el segundo for de actualizar mapa");
                    //System.out.println("Debug: He entrado en el primer if del segundo for de actualizar mapa");
                    //System.out.println("Debug matriz mapa:"+matriz_mapa[posicion_inicial_i+i][posicion_inicial_j+j]);
                
                    //Nos interesa que primero actualice el mapa y despues compruebe si hay un 2
                    if(matriz_mapa[posicion_inicial_y+i][posicion_inicial_x+j]==3)
                    {
                        //System.out.println("Debug: He entrado en el segundo if del segundo for de actualizar mapa");
                        matriz_mapa[posicion_inicial_y+i][posicion_inicial_x+j]=matriz_radar[i][j];
                    }
                    //Compruebo si hay un 2
                    if(matriz_mapa[posicion_inicial_y+i][posicion_inicial_x+j] == 2){
                        this.posicion_objetivo= new Pair(posicion_inicial_x+2,posicion_inicial_y+2);
                    }
            }
        }
        
        if(matriz_mapa[posicion.getValue()+2][posicion.getKey()+2]!=2)
        {
            matriz_mapa[posicion.getValue()+2][posicion.getKey()+2]=antiguedad;
        }
        
        for(int i=0;i<TAMANIO_RADAR;i++)
        {
            for(int j=0;j<TAMANIO_RADAR;j++)
            {
                    System.out.print(matriz_mapa[posicion_inicial_y+i][posicion_inicial_x+j]+" ");
            }
            System.out.println();
        }
        
    }
    public boolean pisandoObjetivo(Pair<Integer, Integer> posicion){
        if(matriz_mapa[posicion.getValue()+2][posicion.getKey()+2]==2)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Método para inicializar la matriz mapa de conocimiento del mundo
     * @author Juan José Jiménez García
     */
    public void inicializarMapa() {
        
        for (int i = 0; i < TAMANIO_MAPA; i++) {
	    for (int j = 0; j < TAMANIO_MAPA; j++) {
		matriz_mapa[i][j] = 3;
	    }
	}
    }
    
    /**
     * Método para obtener la matriz del mapa de conocimiento del mundo
     * @return La matriz del mapa
     * @author Juan José Jiménez García
     */
    public int[][] devolverMapa() {
        
        return this.matriz_mapa;
    }
    
    /**
     * Método para encontrar el objetivo en una matriz radar
     * @param radar La matriz de 5x5 casillas en la que buscar el objetivo
     * @return Un booleano indicando si ha encontrado el objetivo o no
     * @author Juan José Jiménez García
     */
   /* public boolean buscarObjetivo(int[][] radar) {
        
        boolean encontrado = false;
        
        for (int i = 0; i < TAMANIO_RADAR; i++) {
            for (int j = 0; j < TAMANIO_RADAR; j++) {
            
                if (radar[i][j] == 2) {
                    encontrado = true;
                }
            }
        }
        
        return encontrado;
    }*/
    
    /**
     * Método para encontrar el objetivo en una matriz radar
     * @param radar La matriz de 5x5 casillas en la que buscar el objetivo
     * @return Un booleano indicando si ha encontrado el objetivo o no
     * @author Miguel Angel Torres Lopez
     * @author Antonio Javier Benitez Guijarro
     */
    public void setRadar(int[][] radar_percibido) {
        for(int i=0;i<TAMANIO_RADAR;i++)
        {
            for(int j=0;j<TAMANIO_RADAR;j++)
            {
                matriz_radar[i][j]=radar_percibido[i][j];
            }
        }
    }

    public void setMatriz_scanner(double[][] matriz_scanner) {
        this.matriz_scanner = matriz_scanner;
    }
    
    public double[][] getMatriz_scanner() {
        return matriz_scanner;
    }
    public int[][] getMatrizRadar()
    {
        return matriz_radar;
    }

    public int getAntiguedad() {
        return antiguedad;
    }
    
    public void decrementarAntiguedad(){
        this.antiguedad--;
    }
}
