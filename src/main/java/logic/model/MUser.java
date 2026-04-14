
public class MUser {

    private String username;
    private String password;
    private Integer userID;
    //private UserTypes usertype;
    private String firstname;
    private String lastname;
    private String birthdate;
    private String gender;

    public MUser(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void setUserAndPassByBean(BUserData dataBean) {
        this.username = dataBean.getUsername();
        this.password = dataBean.getPassword();
    }

    public void setCredentialsByBean(Bean_User dataBean) {
        this.firstname = dataBean.getFirstName();
        this.lastname = dataBean.getLastName();
        this.birthdate = dataBean.getBirthDate().toString();
        this.gender = dataBean.getGender();
        this.username = dataBean.getUsername();
        this.password = dataBean.getPassword();
       // this.usertype = dataBean.getUserType();
    }
    public String getFirstName(){
        return this.firstname;
    }
    public String getLastName(){
        return this.lastname;
    }
    public String getUsername(){
        return this.username;
    }
    public String getBirthdate(){
        return this.birthdate;
    }
    public String getGender(){
        return this.gender;
    }
    public String getPassword(){
        return this.password;
    }
 // sono necessari i Setters? ChangePassword? ChangeUsername? ecc...
}
