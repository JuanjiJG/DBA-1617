package practica3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author Juan José Jiménez García
 */
public class BaseConocimiento {

    private static BaseConocimiento instancia = null; // Patrón Singleton
    private Pair<Integer, Integer> posicionObjetivo = null; //Si la posicion es null significa que no hemos encontrado el objetivo aún.
    private int[][] mapa;
    private int tamanioMapa;
    private int mapaElegido;
    private int antiguedad;
    private ArrayList<EstadoAgente> conjuntoEstados;

    // Constructor privado como parte del patrón Singleton
    private BaseConocimiento() {
        this.conjuntoEstados = new ArrayList();
    }

    /**
     * Método para obtener la instancia de BaseConocimiento.
     *
     * @return La instancia de BaseConocimiento
     * @author Juan José Jiménez García
     */
    public static BaseConocimiento getInstance() {
        if (instancia == null) {
            instancia = new BaseConocimiento();
        }
        return instancia;
    }

    /**
     * Método para inicializar el mapa Hay que inicializar todas las casillas a
     * valor "Inexplorado".
     *
     * @param tamanio El nº de casillas de un lado del mapa
     * @author Juan José Jiménez García
     */
    public void inicializarMapa(int tamanio) {

        this.tamanioMapa = tamanio;
        this.mapa = new int[tamanio][tamanio];

        for (int i = 0; i < tamanio; i++) {
            for (int j = 0; j < tamanio; j++) {
                this.mapa[i][j] = 5;
            }
        }
    }

