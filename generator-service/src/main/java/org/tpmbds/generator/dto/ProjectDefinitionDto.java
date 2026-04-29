package org.tpmbds.generator.dto;

import java.util.List;

public class ProjectDefinitionDto {
    private Long id;
    private String name;
    private Integer size;
    private List<EntityDefinitionDto> entities;

    public ProjectDefinitionDto() {
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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<EntityDefinitionDto> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityDefinitionDto> entities) {
        this.entities = entities;
    }
}
