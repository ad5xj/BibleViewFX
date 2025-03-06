package org.crosswire.common.xml;

public enum FormatType { AS_IS(false, false, false),
                          ANALYSIS(true, false, false),
                          CLASSIC(true, false, true),
                          ANALYSIS_INDENT(true, true, false),
                          CLASSIC_INDENT(true, true, true);

    private boolean indented;
    private boolean multiline;
    private boolean analytic;
    private boolean classic;

    FormatType(boolean displayNewlines, boolean doIndenting, boolean classicLines) {
        this.multiline = displayNewlines;
        this.indented = (doIndenting && this.multiline);
        this.classic = (classicLines && this.multiline);
        this.analytic = (!classicLines && this.multiline);
    }

    public boolean isMultiline() {
        return this.multiline;
    }

    public boolean isIndented() {
        return this.indented;
    }

    public boolean isAnalytic() {
        return this.analytic;
    }

    public boolean isClassic() {
        return this.classic;
    }
}
