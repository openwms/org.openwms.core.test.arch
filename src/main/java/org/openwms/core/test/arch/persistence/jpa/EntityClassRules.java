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
package org.openwms.core.test.arch.persistence.jpa;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import org.ameba.integration.jpa.ApplicationEntity;
import org.ameba.integration.jpa.BaseEntity;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * A EntityClassRules defines rules for JPA types.
 *
 * @author Heiko Scherrer
 */
@AnalyzeClasses(packages = "org.openwms", cacheMode = CacheMode.PER_CLASS, importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class
})
public final class EntityClassRules {

    private EntityClassRules() {}

    /**
     * ArchUnit rule that ensures all classes annotated with @Entity are
     * also annotated with @Table.
     *
     * This rule is applied to enforce that every JPA entity has a corresponding
     * database table mapping.
     */
    @ArchTest
    public static final ArchRule entitiesMustHaveTableAnnotation = classes()
            .that()
            .areAnnotatedWith(Entity.class)
            .and()
            .areNotAnnotatedWith(DiscriminatorValue.class)
            .should().beAnnotatedWith(Table.class)
            .allowEmptyShould(true);

    /**
     * ArchUnit rule that ensures no classes annotated with @Entity or
     * @MappedSuperclass reside in packages "..entities.." or "..entity..".
     *
     * This rule is applied to enforce a clean package structure by preventing JPA
     * entity classes from being placed in certain packages.
     */
    @ArchTest
    public static final ArchRule noEntityPackages = noClasses()
            .that()
            .areAnnotatedWith(Entity.class).or()
            .areAnnotatedWith(MappedSuperclass.class)
            .should().resideInAnyPackage("..entities..", "..entity..")
            .allowEmptyShould(true);

    /**
     * ArchUnit rule that ensures all classes annotated with @Entity or
     * @MappedSuperclass, except those with the simple name "AuditableRevisionEntity",
     * should extend either ApplicationEntity or BaseEntity.
     *
     * This rule enforces a standard inheritance hierarchy for entities, ensuring
     * that they inherit common behavior and properties from base classes.
     */
    @ArchTest
    public static final ArchRule entitiesMustExtendBaseClasses = classes()
            .that()
            .areAnnotatedWith(Entity.class).or()
            .areAnnotatedWith(MappedSuperclass.class)
            .and().doNotHaveSimpleName("AuditableRevisionEntity")
            .should().beAssignableTo(ApplicationEntity.class)
            .orShould().beAssignableTo(BaseEntity.class)
            .allowEmptyShould(true);

    /**
     * ArchUnit rule that ensures no JPA entity classes annotated with @Entity,
     * @MappedSuperclass, or @Enumerated depend on Lombok classes.
     *
     * This rule is applied to prevent the use of Lombok in JPA entity classes,
     * ensuring manual maintenance of boilerplate code and consistency with JPA
     * specifications.
     */
    @ArchTest
    public static final ArchRule entitiesMustNotUseLombok = noClasses()
            .that()
            .areAnnotatedWith(Entity.class).or()
            .areAnnotatedWith(MappedSuperclass.class).or()
            .areAnnotatedWith(Enumerated.class)
            .should().dependOnClassesThat().resideInAnyPackage("..lombok..")
            .allowEmptyShould(true);

    /**
     * ArchUnit rule that ensures no classes annotated with @Entity or
     * @MappedSuperclass depend on classes that reside in packages
     * related to Jackson serialization.
     *
     * This rule is applied to prevent JPA entity classes from being
     * serialized using Jackson, ensuring compatibility with JPA persistence
     * and avoiding potential issues with serialization mechanisms.
     */
    @ArchTest
    static final ArchRule entitiesMustNotBeSerialized = noClasses().that()
            .areAnnotatedWith(Entity.class)
            .or()
            .areAnnotatedWith(MappedSuperclass.class)
            .should()
            .dependOnClassesThat().resideInAnyPackage("com.fasterxml.jackson..")
            .allowEmptyShould(true);
}
