/*
 * Copyright 2005-2025 the original author or authors.
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
package org.openwms.core.test.arch.presentation;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * A PresentationRules defines rules that target types in the presentation layer.
 *
 * @author Heiko Scherrer
 */
@AnalyzeClasses(packages = "org.openwms", cacheMode = CacheMode.PER_CLASS, importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class
})
public final class PresentationRules {

    private PresentationRules() {}

    /**
     * An architectural rule that ensures no class in the package "..api.." has a simple name ending with "DTO".
     *
     * The name DTO is a way too generic and can be used in all layers. We want to ensure that the DTO types in the api package are
     * explicitly marked as {@literal VO (ViewObject)} to express where they belong to.
     */
    @ArchTest
    public static final ArchRule notUseDTONaming = noClasses()
            .that()
            .resideInAnyPackage("..api..")
            .should().haveSimpleNameEndingWith("DTO");
}
