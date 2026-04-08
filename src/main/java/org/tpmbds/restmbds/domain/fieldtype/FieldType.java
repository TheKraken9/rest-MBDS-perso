package org.tpmbds.restmbds.domain.fieldtype;

import org.tpmbds.restmbds.domain.constraint.Constraint;
import org.tpmbds.restmbds.domain.generator.DataGenerator;

import java.util.Map;

public interface FieldType {

    String code(); // ex: INTEGER, EMAIL

    DataGenerator getGenerator();

    Constraint createConstraint(Map<String, Object> config);
}