package practica2;

import java.util.ArrayList;
import javafx.util.Pair;

/**
 * Clase para los métodos de la heurística
 *
 * @author Emilio Chica Jiménez
 * @author Miguel Angel Torres López
 * @author Gregorio Carvajal Expósito
 */
public class Heuristica {

    private static final int TAMANIO_MAPA = 504;
    private int[][] mapa_heuristica;

    /**
     * Constructor de la clase Heuristica
     *
     * @author Emilio Chica Jiménez
     */
    public Heuristica() {
        mapa_heuristica = new int[TAMANIO_MAPA][TAMANIO_MAPA];
        inicializarMapa();
    }

    /**
     * Inicializa el mapa de la heuristica con los valores adecuados
     *
     * @author Emilio Chica Jiménez
     */
    private void inicializarMapa() {
        for (int i = 0; i < 504; ++i) {
            for (int j = 0; j < 504; ++j) {
                // Inicializo los bordes a -1
                if (i < 1 || j < 1 || i > 500 || j > 500) {
                    mapa_heuristica[i][j] = -1;
                } else {
                    mapa_heuristica[i][j] = 3;
                }
            }
        }
    }

    /**
     * Calcula el siguiente mejor movimiento teniendo en cuenta los movimientos
     * posibles y los datos del scanner
     *
     * @author Emilio Chica Jiménez
     * @author Miguel Angel Torres López
     * @param mapa Se usa para comprobar las acciones desde la posicion del
     * coche en el mapa
     * @param posicion_coche_aux La posicion actual del coche
     * @return Devuelve la mejor acción
     */
    public Acciones calcularSiguienteMovimiento(Mapa mapa, Pair<Integer, Integer> posicion_coche_aux) {

        int pos = 2;
        double[][] matriz_scanner = mapa.getMatrizScanner();

        Pair<Integer, Integer> posicion_coche = new Pair(posicion_coche_aux.getKey() + 2, posicion_coche_aux.getValue() + 2);
        Acciones accion = Acciones.moveE;
        ArrayList<Acciones> acciones_posibles = comprobarAccionesPosibles(mapa, posicion_coche);
        
        // Paso 4.b
        if (acciones_posibles.size() > 1) {

            ArrayList<Double> distancias = new ArrayList();

            for (int i = 0; i < acciones_posibles.size(); i++) {
                switch (acciones_posibles.get(i)) {
                    case moveSW:
                        distancias.add(matriz_scanner[pos - 1][pos - 1]);
                        break;
                    case moveS:
                        distancias.add(matriz_scanner[pos + 1][pos]);
                        break;
                    case moveW:
                        distancias.add(matriz_scanner[pos][pos - 1]);
                        break;
                    case moveNW:
                        distancias.add(matriz_scanner[pos - 1][pos - 1]);
                        break;
                    case moveN:
                        distancias.add(matriz_scanner[pos - 1][pos]);
                        break;
                    case moveNE:
                        distancias.add(matriz_scanner[pos - 1][pos + 1]);
                        break;
                    case moveE:
                        distancias.add(matriz_scanner[pos][pos + 1]);
                        break;
                    case moveSE:
                        distancias.add(matriz_scanner[pos + 1][pos + 1]);
                        break;
                }
            }

            int indice_mejor_accion = 0;
            double min_distancia = distancias.get(0);
            for (int j = 0; j < distancias.size(); j++) {
                if (min_distancia > distancias.get(j)) {
                    min_distancia = distancias.get(j);
                    indice_mejor_accion = j;
                }
            }
            accion = acciones_posibles.get(indice_mejor_accion);
        } else {
            accion = acciones_posibles.get(0);
        }

        // Paso 5
        return accion;
    }

    /**
     * Comprueba las posibles acciones sobre las casillas del mapa
     *
     * @author Emilio Chica Jiménez
     * @author Miguel Angel Torres López
     * @param mapa Se usa para comprobar las acciones desde la posicion del
     * coche en el mapa
     * @param posicion_coche La posicion actual del coche
     * @return El array de acciones posibles
     */
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

        // Paso 4
        // x == getKey(), y == getValue()
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

