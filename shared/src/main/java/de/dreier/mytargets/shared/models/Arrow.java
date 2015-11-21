package de.dreier.mytargets.shared.models;

import java.util.ArrayList;
import java.util.List;

public class Arrow extends ImageHolder {
    static final long serialVersionUID = 50L;

    public String name;
    public String length;
    public String material;
    public String spine;
    public String weight;
    public String tipWeight;
    public String vanes;
    public String nock;
    public String comment;
    public List<ArrowNumber> numbers = new ArrayList<>();
}