package practica3;

import java.util.ArrayList;
import javafx.util.Pair;

/**
 *
 * @author Antonio Javier Benítez Guijarro
 * @author Emilio Manuel Chica Jiménez
 */
public class Heuristica {
    private Pair<Integer,Integer> subObjetivo;
    private BaseConocimiento linkbc;
    private int tamMapa;
    private final double UMBRAL_EMPATE = 5;
    private final double UMBRAL_EMPATE_PUNTUACION = 10;
    private final int UMBRAL_COMBUSTIBLE = 75;
    private final int UMBRAL_PENALIZACION = 3;
    private double[][] distancias;
    Acciones acciones_posibles [];
    private double gradiente_muro_encontrado=Double.MAX_VALUE;
    private boolean siguiendo_muro=false;
    private int cuadrante_1_i_inicio;
    private int cuadrante_1_i_final;
    private int cuadrante_1_j_inicio;
    private int cuadrante_1_j_final;
    
    private int cuadrante_2_i_inicio;
    private int cuadrante_2_i_final;
    private int cuadrante_2_j_inicio;
    private int cuadrante_2_j_final;
    
    private int cuadrante_3_i_inicio;
    private int cuadrante_3_i_final;
    private int cuadrante_3_j_inicio;
    private int cuadrante_3_j_final;
    
    private int cuadrante_4_i_inicio;
    private int cuadrante_4_i_final;
    private int cuadrante_4_j_inicio;
    private int cuadrante_4_j_final;
    private EstadoAgente agenteAnterior=null;
    
    
    public Heuristica(){
        linkbc = BaseConocimiento.getInstance();
    }

	public void setTamMapa(int tamMapa) {
		this.tamMapa = tamMapa;
		dividirCuadrantes();
	}
    
    
    private boolean necesitaRepostar(EstadoAgente agente){
        if(agente.getFuelActual() < UMBRAL_COMBUSTIBLE)
            return true;
        else 
            return false;
    }
    
    private void dividirCuadrantes(){
        this.cuadrante_1_i_inicio = 0;
        this.cuadrante_1_i_final = (tamMapa/2)-1;
        this.cuadrante_1_j_inicio = 0;
        this.cuadrante_1_j_final = (tamMapa/2)-1;
        
        
        this.cuadrante_2_i_inicio = tamMapa/2;
        this.cuadrante_2_i_final = tamMapa-1;
        this.cuadrante_2_j_inicio = 0;
        this.cuadrante_2_j_final = (tamMapa/2)-1;
        
        this.cuadrante_3_i_inicio = 0;
        this.cuadrante_3_i_final = (tamMapa/2)-1;
        this.cuadrante_3_j_inicio = tamMapa/2;
        this.cuadrante_3_j_final = tamMapa-1;
        
        this.cuadrante_4_i_inicio = tamMapa/2;
        this.cuadrante_4_i_final = tamMapa-1;
        this.cuadrante_4_j_inicio = tamMapa/2;
        this.cuadrante_4_j_final = tamMapa-1;
        
    }
    
    private Acciones comprobarDireccion(Acciones[] acciones, int [][] mapa,Pair<Integer,Integer> posicion_agente){
        int i = posicion_agente.getKey();
        int j = posicion_agente.getValue();
        Acciones accion_resultado = null;
        
        for(int h = 0; h < 7 && accion_resultado == null; h++){
            switch(acciones[h]){
                        case moveNW:
                            if(mapa[i-1][j-1] == 0 || mapa[i-1][j-1] == 3){
                                accion_resultado = acciones[h];
                            }
                            break;
                        case moveN:
                            if(mapa[i-1][j] == 0|| mapa[i-1][j] == 3){
                                accion_resultado = acciones[h];
                            }
                            break;
                        case moveNE:
                            if(mapa[i-1][j+1] == 0|| mapa[i-1][j+1] == 3){
                                accion_resultado = acciones[h];
                            }                       
                            break;
                        case moveW:
                            if(mapa[i][j-1] == 0|| mapa[i][j-1] == 3){
                                accion_resultado = acciones[h];
                            }
                            break;
                        case moveE:
                            if(mapa[i][j+1] == 0|| mapa[i][j+1] == 3){
                                accion_resultado = acciones[h];
                            }
                            break;
                        case moveSW:
                            if(mapa[i+1][j-1] == 0|| mapa[i+1][j-1] == 3){
                                accion_resultado = acciones[h];
                            }
                            break;
                        case moveS:
                            if(mapa[i+1][j] == 0|| mapa[i+1][j] == 3){
                                accion_resultado = acciones[h];
                            }
                            break;
                        case moveSE:
                            if(mapa[i+1][j+1] == 0|| mapa[i+1][j+1] == 3){
                                accion_resultado = acciones[h];
                            }
                            break;
                    }
        }
        
        return accion_resultado;
    }
    
