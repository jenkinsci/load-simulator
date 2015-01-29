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
package org.jenkinsci.test.scale;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.jenkinsci.test.acceptance.guice.World;
import org.jenkinsci.test.acceptance.po.Jenkins;
import org.jenkinsci.test.scale.meta.Fixture;
import org.jenkinsci.test.scale.meta.FixtureFactory;
import org.jenkinsci.test.scale.meta.Load;
import org.jenkinsci.test.scale.meta.LoadReport;
import org.openqa.selenium.WebDriver;
import org.reflections.Reflections;

import com.google.inject.Injector;

public class Main {

    private static final @Nonnull Reflections reflections = new Reflections("org.jenkinsci.test.scale");
    private static final int timeToRun = Integer.getInteger("scaletest.Main.timeToRun", 1);

    @Inject public Jenkins jenkins;
    @Inject public WebDriver driver;

    public static final void main(String... args) {
        Main main = new Main();

        World world = World.get();
        Injector injector = world.getInjector();

        world.startTestScope("Scale test");

        injector.injectMembers(main);

        try {
            main.test();
        } catch (Exception ex) {
            throw new AssertionError(ex);
        } finally {
            world.endTestScope();
        }
    }

    private void test() throws Exception {
        List<Load> loads = new ArrayList<Load>();

        for (Class<? extends FixtureFactory> f: reflections.getSubTypesOf(FixtureFactory.class)) {
            FixtureFactory factory = f.newInstance();
            Fixture fixture = factory.create(jenkins);
            loads.addAll(fixture.getLoads());
        }

        for (Load load: loads) {
            load.start();
        }

        long startTime = System.currentTimeMillis();
        Thread.sleep(1000 * 60 * timeToRun);
        long totalTime = System.currentTimeMillis() - startTime;

        for (Load load: loads) {
            load.terminate();
        }

        List<LoadReport> reports = new ArrayList<LoadReport>(loads.size());
        for (Load load: loads) {
            reports.add(load.getReport());
        }

        System.out.printf("Run for %d seconds%n", totalTime / 1000);
        Properties result = new Properties();
        for (LoadReport r: reports) {
            result.putAll(r.getAnnotatedProperties());
        }

        final FileOutputStream fos = new FileOutputStream("./target/result.properties");
        try {
            result.store(fos, null);
        } finally {
            fos.close();
        }
    }
}
