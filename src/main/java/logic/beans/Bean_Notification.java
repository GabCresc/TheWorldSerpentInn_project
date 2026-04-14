public class Bean_Notification {

    private String notified_name;
    private String notifier_name;
    private Integer notification_id;
    // usertype e notification_type dalle enumerazioni. Teoricamente vanno importate

    // SETTERS

    public void setNotifier_name(String notifier_name) {
        this.notifier_name = notifier_name;
    }

    public void setNotified_name(String notified_name) {
        this.notified_name = notified_name;
    }

    public void setNotification_ID(Integer notification_ID) {
        this.notification_id = notification_ID;
    }

    // public void setNotificationType(Notification_types type_notif){
    //  this.type_notif = type_notif;
    // }

    // public void setUserType(UserTypes type_user){
    //    //  this.type_user = type_user;
    //    // }

    //GETTERS

    public void getNotifier_name() {
        return this.notifier_name;
    }
    public void getNotified_name() {
        return this.notified_name;
    }

    public void getNotification_ID() {
        return this.notification_id;
    }

    // public void getNotificationType(){
    //    // return this.type_notif;
    //    // }

    // public void getUserType(){
    //    // return this.type_user;
    //    // }
}


