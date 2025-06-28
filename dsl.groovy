multibranchPipelineJob('Webhook-Test') {
  description('Automatically built for PRs and the main/master branch')

  branchSources {
    github {
      id('Test')
      repoOwner('Robert-Boylan-Visma')
      repository('Test')

      configure { node ->
        def traits = node / source / traits
        traits.appendNode('org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait') {
          strategyId(3) // 3 = Discover PRs and master
        }

        traits.appendNode('jenkins.scm.impl.trait.RegexSCMHeadFilterTrait') {
          regex('master') // Keep only master (besides PRs)
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
      scriptPath('Jenkinsfile')
    }
  }
}
