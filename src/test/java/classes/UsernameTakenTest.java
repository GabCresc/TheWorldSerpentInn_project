package classes;

import logic.beans.BeanUser;
import logic.controllers.LoginControl;
import logic.dao.UserDAO;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.exceptions.TextTooShortException;
import logic.exceptions.UsernameTaken;
import logic.model.User;
import logic.utils.enums.UserTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

//Test della classe UsernameTaken
//Arianna Gabrieli

@ExtendWith(MockitoExtension.class)
 class UsernameTakenTest {
    private LoginControl loginControl;

    @Mock
    private UserDAO userDAO; //non sporchiamo il database, utente necessario per far sì che l'eccezione si verifichi

    @BeforeEach
    void setUp() {
        loginControl = new LoginControl(userDAO);
    }

    @Test //lanciamo qui le eccezioni: non vogliamo che il test vada avanti se si verificano queste eccezioni
    void exceptionDoesOccur() throws TextTooLongException, TextTooShortException, InvalidValueException {

        //istanziamo una beanUser
        BeanUser beanUser = new BeanUser();

        String testID = String.valueOf(System.currentTimeMillis());
        String fakeUsername = "TestUser" + testID;
        String fakeEmail = testID + "@example.com";

        beanUser.setUsername(fakeUsername);
        beanUser.setUserType(UserTypes.PLAYER);
        beanUser.setPassword("987654321");
        beanUser.setEmail(fakeEmail);

        //creiamo il finto utente con lo stesso username della bean
        User user = new User();
        user.setUsername(fakeUsername);
        when(userDAO.retrieveUserByUsername(fakeUsername)).thenReturn(user);


        Exception exception = assertThrows(UsernameTaken.class, () -> {
            loginControl.completeRegistration(beanUser);
        });
        //vediamo se l'eccezione si verifica
        String expectedMessage = "Questo username è già in utilizzo";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

}
