package org.tpmbds.manager.feign.dto;

import java.util.List;
import java.util.Map;

public class PreviewResponse {
    private Long projectId;
    private Map<String, List<Map<String, Object>>> data;
    private String status;
    private String message;

    public PreviewResponse() {
    }

    public PreviewResponse(Long projectId, Map<String, List<Map<String, Object>>> data, String status, String message) {
        this.projectId = projectId;
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Map<String, List<Map<String, Object>>> getData() {
        return data;
    }

    public void setData(Map<String, List<Map<String, Object>>> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
