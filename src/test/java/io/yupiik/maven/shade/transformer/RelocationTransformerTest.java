/*
 * Copyright (C) 2009-2020 Yupiik SAS. - www.yupiik.com
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.yupiik.maven.shade.transformer;

import org.apache.maven.plugins.shade.relocation.SimpleRelocator;
import org.apache.maven.plugins.shade.resource.AppendingTransformer;
import org.codehaus.plexus.util.IOUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RelocationTransformerTest {
    @Test
    void relocate() throws Exception {
        final AppendingTransformer delegate = new AppendingTransformer();
        final Field resource = AppendingTransformer.class.getDeclaredField("resource");
        resource.setAccessible(true);
        resource.set(delegate, "foo/bar.txt");

        final RelocationTransformer resourceTransformer = new RelocationTransformer();
        resourceTransformer.setDelegates(singletonList(delegate));

        assertTrue(resourceTransformer.canTransformResource("foo/bar.txt"));
        resourceTransformer.processResource(
                "foo/bar.txt",
                new ByteArrayInputStream("a=javax.foo.bar".getBytes(StandardCharsets.UTF_8)),
                singletonList(new SimpleRelocator("javax", "jakarta", null, null)));
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (final JarOutputStream jarOutputStream = new JarOutputStream(out)) {
            resourceTransformer.modifyOutputStream(jarOutputStream);
        }
        try (final JarInputStream jarInputStream = new JarInputStream(new ByteArrayInputStream(out.toByteArray()))) {
            final JarEntry entry = jarInputStream.getNextJarEntry();
            assertNotNull(entry);
            assertEquals("foo/bar.txt", entry.getName());
            assertEquals("a=jakarta.foo.bar", IOUtil.toString(jarInputStream).trim());
            assertNull(jarInputStream.getNextJarEntry());
        }
    }
}
