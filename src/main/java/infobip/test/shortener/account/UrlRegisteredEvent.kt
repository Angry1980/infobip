package infobip.test.shortener.account

import infobip.test.shortener.model.UrlData

data class UrlRegisteredEvent(val accountId: String? = null,
                              val data: UrlData? = null,
                              val path: String? = null)