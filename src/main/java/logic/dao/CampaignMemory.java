package logic.dao;

import logic.beans.BeanFilter;
import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.model.ModelCampaign;
import logic.model.User;
import logic.utils.enums.Mode;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class CampaignMemory implements CampaignDAO {

    private static final HashMap<Integer, ModelCampaign> campaignById = new HashMap<>();
    private static int counter = 1;

    public CampaignMemory(){
        if (campaignById.isEmpty()) {
               loadData();
        }
    }

    private static void loadData() {
        ModelCampaign camp1 = new ModelCampaign();
        camp1.setCampId(counter);
        counter++;
        camp1.setCampName("La teoria dei giochi");
        camp1.setCampMode(Mode.ONLINE);
        camp1.setCampFreq("settimanale");
        camp1.setMaxNumberOfPlayers(5);
        String dateString = "2025-05-03 16:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime parsedDate = LocalDateTime.parse(dateString, formatter);
        camp1.setCampStartDate(parsedDate);
        camp1.setCampDmName("Yarissa");
        LocalTime time = LocalTime.parse("16:00:00");
        camp1.setTimeSession(time);
        camp1.setCampDmId(2);
        camp1.setPlatform("Discord");
        campaignById.put(camp1.getCampId(), camp1);

        ModelCampaign camp2 = new ModelCampaign();
        camp2.setCampId(counter);
        counter++;
        camp2.setCampName("La luce dell'alba");
        camp2.setCampMode(Mode.OFFLINE);
        camp2.setCampCity("Roma");
        camp2.setCampFreq("mensile");
        camp2.setMaxNumberOfPlayers(10);
        String dateString2 = "2025-07-03 10:00";
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime parsedDate2 = LocalDateTime.parse(dateString2, formatter2);
        camp2.setCampStartDate(parsedDate2);
        camp2.setCampDmName("Yarissa");
        LocalTime time2 = LocalTime.parse("16:00:00");
        camp2.setTimeSession(time2);
        camp2.setCampDmId(2);
        campaignById.put(camp2.getCampId(),camp2);
    }

    @Override
    public List<ModelCampaign> retrieveCampaigns() {
        ParticipationDAO memory = DaoFactory.getFactory().createParticipationDAO();

        return campaignById.values().stream().filter(camp -> {
            List<User> acceptedPlayers = memory.getPlayersByStatus(camp.getCampId(), "ACCEPTED");
            int currentPlayers;
            if(acceptedPlayers == null){
                 currentPlayers = 0;
            }else{ currentPlayers = acceptedPlayers.size();}
            return camp.getMaxPlayers() > currentPlayers;
        }).toList();
    }

    @Override
    public List<ModelCampaign> findCampaignByFilter(BeanFilter filter) { //ok
        return campaignById.values().stream()
                .filter(camp -> {
                    String filterName = filter.getNameCampaign();
                    if(filterName == null || filterName.isEmpty()){
                        return true; //elemento valido
                    }
                    return camp.getCampName().toLowerCase().contains(filterName.toLowerCase());
                })
                .filter(camp -> (filter.getMode() == null || filter.getMode() == camp.getCampMode())).toList();
    }

    @Override
    public ModelCampaign getCampaignById(int campaignID){ //ok
        return campaignById.get(campaignID);
    }

    @Override
    public boolean addCampaign(ModelCampaign camp){ //ok
        campaignById.put(camp.getCampId(), camp);
        return true;
    }

    @Override
    public void deleteCampaign(int campaignID){ //ok
        campaignById.remove(campaignID);
    }

}





