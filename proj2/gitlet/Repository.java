package gitlet;


import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author
 */
public class Repository {
    /**
     *
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    private static final File STAGE_DIR = join(GITLET_DIR, "staging_area");;
    private static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    private static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");;
    private static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");;
    private static final File BRANCHES_DIR = join(GITLET_DIR, "branches");;
    private static final File RSTAGE_DIR = join(GITLET_DIR, "remove_stage");
    private static final File HEAD = join(GITLET_DIR, "HEAD");;

    public static void init() {
        // create the whole structure of gitlet
        if (GITLET_DIR.exists()) {
            System.out.println(
                    "A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        STAGE_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        RSTAGE_DIR.mkdir();
        try {
            HEAD.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // add the initial commit
        Commit com = new Commit("initial commit");
        File initCommit = join(COMMITS_DIR, com.getCommitID());
        writeObject(initCommit, com);

        // head points to the initial commit
        Branch br = new Branch("master", com.getCommitID());
        File branch = join(BRANCHES_DIR, "master");
        writeObject(branch, br);
        writeContents(HEAD, br.getName());
    }

    public static void add(String filename) {
        File f = join(CWD, filename);

        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        File fs = join(STAGE_DIR, filename);
        File fr = join(RSTAGE_DIR, filename);
        if (fr.exists()) {
            writeContents(f, readContentsAsString(fr));
            fr.delete();
            return;
        }

        if (getCurrentCommit().hasFile(filename)) {
            // remove the file in staging area if the content
            // in CWD is the same as tracked by current commit
            if (Arrays.equals(readContents(join(BLOBS_DIR,
                    getCurrentCommit().getFile(filename))), readContents(f))) {
                if (fs.exists()) {
                    fs.delete();
                }
                return;
            }
        }
        writeContents(fs, readContents(f));
    }


    public static void commit(String message) {
        if (message == null || message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        File currentBranchFile = join(BRANCHES_DIR, readContentsAsString(HEAD));
        Branch currentBranch = readObject(currentBranchFile, Branch.class);

        // create a new commit with parent previously pointed by HEAD
        Commit com = new Commit(message, getSpecificCommit(currentBranch.getRefToCommit()));
        File[] files = STAGE_DIR.listFiles();
        File[] rFiles = RSTAGE_DIR.listFiles();
        boolean changed = false;
        if (files != null && files.length != 0) {
            changed = true;
            for (File file : files) {
                String fileID = sha1(readContents(file));
                com.addFile(file.getName(), fileID);
                File f = join(BLOBS_DIR, com.getFile(file.getName()));
                writeContents(f, readContents(file));
                file.delete();
            }

        }
        if (rFiles != null && rFiles.length != 0) {
            changed = true;
            for (File rfile : rFiles) {
                com.removeFile(rfile.getName());
                rfile.delete();
            }
        }
        if (!changed) {
            System.out.println("No changes added to the commit.");
            return;
        }


        // let Master branch points to newest commit
        File comFile = join(COMMITS_DIR, com.getCommitID());
        writeObject(comFile, com);
        currentBranch.setRefToCommit(com.getCommitID());
        writeObject(currentBranchFile, currentBranch);
    }

    public static void mergeCommit(String message, Commit p1, Commit p2) {
        File master = join(BRANCHES_DIR, "master");
        Branch mas = readObject(master, Branch.class);

        // create a new commit with parent previously pointed by HEAD
        Commit com = new Commit(message, p1, p2);
        File[] files = STAGE_DIR.listFiles();
        File[] rFiles = RSTAGE_DIR.listFiles();
        boolean flag = false;
        if (files != null && files.length != 0) {
            flag = true;
            for (File file : files) {
                String fileID = sha1(readContents(file));
                com.addFile(file.getName(), fileID);
                File f = join(BLOBS_DIR, com.getFile(file.getName()));
                writeContents(f, readContents(file));
                file.delete();
            }


        }
        if (rFiles != null && rFiles.length != 0) {
            flag = true;
            for (File rfile : rFiles) {
                com.removeFile(rfile.getName());
                rfile.delete();
            }
        }
        if (!flag) {
            System.out.println("No changes added to the commit.");
            return;
        }


        // let Master branch points to newest commit
        File comFile = join(COMMITS_DIR, com.getCommitID());
        writeObject(comFile, com);
        mas.setRefToCommit(com.getCommitID());
        writeObject(master, mas);

        // HEAD should point to Master
        writeContents(HEAD, mas.getName());
    }

    public static void rm(String filename) {
        Commit com = getCurrentCommit();
        File stageFile = join(STAGE_DIR, filename);
        if (!stageFile.exists() && !com.hasFile(filename)) {
            System.out.println("No reason to remove the file.");
        }
        if (stageFile.exists()) {
            stageFile.delete();
        }
        if (com.hasFile(filename)) {
            File f = join(CWD, filename);
            File rf = join(RSTAGE_DIR, filename);
            if (f.exists()) {
                writeContents(rf, readContentsAsString(f));
                f.delete();
            } else {
                writeContents(rf, readContentsAsString(join(BLOBS_DIR, com.getFile(filename))));
            }
        }
    }

    public static void log() {
        Commit com = getCurrentCommit();
        System.out.println("===");
        System.out.println("commit " + com.getCommitID());
        if (com.getParent2() != null) {
            System.out.println("Merge: "
                    + com.getParent1().getCommitID().substring(0, 7)
                    + " "
                    + com.getParent2().getCommitID().substring(0, 7));
        }
        System.out.println("Date: " + com.getTimeStamp());
        System.out.println(com.getMessage());
        System.out.println();
        while (com.getParentID() != null) {
            com = readObject(join(COMMITS_DIR, com.getParentID()), Commit.class);
            System.out.println("===");
            System.out.println("commit " + com.getCommitID());
            if (com.getParent2() != null) {
                System.out.println("Merge: "
                        + com.getParent1().getCommitID().substring(0, 7)
                        + " "
                        + com.getParent2().getCommitID().substring(0, 7));
            }
            System.out.println("Date: " + com.getTimeStamp());
            System.out.println(com.getMessage());
            System.out.println();
        }
    }

    public static void globalLog() {
        List<String> files = plainFilenamesIn(COMMITS_DIR);
        if (files.isEmpty()) {
            return;
        }
        for (String filename: files) {
            Commit com = getSpecificCommit(filename);
            System.out.println("===");
            System.out.println("commit " + com.getCommitID());
            System.out.println("Date: " + com.getTimeStamp());
            System.out.println(com.getMessage());
            System.out.println();
        }
    }

    private static Commit getCurrentCommit() {
        String branchName = Utils.readContentsAsString(HEAD);
        Branch current = readObject(join(BRANCHES_DIR, branchName), Branch.class);
        return readObject(join(COMMITS_DIR, current.getRefToCommit()), Commit.class);
    }

    private static Commit getSpecificCommit(String commitID) {
        File com = join(COMMITS_DIR, commitID);
        if (!com.exists()) {
            return null;
        }
        return readObject(com, Commit.class);
    }

    public static void checkoutFile(String filename) {
        Commit com = getCurrentCommit();
        if (!com.hasFile(filename)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File f = join(CWD, filename);
        writeContents(f, readContents(join(BLOBS_DIR, com.getFile(filename))));

    }

    public static void checkoutBranch(String branch)  {
        File br = join(BRANCHES_DIR, branch);
        if (!br.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        String branchName = Utils.readContentsAsString(HEAD);
        File currentBr = join(BRANCHES_DIR, branchName);
        if (currentBr.getName().equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Branch currentBranch = readObject(currentBr, Branch.class);
        Commit com = readObject(join(COMMITS_DIR, currentBranch.getRefToCommit()), Commit.class);
        Branch targetBranch = readObject(join(BRANCHES_DIR, branch), Branch.class);
        Commit targetCom = readObject(join(COMMITS_DIR, targetBranch.getRefToCommit()),
                Commit.class);
        // check whether there are untracked files in CWD that would result in conflict
        List<String> untracked = getUntracked();
        for (String f : untracked) {
            if (targetCom.hasFile(f)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                return;
            }
        }

        // overwrite files in CWD
        HashMap<String, String> map = targetCom.getRefs();

        Set<String> processedFiles = new HashSet<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            File fRecorded = join(BLOBS_DIR, entry.getValue());
            File fCWD = join(CWD, entry.getKey()); // Changed to entry.getKey() for the file name
            writeContents(fCWD, readContentsAsString(fRecorded));
            processedFiles.add(fCWD.getName());
        }

        // Iterate through CWD and delete files not in processedFiles
        File[] cwdFiles = CWD.listFiles();
        if (cwdFiles != null) {
            for (File file : cwdFiles) {
                if (file.isFile() && !processedFiles.contains(file.getName())) {
                    file.delete();
                }
            }
        }

        // clear staging area
        File[] stageFiles = STAGE_DIR.listFiles();
        assert stageFiles != null;
        for (File file: stageFiles) {
            file.delete();
        }

        // track current branch
        writeContents(HEAD, targetBranch.getName());
    }


    public static void checkoutFileWithID(String commitID, String filename)  {
        String[] fileList = COMMITS_DIR.list();
        for (String str : fileList) {
            if (str.startsWith(commitID)) {
                commitID = str;
            }
        }
        File comFile = join(COMMITS_DIR, commitID);
        if (!comFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit com = readObject(comFile, Commit.class);
        if (!com.hasFile(filename)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File f = join(CWD, filename);
        writeContents(f, readContents(join(BLOBS_DIR, com.getFile(filename))));
    }

    public static void find(String message) {
        List<String> files = plainFilenamesIn(COMMITS_DIR);
        boolean flag = false;
        for (String filename: files) {
            Commit com = getSpecificCommit(filename);
            if (com.getMessage().equals(message)) {
                System.out.println(com.getCommitID());
                flag = true;
            }
        }
        if (!flag) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        System.out.println("=== Branches ===");
        List<String> branches = plainFilenamesIn(BRANCHES_DIR);
        Collections.sort(branches);
        for (String branchName: branches) {
            if (branchName.equals(readContentsAsString(HEAD))) {
                System.out.print("*");
            }
            System.out.println(branchName);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        List<String> stages = plainFilenamesIn(STAGE_DIR);
        Collections.sort(stages);
        for (String fileName: stages) {
            System.out.println(fileName);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        List<String> removes = plainFilenamesIn(RSTAGE_DIR);
        Collections.sort(removes);
        for (String fileName: removes) {
            System.out.println(fileName);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (String filename: getUntracked()) {
            System.out.println(filename);
        }
        System.out.println();
    }

    public static void branch(String branchName) {
        File branchFile = join(BRANCHES_DIR, branchName);
        if (branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        try {
            branchFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Branch branch = new Branch(branchName, getCurrentCommit().getCommitID());
        writeObject(branchFile, branch);
    }

    public static void removeBranch(String branchName) {
        File branchFile = join(BRANCHES_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
        } else if (readContentsAsString(HEAD).equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            branchFile.delete();
        }
    }

    public static void reset(String commitID) {
        Commit com = getSpecificCommit(commitID);
        if (com == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        for (String filename: getUntracked()) {
            if (com.hasFile(filename)) {
                System.out.println("There is an untracked file in the way; delete it,"
                        + "or add and commit it first.");
                return;
            }
        }
        for (Map.Entry<String, String> set: com.getRefs().entrySet()) {
            checkoutFileWithID(commitID, set.getKey());
        }
        for (Map.Entry<String, String> set: getCurrentCommit().getRefs().entrySet()) {
            if (!com.hasFile(set.getKey())) {
                join(CWD, set.getKey()).delete();
            }
        }
        File[] stageFiles = STAGE_DIR.listFiles();
        assert stageFiles != null;
        for (File file: stageFiles) {
            file.delete();
        }
        Branch currBranch = readObject(join(BRANCHES_DIR,
                readContentsAsString(HEAD)), Branch.class);
        currBranch.setRefToCommit(commitID);
        File branchFile = join(BRANCHES_DIR, currBranch.getName());
        writeObject(branchFile, currBranch);
    }

    public static void merge(String branchName) {
        if (plainFilenamesIn(STAGE_DIR).size() > 0 || plainFilenamesIn(RSTAGE_DIR).size() > 0) {
            System.out.println("You have uncommitted changes");
            return;
        }
        File branch = join(BRANCHES_DIR, branchName);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (branchName.equals(readContentsAsString(HEAD))) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        if (getUntracked().size() > 0) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            return;
        }
        Branch br = readObject(branch, Branch.class);
        Commit givenCom = readObject(join(COMMITS_DIR, br.getRefToCommit()), Commit.class);
        Commit currCom = getCurrentCommit();
        Commit ancestor = getLatestCommonAncestor(givenCom);
        if (ancestor == null) {
            System.out.println("ancestor not found");
        }
        if (givenCom.equals(ancestor)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (currCom.equals(ancestor)) {
            System.out.println("Current branch fast-forwarded.");
            checkoutBranch(branchName);
            return;
        }

        for (Map.Entry<String, String> givenEntry : givenCom.getRefs().entrySet()) {
            // if files are modified in given branch
            // since the split point but not modified in current branch
            if (ancestor.hasFile(givenEntry.getKey())
                    && !givenEntry.getValue().equals(ancestor.getFile(givenEntry.getKey()))
                    && ancestor.getFile(givenEntry.getKey()).
                    equals(currCom.getFile(givenEntry.getKey()))) {
                checkoutFileWithID(currCom.getCommitID(), givenEntry.getKey());
                // stage files differing from ancestor's record
                File f = join(BLOBS_DIR, givenEntry.getValue());
                File fStage = join(STAGE_DIR, givenEntry.getKey());
                writeContents(fStage, readContentsAsString(f));
            }
            if (!ancestor.hasFile(givenEntry.getKey()) && !currCom.hasFile(givenEntry.getKey())) {
                // take file in head version, put in CWD
                checkoutFileWithID(givenCom.getCommitID(), givenEntry.getKey());
                File f = join(BLOBS_DIR, givenEntry.getValue());
                File fStage = join(STAGE_DIR, givenEntry.getKey());
                writeContents(fStage, readContentsAsString(f));
            }

        }

        for (Map.Entry<String, String> currEntry : currCom.getRefs().entrySet()) {
            if (ancestor.hasFile(currEntry.getKey()) && !givenCom.hasFile(currEntry.getKey())
                    && ancestor.getFile(currEntry.getKey()).equals(currEntry.getValue())) {
                rm(currEntry.getKey());
                join(CWD, currEntry.getKey()).delete();
            }
        }

        // check for conflicts
        Set<String> conflict = getConflict(currCom, givenCom, ancestor);
        handleConflict(conflict, currCom, givenCom);


        mergeCommit("Merged " + branchName + " into "
                + readContentsAsString(HEAD) + ".", currCom, givenCom);

    }

    private static Set<String> getConflict(Commit currCom, Commit givenCom, Commit ancestor) {
        Set<String> keys1 = new HashSet<>(currCom.getRefs().keySet());
        Set<String> keys2 = new HashSet<>(givenCom.getRefs().keySet());

        Set<String> commonKeys = new HashSet<>(keys1);
        commonKeys.retainAll(keys2);
        Set<String> uniqueKeys1 = new HashSet<>(keys1);
        Set<String> uniqueKeys2 = new HashSet<>(keys2);
        uniqueKeys1.removeAll(commonKeys);
        uniqueKeys2.removeAll(commonKeys);
        uniqueKeys1.addAll(uniqueKeys2);

        Set<String> conflictValueKeys = new HashSet<>();
        for (String key : commonKeys) {
            if (!currCom.getRefs().get(key).equals(givenCom.getRefs().get(key))) {
                conflictValueKeys.add(key);
            }
        }

        for (String key : uniqueKeys1) {
            if (ancestor.hasFile(key) && currCom.hasFile(key)
                    && !currCom.getFile(key).equals(ancestor.getFile(key))) {
                conflictValueKeys.add(key);
            } else if (ancestor.hasFile(key) && givenCom.hasFile(key)
                    && !givenCom.getFile(key).equals(ancestor.getFile(key))) {
                conflictValueKeys.add(key);
            }
        }

        return conflictValueKeys;
    }

    private static void handleConflict(Set<String> set, Commit currCom, Commit givenCom) {
        boolean getConflict = false;
        if (!set.isEmpty()) {
            getConflict = true;
            for (String filename: set) {
                File f = join(CWD, filename);
                if (!currCom.hasFile(filename)) {
                    writeContents(f, "<<<<<<< HEAD\n"
                            + "=======\n"
                            + readContentsAsString(join(BLOBS_DIR, givenCom.getFile(filename)))
                            + ">>>>>>>\n");
                } else if (!givenCom.hasFile(filename)) {
                    writeContents(f, "<<<<<<< HEAD\n"
                            + readContentsAsString(join(BLOBS_DIR, currCom.getFile(filename)))
                            + "=======\n"
                            + ">>>>>>>\n");
                } else {
                    writeContents(f, "<<<<<<< HEAD\n"
                            + readContentsAsString(join(BLOBS_DIR, currCom.getFile(filename)))
                            + "=======\n"
                            + readContentsAsString(join(BLOBS_DIR, givenCom.getFile(filename)))
                            + ">>>>>>>\n");
                }
            }
        }

        if (getConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    public static Commit getLatestCommonAncestor(Commit com) {
        Commit currCom = getCurrentCommit();
        Set<Commit> currParent = new HashSet<>();
        Set<Commit> givenParent = new HashSet<>();

        Queue<Commit> queue = new ArrayDeque<>();
        queue.add(currCom);
        while (!queue.isEmpty()) {
            Commit current = queue.poll();
            currParent.add(current);
            if (current.getParent1() != null) {
                queue.add(current.getParent1());
            }
            if (current.getParent2() != null) {
                queue.add(current.getParent2());
            }
        }

        queue.add(com);
        while (!queue.isEmpty()) {
            Commit current = queue.poll();
            givenParent.add(current);
            if (current.getParent1() != null) {
                queue.add(current.getParent1());
            }
            if (current.getParent2() != null) {
                queue.add(current.getParent2());
            }
        }


        Set<Commit> intersection = findIntersection(currParent, givenParent);

        return Collections.max(intersection, Comparator.comparing(Commit::getDate));
    }

    public static Set<Commit> findIntersection(Set<Commit> set1, Set<Commit> set2) {
        Map<String, Commit> idToCommitMap = new HashMap<>();
        Set<Commit> intersection = new HashSet<>();

        // Populate the map with commits from the first set
        for (Commit commit : set1) {
            idToCommitMap.put(commit.getCommitID(), commit);
        }

        // Check for intersection based on commit IDs
        for (Commit commit : set2) {
            if (idToCommitMap.containsKey(commit.getCommitID())) {
                intersection.add(commit);
            }
        }

        return intersection;
    }

    private static List<String> getUntracked() {
        Commit com = getCurrentCommit();
        List<String> files = plainFilenamesIn(CWD);
        List<String> untracked = new ArrayList<>();
        for (String fileName: files) {
            if (!com.hasFile(fileName) && !join(STAGE_DIR, fileName).exists()
                    && !join(RSTAGE_DIR, fileName).exists()) {
                untracked.add(fileName);
            }
        }
        return untracked;
    }
}
