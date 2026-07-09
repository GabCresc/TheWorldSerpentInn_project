package logic.beans;
import logic.utils.enums.Mode;

public class BeanFilter {
    private String name;
    private Mode mode;

    //SETTERS
    public void setNameCampaign(String name) {
        this.name = name;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    //GETTERS
    public String getNameCampaign() {
        return this.name;
    }

    public Mode getMode() {
        return this.mode;
    }

}