        // Paso 4.a
        // Solo cogemos las acciones con casilla negativa en el caso en el que
        // no tengamos otra opción porque estoy rodeado de casillas negativas
        if (actions.isEmpty()) {
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

    /**
     * Funcion que comprueba si existen cercos en el mapa y si el objetivo o el
     * agente se encuentran encerrados en uno de ellos
     *
     * @author Gregorio Carvajal Exposito
     * @param map Objeto de la clase Mapa
     * @param posicionAgente Pair con la posicion el agente
     * @return Devuelve TRUE en caso de que el mapa sea irresoluble y FALSE en
     * caso contrario
     */
    public boolean comprobarCercos(Mapa map, Pair<Integer, Integer> posicionAgente) {
        boolean esCerco = false;
        ArrayList<Pair<Integer, Integer>> posicionesCerco = new ArrayList<>();
        Pair<Integer, Integer> inicioCerco;
        int i, j;

        this.actualizarMapa(map);

        for (i = 2; i < 502; i++) {
            for (j = 2; j < 502; j++) // Paso 2
            {
                if (mapa_heuristica[i][j] == 1) // Paso 3
                {
                    mapa_heuristica[i][j] = 5;
                    inicioCerco = new Pair<>(i, j);

                    boolean finCerco = false;
                    while (!finCerco) // Bucle azul, Paso 4 - Encontrar un cerco
                    {
                        if (mapa_heuristica[i][j + 1] == 1) // Paso 4.a
                        {
                            j++;
                            mapa_heuristica[i][j] = 4;
                            posicionesCerco.add(new Pair(i, j));
                        } else if (mapa_heuristica[i + 1][j] == 1) // Paso 4.b.i
                        {
                            i++;
                            mapa_heuristica[i][j] = 4;
                            posicionesCerco.add(new Pair(i, j));
                        } else if (mapa_heuristica[i - 1][j] == 1) // Paso 4.b.ii.1
                        {
                            i--;
                            mapa_heuristica[i][j] = 4;
                            posicionesCerco.add(new Pair(i, j));
                        } else if (mapa_heuristica[i][j - 1] == 1) // Paso 4.b.ii.2.a
                        {
                            j--;
                            mapa_heuristica[i][j] = 4;
                            posicionesCerco.add(new Pair(i, j));
                        } else // Paso 4.b.ii.2.b
                        {
                            finCerco = true; // Parar el bucle

                            if (mapa_heuristica[i][j - 1] == 5) // Paso 4.b.ii.2.b.i
                            {
                                esCerco = true;
                            } else // 4.b.ii.2.b.ii - Comprobar si es linea recta
                            {
                                if (((i == 2 || i == 501)
                                        && (j == 2 || j == 501))
                                        && ((inicioCerco.getKey() == 2 || inicioCerco.getKey() == 501)
                                        && (inicioCerco.getValue() == 2 || inicioCerco.getValue() == 501))) {
                                    esCerco = true;
                                }
                            }
                        }
                    } // Fin while

                    i = inicioCerco.getKey();
                    j = inicioCerco.getValue();

                    if (!esCerco) // Paso 5 - Desmarcar casillas
                    {

                        finCerco = false;
                        while (!finCerco) // Bucle morado, Paso 6
                        {
                            if (mapa_heuristica[i][j + 1] == 4) // Paso 6.a
                            {
                                j++;
                                mapa_heuristica[i][j] = 1;
                            } else if (mapa_heuristica[i + 1][j] == 4) // Paso 6.b.i
                            {
                                i++;
                                mapa_heuristica[i][j] = 1;
                            } else if (mapa_heuristica[i - 1][j] == 4) // Paso 6.b.ii.1
                            {
                                i--;
                                mapa_heuristica[i][j] = 1;
                            } else if (mapa_heuristica[i][j - 1] == 4) // Paso 6.b.ii.2.a
                            {
                                j--;
                                mapa_heuristica[i][j] = 1;
                            } else // Paso 6.b.ii.2.b
                            {
                                finCerco = true; // Parar el bucle
                            }
                        } // Fin while
                    } // Fin if
                    else // Paso 7 - Comprobar si el objetivo esta dentro del cerco
                    {
                        // Obtener los extremos del cerco
                        int maxi = Integer.MIN_VALUE;
                        int mini = Integer.MAX_VALUE;
                        int maxj = Integer.MIN_VALUE;
                        int minj = Integer.MAX_VALUE;

                        for (int k = 0; k < posicionesCerco.size(); k++) {
                            Pair<Integer, Integer> actual = posicionesCerco.get(k);

                            if (actual.getKey() > maxi) {
                                maxi = actual.getKey();
                            }

                            if (actual.getKey() < mini) {
                                mini = actual.getKey();
                            }

                            if (actual.getValue() > maxj) {
                                maxj = actual.getValue();
                            }

                            if (actual.getValue() < minj) {
                                minj = actual.getValue();
                            }
                        }

                        // Comprobamos
                        if (map.getPosicionObjetivo().getKey() < maxj
                                && map.getPosicionObjetivo().getKey() > minj
                                && map.getPosicionObjetivo().getValue() < maxi
                                && map.getPosicionObjetivo().getValue() > mini) {
                            return true; // Hemos encontrado el objetivo dentro del cerco
                        } else // Comprobamos si el AGENTE esta dentro del cerco 
                        if (posicionAgente.getKey() < maxj
                                && posicionAgente.getKey() > minj
                                && posicionAgente.getValue() < maxi
                                && posicionAgente.getValue() > mini) {
                            return true; // El agente esta encerrado en el cerco
                        }
                    }
                }
            }
        }

        // Si hemos llegado a este punto, es que hemos recorrido la matriz
        // completamente sin encontrar el objetivo rodeado
        return false;
    }

    /**
     * Funcion que actualiza la matriz de la heuristica con los nuevos muros
     * descubiertos del mapa
     *
     * @author Gregorio Carvajal Exposito
     * @param map Mapa que ha percibido el agente
     */
    private void actualizarMapa(Mapa map) {
        int[][] matriz_mapa = map.devolverMapa();

        for (int i = 2; i < 502; i++) {
            for (int j = 2; j < 502; j++) {
                if (mapa_heuristica[i][j] == 3) {
                    if (matriz_mapa[i][j] < 0) {
                        mapa_heuristica[i][j] = 0;
                    } else {
                        mapa_heuristica[i][j] = matriz_mapa[i][j];
                    }
                }
            }
        }
    }
}
