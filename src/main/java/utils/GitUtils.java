package utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.log.Hash;
import git4idea.GitCommit;
import git4idea.GitRevisionNumber;
import git4idea.commands.GitCommand;
import git4idea.commands.GitLineHandler;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GitUtils {

    private Project project;
    private GitRepository repo;
    private Git git;

    public GitUtils(GitRepository repository, Project proj) throws IOException {
        repo = repository;
        project = proj;
    }

    public GitUtils(File repoDir) throws IOException, GitAPIException, VcsException {
        git = Git.open(repoDir);
        gitReset();
    }

    public GitUtils(Git git)  {
        this.git = git;
    }

    public void gitReset() throws VcsException {
        GitLineHandler resetHandler = new GitLineHandler(project, repo.getRoot(), GitCommand.RESET);
        resetHandler.setSilent(true);
        resetHandler.addParameters("--hard");
        String result = git4idea.commands.Git.getInstance().runCommand(resetHandler).getOutputOrThrow();
        if(result.contains(".git/index.lock")) {
            Utils.runSystemCommand(project.getBasePath(),
                    "rm", ".git/index.lock");
            git4idea.commands.Git.getInstance().runCommand(resetHandler);
        }
    }

    public void checkout(String commit) throws VcsException {
        gitReset();
        GitLineHandler lineHandler = new GitLineHandler(project, repo.getRoot(), GitCommand.CHECKOUT);
        lineHandler.setSilent(true);
        lineHandler.addParameters(commit);
        git4idea.commands.Git.getInstance().runCommand(lineHandler);
    }

    public String getBaseCommit(String left, String right) throws VcsException {
        VirtualFile root = repo.getRoot();
        GitRevisionNumber num = GitHistoryUtils.getMergeBase(project, root, left, right);
        String base = num.getRev();
        return base;
    }

    public List<GitCommit> getMergeCommits() throws VcsException {
        // get list of commits
        VirtualFile root = repo.getRoot();
        List<GitCommit> commits = GitHistoryUtils.history(project, root);
        List<GitCommit> mergeCommits = new ArrayList<>();
        System.out.println(commits.size());
        for(GitCommit commit : commits) {
            // check if each commit is a merge commit
            if(commit.getParents().size() == 2) {
                // if yes, add to a list of merge commits
                mergeCommits.add(commit);
            }
        }
        return mergeCommits;
    }

}
