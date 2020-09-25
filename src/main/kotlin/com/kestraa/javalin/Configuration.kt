package com.kestraa.javalin

import io.javalin.core.JavalinConfig
import io.javalin.core.util.Util
import io.javalin.plugin.json.JavalinJson
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.swagger.v3.oas.models.info.Info
import org.eclipse.jetty.util.thread.QueuedThreadPool
import java.nio.charset.Charset
import java.util.function.Consumer

data class Server(val host: String, val port: Int)

data class AppConfig(
    val server: Server
)

object Configuration {

    // load external configuration file
    fun load(fileName: String): AppConfig {
        val settingsJson = Util.getResourceUrl(fileName)?.readText(Charset.defaultCharset()) ?: ""
        return JavalinJson.fromJson(settingsJson, AppConfig::class.java)
    }

    // Open API configuration
    fun openApiConfig(): OpenApiOptions {
        val info = Info().version("v1")
                .description("Simple OpenApi")
                .title("Basic User API")

        return OpenApiOptions(info)
                .path("/swagger")
                .swagger(SwaggerOptions("/docs").title("User API"))
    }

    // Javalin configuration
    fun configure() = Consumer<JavalinConfig> { config ->
        config.server { org.eclipse.jetty.server.Server(QueuedThreadPool(100, 25, 30_000)) }
        config.defaultContentType = "application/json; charset=UTF-8"
        config.showJavalinBanner = false
        config.enableCorsForAllOrigins()
        config.registerPlugin(OpenApiPlugin(openApiConfig()))
    }
}