    //Por si falla segun dice antonio deberia de devolver EstadoAgente agente_seleccionado
    private void calcularSiguienteMovimiento(EstadoAgente agente_seleccionado,Pair<Integer,Integer> posicion_agente, Pair<Integer,Integer> posicion_destino){
        Pair<Acciones,Pair<Integer,Integer>> casilla = calcularMejorCasilla(posicion_agente,posicion_destino);
        int j=casilla.getValue().getValue();//Invertimos las variables para nuestro mapa
        int i=casilla.getValue().getKey();//Invertimos las variables para nuestro mapa
        int mapa [][] = this.linkbc.getMapa();
        Acciones direccion = casilla.getKey();
        
        if((mapa[i][j]!=1 && mapa[i][j]!=2 && mapa[i][j]!=4)||(agente_seleccionado.getTipo() == TiposAgente.dron)){
            switch(direccion){
                    case moveNW:
                        if(distancias[0][0] < gradiente_muro_encontrado){
                            this.siguiendo_muro = false;
                            this.gradiente_muro_encontrado = Double.MAX_VALUE;
                            agente_seleccionado.setNextAction(direccion);
                        }
                        break;
                    case moveN:
                        if(distancias[0][1] < gradiente_muro_encontrado){
                            this.siguiendo_muro = false;
                            this.gradiente_muro_encontrado = Double.MAX_VALUE;
                            agente_seleccionado.setNextAction(direccion);
                        }
                        break;
                    case moveNE:
                        if(distancias[0][2] < gradiente_muro_encontrado){
                            this.siguiendo_muro = false;
                            this.gradiente_muro_encontrado = Double.MAX_VALUE;
                            agente_seleccionado.setNextAction(direccion);
                        }                        
                        break;
                    case moveW:
                        if(distancias[1][0] < gradiente_muro_encontrado){
                            this.siguiendo_muro = false;
                            this.gradiente_muro_encontrado = Double.MAX_VALUE;
                            agente_seleccionado.setNextAction(direccion);
                        }
                        break;
                    case moveE:
                        if(distancias[1][2] < gradiente_muro_encontrado){
                            this.siguiendo_muro = false;
                            this.gradiente_muro_encontrado = Double.MAX_VALUE;
                            agente_seleccionado.setNextAction(direccion);
                        }
                        break;
                    case moveSW:
                        if(distancias[2][0] < gradiente_muro_encontrado){
                            this.siguiendo_muro = false;
                            this.gradiente_muro_encontrado = Double.MAX_VALUE;
                            agente_seleccionado.setNextAction(direccion);
                        }
                        break;
                    case moveS:
                        if(distancias[2][1] < gradiente_muro_encontrado){
                            this.siguiendo_muro = false;
                            this.gradiente_muro_encontrado = Double.MAX_VALUE;
                            agente_seleccionado.setNextAction(direccion);
                        }
                        break;
                    case moveSE:
                        if(distancias[2][2] < gradiente_muro_encontrado){
                            this.siguiendo_muro = false;
                            this.gradiente_muro_encontrado = Double.MAX_VALUE;
                            agente_seleccionado.setNextAction(direccion);
                        }
                        break;
                }
        }
        
        //Si es un muro o es un agente
        //Si ya esabamos siguiendo el muro o es la primera vez que lo encontramos
        if(mapa[i][j]==4 || ((mapa[i][j]==1 || mapa[i][j]==2 || siguiendo_muro) &&(agente_seleccionado.getTipo() != TiposAgente.dron))){
            siguiendo_muro=true;
            switch(direccion){
                case moveNW:
                    if(distancias[0][0] < gradiente_muro_encontrado)
                        gradiente_muro_encontrado = distancias[0][0];
                    this.acciones_posibles = new Acciones[]{Acciones.moveW,Acciones.moveSW,Acciones.moveS,Acciones.moveSE,Acciones.moveE,Acciones.moveNE,Acciones.moveN};
                    agente_seleccionado.setNextAction(comprobarDireccion(this.acciones_posibles,mapa,posicion_agente));
                    break;
                case moveN:
                    if(distancias[0][1] < gradiente_muro_encontrado)
                        gradiente_muro_encontrado = distancias[0][1];
                    this.acciones_posibles = new Acciones[]{Acciones.moveNW,Acciones.moveW,Acciones.moveSW,Acciones.moveS,Acciones.moveSE,Acciones.moveE,Acciones.moveNE};
                    agente_seleccionado.setNextAction(comprobarDireccion(this.acciones_posibles,mapa,posicion_agente));
                    break;
                case moveNE:
                    if(distancias[0][2] < gradiente_muro_encontrado)
                        gradiente_muro_encontrado = distancias[0][2];
                    this.acciones_posibles = new Acciones[]{Acciones.moveN,Acciones.moveNW,Acciones.moveW,Acciones.moveSW,Acciones.moveS,Acciones.moveSE,Acciones.moveE};
                    agente_seleccionado.setNextAction(comprobarDireccion(this.acciones_posibles,mapa,posicion_agente));
                    break;
                case moveW:
                    if(distancias[1][0] < gradiente_muro_encontrado)
                        gradiente_muro_encontrado = distancias[1][0];
                    this.acciones_posibles = new Acciones[]{Acciones.moveSW,Acciones.moveS,Acciones.moveSE,Acciones.moveE,Acciones.moveNE,Acciones.moveN,Acciones.moveNW};
                    agente_seleccionado.setNextAction(comprobarDireccion(this.acciones_posibles,mapa,posicion_agente));
                    break;
                case moveE:
                    if(distancias[1][2] < gradiente_muro_encontrado)
                        gradiente_muro_encontrado = distancias[1][2];
                    this.acciones_posibles = new Acciones[]{Acciones.moveNE,Acciones.moveN,Acciones.moveNW,Acciones.moveW,Acciones.moveSW,Acciones.moveS,Acciones.moveSE};
                    agente_seleccionado.setNextAction(comprobarDireccion(this.acciones_posibles,mapa,posicion_agente));
                    break;
                case moveSW:
                    if(distancias[2][0] < gradiente_muro_encontrado)
                        gradiente_muro_encontrado = distancias[2][0];
                    this.acciones_posibles = new Acciones[]{Acciones.moveS,Acciones.moveSE,Acciones.moveE,Acciones.moveNE,Acciones.moveN,Acciones.moveNW,Acciones.moveW};
                    agente_seleccionado.setNextAction(comprobarDireccion(this.acciones_posibles,mapa,posicion_agente));
                    break;
                case moveS:
                    if(distancias[2][1] < gradiente_muro_encontrado)
                        gradiente_muro_encontrado = distancias[2][1];
                    this.acciones_posibles = new Acciones[]{Acciones.moveSE,Acciones.moveE,Acciones.moveNE,Acciones.moveN,Acciones.moveNW,Acciones.moveW,Acciones.moveSW};
                    agente_seleccionado.setNextAction(comprobarDireccion(this.acciones_posibles,mapa,posicion_agente));
                    break;
                case moveSE:
                    if(distancias[2][2] < gradiente_muro_encontrado)
                        gradiente_muro_encontrado = distancias[2][2];
                    this.acciones_posibles = new Acciones[]{Acciones.moveE,Acciones.moveNE,Acciones.moveN,Acciones.moveNW,Acciones.moveW,Acciones.moveSW,Acciones.moveS};
                    agente_seleccionado.setNextAction(comprobarDireccion(this.acciones_posibles,mapa,posicion_agente));
                    break;
            }           
            
            
        }
        
    }
    
