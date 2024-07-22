package gitlet;

import jdk.jshell.execution.Util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File STAGE_DIR = join(GITLET_DIR, "staging_area");;
    public static File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static File BLOBS_DIR = join(OBJECTS_DIR, "blobs");;
    public static File COMMITS_DIR = join(OBJECTS_DIR, "commits");;
    public static File BRANCHES_DIR = join(GITLET_DIR, "branches");;
    public static final File HEAD = join(GITLET_DIR, "HEAD");;
    private Branch currentBranch;

    /* TODO: fill in the rest of this class. */
    public static void init() throws IOException {
        // create the whole structure of gitlet
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        STAGE_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        HEAD.createNewFile();

        // add the initial commit
        Commit com = new Commit("initial commit");
        File initCommit = join(COMMITS_DIR, com.getCommitID());
        initCommit.createNewFile();
        writeObject(initCommit, com);

        // head points to the initial commit
        Branch br = new Branch("Master", com.getCommitID());
        File branch = join(BRANCHES_DIR, "Master");
        branch.createNewFile();
        writeObject(branch, br);
        writeContents(HEAD, br.name);
    }

    public static void add(String filename) throws IOException {
        File f = join(CWD, filename);

        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        // check if the file being added to staging area has already been added
        File fs = join(STAGE_DIR, filename);
        if (fs.exists()) {
            // further check if they are different, if so, replace, else remove the file in staging area
            if (compareFiles(f, fs)) {
                Files.copy(f.toPath(), fs.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                Utils.restrictedDelete(fs);
            }
        } else {
            fs.createNewFile();
            Files.copy(f.toPath(), fs.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void commit(String message) throws IOException {
        if (message == null) {
            System.out.println("Please enter a commit message.");
            return;
        }
        File master = join(BRANCHES_DIR, "Master");
        Branch mas = readObject(master, Branch.class);

        // create a new commit with parent previously pointed by HEAD
        Commit com = new Commit(message, getSpecificCommit(mas.refToCommit));
        File[] files = STAGE_DIR.listFiles();
        if (files != null && files.length != 0) {
            String fileID = sha1(readContents(files[0]));
            com.addFile(files[0].getName(), fileID);
            File f = join(BLOBS_DIR, com.getFile(files[0].getName()));
            f.createNewFile();
            writeContents(f, readContents(files[0]));
            files[0].delete();
        } else {
            System.out.println("No changes added to the commit.");
            return;
        }


        // let Master branch points to newest commit
        File comFile = join(COMMITS_DIR, com.getCommitID());
        comFile.createNewFile();
        writeObject(comFile, com);
        mas.refToCommit = com.getCommitID();
        writeObject(master, mas);
    }

    public static void rm(String filename) {
        Commit com = getCurrentCommit();
        File stageFile = join(STAGE_DIR, filename);
        if (!stageFile.exists() && com.hasFile(filename)) {
            System.out.println("No reason to remove the file.");
        }
        if (stageFile.exists()) {
            Utils.restrictedDelete(stageFile);
        }
        if (com.hasFile(filename)) {
            join(CWD, filename).delete();
        }

    }

    public static void log() {
        Commit com = getCurrentCommit();
        System.out.println("===");
        System.out.println("Commit " + com.getCommitID());
        System.out.println("Date: " + com.getTimeStamp());
        System.out.println(com.getMessage());
        while (com.getParentID() != null) {
            com = readObject(join(COMMITS_DIR, com.getParentID()), Commit.class);
            System.out.println();
            System.out.println("===");
            System.out.println("Commit " + com.getCommitID());
            System.out.println("Date: " + com.getTimeStamp());
            System.out.println(com.getMessage());
        }
    }

    private static boolean compareFiles(File f1, File f2) throws IOException {
        Path p1 = f1.toPath();
        Path p2 = f2.toPath();

        byte[] file1Bytes = Files.readAllBytes(p1);
        byte[] file2Bytes = Files.readAllBytes(p2);

        return java.util.Arrays.equals(file1Bytes, file2Bytes);
    }

    private static Commit getCurrentCommit() {
        String branchName = Utils.readContentsAsString(HEAD);
        Branch current = readObject(join(BRANCHES_DIR, branchName), Branch.class);
        return readObject(join(COMMITS_DIR, current.refToCommit), Commit.class);
    }

    private static Commit getSpecificCommit(String commitID) {
        return readObject(join(COMMITS_DIR, commitID), Commit.class);
    }

    public static void checkoutFile(String filename) throws IOException {
        Commit com = getCurrentCommit();
        if (!com.hasFile(filename)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File f = join(CWD, filename);
        if (!f.exists()) {
            f.createNewFile();
        }
        writeContents(f, readContents(join(BLOBS_DIR, com.getFile(filename))));
    }

    public static void checkoutBranch(String branch) throws IOException {
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
        Commit com = readObject(join(COMMITS_DIR, currentBranch.refToCommit), Commit.class);

        // check whether there are untracked files in CWD
        File[] files = CWD.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isHidden();
            }
        });
        for (File f : files) {
            if (!com.hasFile(f.getName())) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

        // overwrite files in CWD
        HashMap<String, String> map = com.getRefs(); // Replace with actual method call

        Set<String> processedFiles = new HashSet<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            File fRecorded = join(BLOBS_DIR, entry.getValue());
            File fCWD = join(CWD, entry.getKey()); // Changed to entry.getKey() for the file name
            if (!fCWD.exists()) {
                fCWD.createNewFile();
                writeContents(fCWD, readContentsAsString(fRecorded));
            } else {
                writeContents(fCWD, readContentsAsString(fRecorded));
            }
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
        if (stageFiles[0].exists()) {
            restrictedDelete(stageFiles[0]);
        }

        // track current branch
        writeContents(HEAD, currentBranch.name);


    }

    public static void checkoutFileWithID(String commitID, String filename) throws IOException {
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
        if (!f.exists()) {
            f.createNewFile();
        }
        writeContents(f, readContents(join(BLOBS_DIR, com.getFile(filename))));
    }

}
