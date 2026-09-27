package br.com.arthiviatech.myfinances.security

class SessionLockPolicy(
    private val timeoutMillis: Long = DEFAULT_TIMEOUT_MILLIS,
) {
    fun requiresAuthentication(
        deviceIsSecure: Boolean,
        authenticatedInSession: Boolean,
        backgroundedAtMillis: Long?,
        nowMillis: Long,
    ): Boolean {
        if (!deviceIsSecure) return false
        if (!authenticatedInSession) return true
        return backgroundedAtMillis != null && nowMillis - backgroundedAtMillis >= timeoutMillis
    }

    companion object {
        const val DEFAULT_TIMEOUT_MILLIS = 5 * 60 * 1_000L
    }
}
