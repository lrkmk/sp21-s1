package gitlet;

import java.io.Serializable;

public class Branch implements Serializable {
    public String name;
    public String refToCommit;

    public Branch(String name, String ref) {
        this.name = name;
        this.refToCommit = ref;
    }

    public byte[] toFile() {
        return Utils.serialize(this);
    }
}
