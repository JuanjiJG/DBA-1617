package practica3;

import javafx.util.Pair;

/**
 * Clase que contiene las variables de estado de un agente
 *
 * @author Miguel Ángel Torres López
 */
public class EstadoAgente {
    private Pair<Integer,Integer> posicion;
    private int[][] percepcion;
    private int fuelActual;
    private boolean crashed;
    private boolean pisandoObjetivo;
    private String replyWithControlador;
    private TiposAgente tipo;
    private Acciones nextAction;
    
    /**
    * Metodo contstructor de la clase agente
    *
    * @param replyWithControlador contiene el codigo con el que se comunicaran ela gente y el controlador
    * 
    * @author Miguel Ángel Torres López
    */
    public EstadoAgente(String replyWithControlador) {
        this.replyWithControlador = replyWithControlador;
    }
    
    /**
    * Metodo contstructor de la clase agente
    *
    * @param percepcion es la percepción que tiene el agente de su entorno
    * @param posicion es la posicion actual del agente en el mapa
    * @param fuelActual es la cantidad de fuel actual que tiene el agente
    * @param crashed nos informa de si el agente ha crasheado
    * @param pisandoObjetivo nos informa de si el agente esta sobre el objetivo
    * @param replyWithControlador contiene el codigo con el que se comunicaran ela gente y el controlador
    * @param tipo contiene el tipo de agente que es el agente actual
    * @param nextAction contiene la siguiente acción que realizará el agente
    * 
    * @author Miguel Ángel Torres López
    */
    public EstadoAgente(int[][] percepcion,Pair<Integer,Integer> posicion, int fuelActual, boolean crashed, boolean pisandoObjetivo, String replyWithControlador, TiposAgente tipo, Acciones nextAction) {
        this.percepcion = percepcion;
        this.posicion = posicion;
        this.fuelActual = fuelActual;
        this.crashed = crashed;
        this.pisandoObjetivo = pisandoObjetivo;
        this.replyWithControlador = replyWithControlador;
        this.tipo = tipo;
        this.nextAction = nextAction;
    }
    
    /**
    * Metodo get para la variable posicion
    *
    * @return Devuelve una matriz con la posicion actual del agente
    * 
    * @author Miguel Ángel Torres López
    */
    public Pair<Integer, Integer> getPosicion() {
        return posicion;
    }

    /**
    * Metodo get para la variable percepcion
    *
    * @return Devuelve una matriz con la percepción del agente
    * 
    * @author Miguel Ángel Torres López
    */
    public int[][] getPercepcion() {
        return percepcion;
    }

    /**
    * Metodo get para la variable fuelActual
    *
    * @return Devuelve un entero con la cantidad de fuel de la que dispone el agente
    * 
    * @author Miguel Ángel Torres López
    */
    public int getFuelActual() {
        return fuelActual;
    }

    /**
    * Metodo get para la variable crashed
    *
    * @return Devuelve un boolean que informa de si el agente esta crasheado o no
    * 
    * @author Miguel Ángel Torres López
    */
    public boolean isCrashed() {
        return crashed;
    }

    /**
    * Metodo get para la variable pisandoObjetivo
    *
    * @return Devuelve un boolean que informa de si el agente está pisando el objetivo
    * 
    * @author Miguel Ángel Torres López
    */
    public boolean isPisandoObjetivo() {
        return pisandoObjetivo;
    }

    /**
    * Metodo get para la variable replyWithControlador
    *
    * @return Devuelve un String con el código que se usara para la comunicación con el controlador
    * 
    * @author Miguel Ángel Torres López
    */
    public String getReplyWithControlador() {
        return replyWithControlador;
    }

    /**
    * Metodo get para la variable tipo
    *
    * @return Devuelve un TipoAgente con el tipo del agente actual
    * 
    * @author Miguel Ángel Torres López
    */
    public TiposAgente getTipo() {
        return tipo;
    }

    /**
    * Metodo get para la variable nextAction
    *
    * @return Devuelve un Acciones con la siguiente accion que va a ejecutar el agente
    * 
    * @author Miguel Ángel Torres López
    */
    public Acciones getNextAction() {
        return nextAction;
    }    

