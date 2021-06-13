package de.timmi6790.mpstats.api.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class MetricsComponent {
    public static MeterRegistry registry = null;

    public MetricsComponent(final MeterRegistry registry) {
        log.info("Start metrics");
        MetricsComponent.registry = registry;

        // We need to register it this way, the config beans are not working ... for some reason
        new JvmThreadMetrics().bindTo(registry);
        new ClassLoaderMetrics().bindTo(registry);
        new JvmMemoryMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new UptimeMetrics().bindTo(registry);

        try (final JvmGcMetrics gcMetrics = new JvmGcMetrics()) {
            gcMetrics.bindTo(registry);
        } catch (final Exception e) {
            Sentry.captureException(e);
            log.catching(e);
        }
    }
}
