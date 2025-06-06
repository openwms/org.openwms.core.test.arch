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
package org.openwms.core.test.arch;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

/**
 * A OnionArchitectureRules.
 *
 * @author Heiko Scherrer
 */
@AnalyzeClasses(packages = "org.openwms", cacheMode = CacheMode.PER_CLASS, importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class
})
public class OnionArchitectureRules {

    @ArchTest
    public final ArchRule adaptersShouldNotDependOnEachOther =
            SlicesRuleDefinition.slices().matching("..adapter.(*)..").should().notDependOnEachOther().allowEmptyShould(true);

    @ArchTest
    public final ArchRule onionArchitecture =
            Architectures.layeredArchitecture()
                    .consideringAllDependencies()
                    .layer("adapter").definedBy("..adapter..")
                    .layer("domain").definedBy("..domain..")
                    .layer("application").definedBy("..application..")
                    .whereLayer("adapter").mayNotBeAccessedByAnyLayer()
                    .whereLayer("application").mayOnlyBeAccessedByLayers("adapter").allowEmptyShould(true);
}
