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

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.jenkinsci.test.acceptance.po.PageObject;
import org.jenkinsci.test.scale.Util;

public abstract class ConfigXmlRoundtrip extends LoadThread<Void> {

    private final URL url;

    public static <Ret extends ConfigXmlRoundtrip> Ret newInstance(PageObject subject, Class<Ret> type) {

        String period = System.getProperty(Util.propertyName(type, "period"));
        if (period == null) return null;

        try {
            Constructor<Ret> cons = type.getDeclaredConstructor(URL.class, long.class);
            return cons.newInstance(subject.url("config.xml"), Long.parseLong(period));
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    public ConfigXmlRoundtrip(URL url, long sleep) {
        super("ConfigXmlRoundtrip for " + url.toString(), sleep);
        this.url = url;
    }

    @Override
    protected Void invoke() throws Exception {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        InputStream content = url.openConnection().getInputStream();
        try {
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            IOUtils.copy(content, con.getOutputStream());
        } finally {
            content.close();
        }

        if (con.getResponseCode() != 200) {
            throw new AssertionError("Unexpected response code: " + con.getResponseCode());
        }
        return null;
    }
}
