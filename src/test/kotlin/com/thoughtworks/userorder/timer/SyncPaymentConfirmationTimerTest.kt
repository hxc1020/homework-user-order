package com.thoughtworks.userorder.timer

import com.ninjasquad.springmockk.SpykBean
import io.kotest.core.spec.style.StringSpec
import io.mockk.verify
import org.awaitility.kotlin.await
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration

@SpringBootTest
class SyncPaymentConfirmationTimerTest(
    @SpykBean val syncPaymentConfirmationTimer: SyncPaymentConfirmationTimer
) : StringSpec({

    "should execute at least 1" {
        await.atMost(Duration.ofMinutes(2))
            .untilAsserted { verify(atLeast = 1) { syncPaymentConfirmationTimer.execute() } }
    }
})
