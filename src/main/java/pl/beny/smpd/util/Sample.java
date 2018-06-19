package pl.beny.smpd.util;

import java.util.List;

public class Sample {

    private String className;
    private List<Double> attr;

    public Sample(String className, List<Double> attr) {
        this.className = className;
        this.attr = attr;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Double> getAttr() {
        return attr;
    }

    public void setAttr(List<Double> attr) {
        this.attr = attr;
    }
}
