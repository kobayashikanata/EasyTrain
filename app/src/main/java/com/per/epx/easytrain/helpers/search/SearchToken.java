package com.per.epx.easytrain.helpers.search;

import java.io.Serializable;
import java.util.Locale;

public class SearchToken implements Serializable {
    private String simpleSpell="";//Simple spell
    private String wholeSpell="";//Complete spell
    private String origin ="";//Text value
    private String simpleSpellLowerCase="";
    private String wholeSpellLowerCase="";

    public SearchToken(String simpleSpell, String wholeSpell, String origin) {
        this.simpleSpell = simpleSpell;
        this.wholeSpell = wholeSpell;
        this.origin = origin;
        this.simpleSpellLowerCase = simpleSpell.toLowerCase(Locale.CHINESE);
        this.wholeSpellLowerCase = wholeSpell.toLowerCase(Locale.CHINESE);
    }

    public String getOrigin() {
        return origin;
    }

    public String getSimpleSpell() {
        return simpleSpell;
    }

    public String getWholeSpell() {
        return wholeSpell;
    }

    public String getSimpleSpellLowerCase() {
        return simpleSpellLowerCase;
    }

    public String getWholeSpellLowerCase() {
        return wholeSpellLowerCase;
    }

}
