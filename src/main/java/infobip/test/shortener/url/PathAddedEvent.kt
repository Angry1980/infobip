package infobip.test.shortener.url

import infobip.test.shortener.model.UrlData

data class PathAddedEvent(val path: String? = null,
                          val data: UrlData? = null)