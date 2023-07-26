package io.sentry.samples.example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import io.sentry.Sentry;
import io.sentry.SentryOptions;
import io.sentry.SentryEvent;
import java.io.PrintStream;
import java.util.Arrays;

@Component
public class CustomBeforeSendCallback implements SentryOptions.BeforeSendCallback {
  
    @Override
    public SentryEvent execute(SentryEvent event, Object hint) {
        // Data Scrubbing
        // Example: Never send server name in events
        // event.setServerName(null);

        // Fingerprinting
        // Example: Group together errors that are not captured by the Application class's logger.
        // if (event.getLogger() != "io.sentry.samples.example.Application" && event.getLogger() != null) {
        //     System.out.print("\nnon-app, so set same fingerprint\n");
        //     event.setFingerprints(Arrays.asList("non-app"));
        // }
        return event;
    }
}