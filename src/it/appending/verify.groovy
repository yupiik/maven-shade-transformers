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
import org.codehaus.plexus.util.IOUtil
import java.util.jar.JarFile
import java.util.jar.JarEntry

File jar = new File(basedir, "target/appending-1.0.jar")
assert jar.isFile()

JarFile jarFile = new JarFile( jar );

JarEntry jarEntry = jarFile.getEntry( "foo/bar.txt" );
if ( jarEntry == null ) {
  throw new IllegalStateException( "wanted path is missing: foo/bar.txt");
}

String content1 = IOUtil.toString( jarFile.getInputStream( jarEntry ), "UTF-8" );

jarEntry = jarFile.getEntry( "foo/prefix.txt" );
if ( jarEntry == null ) {
  throw new IllegalStateException( "wanted path is missing: foo/prefix.txt");
}

String content2 = IOUtil.toString( jarFile.getInputStream( jarEntry ), "UTF-8" );
jarFile.close();

// Simple replacement
assert content1.contains('a=jakarta.foo.bar')
// Replacement with prefix
assert content2.contains('a=mypackage.shaded.com.foo.bar')
