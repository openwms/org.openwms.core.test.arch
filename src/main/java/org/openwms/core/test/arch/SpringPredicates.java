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
import com.tngtech.archunit.core.domain.JavaClass;
import org.ameba.annotation.TxService;
import org.ameba.http.MeasuredRestController;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

/**
 * A SpringPredicates.
 *
 * @author Heiko Scherrer
 */
public final class SpringPredicates {

    private SpringPredicates() {}

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

    private static boolean isMapperImpl(JavaClass input) {
        boolean result = false;
        if (input.getSuperclass().isPresent()) {
            result = input.getSuperclass().get().getAllInvolvedRawTypes().stream().anyMatch(i -> i.isAnnotatedWith("org.mapstruct.Mapper"));
        }
        if (!result) {
            result = input.getRawInterfaces().stream().anyMatch(i -> i.isAnnotatedWith("org.mapstruct.Mapper"));
        }
        return result;
    }
}
