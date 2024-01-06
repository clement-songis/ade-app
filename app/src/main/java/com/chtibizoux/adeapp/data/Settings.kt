package com.chtibizoux.adeapp.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class Settings(
    val user: User? = null,
    val firstTime: Boolean = true,
    val alarms: PersistentList<Alarm> = persistentListOf(),
    val defaultAlarmRepeat: Int = 1,
    val defaultInterval: Int = 0,
//    val defaultRingTone: String,
//    val defaultVibration: Boolean,
)

@Serializable
data class User(val resourceId: Int, val data: String)

@Serializable
data class Alarm(
    val forHour: String,
    val hours: PersistentList<String>,
    val summary: String,
    val description: String,
    //  val ringTone: String, uri or "silent"
    //  val vibration: bool,
)

object SettingsSerializer : Serializer<Settings> {

    override val defaultValue = Settings()

    override suspend fun readFrom(input: InputStream): Settings = try {
        Json.decodeFromString(
            Settings.serializer(), input.readBytes().decodeToString()
        )
    } catch (serialization: SerializationException) {
        throw CorruptionException("Unable to read Settings", serialization)
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) {
        output.write(
            Json.encodeToString(Settings.serializer(), t).encodeToByteArray()
        )
    }
}

val Context.dataStore by dataStore("settings.json", SettingsSerializer)