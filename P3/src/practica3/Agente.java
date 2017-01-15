/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica3;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 *
 * @author Miguel Ángel Torres López
 * @author Gregorio Carvajal Expósito
 */
public class Agente extends SingleAgent {

    public Agente(AgentID aid) throws Exception {
        super(aid);
    }
    
    /**
     * Inicialización de las variables antes de la ejecución del agente
     * 
     * @author Miguel Ángel Torres López
     */
    @Override
    public void init(){
        System.out.println("\n\nInicializando estado del agente...");
    }
    
    /**
     * Método que ejecutará el agente cuando se inicie
     * 
     * @author Miguel Ángel Torres López
     */
    @Override
    public void execute(){
        System.out.println("Ejecutando el agente...");
    }
    
    /**
     * Método que se ejecutará cuando el agente vaya a finalizar su ejecución
     *
     * @author Miguel Ángel Torres López
     */
    @Override
    public void finalize() {
        System.out.println("Finalizando agente...");
    }
	
	/**
	 * Ejecua el recieve y actualiza las variables necesarias del controlador
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void recibir() {
		
	}
	
	/**
	 * Responde al Controlador (send) enviandole
	 * las variables de estado del agente
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void enviarEstado() {
		
	}
	
	/**
	 * Manda al Server (send) una peticion para moverse o repostar
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void ejecutarAccion() {
		
	}
	
	/**
	 * Realiza el Checkin en el Server
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void checkin() {
		
	}
	
	/**
	 * Informa al Controlador (send) si se ha ejecutado o no la accion
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void informarResultadoAccion() {
		
	}
	
	/**
	 * Envia al Server (send) una peticion para que le
	 * responda con las percepciones del agente
	 * 
	 * @author Gregorio Carvajal Expósito
	 */
	public void solicitarPercepcion() {
		
	}
}
