package webApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webApp.models.SearchPreset;
import java.util.Optional;

public interface SearchPresetRepository extends JpaRepository<SearchPreset, Long> {
    Optional<SearchPreset> findByPresetName(String presetName);
    void deleteByPresetName(String presetName);
    boolean existsByPresetName(String presetName);
}
