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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.jenkinsci.test.acceptance.po.PageObject;
import org.jenkinsci.test.scale.load.ConfigXmlRoundtrip;

public class Util {

    public static void configXmlRoundtrip(URL url) throws IOException {
        InputStream content = url.openConnection().getInputStream();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setDoOutput(true);
        IOUtils.copy(content, con.getOutputStream());

        if (con.getResponseCode() != 200) {
            throw new AssertionError("Unexpected response code: " + con.getResponseCode());
        }
    }

    public static <Ret extends ConfigXmlRoundtrip> Ret getConfigXmlRoundtrip(PageObject subject, Class<Ret> type) {

        String period = System.getProperty(propertyName(type, "period"));
        if (period == null) return null;

        try {
            Constructor<Ret> cons = type.getDeclaredConstructor(subject.getClass(), long.class);
            return cons.newInstance(subject, Long.parseLong(period));
        } catch (NoSuchMethodException ex) {
            throw new AssertionError(ex);
        } catch (SecurityException ex) {
            throw new AssertionError(ex);
        } catch (InstantiationException ex) {
            throw new AssertionError(ex);
        } catch (IllegalAccessException ex) {
            throw new AssertionError(ex);
        } catch (IllegalArgumentException ex) {
            throw new AssertionError(ex);
        } catch (InvocationTargetException ex) {
            throw new AssertionError(ex);
        }
    }

    /**
     * Get the property name user for input and output.
     */
    public static String propertyName(Class<?> type, String attribute) {
        String packageName = type.getPackage().getName();
        String canName = type.getCanonicalName();
        assert canName.startsWith(packageName);
        String propertyName = String.format("scaletest%s.%s", canName.substring(packageName.length()), attribute);
        return propertyName;
    }
}
