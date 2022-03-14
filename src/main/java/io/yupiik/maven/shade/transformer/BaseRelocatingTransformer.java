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

import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;

import java.util.List;

/**
 * Abstract class providing the needed logic to relocate any content.
 */
public abstract class BaseRelocatingTransformer implements ResourceTransformer {
    protected String relocate(final String originalValue, final List<Relocator> relocators) {
        String newValue = originalValue;
        for (Relocator relocator : relocators) {
            newValue = relocator.applyToSourceContent(newValue);
        }
        return newValue;
    }
}
