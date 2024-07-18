package com.xuecheng.content.config;

import com.xuecheng.content.service.CourseCategoryService;
import com.xuecheng.content.service.impl.CourseCategoryServiceDatabaseImpl;
import com.xuecheng.content.service.impl.CourseCategoryServiceJavaImpl;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.annotation.PostConstruct;

@Configuration
@Data
public class AppServiceStrategyConfig {
    @Value("${content.category.service.strategy}")
    private String courseCategoryServiceStrategy;

    @Bean
    @Conditional(JavaStrategyCondition.class)
    public CourseCategoryService courseCategoryServiceJava() {
        return new CourseCategoryServiceJavaImpl();
    }

    @Bean
    @Primary
    public CourseCategoryService courseCategoryServiceDatabase() {
        return new CourseCategoryServiceDatabaseImpl();
    }
}

class JavaStrategyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String strategy = context.getEnvironment().getProperty("course.category.service.strategy");
        return "java".equalsIgnoreCase(strategy);
    }
}