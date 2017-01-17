package shenron;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import java.util.Scanner;

/**
 * Clase Main del agente Mutenroshi que contactara con el agente Shenron
 * 
 * @author Miguel Ángel Torres López
 */
public class ShenronClient {

    /**
     * Metodo Main de la clase ShenronClient
     * 
     * @param args the command line arguments
     * 
     * @author Miguel Ángel Torres López
     */
    public static void main(String[] args) {
        String controller = "Furud",
                user="Ishiguro",
                password="Leon";
        
        Scanner keyboard = new Scanner(System.in);

        System.out.println("Introduce el numero:");
        System.out.println("    1 si quieres reiniciar el servidor");
        System.out.println("    2 si quieres consultar el registro de shenron");
        System.out.println("Entrada:");
        
        int accion=keyboard.nextInt();
        
        System.out.println("Conectando ...");
        AgentsConnection.connect("isg2.ugr.es",6000, "test", user, password, false);
        
        try {
            System.out.println("Lanzando a Mutenroshi...");
            Mutenroshi m = new Mutenroshi(new AgentID("Mutenroshi"), controller, user, password, accion);
            m.start();
        } catch (Exception ex) {
        }
    }
    
}
