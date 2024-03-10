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

import static java.util.stream.Collectors.joining;

public class FusionOpenRPCTransformer extends BaseMergerTransformer {
    public FusionOpenRPCTransformer() {
        super("META-INF/fusion/jsonrpc/openrpc.json");
    }

    @Override
    protected String merge() {
        return "{" + // "methods": {
                "\"schemas\":{" + files.stream()
                .map(this::extractSchemas)
                .collect(joining(",")) + "}}," +
                "\"methods\":{" + files.stream()
                .map(this::extractMethods)
                .collect(joining(",")) + "}}";
    }

    // todo: make it more robust
    private String extractMethods(final String json) {
        return json.substring(
                json.indexOf("}},\"methods\":{\"") + "}},\"methods\":{".length(),
                json.lastIndexOf('}', json.lastIndexOf('}') - 1));
    }

    private String extractSchemas(final String json) {
        return json.substring(json.indexOf("{", 1) + 1, json.indexOf("}},\"methods\":{\""));
    }
}
