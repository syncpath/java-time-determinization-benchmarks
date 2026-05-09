package org.example;

import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.profile.GCProfiler;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {
        String configPath = args.length > 0 ? args[0] : "config.json";
        BenchmarkConfig cfg = BenchmarkConfig.load(configPath);

        OptionsBuilder builder = (OptionsBuilder) new OptionsBuilder()
                .include(".*Benchmark.*")
                .forks(cfg.benchmarkSettings.forks)
                .warmupIterations(cfg.benchmarkSettings.warmupIterations)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(cfg.benchmarkSettings.measurementIterations)
                .measurementTime(TimeValue.seconds(1))
                .timeUnit(TimeUnit.MILLISECONDS)
                .resultFormat(ResultFormatType.CSV)
                .result(cfg.benchmarkSettings.outputCsv);

        if (cfg.benchmarkSettings.gcProfiler) {
            builder.addProfiler(GCProfiler.class);
        }

        Options opt = builder.build();
        Collection<RunResult> results = new Runner(opt).run();

        System.out.println("Замеров завершено: " + results.size()
                + ". CSV сохранён в: " + cfg.benchmarkSettings.outputCsv);
    }
}
