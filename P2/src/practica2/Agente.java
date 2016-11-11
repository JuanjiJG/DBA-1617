/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

import com.eclipsesource.json.*;
import es.upv.dsic.gti_ia.core.*;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.util.Pair;

/**
 * Esta clase contiene los atributos y métodos para el comportamiento de nuestro agente 
 * @author Juan José Jiménez García
 */
public class Agente extends SingleAgent {
    
    private static final int NUM_PERCEPCIONES = 100;
    private Pair<Integer, Integer> posicion;
    private String percepcion;
    private boolean pisando_objetivo;
    private boolean crashed;
    private int cont_bateria;
    private boolean objetivo_detectado;
    private String server_key;
    
    /**
     * Constructor de la clase Agente
     * @param aid
     * @throws Exception 
     * @author Juan José Jiménez García
     */
    public Agente(AgentID aid) throws Exception {
        
        super(aid);
    }
    
    /**
     * Método execute del agente
     * @author Juan José Jiménez García
     */
    @Override
    public void execute() {
        
    }
    
    /**
     * Método para el procesamiento de la traza de imagen
     * @author Juan José Jiménez García
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    public void procesarTraza() throws InterruptedException, IOException {
        
        try {
            System.out.println("Recibiendo la traza");
            
            ACLMessage inbox = this.receiveACLMessage();
            JsonObject injson = Json.parse(inbox.getContent()).asObject();
            JsonArray ja = injson.get("trace").asArray();
            byte data[] = new byte [ja.size()];
            
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) ja.get(i).asInt();
            }
            
            FileOutputStream fos = new FileOutputStream("mitraza.png");
            fos.write(data);
            fos.close();
            System.out.println("Traza guardada");
            
        } catch (InterruptedException | IOException ex) {
            
            System.err.println("Error procesando la traza");
        }
    }
}
