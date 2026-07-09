package logic.dao;

import logic.model.User;
import logic.utils.enums.Status;

import java.util.List;

public interface ParticipationDAO {

    void addWaitingPlayer(int userID, int campaignID);
    boolean isRequestAlreadyPresent(int userID, int campaignID);
    boolean removeRequestOfParticipation(int userID, int campaignID);
    List<User> getPlayersByStatus(int campaignID, String status);
    boolean acceptPlayer(int userID, int campaignID, Status status1);


}
