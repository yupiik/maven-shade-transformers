/*
 * Copyright (c) 2020-2023 - Yupiik SAS - https://www.yupiik.com
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

import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ReproducibleResourceTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static io.yupiik.maven.shade.transformer.IOUtil.slurp;
import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class BaseMergerTransformer implements ReproducibleResourceTransformer {
    protected final List<String> files = new ArrayList<>();
    private final String path;

    protected BaseMergerTransformer(final String path) {
        this.path = path;
    }

    @Override
    public boolean canTransformResource(final String resource) {
        return path.equals(resource);
    }

    protected abstract String merge();

    @Override
    public void processResource(final String resource, final InputStream is, final List<Relocator> relocators, final long time) throws IOException {
        files.add(slurp(is, UTF_8));
    }

    @Override
    public void processResource(final String resource, final InputStream is, final List<Relocator> relocators) throws IOException {
        processResource(resource, is, relocators, 0L);
    }

    @Override
    public void modifyOutputStream(final JarOutputStream os) throws IOException {
        os.putNextEntry(new JarEntry(path));
        os.write(merge().getBytes(UTF_8));
        os.closeEntry();
    }

    @Override
    public boolean hasTransformedResource() {
        return !files.isEmpty();
    }
}
