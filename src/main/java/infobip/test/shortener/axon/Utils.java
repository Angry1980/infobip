package infobip.test.shortener.axon;

import org.axonframework.commandhandling.CommandExecutionException;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.PersistenceException;

public class Utils {

    private Utils(){}

    public static boolean isKeyDuplication(CommandExecutionException e){
        return e.getCause() != null
                && e.getCause() instanceof PersistenceException
                && isKeyDuplication((PersistenceException) e.getCause());
    }

    public static boolean isKeyDuplication(PersistenceException e){
        return e.getCause() != null
                && e.getCause() instanceof ConstraintViolationException;
    }

}
