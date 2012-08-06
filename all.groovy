#!/usr/bin/env groovy
@Grab("org.kohsuke:github-api:1.29")
import org.kohsuke.github.*;

def repo = new File(args[0])
if (!repo.isDirectory())
    throw new Error(repo.absolutePath+" doesn't exist")

if (args.length==0) {
    args = GitHub.connect().getOrganization("jenkinsci").getRepositories().keySet()
}

args.each { k ->
    println k;
    def p = new ProcessBuilder(["sh","-c","cd '${repo}' && git fetch --no-tags https://github.com/jenkinsci/${k}.git '+refs/heads/*:refs/heads/${k}/*' '+refs/tags/*:refs/tags/${k}/*'"]).redirectErrorStream(true).start()
    p.in.eachLine { line -> println "  ${line}" }
    if (p.waitFor()!=0)
        System.exit(p.exitValue());
}


