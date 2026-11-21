package com.menna.nabatan_7asena.domain.usecase

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.testsupport.FakeUserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class GetCurrentUserRankUseCaseTest {
    private val repository = FakeUserRepository()
    private val getRank = GetCurrentUserRankUseCase(repository)

    @Test fun zeroScoreAndEmptyTopListStillUseServerRank() = runTest {
        repository.fetchRank = { Resource.Success(501L) }
        assertEquals(Resource.Success(501L), getRank(repository.user.value.copy(totalStars = 0), emptyList()))
        assertEquals(listOf(0), repository.rankRequests)
    }

    @Test fun serverRanksRemainLongWithoutTruncation() = runTest {
        val rank = Int.MAX_VALUE.toLong() + 100
        repository.fetchRank = { Resource.Success(rank) }
        assertEquals(Resource.Success(rank), getRank(repository.user.value, emptyList()))
    }

    @Test fun outsideTop50RankIsNotArtificiallyClampedTo51WhenScoresTie() = runTest {
        val tied = (1..50).map {
            com.menna.nabatan_7asena.domain.entity.LeaderboardEntry("other-$it", "طفل",
                repository.user.value.gender, 50, 0, it)
        }
        repository.fetchRank = { Resource.Success(1L) }
        assertEquals(Resource.Success(1L), getRank(repository.user.value, tied))
        assertEquals(listOf(50), repository.rankRequests)
    }

    @Test fun cancellationIsNotTranslatedIntoRankFailure() = runTest {
        repository.fetchRank = { throw CancellationException("cancelled") }
        try {
            getRank(repository.user.value, emptyList())
            fail("Expected cancellation")
        } catch (expected: CancellationException) {
            assertEquals("cancelled", expected.message)
        }
    }
}
