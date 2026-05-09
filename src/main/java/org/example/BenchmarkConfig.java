package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class BenchmarkConfig {

    @JsonProperty("benchmark_settings")
    public BenchmarkSettings benchmarkSettings = new BenchmarkSettings();

    @JsonProperty("sut_parameters")
    public SutParameters sutParameters = new SutParameters();

    public static class BenchmarkSettings {
        public String mode = "AverageTime";
        @JsonProperty("time_unit") public String timeUnit = "MILLISECONDS";
        public int forks = 1;
        @JsonProperty("warmup_iterations") public int warmupIterations = 2;
        @JsonProperty("measurement_iterations") public int measurementIterations = 25;
        @JsonProperty("output_csv") public String outputCsv = "results.csv";
        @JsonProperty("gc_profiler") public boolean gcProfiler = true;
    }

    public static class SutParameters {
        @JsonProperty("expiring_cache") public ExpiringCacheParams expiringCache = new ExpiringCacheParams();
        @JsonProperty("retry_service") public RetryServiceParams retryService = new RetryServiceParams();
        @JsonProperty("async_processor") public AsyncProcessorParams asyncProcessor = new AsyncProcessorParams();
    }

    public static class ExpiringCacheParams {
        @JsonProperty("ttl_ms") public long ttlMs = 100;
    }

    public static class RetryServiceParams {
        @JsonProperty("max_attempts") public int maxAttempts = 3;
        @JsonProperty("delay_ms") public long delayMs = 500;
    }

    public static class AsyncProcessorParams {
        @JsonProperty("delay_ms") public long delayMs = 1000;
    }

    public static BenchmarkConfig load(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            return new BenchmarkConfig();
        }
        return new ObjectMapper().readValue(file, BenchmarkConfig.class);
    }
}
