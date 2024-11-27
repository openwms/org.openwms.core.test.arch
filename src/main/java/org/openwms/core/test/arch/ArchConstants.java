/*
 * Copyright 2005-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.core.test.arch;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * A ArchConstants defines useful constants.
 *
 * @author Heiko Scherrer
 */
public final class ArchConstants {

    private ArchConstants() {}

    /**
     * A `PatternPredicate` instance used to identify Maven-generated source files.
     *
     * The `MAVEN_GEN_SOURCES` pattern specifically matches file paths
     * that include the directory `target/generated-sources/annotations/`.
     * This pattern is employed to exclude such generated source files,
     * particularly in contexts like code analysis and importing processes,
     * where including generated sources is typically undesirable.
     */
    static final PatternPredicate MAVEN_GEN_SOURCES = new PatternPredicate(".*/target/generated-sources/annotations/.*");

    /**
     * A Predicate implementation that checks if a given Location matches a specified pattern.
     */
    private static class PatternPredicate implements Predicate<Location> {
        private final Pattern pattern;

        PatternPredicate(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        @Override
        public boolean test(Location input) {
            return input.matches(pattern);
        }
    }

    /**
     * A class that implements the ImportOption interface to exclude generated source files
     * from being included in the import process.
     *
     * This option checks if a given location corresponds to the generated sources directory
     * as specified by the Maven build system. If the location matches the Maven generated sources pattern,
     * it will be excluded.
     */
    public static class DoNotIncludeGeneratedSources implements ImportOption {

        @Override
        public boolean includes(Location location) {
            System.out.println(location.asURI().toString());
            return !MAVEN_GEN_SOURCES.test(location);
        }
    }
}
