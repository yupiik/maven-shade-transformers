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

import java.util.List;
import org.apache.maven.plugins.shade.relocation.Relocator;

public class ClassRelocationHandler implements RelocationHandler {
    @Override
    public String relocate(String originalValue, List<Relocator> relocators) {
        String newValue = originalValue;
        for (Relocator relocator : relocators) {
            String value;
            do {
                value = newValue;
                newValue = relocator.relocateClass(value);
            }
            while (!value.equals(newValue));
        }
        return newValue;
    }
}
