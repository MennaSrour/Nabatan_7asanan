package com.menna.nabata_7asena.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResourceTest {
    @Test
    fun loading_isInstance() {
        assertTrue(Resource.Loading is Resource<*>)
    }

    @Test
    fun success_holdsData() {
        val data = listOf("a", "b")
        val res = Resource.Success(data)
        assertEquals(data, res.data)
    }

    @Test
    fun error_holdsThrowable() {
        val e = Exception("boom")
        val res = Resource.Error(e)
        assertEquals("boom", res.throwable?.message)
    }
}

