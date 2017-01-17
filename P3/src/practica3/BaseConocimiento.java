package practica3;

import java.util.ArrayList;
import javafx.util.Pair;

/**
 *
 * @author Juan José Jiménez García
 */
public class BaseConocimiento {

    private static BaseConocimiento instancia = null; // Patrón Singleton
    private Pair<Integer, Integer> posicionObjetivo; //Si la posicion es null significa que no hemos encontrado el objetivo aún.
    private int[][] mapa;
    private int tamanioMapa;
    private ArrayList<EstadoAgente> estadoActivos;
    private ArrayList<EstadoAgente> estadoInactivos;

    // Constructor privado como parte del patrón Singleton
    private BaseConocimiento() {
        this.estadoActivos = new ArrayList();
        this.estadoInactivos = new ArrayList();
    }

    /**
     * Método para obtener la instancia de BaseConocimiento.
     *
     * @return La instancia de BaseConocimiento
     * @author Juan José Jiménez García
     */
    public static BaseConocimiento getInstancia() {
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

        this.mapa = new int[tamanio][tamanio];

        for (int i = 0; i < tamanio; i++) {
            for (int j = 0; j < tamanio; j++) {
                this.mapa[i][j] = 5;
            }
        }
    }

    /**
     * Método para cargar el mapa almacenado en un fichero. También se encarga
     * de establecer la posicionObjetivo en caso de que sea visible
     *
     * @author Juan José Jiménez García
     */
    public void cargarMapa(String ruta) {

        /*
        Idea del método:
            - Obtener por parámetro el mapa a cargar
            - Buscar el archivo del mapa y cargarlo
                - Para cargarlo lo que haremos será reemplazar el mapa con lo que vamos leyendo
            - ¿Y si no lo encuentra?
                - Llamar al método inicializar (lo más factible)
                - Gestionar algun tipo de error?
        
        La idea de inicializar el mapa tiene sentido siempre para la primera "carga"
        Se puede "resetear" si borramos el archivo para que no lo encuentre y reinicialice
         */
    }

    /**
     * Método para guardar el estado del mapa en un fichero.
     *
     * @author Juan José Jiménez García
     */
    public void guardarMapa(String ruta) {

        /*
        La idea del método sería:
            - Comprobar si hay un archivo existente (o no, hay que verlo)
            - Escribir en un archivo de disco la variable mapa
            - Si este procedimiento tiene alguna información de exito o fallo, ¿usarla?
         */
    }

    /**
     * Método para actualizar el mapa.
     *
     * @param estadoAgente El objeto de tipo EstadoAgente que contiene
     * información sobre el agente
     * @author Juan José Jiménez García
     */
    public void actualizarMapa(EstadoAgente estadoAgente) {

        /*
        La idea del método sería:
            - Coger el estado del agente y ver qué tipo de agente es.
            Con un switch se haría eso.
        
            - Acceder a su matriz de percepcion y posicion actual en el mundo
            - Actualizar la información del mapa (ver P2 que hay algo así)
            - Si en esa percepción hay objetivo, marcar la posición
        
            - ¿Implementamos que la funcion devuelva algo según el resultado?
                - ¿Hay objetivo o no (true o false)?
         */
        int[][] percepcion = estadoAgente.getPercepcion();
        TiposAgente tipo = estadoAgente.getTipo();
        Pair<Integer, Integer> posicion = estadoAgente.getPosicion();

        switch (tipo) {
            case CAMION:
                break;
            case DRON:
                break;
            case COCHE:
                break;
        }
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
}
