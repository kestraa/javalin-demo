package com.kestraa.javalin

import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.apibuilder.EndpointGroup
import io.javalin.http.ExceptionHandler
import io.javalin.http.Handler
import io.javalin.http.UnauthorizedResponse
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

// used by 'saveUserHandler' to convert JSON request to an object
data class User(val name: String, val email: String, val password: String)

// used by async handler to simulate failures
val numRequest = AtomicInteger(0)

// generic type to represent a simple Json type
typealias JsonType = Map<String, Any>

// exception handler that will catch all errors from 'Exception' type
val exceptionHandler = ExceptionHandler<Exception> { err, ctx ->
    ctx.status(500).json(mapOf("status" to "fail", "message" to err.message))
}

// handler that will be performed before all other handlers
val beforeHandler = Handler { ctx ->
    val authToken = ctx.header("Authorization")
    val path = ctx.path()
    val isInsecurePath = ("/" == path || "/swagger" == path || "/docs" == path || path.startsWith("/webjars"))

    if (Objects.isNull(authToken) && !isInsecurePath) {
        logger.warn("Unauthorized request to $path")
        throw UnauthorizedResponse("Unauthorized access to $path")
    }

    logger.info("Client address: ${ctx.ip()} - Auth Token: $authToken")
}

// default handler
val indexHandler = Handler { ctx -> ctx.result("Hello Javalin API!\n") }

// this handler demonstrates how to get a path parameter from request, e.g 'name'
fun messageHandler() = Handler { ctx ->
    val name = ctx.pathParam("name")
    val res = mapOf("status" to "ok", "message" to "Hello $name!")
    ctx.json(res)
}

// handler that get data from request and convert it to an object
internal fun saveUserHandler() = Handler { ctx ->
    val user = ctx.body<User>()
    logger.info("Save user: ${user.name} - ${user.email}")
    ctx.status(201).json(mapOf("status" to "ok", "message" to "User created."))
}

// asynchronous handler - it has a delay of 500ms before respond
internal fun asyncHandler() = Handler { ctx ->
    val future = CompletableFuture<JsonType>().apply {
        Thread.sleep(500) // simulates heavy processing
        val counter = numRequest.incrementAndGet()
        if (counter % 3 == 0)
            completeExceptionally(Exception("Fail to process"))
        else
            complete(mapOf<String, Any>("status" to "ok", "message" to "$counter - Delayed result!"))
    }
    ctx.json(future)
}

fun registerHandlers() = EndpointGroup {
    before(beforeHandler)
    get("/", indexHandler)
    get("/async", asyncHandler())
    get("/:name", messageHandler())
    post("/user", saveUserHandler())
}