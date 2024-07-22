package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private String parent1;
    private String parent2;
    private Date timeStamp;
    private HashMap<String,String> refs;


    /* TODO: fill in the rest of this class. */
    public Commit(String m) {
        message = m;
        timeStamp = new Date(0);
        refs = new HashMap<>();
    }

    public Commit(String m, Commit parent) {
        message = m;
        parent1 = parent.getCommitID();
        timeStamp = new Date();
        refs = new HashMap<>(parent.getRefs());
    }

    public String getMessage() { return message; };
    public String getParentID() { return  parent1; }
    public String getTimeStamp() { return  timeStamp.toString(); }
    public String getCommitID() { return this.hash(); }
    public HashMap<String,String> getRefs() { return refs; }

    private String hash() {
        return Utils.sha1(Utils.serialize(this));
    }

    public void addFile(String fname, String fid) {
        refs.put(fname, fid);
    }

    public String getFile(String fname) {
        return refs.get(fname);
    }

    public boolean hasFile(String fname) {
        return refs.containsKey(fname);
    }

}
