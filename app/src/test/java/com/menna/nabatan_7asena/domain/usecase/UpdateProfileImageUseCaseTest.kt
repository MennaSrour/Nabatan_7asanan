package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.testsupport.FakeUserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class UpdateProfileImageUseCaseTest {
    private val repository = FakeUserRepository()
    private val update = UpdateProfileImageUseCase(repository)

    @Test fun savesTheSelectedIndexForTheSavedUser() = runTest {
        assertTrue(update("saved-user", User.Gender.GIRL, 4) is Resource.Success)
        assertEquals(4, repository.user.value.avatarIndex)
    }

    @Test fun invalidInputDoesNotReachPersistence() = runTest {
        assertTrue(update("", User.Gender.GIRL, 1) is Resource.Error)
        assertTrue(update("saved-user", User.Gender.GIRL, -1) is Resource.Error)
        assertTrue(repository.avatarWrites.isEmpty())
    }

    @Test fun repositoryFailuresUseTheExistingErrorContract() = runTest {
        val failure = IllegalStateException("Disk full")
        repository.saveAvatar = { throw failure }
        assertSame(failure, (update("saved-user", User.Gender.GIRL, 4) as Resource.Error).throwable)
    }

    @Test fun cancellationIsPropagated() = runTest {
        repository.saveAvatar = { throw CancellationException("Cancelled") }
        try {
            update("saved-user", User.Gender.GIRL, 4)
            fail("Expected cancellation")
        } catch (_: CancellationException) {
            assertEquals(1, repository.user.value.avatarIndex)
        }
    }
}
