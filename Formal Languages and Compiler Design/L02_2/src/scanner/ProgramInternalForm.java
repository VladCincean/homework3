package scanner;

import util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ProgramInternalForm {
    private List<Pair<Integer, Integer>> list;

    public ProgramInternalForm() {
        list = new ArrayList<>();
    }

    public void add(Integer tokenCode, Integer indexST) {
        this.list.add(new Pair<>(tokenCode, indexST));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %-5s | %-5s |\n", "Code", "Addr"));
        sb.append(String.format("+-%-5s-+-%-5s-+\n", "-----", "-----"));
        for (Pair<Integer, Integer> el : this.list) {
            sb.append(String.format("| %-5s | %-5s |\n", el.getFirst(), el.getSecond()));
        }
        sb.append(String.format("+-%-5s-+-%-5s-+\n", "-----", "-----"));

        return sb.toString();
//        return "ProgramInternalForm{" +
//                "list=" + list +
//                '}';
    }
}
