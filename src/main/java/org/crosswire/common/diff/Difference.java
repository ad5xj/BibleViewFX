package org.crosswire.common.diff;

public class Difference {
    private int index;

    private String text;

    private EditType editType;

    public Difference(EditType edit, String text) {
        this.editType = edit;
        this.text = text;
    }

    public EditType getEditType() {
        return this.editType;
    }

    public void setEditType(EditType newEditType) {
        this.editType = newEditType;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String newText) {
        this.text = newText;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int newIndex) {
        this.index = newIndex;
    }

    public void appendText(String addText) {
        this.text += addText;
    }

    public void appendText(char addText) {
        this.text += addText;
    }

    public void prependText(String addText) {
        this.text = addText + this.text;
    }

    public void prependText(char addText) {
        this.text = addText + this.text;
    }

    public String toString() {
        return this.editType.toString() + ':' + this.text;
    }

    public int hashCode() {
        return 31 * this.editType.hashCode() + this.text.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Difference other = (Difference) obj;
        return (this.editType.equals(other.editType) && this.text.equals(other.text));
    }
}
