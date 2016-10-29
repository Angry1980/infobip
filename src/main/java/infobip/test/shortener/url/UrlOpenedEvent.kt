package infobip.test.shortener.url

data class UrlOpenedEvent(val accountId: String? = null,
                          val link: String? = null,
                          val count: Int = 0)