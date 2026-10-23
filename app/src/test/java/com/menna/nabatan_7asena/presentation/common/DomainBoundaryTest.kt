package com.menna.nabatan_7asena.presentation.common

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainBoundaryTest {
    @Test
    fun domainSources_doNotDependOnPlatformOrOuterLayers() {
        val domain = File("src/main/java/com/menna/nabatan_7asena/domain")
        assertTrue(domain.isDirectory)
        val forbiddenImport = Regex(
            "(?m)^import (android\\.|androidx\\.|java\\.|com\\.menna\\.nabatan_7asena\\.(data\\.|presentation\\.|core\\.|R(?:\\.|$)))"
        )
        val violations = domain.walkTopDown().filter { it.extension == "kt" }
            .filter { forbiddenImport.containsMatchIn(it.readText()) }.map { it.relativeTo(domain).path }.toList()
        assertTrue("Platform or outer-layer imports in domain: $violations", violations.isEmpty())
    }
}
