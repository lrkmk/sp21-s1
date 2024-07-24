package gitlet;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Commit parent1;
    private Commit parent2;
    private Date timeStamp;
    private HashMap<String, String> refs;
    private String commitID;

    public Commit(String m) {
        message = m;
        timeStamp = new Date(0);
        refs = new HashMap<>();
        commitID = hash();
    }

    public Commit(String m, Commit parent) {
        message = m;
        parent1 = parent;
        timeStamp = new Date();
        refs = new HashMap<>(parent.getRefs());
        commitID = hash();
    }

    public Commit(String m, Commit parent1, Commit parent2) {
        message = m;
        this.parent1 = parent1;
        this.parent2 = parent2;
        timeStamp = new Date();
        refs = new HashMap<>(parent1.getRefs());
        commitID = hash();
    }

    public String getMessage() {
        return message;
    };

    public String getParentID() {
        if (parent1 == null) {
            return null;
        }
        return  parent1.getCommitID();
    }

    public Commit getParent() {
        return parent1;
    }

    public Commit getParent2() {
        if (parent2 != null) {
            return parent2;
        }
        return null;
    }

    public String getTimeStamp() {
        return  getFormattedDate();
    }

    public String getCommitID() {
        return commitID;
    }

    public HashMap<String,String> getRefs() {
        return refs;
    }

    private String hash() {
        return Utils.sha1(Utils.serialize(this));
    }

    public void addFile(String fname, String fid) {
        refs.put(fname, fid);
        commitID = hash();
    }

    public String getFile(String fname) {
        return refs.get(fname);
    }

    public void removeFile(String fname) {
        refs.remove(fname);
    }

    public boolean hasFile(String fname) {
        return refs.containsKey(fname);
    }

    private String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles")); // Set time zone
        return sdf.format(timeStamp);
    }
}
