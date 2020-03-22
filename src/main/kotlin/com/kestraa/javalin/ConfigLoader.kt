package com.kestraa.javalin

import io.javalin.core.util.Util
import io.javalin.plugin.json.JavalinJson
import java.nio.charset.Charset

data class Server(val host: String, val port: Int)

data class AppConfig(
    val server: Server
)

object ConfigurationLoader {
    
    fun load(fileName: String): AppConfig {
        val settingsJson = Util.getResourceUrl(fileName)?.readText(Charset.defaultCharset()) ?: ""
        return JavalinJson.fromJson(settingsJson, AppConfig::class.java)
    }
    
}
