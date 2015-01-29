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
package org.jenkinsci.test.scale.meta;

import java.util.Properties;

public class LoadReport {

    private final Properties properties;

    public static Builder builder(Class<? extends Load> source) {
        return new Builder(source);
    }

    private LoadReport(Builder builder) {
        this.properties = builder.properties;
    }

    /**
     * Report in form of properties.
     */
    public Properties getAnnotatedProperties() {
        return properties;
    }

    public static final class Builder {
        private final Class<? extends Load> source;
        private final Properties properties = new Properties();

        private Builder(Class<? extends Load> source) {
            this.source = source;
        }

        public Builder put(String key, Object value) {
            properties.setProperty(source.getName() + '.' + key, value.toString());
            return this;
        }

        public LoadReport build() {
            return new LoadReport(this);
        }
    }
}
