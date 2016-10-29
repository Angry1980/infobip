package infobip.test.shortener.rest

import java.util.*

data class AccountResult(val success: Boolean = true,
                         val description: String? = null,
                         val password: Optional<String> = Optional.empty())