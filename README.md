This script generates a single Git repository that contains all Git repositories in GitHub. This huge union repository is convenient for datamining and other analytics.

`git clone http://git.jenkins-ci.org/all.git` to clone this repository. For each branch `foo` in the repository `http://github.com/jenkinsci/bar`, the union repository contains `bar/foo`. The same naming convention applies for tags.
