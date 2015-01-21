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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

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

//    public static <R> long measure(Callable<R> runnable) {
//        try {
//            long start = System.currentTimeMillis();
//            runnable.call();
//            return System.currentTimeMillis() - start;
//        } catch (Exception ex) {
//            throw new AssertionError(ex);
//        }
//    }
//
//    public static <R> Result<R> measureResult(Callable<R> runnable) {
//        try {
//            long start = System.currentTimeMillis();
//            R ret = runnable.call();
//            return new Result<R>(ret, System.currentTimeMillis() - start);
//        } catch (Exception ex) {
//            throw new AssertionError(ex);
//        }
//    }
//
//    public static final class Result<R> {
//        private final R ret;
//        private final long time;
//        private Result(R ret, long time) {
//            this.ret = ret;
//            this.time = time;
//        }
//
//        public R getResult() {
//            return ret;
//        }
//
//        public long getTime() {
//            return time;
//        }
//    }
}
