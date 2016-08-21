package infobip.test.shortener.service;

import infobip.test.shortener.axon.Utils;
import infobip.test.shortener.core.ApplicationException;
import infobip.test.shortener.account.ImmutableOpenAccountCommand;
import infobip.test.shortener.model.AccountData;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import rx.Observable;

import java.security.SecureRandom;
import java.util.Objects;

public class AccountServiceAxonImpl implements AccountService {

    private static SecureRandom random = new SecureRandom();

    private int passwordLength;
    private CommandGateway commandGateway;

    public AccountServiceAxonImpl(int passwordLength, CommandGateway commandGateway) {
        this.passwordLength = passwordLength > 0 ? passwordLength : 8;
        this.commandGateway = Objects.requireNonNull(commandGateway);
    }

    @Override
    public Observable<String> open(AccountData data) {
        return Observable.create(sub -> {
            String password = generatePassword();
            try {
                commandGateway.sendAndWait(ImmutableOpenAccountCommand.builder().data(data).password(password).build());
            } catch (CommandExecutionException e){
                sub.onError(Utils.isKeyDuplication(e) ? new ApplicationException("Account has been already opened") : e.getCause());
                return;
            }
            sub.onNext(password);
            sub.onCompleted();
        });
    }

    private String generatePassword(){
        StringBuilder password = new StringBuilder();
        while (password.length() < passwordLength) {
            char character = (char) random.nextInt(Character.MAX_VALUE);
            if ((character >= 'a' && character <= 'z')
                    || (character >= 'A' && character <= 'Z')
                    || (character >= '0' && character <= '9') ) {
                password.append(character);
            }
        }
        return password.toString();
    }

}
