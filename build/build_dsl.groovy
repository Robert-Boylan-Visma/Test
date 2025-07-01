def modules = [
    [name: 'Test', owner: 'Robert-Boylan-Visma'],
    [name: 'test', owner: 'robertboylan94', repo_id: '747'],
]

folder('test-build-pipelines') {
  description('Folder containing test multi-branch build pipelines')
}

modules.each { module ->
  if(module.repo_id) {
    gitName = "Gitlab"
  } else {
    gitName = "GitHub"
  }
  multibranchPipelineJob("test-build-pipelines/${module.name}-${gitName}") {
  if(module.repo_id) {
    configure { project ->
      def sources = project / sources / data
      sources << 'jenkins.branch.BranchSource' {
        source(class: 'io.jenkins.plugins.gitlabbranchsource.GitLabSCMSource') {
          id("${module.repo_id}")
          projectOwner("${module.owner}")
          projectPath("${module.owner}/${module.name}")
          traits {
            'io.jenkins.plugins.gitlabbranchsource.BranchDiscoveryTrait' {
              strategyId(3)
            }
            'jenkins.scm.impl.trait.WildcardSCMHeadFilterTrait' {
              includes('master master-2.8.x master-2.6.x master-2.5.x release* vsw-* vigo-* VSW-* VIGO-* insv-* INSV-* test_jenkinsfile update_jenkinsfile2 revert-* cherry-pick-* poc*')
              excludes('*_increment')
            }
            'jenkins.plugins.git.traits.CloneOptionTrait' {
              extension(class: 'hudson.plugins.git.extensions.impl.CloneOption') {
                shallow(true)
                noTags(true)
                reference('')
                depth(10)
                honorRefspec(false)
              }
            }
          }
        }
      }
    }
  }
  else {
      configure { project ->
        def sources = project / sources / data
        sources << 'jenkins.branch.BranchSource' {
          source(class: 'org.jenkinsci.plugins.github_branch_source.GitHubSCMSource') {
            id("${module.name}-GitHub")
            credentialsId('github-app-credentials')
            repoOwner("${module.owner}")
            repository("${module.name}")
            traits {
              'org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait' {
                strategyId(3)
              }
              'org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait' {
                strategyId(2)
              }
              'org.jenkinsci.plugins.github__branch__source.IgnoreDraftPullRequestFilterTrait' {}
              'jenkins.scm.impl.trait.RegexSCMHeadFilterTrait' {
                regex('^(master|PR-\\d+)$')
              }
              'jenkins.plugins.git.traits.CloneOptionTrait' {
                extension(class: 'hudson.plugins.git.extensions.impl.CloneOption') {
                  shallow(true)
                  noTags(true)
                  reference('')
                  depth(10)
                  honorRefspec(false)
                }
              }
            }
          }
        }
      }
    }
    orphanedItemStrategy {
      discardOldItems {
        daysToKeep(7)
        numToKeep(20)
      }
    }

    factory {
      workflowBranchProjectFactory {
        scriptPath('build/Jenkinsfile')
      }
    }
  }
}
