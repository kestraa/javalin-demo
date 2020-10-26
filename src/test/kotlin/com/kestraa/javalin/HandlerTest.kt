package com.kestraa.javalin

import io.javalin.http.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class HandlerTest {
    private val ctx = mockk<Context>(relaxed = true)

    @Test
    fun `test authorization token`() {
        every { ctx.header("Authorization") } returns ""
        every { ctx.path() } returns "/"
        every { ctx.ip() } returns "localtest"

        beforeHandler.handle(ctx)

        verify { ctx.path() }
    }

    @Test
    fun `test index handler`() {
        val indexHandler = indexHandler()
        indexHandler.handle(ctx)
        verify { ctx.status(200) }
    }

    @Test
    fun `test message`() {
        every { ctx.pathParam("name") } returns "Test"

        val msgHandler = messageHandler()
        msgHandler.handle(ctx)

        verify { ctx.status(200) }
    }

    @Test
    fun `test save user`() {
        every { ctx.body<User>() } returns User("User Test", "user@test.com", "12345")

        val saveHandler = saveUserHandler()
        saveHandler.handle(ctx)

        verify { ctx.status(201) }
    }
}
