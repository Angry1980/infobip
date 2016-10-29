package infobip.test.shortener.account

import infobip.test.shortener.model.AccountData

data class AccountOpenedEvent(val data: AccountData,
                              val password: String)