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
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.lang.ArchRule;
import org.ameba.annotation.Public;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.openwms.core.test.arch.SpringPredicates.areSpringBeansButNoControllers;

/**
 * A SpringRules class defines rules for Spring managed components.
 *
 * @author Heiko Scherrer
 */
@AnalyzeClasses(packages = "org.openwms", cacheMode = CacheMode.PER_CLASS, importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class
})
public final class SpringRules {

    private SpringRules() {}

    /**
     * Ensures that Spring Bean implementations (except controllers) that are not public are either package-private or protected.
     * This rule is designed to enforce restricted visibility of Spring Beans to maintain proper encapsulation and control over bean access.
     *
     * This rule applies to classes that:
     * - Are Spring Beans but not controllers.
     * - Are not annotated with @Public.
     *
     * The objective is to ensure that such classes are either package-private or protected.
     */
    @ArchTest
    public static final ArchRule springBeanImplementationsMustBeRestrictedInVisibility = classes()
            .that(areSpringBeansButNoControllers)
            .and().areNotAnnotatedWith(Public.class)
            .should().bePackagePrivate()
            .orShould().beProtected();
}
