def modules = [
    [name: 'Test-GitHub', repo: 'https://github.com/Robert-Boylan-Visma/Test.git'],
    [name: 'Test-Gitlab', repo: 'https://gitlab.com/robertboylan94/test.git']
]

folder('test-code-review-pipeline') {
  description('Folder containing all test code review pipelines')
}

modules.each { module ->
  def jobName = "test-code-review-pipeline/${module.name}-code-review-pipeline"
  def isGitHub = module.repo.contains('github')
  def isGitLab = module.repo.contains('gitlab')

  pipelineJob(jobName) {
    logRotator {
      numToKeep(30)
    }

    if (isGitHub) {
      properties {
        githubProjectProperty {
          projectUrlStr("${module.repo}")
        }
      }
    }

    definition {
      cpsScm {
        scriptPath('code_review/Jenkinsfile')
        scm {
          git {
            remote {
              url("${module.repo}")
              if (isGitHub) {
                credentials('github-app-credentials')
              }
            }
            branches('*/master')
            extensions {
              localBranch()
            }
          }
        }
        lightweight(false)
      }
    }

    triggers {
      if (isGitLab) {
        configure { triggerNode ->
          triggerNode << 'com.dabsquared.gitlabjenkins.GitLabPushTrigger' {
            spec('')
            triggerOnPush(false)
            triggerOnMergeRequest(true)
            branchFilterType('All')
            rebuildOpenMergeRequest('source')
            triggerOpenMergeRequestOnPush('never')
            ciSkip(false)
            skipWorkInProgressMergeRequest(false)
            setBuildDescription(false)
            addNoteOnMergeRequest(false)
            addCiMessage(false)
            addVote(false)
            acceptMergeRequestOnSuccess(false)
          }
        }
      }

      if (isGitHub) {
        configure { triggerNode ->
          triggerNode << 'org.jenkinsci.plugins.github.pullrequest.GitHubPRTrigger' {
            spec('')
            triggerMode('HEAVY_HOOKS')
            cancelQueued(true)
            abortRunning(false)
            skipFirstRun(false)
            events {
              'org.jenkinsci.plugins.github.pullrequest.events.impl.GitHubPROpenEvent'()
              'org.jenkinsci.plugins.github.pullrequest.events.impl.GitHubPRCommitEvent'()
            }
            preStatus(true)
          }
        }
      }
    }
  }
}
