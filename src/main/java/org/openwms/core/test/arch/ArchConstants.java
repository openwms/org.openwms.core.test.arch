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
 * A ArchConstants.
 *
 * @author Heiko Scherrer
 */
public final class ArchConstants {

    private ArchConstants() {}

    static final PatternPredicate MAVEN_GEN_SOURCES = new PatternPredicate(".*/target/generated-sources/annotations/.*");

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
    public static class DoNotIncludeGeneratedSources implements ImportOption {


        @Override
        public boolean includes(Location location) {
            System.out.println(location.asURI().toString());
            return !MAVEN_GEN_SOURCES.test(location);
        }
    }
}