    private double calcularPuntuacion(EstadoAgente estado, Pair<Integer,Integer> posicion_destino){
        double puntuacion = 0;
        double distancia = calcularDistanciaEuclidea(estado.getPosicion(),posicion_destino);
        puntuacion = distancia * estado.getGasto();
        
        
        if(estado.getTipo()!= TiposAgente.dron){                
            puntuacion *= this.UMBRAL_PENALIZACION;
        }        
        return puntuacion;
    }
    
    //Devolveremos la direccion (norte sur, este...) donde teoricamente nos deberiamos mover si no hay obstaculos y la casilla que es el mapa del profesor.
    private Pair<Acciones,Pair<Integer,Integer>> calcularMejorCasilla(Pair<Integer,Integer> posicion_agente, Pair<Integer,Integer> posicion_destino){
        Pair<Integer,Integer> posicion_objetivo = new Pair(posicion_destino.getValue(),posicion_destino.getKey());
        //Cambiamos los indices para que concuerden con los del profesor.
        int i = posicion_agente.getKey();
        int j = posicion_agente.getValue();
        double minimo;
        Pair<Integer,Integer> resultado = null;
        Acciones direccion = null;
        
        
        distancias = new double[3][3];
        
        
        distancias[0][0] = calcularDistanciaEuclidea(new Pair(i-1,j-1),posicion_objetivo);
        distancias[0][2] = calcularDistanciaEuclidea(new Pair(i-1,j+1),posicion_objetivo);
        distancias[0][1] = calcularDistanciaEuclidea(new Pair(i-1,j),posicion_objetivo);
        
        distancias[1][2] = calcularDistanciaEuclidea(new Pair(i,j+1),posicion_objetivo); 
        distancias[1][0] = calcularDistanciaEuclidea(new Pair(i,j-1),posicion_objetivo);
        
        distancias[2][2] = calcularDistanciaEuclidea(new Pair(i+1,j+1),posicion_objetivo);
        distancias[2][1] = calcularDistanciaEuclidea(new Pair(i+1,j),posicion_objetivo);
        distancias[2][0] = calcularDistanciaEuclidea(new Pair(i+1,j-1),posicion_objetivo);
        
        minimo = distancias[0][0];
		resultado = new Pair(i-1,j-1);
		direccion = Acciones.moveNW;
		
        //Puede fallar la conversion de los indices
        for(int c=0; c < 3; c++){
            for(int h=0; h<3; h++){
                if((c!=1)&&(h!=1)){
                    if(distancias[c][h]<minimo)
                    {
                        minimo = distancias[c][h];
                        resultado = new Pair(i+(c-1),j+(h-1));
                        int indice_i = c;
                        int indice_j = h;

                        if((indice_i == 0)&&(indice_j == 0))
                            direccion = Acciones.moveNW;
                        if((indice_i == 0)&&(indice_j == 1))
                            direccion = Acciones.moveN;
                        if((indice_i == 0)&&(indice_j == 2))
                            direccion = Acciones.moveNE;
                        if((indice_i == 1)&&(indice_j == 0))
                            direccion = Acciones.moveW;
                        if((indice_i == 1)&&(indice_j == 2))
                            direccion = Acciones.moveE;
                        if((indice_i == 2)&&(indice_j == 0))
                            direccion = Acciones.moveSW;
                        if((indice_i == 2)&&(indice_j == 1))
                            direccion = Acciones.moveS;
                        if((indice_i == 2)&&(indice_j == 2))
                            direccion = Acciones.moveSE;
                    }
                }
            }
        }
        
        return new Pair(direccion,resultado);        
    }
    
