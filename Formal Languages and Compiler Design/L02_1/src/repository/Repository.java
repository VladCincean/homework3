package repository;

import model.State;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Repository {
    private Set<State> states;

    public Repository() {
        states = new HashSet<>();
    }

    public void add(State state) {
        this.states.add(state);
    }

    public boolean contains(String label) {
        return !this.states.stream()
                .filter(s -> s.getLabel().equals(label))
                .collect(Collectors.toList())
                .isEmpty();
    }

    public State get(String label) {
        Optional<State> optional = this.states.stream()
                .filter(s -> s.getLabel().equals(label))
                .findAny();

        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public Set<State> getAll() {
        return this.states;
    }

    public Set<State> getFinalStates() {
        return this.states.stream()
                .filter(State::isFinal)
                .collect(Collectors.toSet());
    }
}
