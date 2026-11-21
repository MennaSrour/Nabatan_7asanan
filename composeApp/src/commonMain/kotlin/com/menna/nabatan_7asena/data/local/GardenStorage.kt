package com.menna.nabatan_7asena.data.local

import com.menna.nabatan_7asena.platform.PlatformPreferences

interface GardenStorage {
    fun read(): String?
    fun write(value: String)
}

internal class PlatformGardenStorage : GardenStorage {
    private val preferences = PlatformPreferences("good_deeds_garden")
    override fun read() = preferences.getString("garden_v1")
    override fun write(value: String) = preferences.putString("garden_v1", value)
}
