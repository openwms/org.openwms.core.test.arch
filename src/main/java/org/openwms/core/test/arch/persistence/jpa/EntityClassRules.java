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
package org.openwms.core.test.arch.persistence.jpa;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import org.ameba.integration.jpa.ApplicationEntity;
import org.ameba.integration.jpa.BaseEntity;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * A EntityClassRules.
 *
 * @author Heiko Scherrer
 */
@AnalyzeClasses(packages = "org.openwms", cacheMode = CacheMode.PER_CLASS, importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class
})
public final class EntityClassRules {

    private EntityClassRules() {}

    @ArchTest
    public static final ArchRule entitiesMustHaveTableAnnotation = classes()
            .that()
            .areAnnotatedWith(Entity.class)
            .should().beAnnotatedWith(Table.class);

    @ArchTest
    public static final ArchRule noEntityPackages = noClasses()
            .that()
            .areAnnotatedWith(Entity.class).or()
            .areAnnotatedWith(MappedSuperclass.class)
            .should().resideInAnyPackage("..entities..", "..entity..");

    @ArchTest
    public static final ArchRule entitiesMustExtendBaseClasses = classes()
            .that()
            .areAnnotatedWith(Entity.class).or()
            .areAnnotatedWith(MappedSuperclass.class)
            .and().doNotHaveSimpleName("AuditableRevisionEntity")
            .should().beAssignableTo(ApplicationEntity.class)
            .orShould().beAssignableTo(BaseEntity.class);

    @ArchTest
    public static final ArchRule entitiesMustNotUseLombok = noClasses()
            .that()
            .areAnnotatedWith(Entity.class).or()
            .areAnnotatedWith(MappedSuperclass.class).or()
            .areAnnotatedWith(Enumerated.class)
            .should().dependOnClassesThat().resideInAnyPackage("..lombok..");
}
