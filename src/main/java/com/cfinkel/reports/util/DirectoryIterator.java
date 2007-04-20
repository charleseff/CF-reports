package com.cfinkel.reports.util;


import java.io.File;
import java.util.regex.Pattern;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: May 1, 2006
 * Time: 5:31:46 PM
 *
 * Iterates through a directory to find all files matching the appropriate pattern,
 * performs some function for each match
 */
public class DirectoryIterator {

    private final Pattern pattern;
    private final RunnerForFile runnerForFile;

    public void iterateForDirectory(File dir) throws RunnerException {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                iterateForDirectory(new File(dir, child));
            }

        } else if (dir.toString().matches(pattern.pattern())) {
            runnerForFile.run(dir);
        }
    }

    public DirectoryIterator(Pattern pattern,RunnerForFile r) {
        this.pattern = pattern;
        this.runnerForFile = r;
    }

}
