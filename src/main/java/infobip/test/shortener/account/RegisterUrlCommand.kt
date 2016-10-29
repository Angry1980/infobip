package infobip.test.shortener.account

import infobip.test.shortener.model.UrlData
import infobip.test.shortener.model.User
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier

data class RegisterUrlCommand(val user: User? = null,
                              val data: UrlData? = null,
                              val path: String? = null) {

    @TargetAggregateIdentifier
    fun getAccountId() = user?.accountId
}