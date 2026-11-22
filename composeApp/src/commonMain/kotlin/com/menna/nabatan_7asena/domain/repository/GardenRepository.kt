package com.menna.nabatan_7asena.domain.repository

import com.menna.nabatan_7asena.domain.Resource
import com.menna.nabatan_7asena.domain.entity.GardenProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface GardenRepository {
    fun observe(profileId: String): Flow<Resource<GardenProgress>>
    suspend fun creditTasks(profileId: String, date: LocalDate, completedIds: Set<Int>): GardenProgress
    suspend fun water(profileId: String, date: LocalDate, treeId: String? = null): com.menna.nabatan_7asena.domain.entity.GardenWatering
    suspend fun buy(profileId: String, item: com.menna.nabatan_7asena.domain.entity.GardenDecoration): com.menna.nabatan_7asena.domain.entity.GardenPurchase
    suspend fun equip(profileId: String, item: com.menna.nabatan_7asena.domain.entity.GardenDecoration, equipped: Boolean): GardenProgress
    suspend fun buyProduct(profileId: String, product: com.menna.nabatan_7asena.domain.entity.GardenProduct): com.menna.nabatan_7asena.domain.entity.GardenPurchase
    suspend fun plantTree(profileId: String, treeId: String, plot: Int?): GardenProgress
    suspend fun showTree(profileId: String, treeId: String, visible: Boolean): GardenProgress
    suspend fun placeDecoration(profileId: String, item: com.menna.nabatan_7asena.domain.entity.GardenDecoration, plot: Int): GardenProgress
    suspend fun managePlacement(profileId: String, placementId: String, plot: Int? = null, visible: Boolean? = null, remove: Boolean = false): GardenProgress
    suspend fun sync(profileId: String): Boolean
}
