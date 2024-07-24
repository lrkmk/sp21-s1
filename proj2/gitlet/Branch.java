package gitlet;

import java.io.Serializable;

public class Branch implements Serializable {
    private String name;
    private String refToCommit;

    public Branch(String name, String ref) {
        this.name = name;
        this.refToCommit = ref;
    }

    public byte[] toFile() {
        return Utils.serialize(this);
    }

    public String getName() {
        return this.name;
    }

    public String getRefToCommit() {
        return this.refToCommit;
    }

    public void setRefToCommit(String commit) {
        this.refToCommit = commit;
    }
}
