import java.time.LocalDate;

public class Bean_User {
    private Integer userID;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String gender;
    private UserTypes userType;
    private String username;
    private String password;
}

public Bean_User(){
    //empty
}


public Bean_User(String username){ //perché mettere anche questo e non usare semplicemente il costruttore sotto?
    this.username = username;
}

public Bean_User(String username,String password){
    this(username);
    this.password = password;
}

// setters

public void setFirstName(String firstName){ /* throws InvalidValueException, TextTooLongException {
    if(firstName == null || firstName.equalsIgnoreCase("")) {
        throw new InvalidValueException("Please insert a valid name");
    }
    else if(firstName.length() > 20) {
        throw new TextTooLongException("Too many characters for name field");
    }*/
    this.firstName = firstName;
}

public void setLastName(String lastName){/* throws InvalidValueException, TextTooLongException {
    if(lastName == null || lastName.equalsIgnoreCase("")){
        throw new InvalidValueException("Please insert a valid lastname");
    }
    else if(lastName.length() > 20) {
        throw new TextTooLongException("Too many characters for lastname field");
    }*/
    this.lastName = lastName;
}

public void setBirthDate(LocalDate birthDate) {  /* throws InvalidValueException {
        if(birthDate == null){
            throw new InvalidValueException("Please insert a valid date of birth");
        }*/
        this.birthDate = LocalDate.parse(birthDate.toString()); //parse legge il valore di un oggetto per restituirlo in
    // un altro tipo di dato
    }

public void setGender(String gender) { /* throws InvalidValueException {
    if(gender == null){
        throw new InvalidValueException("Please insert a valid gender type");
    }*/
    this.gender = gender;
}

public void setType(UserTypes typeOfUsr) { // mettere enum
    this.userType = typeOfUsr;
}

public void setUsername(String username) { /*throws InvalidValueException, TextTooLongException {
    if(usrName == null || usrName.equalsIgnoreCase("")) {
        throw new InvalidValueException("Please insert a valid username");
    }
    else if(usrName.length() > 30) {
        throw new TextTooLongException("Too many characters for username field");
    } */
    this.username = username;
}

public void setPassword(String password){ /* throws InvalidValueException, TextTooLongException {
    if(passwd == null || passwd.equalsIgnoreCase("")) {
        throw new InvalidValueException("Please insert a valid password");
    }
    else if(passwd.length() > 25) {
        throw new TextTooLongException("Too many character for password field");
    } */
    this.password = password;
}

public void setUserID(int userID){
    this.userID = userID;
}

public String getFirstName() {
    return this.firstName;
}

public String getLastName() {
    return this.lastName;
}

public LocalDate getBirthDate() {
    return this.birthDate;
}

public String getGender() {
    return this.gender;
}

public UserTypes getUserType() {
    return this.userType;
}

public String getCity() {
    return this.city;
}

public void getUsername(){
    return this.username;
}

public void getPassword(){
    return this.password;
}

public void getUserID(){
   return this.userID;
}