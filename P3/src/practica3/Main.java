package practica3;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import java.util.Scanner;

/**
 * Clase Main para comenzar la ejecución de nuestro agente
 *
 * @author Miguel Ángel Torres López
 */
public class Main {

    public static void main(String[] args) throws Exception {
        // Pedir al usuario el mundo al que conectarse
        Scanner keyboard = new Scanner(System.in);

        System.out.println("Introduce el nº del mapa que quieres explorar (del 1 al 10):");
        int mundo = keyboard.nextInt();

        Controlador gugel = null;

        // Conectar a la plataforma de agentes
        AgentsConnection.connect("isg2.ugr.es", 6000, "Furud", "Ishiguro", "Leon", false);

        // Creamos el objeto Controlador
        try {
            gugel = new Controlador(new AgentID("GugelCarRedForest"), mundo);
        } catch (Exception e) {
            System.err.println("Ha habido un error creando el controlador.");
            System.exit(1);
        }

        // Iniciamos el agente
        gugel.start();
    }
    
}
