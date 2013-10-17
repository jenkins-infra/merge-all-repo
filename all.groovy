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

args.each { k ->
    println k;
    def p = new ProcessBuilder(["sh","-c","cd '${repo}' && git fetch --no-tags https://github.com/jenkinsci/${k}.git '+refs/heads/*:refs/heads/${k}/*' '+refs/tags/*:refs/tags/${k}/*'"] as String[]).redirectErrorStream(true).start()
    p.in.eachLine { line -> println "  ${line}" }
    if (p.waitFor()!=0)
        System.exit(p.exitValue());
}


