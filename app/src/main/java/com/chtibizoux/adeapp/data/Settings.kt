package com.chtibizoux.adeapp.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class Settings(
    val lastUpdate: Long
)

class SettingsSerializer constructor() : Serializer<Settings> {

    override val defaultValue = Settings(lastUpdate = 0)

    override suspend fun readFrom(input: InputStream): Settings =
        try {
            Json.decodeFromString(
                Settings.serializer(), input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: Settings, output: OutputStream) {
        output.write(
            Json.encodeToString(Settings.serializer(), t)
                .encodeToByteArray()
        )
    }
}