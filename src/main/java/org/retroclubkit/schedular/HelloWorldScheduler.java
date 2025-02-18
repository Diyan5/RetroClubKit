package org.retroclubkit.schedular;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class HelloWorldScheduler {

    // Scheduled Job = Cron Job
//    @Scheduled(fixedDelay = 20000)
    public void sayHellowEvery10Seconds() {

        System.out.println(LocalDateTime.now() + " Hello World!!!!");
    }
}
