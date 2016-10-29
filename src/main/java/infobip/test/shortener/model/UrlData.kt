package infobip.test.shortener.model

data class UrlData(val link: String? = null,
                   val redirectType: RedirectType = RedirectType.RT_302)