package infobip.test.shortener.url

import infobip.test.shortener.model.UrlData
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier

data class AddPathCommand(@TargetAggregateIdentifier val path: String? = null,
                          val data: UrlData? = null)