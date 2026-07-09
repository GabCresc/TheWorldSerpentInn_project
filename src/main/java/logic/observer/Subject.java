package logic.observer;
import java.util.List;
import java.util.ArrayList;

public abstract class Subject {

    private List<Observer> listOfObs = new ArrayList<>();

    public void attach (Observer obs) {listOfObs.add(obs);}
    public void detach (Observer obs) {listOfObs.remove(obs);}

    protected void notifyObs(){
        for (Observer o : listOfObs){
            o.update();
        }
    }
}
