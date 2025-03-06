package webApp.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webApp.models.SearchPreset;
import webApp.repositories.SearchPresetRepository;
import java.util.List;
import java.util.Optional;

@Service
public class SearchPresetService {

    @Autowired
    private SearchPresetRepository repository;

    public SearchPreset savePreset(SearchPreset preset) {
        return repository.save(preset);
    }

    public Optional<SearchPreset> getPreset(String name) {
        return repository.findByPresetName(name);
    }

    public List<SearchPreset> getAllPresets() {
        return repository.findAll();
    }

    @Transactional // ✅ Ensures transaction is active when deleting
    public void deletePreset(String name) {
        if (!repository.existsByPresetName(name)) {
            throw new IllegalArgumentException("Preset not found: " + name);
        }
        repository.deleteByPresetName(name);
    }

    public boolean presetExists(String name) {
        return repository.existsByPresetName(name);
    }

}