    public EstadoAgente buscandoObjetivo(ArrayList<EstadoAgente> estados,boolean tenemosFuelEnElMundo){
        EstadoAgente agente_seleccionado;
        double minDistancia;
        ArrayList<Integer> indices_posibles = new ArrayList();
        int indice_agente_seleccionado = 0;
        
        
        
        if(this.subObjetivo == null){
            calcularSubObjetivo();//Analiza los cuadrantes y asigna un subObjetivo que sea un grupo de casillas inexploradas 3x3
        }
        ///Elegimos al agente que esté más cerca del objetivo
        minDistancia = calcularDistanciaEuclidea(estados.get(0).getPosicion(),this.subObjetivo);
        //Sacamos el agente que este mas cerca del objetivo, es decir, que su distancia hacia el objetivo sea la minima o que haya empatado con otro.
        for(int i = 1; i < estados.size(); i++){
            double distancia_aux = calcularDistanciaEuclidea(estados.get(i).getPosicion(),this.subObjetivo);
            
            if(distancia_aux < minDistancia){
                minDistancia = distancia_aux;
                indice_agente_seleccionado = i;
            }
        }
        
            
        for(int i = 0; i < estados.size(); i++){
            double distancia_aux = calcularDistanciaEuclidea(estados.get(i).getPosicion(),this.subObjetivo);
            if(distancia_aux <= minDistancia + this.UMBRAL_EMPATE ){
                indices_posibles.add(i);
            }            
        }  
        
        //Preferencia CAMION, AVION, COCHE.
        if(indices_posibles.size() > 0){
            int agente_seleccionado_empate = indices_posibles.get(0);
            for(int i =0; i < indices_posibles.size(); i++){
                switch(estados.get(indices_posibles.get(i)).getTipo()){
                    case camion :
                        if(estados.get(agente_seleccionado_empate).getTipo() != TiposAgente.camion)
                            agente_seleccionado_empate = indices_posibles.get(i);
                        break;
                    case dron :
                        if(estados.get(agente_seleccionado_empate).getTipo() == TiposAgente.coche)
                            agente_seleccionado_empate = indices_posibles.get(i);                        
                        break;
                }
            }
            indice_agente_seleccionado = agente_seleccionado_empate;            
        }
        
        agente_seleccionado = estados.get(indice_agente_seleccionado);
        if(agenteAnterior!=null && agente_seleccionado.getReplyWithControlador().compareTo(agenteAnterior.getReplyWithControlador())!=0){
            siguiendo_muro=false;
            gradiente_muro_encontrado = Double.MAX_VALUE;
        }
        agenteAnterior = agente_seleccionado;
        
        //Una vez seleccionado el agente que queremos comprobamos si hay fuel en el mundo
        //Si hay combustible lo hacemos repostar
        if(tenemosFuelEnElMundo){
            if(necesitaRepostar(agente_seleccionado)){
                agente_seleccionado.setNextAction(Acciones.refuel);

                return agente_seleccionado;
            }else{
				calcularSiguienteMovimiento(agente_seleccionado,agente_seleccionado.getPosicion(),this.subObjetivo);
			}
        }else
        {
            //Sino combrobamos si tiene combustible el agente
            if(agente_seleccionado.getFuelActual()>0){
                calcularSiguienteMovimiento(agente_seleccionado,agente_seleccionado.getPosicion(),this.subObjetivo);
            }else{
                //Si tenemos agentes en el array eliminamos el agente seleccionado 
                //porque no tiene fuel y no hay fuel en el mundo por lo que ya no nos sirve
                if(estados.size()>0){
                    estados.remove(agente_seleccionado);
                    return buscandoObjetivo(estados,false);
                }else{ //Sino tenemos más agentes que podamos mover devolvemos un EstadoAgente a null porque ya solo queda hacer el logout 
                    //CONDICIÓN DE PARADA SI NO HEMOS ENCONTRADO EL OBJETIVO
                    return null;
                }
            }
        }
        
        
        return agente_seleccionado;
    }
    
