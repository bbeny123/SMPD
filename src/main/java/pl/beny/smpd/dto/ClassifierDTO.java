package pl.beny.smpd.dto;

import java.util.List;

public class ClassifierDTO {

    private String name;
    private String correct;
    private String incorrect;

    public ClassifierDTO(String name, List<Boolean> results) {
        this.name = name;

        long correct = results.stream().filter(r -> r).count();
        this.correct = String.format("%d - %.2f%%", correct, (double) correct / results.size() * 100);

        long incorrect = results.stream().filter(r -> !r).count();
        this.incorrect = String.format("%d - %.2f%%", incorrect, (double) incorrect / results.size() * 100);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getIncorrect() {
        return incorrect;
    }

    public void setIncorrect(String incorrect) {
        this.incorrect = incorrect;
    }
}
