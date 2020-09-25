package com.kestraa.javalin

import io.javalin.http.ExceptionHandler
import io.javalin.http.Handler
import java.util.concurrent.CompletableFuture

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

// handler that get data from request and convert it to an object
val saveUserHandler = Handler { ctx ->
    val user = ctx.body<User>()
    logger.info("Save user: ${user.name} - ${user.email}")
    ctx.status(201).json(mapOf("status" to "ok", "message" to "User created."))
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
