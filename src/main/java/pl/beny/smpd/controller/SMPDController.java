package pl.beny.smpd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import pl.beny.smpd.dto.BootstrapDTO;
import pl.beny.smpd.dto.ClassifierDTO;
import pl.beny.smpd.dto.FisherDTO;
import pl.beny.smpd.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class SMPDController {

    private List<Sample> training;
    private List<Sample> test;
    private int part;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/fisher")
    public String fisher() {
        return "fisher";
    }

    @PostMapping("/fisher")
    public String computeFisher(Model model, int n) {
        FisherDTO results = new FisherDTO();

        long startTime = System.nanoTime();
        if (n < 6) results.addFisher(new Fisher().computeFisherP(n), execTime(startTime));

        startTime = System.nanoTime();
        results.addSFS(new SFS().computeSFSP(n), execTime(startTime));

        model.addAttribute("results", results);
        return "fisher";
    }

    @GetMapping("/classifiers")
    public String classifiers() {
        return "classifiers";
    }

    @PostMapping("/classifiers/train")
    public String train(int part) {
        training = new ArrayList<>();
        test = new ArrayList<>(Database.getSamples());
        this.part = part;

        int size = (int) (test.size() * part * 0.01);
        IntStream.range(0, size).forEach(i ->
                training.add(test.remove(new Random().nextInt(test.size() - 1)))
        );

        return "redirect:/classifiers";
    }

    @PostMapping("/classifiers/{method}")
    public RedirectView classify(@PathVariable("method") String method, Integer k, RedirectAttributes attributes) {
        if (training == null) return new RedirectView("/classifiers");

        List<ClassifierDTO> results = new ArrayList<>();
        boolean all = "all".equals(method);

        if (all || "nn".equals(method))
            results.add(new ClassifierDTO("NN", test.parallelStream().map(s -> Classifiers.classifyNN(s, training)).collect(Collectors.toList())));
        if (all || "nm".equals(method))
            results.add(new ClassifierDTO("NM", test.parallelStream().map(s -> Classifiers.classifyNM(s, training)).collect(Collectors.toList())));
        if (all || "knn".equals(method))
            results.add(new ClassifierDTO(String.format("KNN (k = %d)", k), test.parallelStream().map(s -> Classifiers.classifyKNN(s, training, k)).collect(Collectors.toList())));

        attributes.addFlashAttribute("training", String.format("%d - %d%%", training.size(), part));
        attributes.addFlashAttribute("test", String.format("%d - %d%%", test.size(), 100 - part));
        attributes.addFlashAttribute("results", results);
        return new RedirectView("/classifiers");
    }

    @GetMapping("/bootstrap")
    public String bootstrap() {
        return "bootstrap";
    }

    @PostMapping("/bootstrap/{method}")
    public RedirectView quality(@PathVariable("method") String method, Integer iterations, Integer samples, Integer subsets, Integer k, RedirectAttributes attributes) {
        List<BootstrapDTO> results = new ArrayList<>();
        boolean both = "both".equals(method);

        if (both || "csv".equals(method))
            results.add(new BootstrapDTO(String.format("Cross-validation (%d subsets)", subsets), Quality.checkCrossvalidation(subsets, k), k));
        if (both || "bts".equals(method))
            results.add(new BootstrapDTO(String.format("Bootstrap (%d iterations, samples size = %d)", iterations, samples), Quality.checkBootstrap(iterations, samples, k), k));

        attributes.addFlashAttribute("results", results);
        return new RedirectView("/bootstrap");
    }

    private long execTime(long startTime) {
        return (System.nanoTime() - startTime) / 1000000;
    }

}
