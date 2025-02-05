# RefactoringAwareMergingEvaluation

In RefactoringAwareMergingEvaluation, we perform a quantitative comparison between operation-based refactoring-aware merging (implemented in RefMerge) and 
graph-based refactoring-aware merging (implemented in IntelliMerge). Afterwards, we dive deeper into the results by manually sampling 50 merge scenarios and 
investigate the conflicts that Git, RefMerge, and IntelliMerge report as well as what their causes are. This evaluation was used in the paper, "Refactoring-aware Operation-based Merging: An Empirical Evaluation" (http://arxiv.org/abs/2112.10370).

## RefMerge

We implemented operation-based rerfactoring-aware merging in [RefMerge](https://github.com/ualberta-smr/RefMerge). RefMerge works by undoing refactorings, merging, and then replaying the refactorings. When merging, RefMerge considers the interactions between each pair of refactorings and how these interactions can lead to a conflict or how they can result in a dependence relationship. We provide the conflict and dependence detection logic for each pair in the [conflict detection wiki](https://github.com/ualberta-smr/RefMerge/wiki/Conflict-&-Dependence-Logic). 

## System requirements
* Linux
* git
* Java 11
* IntelliJ (Community Addition) Version 2020.1.2

## How to run

### 1. Clone and build RefactoringMiner 
Use `Git Clone https://github.com/tsantalis/RefactoringMiner.git` to clone RefactoringMiner. 
Then build RefactoringMiner with `./gradlew distzip`. It will be under build/distributions.

### 2. Add RefactoringMiner to your local maven repository
You will need to add RefactoringMiner to your local maven repository to
use it in the build.gradle. You can use `mvn install:install-file -Dfile=<path-to-file>`
to add it to your local maven repository. You can verify that it's been installed 
by checking the path `/home/username/.m2/repository/org/refactoringminer`.

### 3. Populate databases
Use the refactoring analysis dump found [here](https://github.com/ualberta-smr/refactoring-analysis-results)
to populate the original_analysis database. We use the original_analysis database to collect the 10 additional projects that we evaluate on.
Use the `database/intelliMerge_data1`
sql dump to populate intelliMerge_data1 database. This database contains the projects and merge scenarios used in IntelliMerge's evaluation.
Use the database/refactoringAwareMerging_dataset to populate refactoringAwareMerging_dataset database. A description of how to populate the databases can be found 
[here](https://github.com/ualberta-smr/RefactoringAwareMergingEvaluation/wiki/Datasets).

## IntelliMerge Replication

We use the IntelliMerge Replication to reproduce the IntelliMerge results as is with only the 10 projects in their paper.

### Using IntelliMerge

This project comes with the version of IntelliMerge that we used to run the IntelliMerge replication, 
found in our fork of IntelliMerge, https://github.com/max-ellis/IntelliMerge.git, at commit `5966f75`.

### Get IntelliMerge replication commits
Run `python intelliMerge_data_resolver` to get the IntelliMerge commits used in
the IntelliMerge replication. 

### Edit configuration
Edit the configuration tasks to have `:runIde -Pmode=comparison -PdataPath=path 
-PevaluationProject=project`, where path is the path to the cloned test projects
and project is the test project.

## RefactoringAwareMerging Comparison

### Using IntelliMerge and RefMerge

This project comes with the versions of IntelliMerge and RefMerge used in the paper. The source code for 
the version of IntelliMerge we used is the same as that in our IntelliMerge replication.
 If you would like to use a different version of IntelliMerge, build the respective IntelliMerge version 
and copy and paste that version into the lib folder.

RefMerge's history can be found here: https://github.com/ualberta-smr/RefMerge.git. The
version used within this evaluation is release 1.0.0 in commit `adb13ff`.
If you would like to use a different version of
RefMerge, you first need to clone RefMerge. After you clone RefMerge, copy the code in
`ca.ualberta.cs.smr.refmerge` and replace the code in the `ca.ualberta.cs.smr.refmerge` package within this project (found [here](https://github.com/ualberta-smr/RefactoringAwareMergingEvaluation/tree/master/src/main/java/ca/ualberta/cs/smr/refmerge)).

### Edit configuration
Edit the configuration tasks in the IntelliJ IDE under `Run | Edit Configurations` (more information can be found [here](https://www.jetbrains.com/help/idea/run-debug-configuration.html#create-permanent)) to have `:runIde` and include set `-Pmode=` to `comparison`.
Then, set `-PevaluationProject=` to the project that you want to evaluate on. For example,
it would look like `-PevaluationProject=error-prone` if you want to evaluate on error-prone.

### RQ1 data

The projects we evaluated on can be found in the file `experiment_data/refMerge_evaluation_projects`. A list of merge scenarios and their corresponding projects that we evaluated on are located in `experiment_data/refMerge_evaluation_commits`.

### Running RQ1 experiment

To replicate RQ1, first run `python project_sampler` to get the additional 10 projects used 
in the evaluation. Then, run `python refMerge_data_resolver` to get the commits with
refactoring-related conflicts.

Put the `stats/refMerge_evaluation_commits` file generated by `refMerge_data_resolver` in the resources folder. While running the evaluation, you have to manually load each project but after that the evaluation is automated. For each project, you will need to do the following:

1. Add the corresponding evaluation project to the configuration in the IntelliJ IDE under `Run | Edit Configurations`. For example, `-PevaluationProject=error-prone`. 

2. Clone the corresponding evaluation project.

3. Open the evaluation project with the IntelliJ IDEA in a new window. 

4. Wait for IntelliJ to build the cloned project, then close it.

5. Run the evaluation for the project by clicking the `Run` button in the IntelliJ IDE.

6. Wait for the evaluation pipeline to finish processing that project.

The data from the evaluation pipeline will be stored in the database, `refMerge_evaluation`. The evaluation pipeline will create the database if it does not already exist. Finally, use the scripts in evaluation_data_plotter to get tables and plots from the data.


### RQ2 data

A list of merge scenarios and their corresponding projects that we used in RQ2 is stored in `experiment_data/rq2_scenarios`.

### Running RQ2 experiment

First, use  the file `stats/evaluation_data_resolver` by running the command `python evaluation_data_resolver` to get the list of merge commit ids to sample. This will always result in the same merge scenarios when given the same data.
This experiment is a manual analysis and we manually re-run the merge for each merge scenario. These are the steps that we follow for each merge scenario:

1. Query the results in RQ1 using `SELECT * FROM merge_commit WHERE id = x` where x is the merge commit ID. Use this query to get the parent commits.

2. Use `git merge-base p1 p2` in the corresponding project to get the base commit where p1 is the left parent commit and p2 is the right parent commit.

3. Use `SELECT distinct path FROM conflict WHERE merge_commit_id = x;` to get a list of the conflicting file paths.

4. Compare each conflicting region with the corresponding region in the base, left, and right commits.

5. Record if the region should be a merge conflict or not based on the base, left, and right commits. 

6. Record if the conflict reported by the merge tool is a true positive or false positive based on the previous step. Record why you think so.

7. Investigate the same region in the other merge tools and record what you find. 

8. Record any additional notes about the discrepancies you find, such as the reasons for the discrepancies. 


### RQ1 results

The zip file, database/refactoringAwareMerging_results.zip, contains the results from
 running the evaluation pipeline for RQ1. A description of how to use these results can be 
 found [here](https://github.com/ualberta-smr/RefactoringAwareMergingEvaluation/wiki/Datasets).
 
### RQ2 results

The overall results for our manual analysis are stored in `results/manual_sampling_results.csv`. This includes the results for each merge scenario as well as reasons that we think each conflict falls into its corresponding category. The results used to produce the Git table in RQ2 are stored in `results/git_table.csv`. The results used for the RefMerge and IntelliMerge tables are respectively stored in `results/refMerge_table.csv` and `results/intelliMerge.csv`.



