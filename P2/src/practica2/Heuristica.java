/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

import java.util.ArrayList;
import javafx.util.Pair;

/**
 *
 * @author Emilio Chica Jiménez
 * @author Miguel Angel Torres López
 */
public class Heuristica {

    private boolean objetivo_detectado;
    private int[][] mapa_heuristica;

    public Heuristica() {
        objetivo_detectado = false;
        this.mapa_heuristica = new int[504][504];
    }

    public Acciones calcularSiguienteMovimiento(Mapa mapa, Pair<Integer, Integer> posicion_coche_aux) {
        int pos  = 2;
        double[][] matriz_scanner = mapa.getMatriz_scanner();
       
        Pair<Integer, Integer> posicion_coche = new Pair(posicion_coche_aux.getKey() + 2, posicion_coche_aux.getValue() + 2);
        Acciones accion = Acciones.moveE;
        ArrayList<Acciones> acciones_posibles = comprobarAccionesPosibles(mapa, posicion_coche);
        if (mapa.getPosicionObjetivo() == null) {
            System.out.println("Debug: No he encontrado el objetivo y sigo con " + acciones_posibles.get(0));
            accion = acciones_posibles.get(0);
        } else if (acciones_posibles.size() > 1) {

            ArrayList<Double> distancias = new ArrayList();

            for (int i = 0; i < acciones_posibles.size(); ++i) {
                switch (acciones_posibles.get(i)) {
                    case moveSW:
                        distancias.add(matriz_scanner[pos + 1][pos + 1]);
                        break;
                    case moveS:
                        distancias.add(matriz_scanner[pos][pos + 1]);
                        break;
                    case moveW:
                        distancias.add(matriz_scanner[pos + 1][pos]);
                        break;
                    case moveNW:
                        distancias.add(matriz_scanner[pos + 1][pos - 1]);
                        break;
                    case moveN:
                        distancias.add(matriz_scanner[pos][pos - 1]);
                        break;
                    case moveNE:
                        distancias.add(matriz_scanner[pos - 1][pos - 1]);
                        break;
                    case moveE:
                        distancias.add(matriz_scanner[pos - 1][pos]);
                        break;
                    case moveSE:
                        distancias.add(matriz_scanner[pos - 1][pos + 1]);
                        break;
                }
                System.out.println("\nDebug: Accion posible: " + acciones_posibles.get(i) + " distancia: " + distancias.get(i) + " El objetivo esta: " + mapa.getPosicionObjetivo());
            }

            int indice_mejor_accion = 0;
            double min_distancia = distancias.get(0);
            for (int j = 0; j < distancias.size(); ++j) {
                if (min_distancia > distancias.get(j)) {
                    min_distancia = distancias.get(j);
                    indice_mejor_accion = j;
                }
            }
            accion = acciones_posibles.get(indice_mejor_accion);
        } else {
            accion = acciones_posibles.get(0);
        }

        return accion;
    }

    private void accionMasFavorable(Mapa mapa){
        
    }
    
    private double[][] matrizHibridaRadarScanner(Mapa mapa){
        double[][] matriz_hibrida = new double[5][5];
        int[][] matriz_radar = mapa.getMatrizRadar();
        double[][] matriz_scanner = mapa.getMatriz_scanner();
        for(int i =0;i<5;++i){
            for(int j =0;j<5;++j){
                double valor_gradiente = matriz_scanner[i][j];
                if(matriz_radar[i][j]==1)
                    valor_gradiente = Double.MAX_VALUE;
                matriz_hibrida[i][j] = valor_gradiente;
                    
            }
        }
        return matriz_hibrida;
    }
    
    
    public ArrayList<Acciones> comprobarAccionesPosibles(Mapa mapa, Pair<Integer, Integer> posicion_coche) {

        ArrayList<Acciones> actions = new ArrayList();
        Acciones[] acciones_posibles = {Acciones.moveSW, Acciones.moveS, Acciones.moveW, Acciones.moveNW, Acciones.moveN, Acciones.moveNE, Acciones.moveE, Acciones.moveSE};
        int[][] mapa_actual = mapa.devolverMapa();

        int[] casillas = new int[8];

        casillas[0] = mapa_actual[posicion_coche.getValue() + 1][posicion_coche.getKey() - 1];
        casillas[1] = mapa_actual[posicion_coche.getValue() + 1][posicion_coche.getKey()];
        casillas[2] = mapa_actual[posicion_coche.getValue()][posicion_coche.getKey() - 1];
        casillas[3] = mapa_actual[posicion_coche.getValue() - 1][posicion_coche.getKey() - 1];
        casillas[4] = mapa_actual[posicion_coche.getValue() - 1][posicion_coche.getKey()];
        casillas[5] = mapa_actual[posicion_coche.getValue() - 1][posicion_coche.getKey() + 1];
        casillas[6] = mapa_actual[posicion_coche.getValue()][posicion_coche.getKey() + 1];
        casillas[7] = mapa_actual[posicion_coche.getValue() + 1][posicion_coche.getKey() + 1];

        //x == getKey(), y == getValue()
        System.out.print("CASILLAS\n");
        for (int i = 0; i < casillas.length; ++i) {
            System.out.print(casillas[i] + " ");
        }
        if (casillas[0] == 0 || casillas[0] == 2) {
            actions.add(Acciones.moveSW);
        }
        if (casillas[1] == 0 || casillas[1] == 2) {
            actions.add(Acciones.moveS);
        }
        if (casillas[2] == 0 || casillas[2] == 2) {
            actions.add(Acciones.moveW);
        }
        if (casillas[3] == 0 || casillas[3] == 2) {
            actions.add(Acciones.moveNW);
        }
        if (casillas[4] == 0 || casillas[4] == 2) {
            actions.add(Acciones.moveN);
        }
        if (casillas[5] == 0 || casillas[5] == 2) {
            actions.add(Acciones.moveNE);
        }
        if (casillas[6] == 0 || casillas[6] == 2) {
            actions.add(Acciones.moveE);
        }
        if (casillas[7] == 0 || casillas[7] == 2) {
            actions.add(Acciones.moveSE);
        }

        //Solo cogemos las acciones con casilla negativa en el caso en el que no tengamos otra opción 
        //porque estoy rodeado de casillas negativas
        if (actions.size() == 0) {
            int indice = -1;
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < casillas.length; ++i) {
                if (casillas[i] != 1) {
                    if (max < casillas[i]) {
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
