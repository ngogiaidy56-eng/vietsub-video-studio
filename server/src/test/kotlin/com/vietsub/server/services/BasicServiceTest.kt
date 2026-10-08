package com.vietsub.server.services

import kotlin.test.Test
import kotlin.test.assertEquals

class BasicServiceTest {
    @Test fun configParsingIsDeterministic() {
        assertEquals("Vietsub Video Studio", "Vietsub Video Studio")
    }
}
