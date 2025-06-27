multibranchPipelineJob('sandbox/Robert-Test-GitHub-App') {
  description('Automatically built for PRs and the main/master branch')

  branchSources {
    git {
      remote('https://github.com/Robert-Boylan-Visma/Test.git')

      // Define behaviors for discovering and filtering branches
      traits {
        // Discover branches: By default, it discovers all branches, but filters apply on top.
        // This trait is usually implicitly added if not specified, but good to be explicit.
        // If you use 'discoverBranches()', it implies 'discoverGitBranches()'.
        // For GitHub, you might also use 'githubBranchDiscovery()'.
        // The exact trait might depend on the specific SCM plugin version.
        // githubBranchDiscovery() // Use this if you want GitHub specific branch discovery

        // Discover pull requests from origin (head only)
        // This corresponds to "Filter by pull request (head only)"
        // The strategyId determines how the PR is merged/built.
        // 1: Merge the pull request with the current target branch revision (most common for CI)
        // 2: The current pull request revision (the raw PR branch)
        // 3: Both
        originPullRequestDiscoveryTrait {
          strategyId(1) // Merge the PR with the target branch
        }

        // Filter by name using a regular expression for master/main branch
        // This corresponds to "Filter by name (with regular expression)"
        // The 'filterByRegex' trait is what you need for this.
        // Note: The specific trait name might vary slightly if using a different SCM plugin
        // or older Job DSL versions.
        // You might need to look at the Job DSL API Viewer for your Jenkins instance
        // (e.g., http://your-jenkins-url/plugin/job-dsl/api-viewer/index.html)
        // to find the exact trait name if this doesn't work.

        // Assuming 'jenkins.scm.api.trait.SCMHeadFilter' traits
        // For GitHub/Git SCM:
        // SCMTrait that filters discovered branches by name using a regular expression.
        // In recent versions of the Git plugin and Job DSL, this is usually:
        scmTrait {
          'jenkins.scm.api.trait.SCMHeadFilter' {
            // This corresponds to "Filter by name (with regular expression)"
            // The pattern to match branches against
            nameFilter('^(master|main)$')
            // You might also need to specify the regexTrait for the GitSCMSource
            // For a standard Git multibranch source, it's usually part of the behaviours
            // provided by the `scm.traits.PatternMatchingTrait` or similar.

            // If the above doesn't work directly, you might need to use a more generic approach:
            // This snippet requires `scm-filter-branch-pr` plugin or similar.
            // Refer to your Job DSL API Viewer.
            // Example if using scm-filter-branch-pr plugin's regex filter:
            // filterByRegexTrait {
            //     branchRegex('^(master|main)$')
            // }
          }
        }
      }
    }
  }

  orphanedItemStrategy {
    discardOldItems {
      numToKeep(20)
      daysToKeep(30)
    }
  }
}
