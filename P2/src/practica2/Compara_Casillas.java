/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

import java.util.Comparator;
import javafx.util.Pair;

/**
 *
 * @author NeN
 */
public class Compara_Casillas implements Comparator{

    @Override
    public int compare(Object o1, Object o2) {
        int res=0;
        Pair<Integer,Integer> ele1 = (Pair<Integer,Integer>)o1;
        Pair<Integer,Integer> ele2 = (Pair<Integer,Integer>)o2;

        if(ele1.getValue()<ele2.getValue())
            res=1;
        else if(ele1.getValue()>ele2.getValue())
             res=-1;
        else
            res=0;
                
        return res;
    }
    
}
