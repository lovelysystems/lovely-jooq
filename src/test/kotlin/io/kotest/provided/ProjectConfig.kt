package io.kotest.provided

import DBExtension
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension

object ProjectConfig : AbstractProjectConfig() {

    override fun extensions(): List<Extension> = listOf(
        DBExtension,
    )
}
