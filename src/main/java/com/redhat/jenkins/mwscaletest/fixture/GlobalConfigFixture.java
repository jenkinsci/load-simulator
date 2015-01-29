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
package com.redhat.jenkins.mwscaletest.fixture;

import java.util.Arrays;
import java.util.Collection;

import org.jenkinsci.test.acceptance.po.Jenkins;

import com.redhat.jenkins.mwscaletest.load.ConfigXmlRoundtrip;
import com.redhat.jenkins.mwscaletest.meta.Fixture;
import com.redhat.jenkins.mwscaletest.meta.FixtureFactory;
import com.redhat.jenkins.mwscaletest.meta.Load;

public class GlobalConfigFixture implements Fixture {

    private final Jenkins j;

    public GlobalConfigFixture(Jenkins j) {
        this.j = j;
    }

    public Collection<? extends Load> getLoads() {
        return Arrays.asList(
                new ConfigXmlRoundtrip(j.url("config.xml"), 1000)
        );
    }

    public static final class Factory implements FixtureFactory {

        public Fixture create(Jenkins j) {
            return new GlobalConfigFixture(j);
        }
    }
}
