/*
 * Copyright (c) 2020 - Yupiik SAS - https://www.yupiik.com
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.yupiik.maven.shade.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;

public final class IOUtil {
    private IOUtil() {
        // no-op
    }

    public static String slurp(final InputStream stream, final Charset charset) throws IOException {
        final char[] buffer = new char[8192];
        final StringWriter sw = new StringWriter();
        try (final Reader reader = new InputStreamReader(stream, charset)) {
            int n;
            while (-1 != (n = reader.read(buffer))) {
                sw.write(buffer, 0, n);
            }
        }
        sw.flush();
        return sw.toString();
    }
}
