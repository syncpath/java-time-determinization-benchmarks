package org.example;

import org.junit.jupiter.api.Test;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RetryServiceTest {
   //1. реальное время
   @Test
   void testRetryWithRealSleep() {
       Sleeper realSleeper = millis -> {
           try {
               Thread.sleep(millis);
           } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
           }
       };

       RetryService retryService = new RetryService(3, 500, realSleeper);

       Supplier<String> failingAction = () -> {
           throw new RuntimeException("Сервер недоступен");
       };
       assertThrows(RuntimeException.class, () -> retryService.execute(failingAction));
   }

   //2. инъекция зависимости, virtual sleeper
   @Test
   void testRetryWithFastSleeper() {
       Sleeper fastSleeper = millis -> {
       };

       RetryService retryService = new RetryService(3, 5000, fastSleeper);

       int[] attemptcount = {0};

       Supplier<String> failingAction = () -> {
           attemptcount[0]++;
           throw new RuntimeException("Ошибка баз данных");
       };

       assertThrows(RuntimeException.class, () -> retryService.execute(failingAction));
       assertEquals(3, attemptcount[0], "Должно быть 3 попытки");
   }
}
