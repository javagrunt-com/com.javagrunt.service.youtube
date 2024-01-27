package com.javagrunt.service.youtube;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

import java.util.HashSet;
import java.util.Set;

public class RunTimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(org.springframework.data.mapping.PersistentEntity.class);
        try {
            hints.reflection().registerType(TypeReference.of("org.springframework.data.domain.Unpaged"),
                    builder -> builder
                            .withMembers(MemberCategory.values()));
            for (Class<?> c : classes) {
                hints.reflection().registerType(TypeReference.of(c.getName()),
                        builder -> builder
                                .withMembers(MemberCategory.values()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