    public EstadoAgente objetivoAlcanzado(ArrayList<EstadoAgente> estados, Pair<Integer,Integer> objetivo,boolean tenemosFuelEnElMundo){
        ArrayList<EstadoAgente> agentes_no_en_el_objetivo = new ArrayList(estados);
         for(int i = 0; i < estados.size(); i++){
             if(estados.get(i).isPisandoObjetivo()){
                 agentes_no_en_el_objetivo.remove(i);
             }
         }
         
         return objetivoEncontrado(agentes_no_en_el_objetivo, objetivo, tenemosFuelEnElMundo);
    }

    public EstadoAgente objetivoEncontrado(ArrayList<EstadoAgente> estados, Pair<Integer,Integer> objetivo,boolean tenemosFuelEnElMundo){
        EstadoAgente agente_seleccionado;
        double minPuntuacion;
        ArrayList<Integer> indices_posibles = new ArrayList();
        int indice_agente_seleccionado = 0;
        
        
        ///Elegimos al agente que esté más cerca del objetivo
        minPuntuacion = calcularPuntuacion(estados.get(0),objetivo);
        //Sacamos el agente que este mas cerca del objetivo, es decir, que su distancia hacia el objetivo sea la minima o que haya empatado con otro.
        for(int i = 1; i < estados.size(); i++){
            double puntuacion_aux = calcularPuntuacion(estados.get(i),objetivo);
            
            if(puntuacion_aux < minPuntuacion){
                minPuntuacion = puntuacion_aux;
                indice_agente_seleccionado = i;
            }
        }
        
            
        for(int i = 0; i < estados.size(); i++){
            double distancia_aux = calcularPuntuacion(estados.get(i),objetivo);
            if(distancia_aux <= minPuntuacion + this.UMBRAL_EMPATE_PUNTUACION ){
                indices_posibles.add(i);
            }            
        }  
        
        //Preferencia Avion, COCHE, camion
        if(indices_posibles.size() > 0){
            int agente_seleccionado_empate = indices_posibles.get(0);
            for(int i =0; i < indices_posibles.size(); i++){
                if(estados.get(indices_posibles.get(i)).getTipo()== TiposAgente.dron){
                        if(estados.get(agente_seleccionado_empate).getTipo() != TiposAgente.dron)
                            agente_seleccionado_empate = indices_posibles.get(i);
                }
            }
            indice_agente_seleccionado = agente_seleccionado_empate;            
        }
        
        agente_seleccionado = estados.get(indice_agente_seleccionado);
        
        if(agenteAnterior!=null && agente_seleccionado.getReplyWithControlador().compareTo(agenteAnterior.getReplyWithControlador())!=0){
            siguiendo_muro=false;
            gradiente_muro_encontrado = Double.MAX_VALUE;
        }
        agenteAnterior = agente_seleccionado;
        
        //Una vez seleccionado el agente que queremos comprobamos si hay fuel en el mundo
        //Si hay combustible lo hacemos repostar
        if(tenemosFuelEnElMundo){
            if(necesitaRepostar(agente_seleccionado)){
                agente_seleccionado.setNextAction(Acciones.refuel);

                return agente_seleccionado;
            }
			else{
				calcularSiguienteMovimiento(agente_seleccionado,agente_seleccionado.getPosicion(),objetivo);
			}
        }else
        {
            //Sino combrobamos si tiene combustible el agente
            if(agente_seleccionado.getFuelActual()>0){
                calcularSiguienteMovimiento(agente_seleccionado,agente_seleccionado.getPosicion(),objetivo);
            }else{
                //Si tenemos agentes en el array eliminamos el agente seleccionado 
                //porque no tiene fuel y no hay fuel en el mundo por lo que ya no nos sirve
                if(estados.size()>0){
                    estados.remove(agente_seleccionado);
                    return objetivoEncontrado(estados,objetivo,false);
                }else{ //Sino tenemos más agentes que podamos mover devolvemos un EstadoAgente a null porque ya solo queda hacer el logout 
                    //CONDICIÓN DE PARADA SI NO HEMOS ENCONTRADO EL OBJETIVO
                    return null;
                }
            }
        }
        
        
        return agente_seleccionado;
    }
    
