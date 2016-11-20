package practica2;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import java.util.Scanner;

/**
 * Clase Main para comenzar la ejecución de nuestro agente
 *
 * @author Juan José Jiménez García
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // Pedir al usuario el mundo al que conectarse
        Scanner keyboard = new Scanner(System.in);

        System.out.println("Introduce el nº del mapa que quieres explorar (del 1 al 10):");
        int mundo = keyboard.nextInt();

        // Heuristica p = new Heuristica();
        // System.out.println(p.calcularDistanciaEuclidea(new Pair(53,46), new Pair(52,47)));
        Agente gugel = null;

        // Conectar a la plataforma de agentes
        AgentsConnection.connect("isg2.ugr.es", 6000, "Furud", "Ishiguro", "Leon", false);

        // Creamos el objeto Agente
        try {
            gugel = new Agente(new AgentID("GugelCarRedForest"), mundo);
        } catch (Exception e) {
            System.err.println("Ha habido un error creando el agente.");
            System.exit(1);
        }

        // Iniciamos el agente
        gugel.start();
    }
}
