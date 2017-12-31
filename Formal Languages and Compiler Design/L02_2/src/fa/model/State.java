package fa.model;

public class State {
    private String label;
    private boolean isInitial;
    private boolean isFinal;

    public State(String label) {
        this.label = label;
        this.isInitial = false;
        this.isFinal = false;
    }

    public State(String label, boolean isInitial, boolean isFinal) {
        this.label = label;
        this.isInitial = isInitial;
        this.isFinal = isFinal;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public void setInitial(boolean initial) {
        isInitial = initial;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        return label.equals(state.label);
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }

    @Override
    public String toString() {
        return "State{" +
                "label='" + label + '\'' +
                ", isInitial=" + isInitial +
                ", isFinal=" + isFinal +
                '}';
    }
}
