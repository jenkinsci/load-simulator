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
package com.redhat.jenkins.mwscaletest.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Load implementation to invoke operation periodically and sample response times.
 *
 * @author ogondza
 */
public abstract class LoadThread<Ret> extends Thread implements Load {

    private final List<Long> measurements = new ArrayList<Long>();

    protected LoadThread() {
        setName(this.getClass().getName());
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
                Thread.sleep(1000); // TODO configure
            }
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    public LoadReport terminate() throws InterruptedException {
        interrupt();
        join();
        return getReport();
    }

    protected LoadReport getReport() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("invocations", String.valueOf(measurements.size()));
        long totalTime = 0;
        for (long m: measurements) {
            totalTime += m;
        }
        map.put("accumulated_time", String.valueOf(totalTime));
        return new LoadReport(getClass(), map);
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
