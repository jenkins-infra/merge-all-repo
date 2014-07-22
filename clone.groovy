#!/usr/bin/env groovy

// As an alternative to all.git, just makes sure you have all repositories cloned. Useful for searching.
// Clones into subdirectories of the current directory (so invoke as ./backend-merge-all-repo/clone.groovy).

@Grab("org.kohsuke:github-api:1.47")
import org.kohsuke.github.*;

GitHub gh;
if (new File(System.getProperty("user.home"), ".github").file) {
    println('connecting as per ~/.github');
    gh = GitHub.connect();
} else {
    println('connecting anonymously; rate limiting will probably make it impossible to do this twice in a row');
    println('try: echo oauth=… > ~/.github');
    gh = GitHub.connectAnonymously();
}

for (repo in gh.getOrganization("jenkinsci").listRepositories()) {
    if (new File(repo.name).directory) {
        println("${repo.name} already here, skipping");
    } else { 
        println("cloning ${repo.name} with size ${repo.size}");
        def r = new ProcessBuilder('git', 'clone', "git@github.com:jenkinsci/${repo.name}.git").redirectOutput(ProcessBuilder.Redirect.INHERIT).start().waitFor();
        if (r != 0) {
            println('Failed!');
            //System.exit(1);
        }
    }
}
