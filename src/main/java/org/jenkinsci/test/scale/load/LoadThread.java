/*
 * The MIT License
 *
 * Copyright (c) 2015 Red Hat, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.test.scale.load;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.test.scale.meta.Load;
import org.jenkinsci.test.scale.meta.LoadReport;
import org.jenkinsci.test.scale.meta.LoadReport.Builder;

/**
 * Load implementation to invoke operation periodically and sample response times.
 *
 * @author ogondza
 */
public abstract class LoadThread<Ret> extends Thread implements Load {

    private final List<Long> measurements = new ArrayList<Long>();
    private final long sleep;

    protected LoadThread(long sleep) {
        this.sleep = sleep;
        setName(this.getClass().getName());
    }

    protected LoadThread(String name, long sleep) {
        super(name);
        this.sleep = sleep;
    }

    protected abstract Ret invoke() throws Exception;

    @Override
    public void run() {
        try {
            for (;;) {
                long start = System.currentTimeMillis();
                invoke();
                synchronized(measurements) {
                    measurements.add(System.currentTimeMillis() - start);
                }
                Thread.sleep(sleep);
            }
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    public void terminate() throws InterruptedException {
        interrupt();
        join();
    }

    public LoadReport getReport() {
        Builder report = LoadReport.builder(getClass());
        report.put("invocations", measurements.size());
        long totalTime = 0;
        for (long m: measurements) {
            totalTime += m;
        }
        report.put("accumulated_time", totalTime);
        return report.build();
    }

    @Override
    public void interrupt() {
        // Ignore exception thrown while interrupting
        setUncaughtExceptionHandler(ignoreExceptions);
        super.interrupt();
    }

    private static final UncaughtExceptionHandler ignoreExceptions = new UncaughtExceptionHandler() {
        public void uncaughtException(Thread t, Throwable e) {
            // Do nothing
        }
    };
}
