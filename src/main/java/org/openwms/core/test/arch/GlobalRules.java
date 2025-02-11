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

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.slf4j.Logger;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * A GlobalRules class defines various rules not tight to any library nor architectural layer.
 *
 * @author Heiko Scherrer
 */
@AnalyzeClasses(packages = "org.openwms", cacheMode = CacheMode.PER_CLASS, importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class
})
public final class GlobalRules {

    private GlobalRules() {}

    /**
     * Ensures that the codebase is free of cyclic dependencies within slices defined by the
     * given package pattern.
     *
     * This rule applies to slices that match the pattern "org.openwms.(*)..". It checks for
     * cyclic dependencies within this pattern to ensure that there are no circular dependencies,
     * enforcing a more maintainable and modular architecture.
     */
    @ArchTest
    public static final ArchRule slicesFreeOfCycles = slices().matching("org.openwms.(*)..").should().beFreeOfCycles();

    /**
     * ArchRule to ensure that classes within the "api" package do not depend on classes within the "impl" package.
     *
     * This rule is intended to maintain a clear separation between the API layer and the implementation layer,
     * ensuring that the API layer, which is exposed to clients, does not have dependencies on the internal implementation details.
     *
     * The rationale behind this rule is to enforce a clean architecture where internal implementation changes do not impact the external API.
     */
    @ArchTest
    public static final ArchRule apiImplDependenciesNotAllowed = noClasses()
            .that()
            .resideInAPackage("..api..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..impl..")
            .because("The API package is exposed to the client and should never expose internals")
            .allowEmptyShould(true);

    /**
     * ArchRule to ensure that Logger fields follow a specific definition pattern.
     *
     * This rule specifies that all fields of type `Logger` must be private, static, and final.
     *
     * The rationale behind this rule is to enforce a consistent logger definition throughout
     * the codebase, ensuring that loggers are properly encapsulated and shared only within the
     * class they are defined in.
     */
    @ArchTest
    public static final ArchRule verify_logger_definition =
            fields().that().haveRawType(Logger.class)
                    .should().bePrivate()
                    .andShould().beStatic()
                    .andShould().beFinal()
                    .because("This a defined logger definition")
                    .allowEmptyShould(true);

    /**
     * Usually the JLS allows also numeric values in the type name {@literal [$_a-zA-Z][$_a-zA-Z0-9]*}. Here we don't.
     */
    @ArchTest
    public static final ArchRule verify_type_names =
            classes()
                    .should(new ArchCondition<>("have a name without numeric values") {
                        @Override
                        public void check(JavaClass item, ConditionEvents events) {
                            if (!"".equals(item.getSimpleName()) && !" ".equals(item.getSimpleName()) &&
                                !item.getSimpleName().matches("(package-info|[A-Z][_$a-zA-Z]+)")) {
                                events.add(SimpleConditionEvent.violated(item, "Class %s contains numeric values".formatted(item.getSimpleName())));
                            }
                        }
                    })
                    .because("it must be aligned with the JLS (https://docs.oracle.com/javase/specs/jls/se21/html/jls-6.html#jls-6.5.5.1)")
                    .allowEmptyShould(true);

    /**
     * Prefixing interfaces with an 'I' is not a practise in Java. For example: IOrderService.
     */
    @ArchTest
    public static final ArchRule verify_no_I_prefix_on_interfaces =
            classes()
                    .that().areInterfaces()
                    .should(new ArchCondition<>("have a name not prefixed with an I") {
                        @Override
                        public void check(JavaClass item, ConditionEvents events) {
                            if (item.getSimpleName().matches("(I[A-Z][a-z$_]).*")) {
                                events.add(SimpleConditionEvent.violated(item, "Interface %s must not be prefixed with I".formatted(item.getSimpleName())));
                            }
                        }
                    })
                    .because("prefixing interfaces with an I is a common rule in the .NET world but not in Java")
                    .allowEmptyShould(true);
}