    //Puede que no este correcto. IMPORTANTE.
    private double calcularDistanciaEuclidea(Pair<Integer,Integer> posicion_objetivo,Pair<Integer,Integer> posicion_agente){ 
        double d=0; 
        d = Math.sqrt(((posicion_objetivo.getValue() - posicion_agente.getValue())*(posicion_objetivo.getValue() - posicion_agente.getValue()))+((posicion_objetivo.getKey() - posicion_agente.getKey())*(posicion_objetivo.getKey() - posicion_agente.getKey()))); 
        return d; 
    }
    
    //Calcula el numero de casillas inexploradas de cada cuadrante y elegimos el cuadrante con mas inexploradas y asignamos el nuevo objetivo.
    private void calcularSubObjetivo(){
        int mapa [][] = this.linkbc.getMapa();
        int cont_cuadrante_1 = 0;
        int cont_cuadrante_2 = 0;
        int cont_cuadrante_3 = 0;
        int cont_cuadrante_4 = 0;
        int maxCuadrante = 0;
        int cuadrante = 1;
        for(int i = 0; i < tamMapa; i++){
            for(int j = 0; j < tamMapa; j++){
				System.out.print(mapa[i][j]+" ");
			}
			System.out.println();
		}
		
		
        for(int i = 5; i < tamMapa; i++){
            for(int j = 5; j < tamMapa; j++){
                //Contamos el numero de casillas inexploras que hay en cada cuadrante. Los if filtran si los indices estan dentro de cada cuadrante.
                if((i>=this.cuadrante_1_i_inicio)&&(i<= this.cuadrante_1_i_final)&&(j>=this.cuadrante_1_j_inicio)&&(j<=this.cuadrante_1_j_final)){
                    if(mapa[i][j]==5)
                        cont_cuadrante_1++;
                }
                if((i>=this.cuadrante_2_i_inicio)&&(i<= this.cuadrante_2_i_final)&&(j>=this.cuadrante_2_j_inicio)&&(j<=this.cuadrante_2_j_final)){
                    if(mapa[i][j]==5)
                        cont_cuadrante_2++;
                }
                if((i>=this.cuadrante_3_i_inicio)&&(i<= this.cuadrante_3_i_final)&&(j>=this.cuadrante_3_j_inicio)&&(j<=this.cuadrante_3_j_final)){
                    if(mapa[i][j]==5)
                        cont_cuadrante_3++;
                }
                if((i>=this.cuadrante_4_i_inicio)&&(i<= this.cuadrante_4_i_final)&&(j>=this.cuadrante_4_j_inicio)&&(j<=this.cuadrante_4_j_final)){
                    if(mapa[i][j]==5)
                        cont_cuadrante_4++;
                }
            }
        }
        
        //Sacamos el cuadrante con mas ceros
        maxCuadrante = cont_cuadrante_1;
        
        
        if(maxCuadrante<cont_cuadrante_2){
            maxCuadrante = cont_cuadrante_2;
            cuadrante = 2;
        }
        
        if(maxCuadrante<cont_cuadrante_3){
            maxCuadrante = cont_cuadrante_3;
            cuadrante = 3;
        }
        
        if(maxCuadrante<cont_cuadrante_4){
            maxCuadrante = cont_cuadrante_4;
            cuadrante = 4;
        }
        
        int indice_i_inicio=0,indice_i_final=0,indice_j_inicio=0,indice_j_final=0;
        
        //Vamos a quitarle 6 a los limites porque la matriz es mas grande y no nos interesa asignar como objetivo los bordes del mapa.
        switch(cuadrante){
            case 1:
                indice_i_inicio = this.cuadrante_1_i_inicio + 6;
                indice_i_final = this.cuadrante_1_i_final;
                
                indice_j_inicio = this.cuadrante_1_j_inicio + 6;
                indice_j_final = this.cuadrante_1_j_final;
                
                break;
            case 2:
                indice_i_inicio = this.cuadrante_2_i_inicio;
                indice_i_final = this.cuadrante_2_i_final - 6;
                
                indice_j_inicio = this.cuadrante_2_j_inicio + 6;
                indice_j_final = this.cuadrante_2_j_final;
                break;
            case 3:
                indice_i_inicio = this.cuadrante_3_i_inicio + 6;
                indice_i_final = this.cuadrante_3_i_final;
                
                indice_j_inicio = this.cuadrante_3_j_inicio;
                indice_j_final = this.cuadrante_3_j_final -6;
                break;
            case 4:
                indice_i_inicio = this.cuadrante_4_i_inicio;
                indice_i_final = this.cuadrante_4_i_final -6;
                
                indice_j_inicio = this.cuadrante_4_j_inicio;
                indice_j_final = this.cuadrante_4_j_final -6;
                break;
        }
        
        //Se acabara el bucle cuando no haya un grupo de casillas inexploradas 3x3 o se haya encontrado un subObjetivo
        for(int i = indice_i_inicio; (i < indice_i_final)&&(this.subObjetivo == null); i++){
            for(int j = indice_j_inicio; (j < indice_j_final)&&(this.subObjetivo == null); j++){
                int contador = 0;
                if(mapa[i-1][j-1]==5)
                    contador++;
                if(mapa[i-1][j+1]==5)
                    contador++;
                if(mapa[i-1][j]==5)
                    contador++;                
                if(mapa[i][j-1]==5)
                    contador++;
                if(mapa[i][j]==5)
                    contador++;
                if(mapa[i][j+1]==5)
                    contador++;
                if(mapa[i+1][j]==5)
                    contador++;
                if(mapa[i+1][j-1]==5)
                    contador++;
                if(mapa[i+1][j+1]==5)
                    contador++;
                
                if(contador == 9)
                    this.subObjetivo = new Pair<Integer,Integer>(i,j);
            }
        }
    }
}