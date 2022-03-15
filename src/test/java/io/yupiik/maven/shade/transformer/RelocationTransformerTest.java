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

import org.apache.maven.plugins.shade.relocation.SimpleRelocator;
import org.apache.maven.plugins.shade.resource.AppendingTransformer;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;
import org.apache.maven.plugins.shade.resource.XmlAppendingTransformer;
import org.codehaus.plexus.util.IOUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    void relocateUri() throws Exception {
        final XmlAppendingTransformer delegate = new XmlAppendingTransformer();
        final Field resource = XmlAppendingTransformer.class.getDeclaredField("resource");
        resource.setAccessible(true);
        resource.set(delegate, "META-INF/faces-config.xml");

        final RelocationTransformer resourceTransformer = createTransformer(delegate, true);
        resourceTransformer.processResource(
                "META-INF/faces-config.xml",
                new ByteArrayInputStream(("" +
                        "<faces-config" +
                        " xmlns=\"http://java.sun.com/xml/ns/javaee\"" +
                        " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"2.0\"" +
                        " xsi:schemaLocation=\"" +
                        "http://java.sun.com/xml/ns/javaee " +
                        "http://java.sun.com/xml/ns/javaee/web-facesconfig_2_0.xsd\" />")
                        .getBytes(StandardCharsets.UTF_8)),
                singletonList(new SimpleRelocator(
                        "http://java.sun.com/xml/ns/javaee",
                        "https://jakarta.ee/xml/ns/jakartaee",
                        null, null, true)));
        assertOutput(resourceTransformer, jarInputStream -> {
            final JarEntry entry = jarInputStream.getNextJarEntry();
            assertNotNull(entry);
            assertEquals("META-INF/faces-config.xml", entry.getName());
            assertEquals("" +
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<faces-config" +
                    " xmlns=\"https://jakarta.ee/xml/ns/jakartaee\"" +
                    " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                    " version=\"2.0\"" +
                    " xsi:schemaLocation=\"" +
                    "https://jakarta.ee/xml/ns/jakartaee " +
                    "https://jakarta.ee/xml/ns/jakartaee/web-facesconfig_2_0.xsd\" />",
                    IOUtil.toString(jarInputStream).replace("\r\n", "\n").trim());
            assertNull(jarInputStream.getNextJarEntry());
        });
    }

    @Test
    void relocate() throws Exception {
        final AppendingTransformer delegate = new AppendingTransformer();
        final Field resource = AppendingTransformer.class.getDeclaredField("resource");
        resource.setAccessible(true);
        resource.set(delegate, "foo/bar.txt");

        final RelocationTransformer resourceTransformer = createTransformer(delegate, false);

        assertTrue(resourceTransformer.canTransformResource("foo/bar.txt"));
        resourceTransformer.processResource(
                "foo/bar.txt",
                new ByteArrayInputStream("a=javax.foo.bar".getBytes(StandardCharsets.UTF_8)),
                singletonList(new SimpleRelocator("javax", "jakarta", null, null)));
        assertOutput(resourceTransformer, jarInputStream -> {
            final JarEntry entry = jarInputStream.getNextJarEntry();
            assertNotNull(entry);
            assertEquals("foo/bar.txt", entry.getName());
            assertEquals("a=jakarta.foo.bar", IOUtil.toString(jarInputStream).trim());
            assertNull(jarInputStream.getNextJarEntry());
        });
    }

    private void assertOutput(final ResourceTransformer resourceTransformer, final IOConsumer<JarInputStream> onJar) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (final JarOutputStream jarOutputStream = new JarOutputStream(out)) {
            resourceTransformer.modifyOutputStream(jarOutputStream);
        }
        try (final JarInputStream jarInputStream = new JarInputStream(new ByteArrayInputStream(out.toByteArray()))) {
            onJar.accept(jarInputStream);
        }
    }

    private RelocationTransformer createTransformer(final ResourceTransformer delegate, final boolean path) {
        final RelocationTransformer resourceTransformer = path ?
                new PathRelocationTransformer() : new RelocationTransformer();
        resourceTransformer.setDelegates(singletonList(delegate));
        return resourceTransformer;
    }

    private interface IOConsumer<A> {
        void accept(A a) throws IOException;
    }
}
