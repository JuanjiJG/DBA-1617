package shenron;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import java.util.Scanner;

/**
 * Clase Main del agente Mutenroshi que contactará con el agente Shenron
 *
 * @author Miguel Ángel Torres López
 */
public class ShenronClient {

    /**
     * Método Main de la clase ShenronClient
     *
     * @param args the command line arguments
     * @author Miguel Ángel Torres López
     */
    public static void main(String[] args) {
        String controller = "Furud",
                user = "Ishiguro",
                password = "Leon";

        Scanner keyboard = new Scanner(System.in);

        System.out.println("Introduce el número:");
        System.out.println("\t1 si quieres reiniciar el servidor");
        System.out.println("\t2 si quieres consultar el registro de Shenron");
        System.out.println("Entrada:");
        int accion = keyboard.nextInt();

        System.out.println("Conectando ...");
        AgentsConnection.connect("isg2.ugr.es", 6000, "test", user, password, false);

        try {
            System.out.println("Lanzando a Mutenroshi...");
            Mutenroshi m = new Mutenroshi(new AgentID("Mutenroshi_5"), controller, user, password, accion);
            m.start();
        } catch (Exception ex) {
            System.err.println("Ha habido un error al intentar lanzar a Mutenroshi");
            System.err.println(ex);
        }
    }

}