    /**
    * Metodo set para la variable posicion
    *
    * @param posicion es la casilla del mapa sobre la que esta el agente
    * 
    * @author Miguel Ángel Torres López
    */
    public void setPosicion(Pair<Integer, Integer> posicion) {
        this.posicion = posicion;
    }
    
    /**
    * Metodo set para la variable percepcion
    *
    * @param percepcion es la información que tiene el agente de las casillas que le rodean
    * 
    * @author Miguel Ángel Torres López
    */
    public void setPercepcion(int[][] percepcion) {
        this.percepcion = percepcion;
    }

    /**
    * Metodo set para la variable fuelActual
    *
    * @param fuelActual es la cantidad de fuel de la que dispone el agente
    * 
    * @author Miguel Ángel Torres López
    */
    public void setFuelActual(int fuelActual) {
        this.fuelActual = fuelActual;
    }

    /**
    * Metodo set para la variable crashed
    *
    * @param crashed nos informa de si hemos crasheado o no
    * 
    * @author Miguel Ángel Torres López
    */
    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
    }

    /**
    * Metodo set para la variable pisandoObjetivo
    *
    * @param pisandoObjetivo nos informa de si el agente está pisando el objetivo o no
    * 
    * @author Miguel Ángel Torres López
    */
    public void setPisandoObjetivo(boolean pisandoObjetivo) {
        this.pisandoObjetivo = pisandoObjetivo;
    }

    /**
    * Metodo set para la variable replyWithControlador
    *
    * @param replyWithControlador es el código que se usa para mantener la conversacion con el controlador
    * 
    * @author Miguel Ángel Torres López
    */
    public void setReplyWithControlador(String replyWithControlador) {
        this.replyWithControlador = replyWithControlador;
    }

    /**
    * Metodo set para la variable tipo
    *
    * @param tipo es tipo de agente del agente actual
    * 
    * @author Miguel Ángel Torres López
    */
    public void setTipo(TiposAgente tipo) {
        this.tipo = tipo;
    }

    /**
    * Metodo set para la variable nextAction
    *
    * @param nextAction es la siguiente acción que ejecutara el agente
    * 
    * @author Miguel Ángel Torres López
    */
    public void setNextAction(Acciones nextAction) {
        this.nextAction = nextAction;
    }
    
    /**
    * Metodo get que devuelve el gasto de combustible del agente
    *
    * @return Devuelve un int con la cantidad de combustible que consume el agente en cada movimiento
    * 
    * @author Miguel Ángel Torres López
    */
    public int getGasto(){
        if(tipo==TiposAgente.camion){
            return CapacidadesAgentes.GASTO_CAMION;
        }
        else if(tipo==TiposAgente.coche){
            return CapacidadesAgentes.GASTO_COCHE;
        }
        else if(tipo==TiposAgente.dron){
            return CapacidadesAgentes.GASTO_DRON;
        }
        else
        {
            return 0;
        }
    }
    
    /**
    * Metodo get que devuelve la visibilidad del agente
    *
    * @return Devuelve un int con la cantidad de casillas que es capaz de ver el agente
    * 
    * @author Miguel Ángel Torres López
    */
    public int getVisibilidad(){
        if(tipo==TiposAgente.camion){
            return CapacidadesAgentes.VISIBILIDAD_CAMION;
        }
        else if(tipo==TiposAgente.coche){
            return CapacidadesAgentes.VISIBILIDAD_COCHE;
        }
        else if(tipo==TiposAgente.dron){
            return CapacidadesAgentes.VISIBILIDAD_DRON;
        }
        else
        {
            return 0;
        }
    }
    
    /**
    * Metodo get que devuelve si el agente puede volar o no
    *
    * @return Devuelve un boolean que informa de si el agente puede volar o no
    * 
    * @author Miguel Ángel Torres López
    */
    public boolean getPuedeVolar(){
        if(tipo==TiposAgente.camion){
            return CapacidadesAgentes.VUELA_CAMION;
        }
        else if(tipo==TiposAgente.coche){
            return CapacidadesAgentes.VUELA_COCHE;
        }
        else if(tipo==TiposAgente.dron){
            return CapacidadesAgentes.VUELA_DRON;
        }
        else
        {
            return false;
        }
    }
}
