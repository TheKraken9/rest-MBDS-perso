package org.tpmbds.generator.dto;

import java.util.List;

public class EntityDefinitionDto {
    private Long id;
    private String name;
    private Integer rowCount;
    private List<FieldDefinitionDto> fields;
    private List<EntityDefinitionDto> subEntities;

    public EntityDefinitionDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public List<FieldDefinitionDto> getFields() {
        return fields;
    }

    public void setFields(List<FieldDefinitionDto> fields) {
        this.fields = fields;
    }

    public List<EntityDefinitionDto> getSubEntities() {
        return subEntities;
    }

    public void setSubEntities(List<EntityDefinitionDto> subEntities) {
        this.subEntities = subEntities;
    }
}
