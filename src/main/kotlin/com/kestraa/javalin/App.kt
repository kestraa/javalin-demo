package com.kestraa.javalin

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.before
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.core.JavalinConfig
import io.javalin.core.event.EventListener
import io.javalin.http.ExceptionHandler
import io.javalin.http.Handler
import io.javalin.http.UnauthorizedResponse
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.swagger.v3.oas.models.info.Info
import org.slf4j.LoggerFactory
import java.util.Objects.isNull
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

// global logger instance
val logger = LoggerFactory.getLogger("Application")

// generic type to represent a simple Json type
typealias JsonType = Map<String, Any>

// used by async handler to simulate failures
val numRequest = AtomicInteger(0)

// used by 'saveUserHandler' to convert JSON request to an object
data class User(val name: String, val email: String, val password: String)

// Open API configuration
fun openApiConfig(): OpenApiOptions {
    val info = Info().version("v1").description("Simple OpenApi").title("Basic User API")
    return OpenApiOptions(info)
        .path("/swagger")
        .swagger(SwaggerOptions("/docs").title("User API"))
}

// Javalin configuration
val configApp = Consumer<JavalinConfig> { config ->
    config.defaultContentType = "application/json; charset=UTF-8"
    config.showJavalinBanner = false
    config.enableCorsForAllOrigins()
    config.registerPlugin(OpenApiPlugin(openApiConfig()))
}

// application events
val applicationEvents = Consumer<EventListener> { event ->
    event.serverStarted { logger.info("[INFO] Application has been started.") }
    event.serverStopped { logger.info("[INFO] Application has been stoped.") }
}

// handler that will be performed before all other handlers
val beforeHandler = Handler { ctx ->
    val authToken = ctx.header("Authorization")
    val path = ctx.path()
    val isInsecurePath = ("/" == path || "/swagger" == path || "/docs" == path || path.startsWith("/webjars"))
    
    if (isNull(authToken) && !isInsecurePath) {
        logger.warn("Unauthorized request to $path")
        throw UnauthorizedResponse("Unauthorized access to $path")
    }
    
    logger.info("Client address: ${ctx.ip()} - Auth Token: $authToken")
}

// exception handler that will catch all errors from 'Exception' type
val exceptionHandler = ExceptionHandler<Exception> { err, ctx ->
    ctx.status(500).json(mapOf("status" to "fail", "message" to err.message))
}

// default handler
val indexHandler = Handler { ctx -> ctx.result("Hello Javalin API!\n") }

// this handler demostrates how to get a path parameter from request, e.g 'name'
val messageHandler = Handler { ctx ->
    val name = ctx.pathParam("name")
    val res = mapOf("status" to "ok", "message" to "Hello $name!")
    ctx.json(res)
}

// asynchronous handler - it has a delay of 500ms before to respond
fun asyncHandler() = Handler { ctx ->
    val future = CompletableFuture<JsonType>().apply {
        Thread.sleep(500)
        val counter = numRequest.incrementAndGet()
        if (counter % 3 == 0)
            completeExceptionally(Exception("Fail to process async"))
        else
            complete(mapOf<String, Any>("status" to "ok", "message" to "$counter - Delayed result!"))
    }
    ctx.json(future)
}

// handler that get data from request and convert it to an object
val saveUserHandler = Handler { ctx ->
    val user = ctx.body<User>()
    logger.info("Save user: ${user.name} - ${user.email}")
    ctx.status(201).json(mapOf("status" to "ok", "message" to "User created."))
}

fun main() {
    val app = Javalin.create(configApp)
        .events(applicationEvents)
        .exception(Exception::class.java, exceptionHandler)
        .routes {
            before(beforeHandler)
            get("/", indexHandler)
            get("/async", asyncHandler())
            get("/:name", messageHandler)
            post("/user", saveUserHandler)
        }
    
    app.start("localhost", 8080)
}
