package practica3;

import javafx.util.Pair;

/**
 *
 * @author Juan José Jiménez García
 */
public class BaseConocimiento {
    
    private static BaseConocimiento instancia = null; // Patrón Singleton
    private Pair<Integer, Integer> posicionObjetivo;
    private int[][] mapa;
    private int tamanioMapa;
    
    // Constructor privado como parte del patrón Singleton
    private BaseConocimiento() {}
    
    /**
     * Método para obtener la instancia de BaseConocimiento
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
     * 
     * @author Juan José Jiménez García
     */
    public void cargarMapa() {
        
    }
    
    /**
     * 
     * @author Juan José Jiménez García
     */
    public void guardarMapa() {
        
    }
    
    /**
     * 
     * @author Juan José Jiménez García
     */
    public void actualizarMapa() {
        
    }
}
