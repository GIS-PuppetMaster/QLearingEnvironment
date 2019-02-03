import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;


public class MainScreenOutput {
    /*Agent的动作列表*/
    final static Hashtable action=new Hashtable(){
        {
            //始化action
            put(0,"NULL");
            put(1,"goForward");
            put(2,"goBack");
            put(3,"holdHead");
            put(4,"holdBody");
            put(5,"holdLeg");
            put(6,"attackHead");
            put(7,"attackBody");
            put(8,"attackLeg");
        }
    };//无排序，线程安全

    public static void showStateUpdate(Agent agent){
        LinkedHashMap state;
        state=agent.getState();
        Iterator iterator=state.keySet().iterator();
        System.out.println("StateUpdate:");
        System.out.println("    "+agent.getId()+":");
        while(iterator.hasNext()){
            Object key=iterator.next();
            System.out.println("        "+key.toString()+":"+action.get(state.get(key)));
        }
    }
    public static void showCurrentState(Agent agent){
        LinkedHashMap state=agent.getState();
        Iterator iterator=state.keySet().iterator();
        System.out.println("CurrentState:");
        System.out.println("    "+agent.getId()+":");
        while(iterator.hasNext()){
            Object key=iterator.next();
            System.out.println("        "+key.toString()+":"+state.get(key));
        }
    }
}
