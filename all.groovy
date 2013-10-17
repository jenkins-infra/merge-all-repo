#!/usr/bin/env groovy
@Grab("org.kohsuke:github-api:1.29")
import org.kohsuke.github.*;

def repo = new File(args[0])
args = args as List
args.remove(0)
if (!repo.isDirectory())
    throw new Error(repo.absolutePath+" doesn't exist")

if (args.size()==0) {
    // there are so many repositories to go through, and often we end up aborting in the middle
    // randomize the order so that we even out the coverage
    args = new ArrayList(GitHub.connectAnonymously().getOrganization("jenkinsci").getRepositories().keySet())
    Collections.shuffle(args);
}

def waitForUpToMinute(p) {
    for (int i=0; i<60; i++) {
        try {
            if (p.exitValue()==0)
                return;
            else
                System.exit(p.exitValue());
        } catch (IllegalThreadStateException e) {
            // hasn't finished yet
            Thread.sleep(1000);
            continue;
        }
    }
    println "Taking too long. Moving on";
    p.destroy();
}

args.each { k ->
    println k;
    def p = new ProcessBuilder(["sh","-c","cd '${repo}' && git fetch --no-tags https://github.com/jenkinsci/${k}.git '+refs/heads/*:refs/heads/${k}/*' '+refs/tags/*:refs/tags/${k}/*'"] as String[]).redirectErrorStream(true).start()
    p.in.eachLine { line -> println "  ${line}" }
    waitForUpToMinute(p);
}


