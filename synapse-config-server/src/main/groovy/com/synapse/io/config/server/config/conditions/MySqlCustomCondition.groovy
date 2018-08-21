package com.synapse.io.config.server.config.conditions

import org.springframework.boot.autoconfigure.condition.ConditionMessage
import org.springframework.boot.autoconfigure.condition.ConditionOutcome
import org.springframework.boot.autoconfigure.condition.SpringBootCondition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata
import org.springframework.util.ClassUtils

class MySqlCustomCondition extends SpringBootCondition {

    static String[] CLASS_NAMES = ["org.hibernate.ejb.HibernateEntityManager", "org.hibernate.jpa.HibernateEntityManager"]

    @Override
    ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message
        message = ConditionMessage.forCondition("Hibernate")

        return Arrays.stream(CLASS_NAMES)
    }
}
