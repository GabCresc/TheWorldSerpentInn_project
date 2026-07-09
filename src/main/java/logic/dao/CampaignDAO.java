package logic.dao;

import logic.beans.BeanFilter;
import logic.model.ModelCampaign;


import java.util.List;

public interface CampaignDAO {

    List<ModelCampaign> retrieveCampaigns();
    List<ModelCampaign> findCampaignByFilter(BeanFilter filter);
    ModelCampaign getCampaignById(int campaignID);
    boolean addCampaign(ModelCampaign camp);
    void deleteCampaign(int campaignID);

}
