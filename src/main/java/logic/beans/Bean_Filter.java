package logic.beans;
import logic.utils.enums.Mode;

public class Bean_Filter {
    private String name;
    private String time_session;
    private Mode mode;

    //SETTERS
    public void setNameCampaign(String name){ this.name = name;}
    public void setTimeSession(String timeSession){this.time_session = timeSession;}
    public void setMode(Mode mode){this.mode = mode;}

    //GETTERS
    public String getNameCampaign(){return this.name;}
    public String getTimeSession(){return this.time_session;}
    public Mode getMode(){return this.mode;}
}
