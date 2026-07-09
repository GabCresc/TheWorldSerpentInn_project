package classes;

import logic.beans.BeanUser;
import logic.controllers.LoginControl;
import logic.dao.UserDAO;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.exceptions.TextTooShortException;
import logic.model.User;
import logic.utils.enums.UserTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Test della classe LoginControl
//Arianna Gabrieli

@ExtendWith(MockitoExtension.class)
 class LoginControlTest {

    private final Logger logger = Logger.getLogger(LoginControlTest.class.getName());
    @Mock
    private UserDAO userDAO; //in questo modo non sporchiamo il database

    private LoginControl loginControl;

    @BeforeEach
    void setUp() {
        loginControl = new LoginControl(userDAO);
    }

    @Test
    void isUsernameTakenWhenUserDoesNotExist(){

        String username = "Stark";

        when(userDAO.retrieveUserByUsername(username)).thenReturn(null); //rispondiamo con null quando chiamiamo il metodo della dao

        boolean result = loginControl.isUsernameTaken(username);
        assertFalse(result, "False if user does not exist");

    }

    @Test
    void isUsernameTakenWhenUserDoesExist(){

        String username = "Lannister";

        User fakeUser = new User(); //creiamo l'utente finto
        fakeUser.setUsername(username); // necessario per .equals(), non può ricevere null

        when(userDAO.retrieveUserByUsername(username)).thenReturn(fakeUser); //questa volta restituiamo l'utente finto

        boolean result = loginControl.isUsernameTaken(username);

        assertTrue(result, "True if username is taken");

    }

    @Test
    void completeRegistration() throws TextTooShortException, TextTooLongException, InvalidValueException {
        // generiamo un utente unico in modo da poter ripetere il test
        String testID = String.valueOf(System.currentTimeMillis());
        String fakeUsername = "TestUser" + testID;
        String fakeEmail = testID + "example.com";

        BeanUser beanUser = new BeanUser();
        beanUser.setUsername(fakeUsername);
        beanUser.setUserType(UserTypes.DM);
        beanUser.setPassword("123456789");
        beanUser.setEmail(fakeEmail);

        // controlliamo se il test va a buon fine assicurandoci che non lanci eccezioni
        assertDoesNotThrow(() -> {
            boolean result = loginControl.completeRegistration(beanUser);
            assertTrue(result, "Registration should return true");
        }, "UsernameTaken should not be happening with a random username");
    }

    @Test
    void loginStandardSuccess(){
        //creiamo l'utente fittizio
        String username = "testUser";
        String password = "testPassword";
        User fakeUser = new User();
        fakeUser.setUsername(username);
        fakeUser.setPassword(password);

        //quando chiamiamo il metodo con questi parametri, vogliamo fakeUser
        when(userDAO.verifyLogin(username, password, false)).thenReturn(fakeUser);

        //chiamiamo il metodo da testare
        BeanUser result = loginControl.loginStandard(username, password);

        //test
        assertEquals(username, result.getUsername());

    }

    @Test
    void loginStandardFailure(){
        //creiamo l'utente fittizio
        String username = "wrongUser";
        String password = "wrongPassword";
        User wrongUser = new User();
        wrongUser.setUsername(username);
        wrongUser.setPassword(password);

        //quando chiamiamo il metodo con questi parametri, vogliamo null
        when(userDAO.verifyLogin(username, password, false)).thenReturn(null);

        //chiamiamo il metodo da testare
        BeanUser result = loginControl.loginStandard(username, password);

        //test
        assertNull(result, "Credentials are wrong: result is null");

    }



}