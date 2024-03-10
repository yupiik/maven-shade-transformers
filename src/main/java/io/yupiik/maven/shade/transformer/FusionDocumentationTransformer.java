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

import static java.util.stream.Collectors.joining;

public class FusionDocumentationTransformer extends BaseMergerTransformer {
    public FusionDocumentationTransformer() {
        super("META-INF/fusion/configuration/documentation.json");
    }

    /*
    {
      "version": 1,
      "classes": {
        "io.yupiik.fusion.jwt.JwtSignerConfiguration": [
          {
            "name": "jwt-signer.algorithm",
            ....
          },
          ....
        ]
      },
      "roots": [
        ....
      ]
    }
     */
    @Override
    protected String merge() {
        return "{" +
                "\"version\":1," +
                "\"classes\":{" + files.stream()
                .map(this::extractClasses)
                .collect(joining(",")) + "}," +
                "\"roots\":[" + files.stream()
                .map(this::extractRoots)
                .collect(joining(",")) + "]}";
    }

    private String extractRoots(final String json) {
        final int from = json.indexOf("},\"roots\":[") + "},\"roots\":[".length();
        return json.substring(from, json.indexOf(']', from));
    }

    private String extractClasses(final String json) {
        return json.substring(json.indexOf("{", 1) + 1, json.indexOf("},\"roots\":["));
    }
}
