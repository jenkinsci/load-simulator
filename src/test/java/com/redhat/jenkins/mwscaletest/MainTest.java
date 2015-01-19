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
package com.redhat.jenkins.mwscaletest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.reflections.Reflections;

import com.redhat.jenkins.mwscaletest.meta.Fixture;
import com.redhat.jenkins.mwscaletest.meta.FixtureFactory;
import com.redhat.jenkins.mwscaletest.meta.Load;
import com.redhat.jenkins.mwscaletest.meta.LoadReport;

public class MainTest {

    private static final @Nonnull Reflections reflections = new Reflections("com.redhat.jenkins.mwscaletest");
    private static final int timeToRun = Integer.getInteger("mwscaletest.timeToRun", 10);

    public @Rule JenkinsRule j = new JenkinsRule();
    private List<Load> loads = new ArrayList<Load>();

    @Before
    public void setUp() throws Exception {
        for (Class<? extends FixtureFactory> f: reflections.getSubTypesOf(FixtureFactory.class)) {
            FixtureFactory factory = f.newInstance();
            Fixture fixture = factory.create(j);
            loads.addAll(fixture.getLoads());
        }

        for (Load load: loads) {
            load.start();
        }
    }

    @Test
    public void test() throws InterruptedException {

        long startTime = System.currentTimeMillis();
        Thread.sleep(1000 * timeToRun);
        long totalTime = System.currentTimeMillis() - startTime;

        List<LoadReport> reports = new ArrayList<LoadReport>(loads.size());
        for (Load load: loads) {
            reports.add(load.terminate());
        }

        System.out.printf("Run for %d seconds%n", totalTime / 1000);
        for (LoadReport r: reports) {
            System.out.println(r.getProperties());
        }
    }
}
