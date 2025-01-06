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

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.lang.ArchRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.openwms.core.test.arch.SpringPredicates.areSpringBeans;

/**
 * A ValidationRules class defines rules for classes that use the JSR-303 Bean Validation API.
 *
 * @author Heiko Scherrer
 */
@AnalyzeClasses(packages = "org.openwms", cacheMode = CacheMode.PER_CLASS, importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class
})
public final class ValidationRules {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationRules.class);

    private ValidationRules() {}

    /**
     * Ensures that classes which are Spring Beans and contain any methods with JSR-303 (Java Bean Validation)
     * annotations are also annotated with @Validated.
     *
     * This rule applies to classes that adhere to the following conditions:
     * - The class is a Spring Bean.
     * - The class contains at least one method with a parameter annotated using JSR-303 (Java Bean Validation) annotations.
     *
     * These classes should be annotated with the @Validated annotation, which ensures that the validation
     * constraints defined by the JSR-303 annotations are enforced by the Spring Framework.
     */
    @ArchTest
    public static final ArchRule useValidatedWhenRequired = classes()
            .that(areSpringBeans)
            .and().containAnyMethodsThat(new DescribedPredicate<>("have a JSR303 annotation") {
                @Override
                public boolean test(JavaMethod javaMethod) {
                    var res = javaMethod.getParameterAnnotations().stream().flatMap(Collection::stream).anyMatch(a -> a.getRawType().getPackageName().startsWith("jakarta.validation"));
                    if (res && LOGGER.isTraceEnabled()) {
                        LOGGER.trace("Method under consideration: [{}]", javaMethod.getFullName());
                    }
                    return res;
                }
            })
            .should().beAnnotatedWith(Validated.class).orShould().beMetaAnnotatedWith(Validated.class);
}
