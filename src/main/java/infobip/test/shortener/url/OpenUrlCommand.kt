package infobip.test.shortener.url

import infobip.test.shortener.model.Url
import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier

data class OpenUrlCommand(@TargetAggregateIdentifier val path: String? = null,
                          val url: Url? = null)