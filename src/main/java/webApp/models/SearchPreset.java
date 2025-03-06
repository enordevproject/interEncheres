package webApp.models;

import jakarta.persistence.*;
import java.util.Map;

@Entity
@Table(name = "search_presets")
public class SearchPreset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "preset_name", unique = true, nullable = false)
    private String presetName;

    @ElementCollection
    @CollectionTable(name = "preset_filters", joinColumns = @JoinColumn(name = "preset_id"))
    @MapKeyColumn(name = "filter_key")
    @Column(name = "filter_value")
    private Map<String, String> filters;  // Stores all selected filters

    public SearchPreset() {}

    public SearchPreset(String presetName, Map<String, String> filters) {
        this.presetName = presetName;
        this.filters = filters;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPresetName() { return presetName; }
    public void setPresetName(String presetName) { this.presetName = presetName; }

    public Map<String, String> getFilters() { return filters; }
    public void setFilters(Map<String, String> filters) { this.filters = filters; }
}
