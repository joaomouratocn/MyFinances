package br.com.arthiviatech.myfinances.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionLockPolicyTest {
    private val policy = SessionLockPolicy()

    @Test
    fun deviceWithoutSecureLock_entersDirectly() {
        assertFalse(policy.requiresAuthentication(false, false, null, 0))
    }

    @Test
    fun secureDevice_requiresAuthenticationOnFirstAccess() {
        assertTrue(policy.requiresAuthentication(true, false, null, 0))
    }

    @Test
    fun returnBeforeFiveMinutes_keepsSessionUnlocked() {
        assertFalse(policy.requiresAuthentication(true, true, 1_000, 300_999))
    }

    @Test
    fun returnAfterFiveMinutes_requiresAuthentication() {
        assertTrue(policy.requiresAuthentication(true, true, 1_000, 301_000))
    }
}
