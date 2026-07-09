package logic.dao;

import logic.controllers.abstract_factory_dao.DaoFactory;
import logic.exceptions.RequestAlreadySent;
import logic.model.User;
import logic.utils.enums.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level; // suggerito da SonarCloud
import java.util.logging.Logger;

public class ParticipationMemory implements ParticipationDAO {

    private static final Logger logger = Logger.getLogger(ParticipationMemory.class.getName());
    private static List<Participation> listPart = new ArrayList<>();

    public static class Participation{
            private int userID;
            private int campaignID;
            private String status;

            public Participation(int userID, int campaignID, String status){
                this.userID = userID;
                this.campaignID = campaignID;
                this.status = status;
            }

        public void setParticipationStatus(String status){
            this.status = status;
        }
    }

    @Override
    public void addWaitingPlayer(int userID, int campaignID) throws RequestAlreadySent{ //ok
        if(isRequestAlreadyPresent(userID, campaignID)){
            throw new RequestAlreadySent("Request already sent to this campaign!");
        }

        listPart.add(new Participation(userID, campaignID, Status.WAITING.name()));
        logger.log(Level.INFO, "Player {0} added to the waiting list successfully", userID);
    }

    @Override
    public boolean isRequestAlreadyPresent(int userID, int campaignID){ //ok
       return listPart.stream().anyMatch(p -> p.userID == userID && p.campaignID == campaignID);
    }

    @Override
    public boolean removeRequestOfParticipation(int userID, int campaignID){ //ok
        for(Participation p : listPart){
            if(p.userID == userID && p.campaignID == campaignID){
               listPart.remove(p);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> getPlayersByStatus(int campaignID, String status){ //ok

        List<User> players = new ArrayList<>();
        for(Participation p : listPart){
            if(p.status.equalsIgnoreCase(status) && p.campaignID == campaignID){
                UserDAO userDao = DaoFactory.getFactory().createUserDAO();
                User user = userDao.retrieveUserByUserID(p.userID);
                if(user != null) {
                    players.add(user);
                }
            }
        }
        if(players.isEmpty()){
            logger.log(Level.INFO, "Player not found");
            return players;
        }
        return players;
    }

    @Override
    public boolean acceptPlayer(int userID, int campaignID, Status status1){

        for(Participation p : listPart){
            if(p.userID == userID && p.campaignID == campaignID){
                p.setParticipationStatus(status1.toString());
                return true;
            }
        }
        return false;
    }



}
