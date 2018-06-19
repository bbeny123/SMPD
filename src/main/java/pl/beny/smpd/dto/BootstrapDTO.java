package pl.beny.smpd.dto;

import java.util.ArrayList;
import java.util.List;

public class BootstrapDTO {

    private String name;
    private List<ClassifierDTO> results = new ArrayList<>();

    public BootstrapDTO(String name, List<List<Boolean>> results, int k) {
        this.name = name;
        this.results.add(new ClassifierDTO("NN", results.get(0)));
        this.results.add(new ClassifierDTO("NM", results.get(1)));
        this.results.add(new ClassifierDTO(String.format("KNN (k = %d)", k), results.get(2)));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ClassifierDTO> getResults() {
        return results;
    }

    public void setResults(List<ClassifierDTO> results) {
        this.results = results;
    }
}
