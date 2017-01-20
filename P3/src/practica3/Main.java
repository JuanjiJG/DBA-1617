package practica3;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import java.util.Scanner;
import org.apache.log4j.BasicConfigurator;

/**
 * Clase Main para comenzar la ejecución de nuestro agente
 *
 * @author Miguel Ángel Torres López
 * @author Juan José Jiménez García
 */
public class Main {

    public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
        // Pedir al usuario el mundo al que conectarse
        Scanner keyboard = new Scanner(System.in);

        System.out.println("NOTA PRÁCTICA 3: Añade dos ceros al final (00) para mapa sin límite de fuel.");
        System.out.println("Introduce el nº del mapa que quieres explorar (del 1 al 10):");
        int mundo = keyboard.nextInt();

        Controlador gugel = null;
        Agente[] agentes = new Agente[4];

        // Conectar a la plataforma de agentes
        AgentsConnection.connect("isg2.ugr.es", 6000, "Furud", "Ishiguro", "Leon", false);

        // Creamos el objeto Controlador y los 4 agentes exploradores
        try {
            gugel = new Controlador(new AgentID(Controlador.AGENT_ID), mundo);
        
            for (int i = 0; i < agentes.length; i++) {
                agentes[i] = new Agente(new AgentID("AAAgente_" + i));
            }
        } catch (Exception e) {
            System.err.println("Ha habido un error creando a los agentes.");
            System.err.println(e.toString());
            System.exit(1);
        }
        // Iniciamos el agente controlador
        gugel.start();
        
        // Iniciamos los agentes exploradores
        for (Agente agente : agentes) {
            agente.start();
        }
    }
    
}
