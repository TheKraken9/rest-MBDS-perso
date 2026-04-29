package org.tpmbds.generator.domain.fieldtype;
import org.tpmbds.generator.domain.constraint.Constraint;
import org.tpmbds.generator.domain.generator.DataGenerator;
import java.util.Map;
public abstract class FieldType {
    public abstract String code();
    public abstract DataGenerator getGenerator();
    public abstract Constraint createConstraint(Map<String, Object> config);
}
