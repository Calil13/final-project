package org.example.finalproject.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.finalproject.service.OverdueEmailService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class OverdueEmailScheduler {

    private final OverdueEmailService overdueEmailService;

    @Scheduled(cron = "0 0 0  * * *") // hər gün saat 00:00
    public void sendOverdueEmails() {
        overdueEmailService.notifyOverdueOrders();
    }
}
