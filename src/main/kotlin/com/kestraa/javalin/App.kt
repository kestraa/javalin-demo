package com.kestraa.javalin

import io.javalin.Javalin
import io.javalin.core.event.EventListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

// global logger instance
val logger: Logger = LoggerFactory.getLogger("Application")

// application events
val applicationEvents = Consumer<EventListener> { event ->
    event.serverStarted { logger.info("Application has been started.") }
    event.serverStopped { logger.info("Application has been stopped.") }
}

fun main() {
    val config = Configuration.load("config.json")
    
    val app = Javalin.create(Configuration.configure())
        .events(applicationEvents)
        .exception(Exception::class.java, exceptionHandler)
        .routes(registerHandlers())
    
    Runtime.getRuntime().addShutdownHook(Thread() { app.stop() })
    
    app.start(config.server.host, config.server.port)
}
