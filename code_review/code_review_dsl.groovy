def modules = [
    [name: 'Test-GitHub', repo: 'git@github.com:Robert-Boylan-Visma/Test.git'],
    [name: 'Test-Gitlab', repo: 'git@gitlab.com:robertboylan94/test.git']]

folder('test-code-review-pipeline') {
  description('Folder containing all test code review pipelines')
}

modules.each { module ->
  pipelineJob("test-code-review-pipeline/${module.name}-code-review-pipeline") {

    logRotator {
      numToKeep(30)
    }

    if(module.repo.contains('github')) {
      properties {
        githubProjectProperty {
          projectUrlStr('https://github.com/Robert-Boylan-Visma/Test')
        }
      }
    }

    if(module.repo.contains('gitlab')) {
      triggers {
        gitlabPush {
          buildOnPushEvents(false)
          buildOnMergeRequestEvents(true)
          rebuildOpenMergeRequest('source')
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
              if(module.repo.contains('github')) {
                credentials('github-app-credentials')
              }
            }
            branches('*/master')
            extensions {
              localBranch()
            }
          }
        }
      }
    }
  }
}