    /**
     * Método para cargar el mapa almacenado en un fichero. También se encarga
     * de establecer la posicionObjetivo en caso de que sea visible.
     *
     * @param numMapa El número del mapa que se quiere cargar
     * @param tamMapa El tamaño del mapa que se quiere cargar
     * @return Un booleano que indica si se ha encontrado el objetivo durante la
     * carga
     * @author Juan José Jiménez García
     */
    public boolean cargarMapa(int numMapa, int tamMapa) {

        /*
        Funcionamiento del método:
        - Obtener por parámetro el mapa a cargar y su tamaño
        - Buscar el archivo del mapa y cargarlo
        - En caso de no encontrar el archivo (excepcion lanzada)...
            - Llamar al método inicializar pasando el tamaño del mapa

        La idea general es que la primera vez que "cargue" el mapa, inicialice uno
        Se puede reiniciar un mapa borrando el archivo generado para que vuelva a inicializar
         */
        ObjectInputStream inputStream = null;
        String fileName = "map_" + numMapa + "_savefile.data";
        this.tamanioMapa = tamMapa;
        boolean existeArchivo = false;
        boolean existeObjetivo = false;

        try {
            //Comprueba si existe el archivo
            File f = new File(fileName);
            if (f.exists() && !f.isDirectory()) {
                inputStream = new ObjectInputStream(new FileInputStream(fileName));
                existeArchivo = true;
                this.mapa = (int[][]) inputStream.readObject();

                //DEBUGGING
                for (int i = 0; i < tamMapa; i++) {
                    for (int j = 0; j < tamMapa; j++) {
                        System.out.print(mapa[i][j] + " ");
                    }
                    System.out.println();
                }
                if (this.buscarObjetivo()) {
                    existeObjetivo = true;
                }
            } else {
                existeArchivo = false;
            }

        } catch (IOException | ClassNotFoundException ex) {
			System.err.println("ERROR DE HDD LOAD");
            Logger.getLogger(BaseConocimiento.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (!existeArchivo) {
                this.inicializarMapa(this.tamanioMapa);
            }
        }

        return existeObjetivo;
    }

    /**
     * Método para comprobar si el objetivo está en el mapa. Si se ha
     * encontrado, se actualiza la posición del objetivo.
     *
     * @return Un booleano que indica si se ha encontrado el objetivo.
     * @author Juan José Jiménez García
     */
    public boolean buscarObjetivo() {

        boolean encontrado = false;

        for (int i = 0; i < this.tamanioMapa; i++) {
            for (int j = 0; j < this.tamanioMapa; j++) {
                if (mapa[i][j] == 3) {
                    encontrado = true;
                    this.posicionObjetivo = new Pair(i, j);
                }
            }
        }

        return encontrado;
    }

    public void setMapaElegido(int mapaElegido) {
        this.mapaElegido = mapaElegido;
    }

    /**
     * Método para guardar el estado del mapa en un fichero.
     *
     * @author Juan José Jiménez García
     */
    public void guardarMapa() {

        /*
        Funcionamiento del método:
            - Crear un archivo con el nombre basado en el mapa elegido
            - Escribir en un archivo de disco la variable mapa
         */
        ObjectOutputStream outputStream = null;
        String fileName = "map_" + this.mapaElegido + "_savefile.data";

        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            outputStream.writeObject(this.mapa);
        } catch (IOException ex) {
            Logger.getLogger(BaseConocimiento.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outputStream.close();
            } catch (IOException ex) {
				System.err.println("ERROR DE HDD SAVE");
                Logger.getLogger(BaseConocimiento.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Método para actualizar el mapa.
     *
     * @param estadoAgente El objeto de tipo EstadoAgente que contiene
     * información sobre el agente
     * @return Un booleano que indica si durante el proceso de actualización se
     * ha encontrado el objetivo
     * @author Juan José Jiménez García
     */
    public boolean actualizarMapa(EstadoAgente estadoAgente,ArrayList<EstadoAgente> agentesEnObjetivo) {

        /*
        La idea del método sería:
            - Coger el estado del agente y ver qué tipo de agente es.
            Con un switch se haría eso.
            - Se entiende que el estado del agente tiene ya las coordenadas y
            la posición bien puestas, así que trabajamos con coordenadas como
            siempre, sin invertir nada. CUIDADO CON ESTO QUE AUN NO ESTÁ CLARO DEL TODO
        
            - Acceder a su matriz de percepcion y posicion actual en el mundo
            - Actualizar la información del mapa (ver P2 que hay algo así)
            - Si en esa percepción hay objetivo, marcar la posición
        
            - ¿Implementamos que la funcion devuelva algo según el resultado?
                - ¿Hay objetivo o no (true o false)?
         */
        boolean objetivoEncontrado = false;

        int[][] percepcion = estadoAgente.getPercepcion();
        int visibilidad = estadoAgente.getVisibilidad();

        Pair<Integer, Integer> posicion = estadoAgente.getPosicion();
        int posicion_inicial_i = posicion.getKey();
        int posicion_inicial_j = posicion.getValue();

        for (int i = 0; i < visibilidad; i++) {
            for (int j = 0; j < visibilidad; j++) {
              
                //Nos interesa que primero actualice el mapa y despues compruebe si hay un 3 (objetivo)
                //if (mapa[posicion_inicial_i - (visibilidad / 2) + i][posicion_inicial_j - (visibilidad / 2) + j] > 3) {
                    mapa[posicion_inicial_i - (visibilidad / 2) + i][posicion_inicial_j - (visibilidad / 2) + j] = percepcion[i][j];
                //}

                  //Compruebo si hay un 3 (casilla de objetivo)
                if (mapa[posicion_inicial_i - (visibilidad / 2) + i][posicion_inicial_j - (visibilidad / 2) + j] == 3) {
                    this.posicionObjetivo = new Pair(posicion_inicial_i - (visibilidad / 2) + i, posicion_inicial_j - (visibilidad / 2) + j);
                    objetivoEncontrado = true;
                }
               
            }
            
        }
        //Nos situamos en el mapa
         mapa[posicion_inicial_i][posicion_inicial_j] = 4;
        // Tras haber actualizado, añadimos el EstadoAgente al array
        // BORRAR COMPROBACION AL TERMINAR PRUEBAS
        if (this.conjuntoEstados.size() < 4) {
            this.conjuntoEstados.add(estadoAgente);
        } else {
            System.out.println("Habia ya 4 EstadoAgente en el conjunto...");
        }
        for(int i=0;i<agentesEnObjetivo.size();++i){
            mapa[agentesEnObjetivo.get(i).getPosicion().getKey()][agentesEnObjetivo.get(i).getPosicion().getValue()] = 4;
        }
        //DEBUGGING
        /*for (int i = 0; i < this.tamanioMapa; i++) {
            for (int j = 0; j < this.tamanioMapa; j++) {
                System.out.print(mapa[i][j] + " ");
            }
            System.out.println();
        }*/
        guardarMapa();
        return objetivoEncontrado;
    }

    /**
     * Método para vaciar el conjunto de elementos EstadoAgente.
     *
     * @author Juan José Jiménez García
     */
    public void limpiarConjuntoEstados() {

        this.conjuntoEstados.clear();
    }

    /**
     * Método para obtener el conjunto de elementos EstadoAgente.
     *
     * @return El conjunto de elementos EstadoAgente
     * @author Juan José Jiménez García
     */
    public ArrayList<EstadoAgente> getConjuntoEstados() {

        return this.conjuntoEstados;
    }

    /**
     * Método para obtener el mapa de la base de conocimiento.
     *
     * @return La matriz del mapa
     * @author Juan José Jiménez García
     */
    public int[][] getMapa() {

        return this.mapa;
    }

    /**
     * Método para devolver la antigüedad.
     *
     * @return El valor de la antigüead
     * @author Juan José Jiménez García
     */
    public int getAntiguedad() {
        return this.antiguedad;
    }

    /**
     * Método para devolver el tamaño del mapa.
     *
     * @return El valor de la antigüead
     * @author Juan José Jiménez García
     */
    public int getTamMapa() {
        return this.tamanioMapa;
    }

    /**
     * Método para devolver la posición del objetivo
     *
     * @return La posición del objetivo
     * @author Juan José Jiménez García
     */
    public Pair<Integer, Integer> getPosicionObjetivo() {
        return this.posicionObjetivo;
    }

    /**
     * Método para decrementar la antigüedad.
     *
     * @author Juan José Jiménez García
     */
    public void decrementarAntiguedad() {
        this.antiguedad--;
    }
}
