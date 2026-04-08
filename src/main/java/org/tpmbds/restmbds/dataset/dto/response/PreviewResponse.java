package org.tpmbds.restmbds.dataset.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreviewResponse {
    private Long projectId;
    private Map<String, List<Map<String, Object>>> data;
}