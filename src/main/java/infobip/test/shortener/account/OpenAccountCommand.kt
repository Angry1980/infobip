package infobip.test.shortener.account

import infobip.test.shortener.model.AccountData
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier

data class OpenAccountCommand(val data: AccountData? = null,
                              val password: String? = null) {

    @TargetAggregateIdentifier fun getAccountId() = data?.accountId
}