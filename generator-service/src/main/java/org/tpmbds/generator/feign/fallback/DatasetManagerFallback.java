package org.tpmbds.generator.feign.fallback;

import org.springframework.stereotype.Component;
import org.tpmbds.generator.dto.ProjectDefinitionDto;
import org.tpmbds.generator.feign.DatasetManagerClient;

/**
 * Fallback Resilience4J : appelé quand dataset-manager-service est indisponible
 * ou dépasse le TimeLimiter (3s).
 *
 * Renvoie un ProjectDefinitionDto vide — le GeneratorService détectera
 * que la liste d'entités est nulle/vide et lèvera une ServiceUnavailableException.
 */
@Component
public class DatasetManagerFallback implements DatasetManagerClient {

    @Override
    public ProjectDefinitionDto getProject(Long id) {
        // Retourne null intentionnellement pour que le service de génération
        // puisse renvoyer une réponse de fallback propre à l'utilisateur.
        return null;
    }
}
