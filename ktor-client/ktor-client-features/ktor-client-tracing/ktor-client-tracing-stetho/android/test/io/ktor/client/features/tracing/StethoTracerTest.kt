/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.features.tracing

import android.app.*
import android.content.*
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import org.junit.Test
import org.mockito.Mockito.*
import kotlin.collections.get
import kotlin.test.*

class StethoTracerTest {
    @Test
    fun testStetho() {
        val context = mock(Context::class.java)
        val application = mock(Application::class.java)

        doReturn(application).`when`(context).applicationContext

        val factory: HttpClientEngineFactory<CIOEngineConfig> = CIO
        val stethoFactory = context.Stetho(factory)
        val engine = stethoFactory.create()
        assertTrue { engine is EngineWithTracer }
    }

    @Test
    fun testHttpClient() {
        val activity = createTestAndroidActivity()
        with(activity) {
            val client = HttpClient(Stetho(CIO))
            val page = runBlocking { client.get<String>("http://www.google.com") }
            assertNotNull(page)
        }
    }
}

private fun createTestAndroidActivity(): Activity {
    val activity = mock(Activity::class.java)
    val application = mock(Application::class.java)

    doReturn(application).`when`(activity).applicationContext

    return activity
}
