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
import com.tngtech.archunit.core.domain.JavaClass;
import org.ameba.annotation.TxService;
import org.ameba.http.MeasuredRestController;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

/**
 * A SpringPredicates defines predicated that can be used in rule definitions.
 *
 * @author Heiko Scherrer
 */
public final class SpringPredicates {

    private SpringPredicates() {}

    /**
     * Predicate that selects classes which are Spring Beans but not Controllers.
     *
     * This predicate evaluates whether a given Java class is a Spring Bean annotated with
     * @TxService, @Service, @Component, or @Repository, and ensures that it is not a Mapper
     * implementation. It specifically excludes classes annotated with controller-related annotations
     * such as @RestController or @MeasuredRestController.
     *
     * The predicate is mainly used in ArchUnit tests to enforce architectural rules on the Spring Beans.
     */
    public static final DescribedPredicate<JavaClass> areSpringBeansButNoControllers =
            new DescribedPredicate<>("are not generated SpringBeans"){
                @Override
                public boolean test(JavaClass input) {
                    var result = (input.isAnnotatedWith(TxService.class) ||
                            input.isAnnotatedWith(Service.class) ||
                            input.isAnnotatedWith(Component.class) ||
                            input.isAnnotatedWith(Repository.class)) &&
                            !isMapperImpl(input)
                            ;
                    return result;
                }
            };

    /**
     * Predicate that selects classes which are identified as Spring Beans.
     *
     * This predicate evaluates whether a given Java class is annotated with
     * one of several Spring-specific annotations including @TxService, @Service,
     * @Component, @Repository, @RestController, or @MeasuredRestController,
     * and ensures that the class is not a Mapper implementation.
     *
     * The predicate is mainly used in ArchUnit tests to enforce architectural
     * rules on the identification and management of Spring Beans within the project.
     */
    public static final DescribedPredicate<JavaClass> areSpringBeans =
            new DescribedPredicate<>("are not generated SpringBeans"){
                @Override
                public boolean test(JavaClass input) {
                    var result = (input.isAnnotatedWith(TxService.class) ||
                            input.isAnnotatedWith(Service.class) ||
                            input.isAnnotatedWith(Component.class) ||
                            input.isAnnotatedWith(Repository.class) ||
                            input.isAnnotatedWith(RestController.class) ||
                            input.isAnnotatedWith(MeasuredRestController.class)) &&
                            !isMapperImpl(input)
                            ;
                    return result;
                }
            };

    /**
     * Determines whether the given Java class is an implementation of a mapper interface
     * annotated with "org.mapstruct.Mapper". The check includes both superclasses and interfaces.
     *
     * @param input the Java class to check for the mapper implementation
     * @return true if the class or any of its superclasses or implemented interfaces
     *         are annotated with "org.mapstruct.Mapper", false otherwise
     */
    private static boolean isMapperImpl(JavaClass input) {
        boolean result = false;
        if (input.getSuperclass().isPresent()) {
            result = input.getSuperclass().get().getAllInvolvedRawTypes().stream().anyMatch(i -> i.isAnnotatedWith("org.mapstruct.Mapper"));
            if (!result) {
                result = input.getAllRawSuperclasses().stream()
                        .flatMap(d -> d.getRawInterfaces().stream())
                        .anyMatch(i -> i.isAnnotatedWith("org.mapstruct.Mapper"));
            }
        }
        if (!result) {
            result = input.getRawInterfaces().stream()
                    .anyMatch(i -> i.isAnnotatedWith("org.mapstruct.Mapper"));
        }
        return result;
    }
}
