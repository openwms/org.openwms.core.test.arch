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

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.openwms.core.test.arch.SpringPredicates.areSpringBeans;

/**
 * A ValidationRules.
 *
 * @author Heiko Scherrer
 */
@AnalyzeClasses(packages = "org.openwms", cacheMode = CacheMode.PER_CLASS, importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class
})
public final class ValidationRules {

    private ValidationRules() {}

    @ArchTest
    public static final ArchRule useValidatedWhenRequired = classes()
            .that(areSpringBeans)
            .and().containAnyMethodsThat(new DescribedPredicate<>("have a JSR303 annotation") {
                @Override
                public boolean test(JavaMethod javaField) {
                    return javaField.getParameterAnnotations().stream().flatMap(Collection::stream).anyMatch(a -> "jakarta.validation".equals(a.getRawType().getPackageName()));
                }
            })
            .should().beAnnotatedWith(Validated.class);
}
