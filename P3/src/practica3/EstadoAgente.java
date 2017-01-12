package practica3;

/**
 * Clase que contiene las variables de estado de un agente
 *
 * @author Miguel Ángel Torres López
 */
public class EstadoAgente {
    private int[][] percepcion;
    private int fuelActual;
    private boolean crashed;
    private boolean pisandoObjetivo;
    private String replyWithControlador;

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
    
    
}
