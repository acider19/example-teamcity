package _Self

import jetbrains.buildServer.configs.kotlin.v2024_12.*
import jetbrains.buildServer.configs.kotlin.v2024_12.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2024_12.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2024_12.features.perfmon

version = "2024.12"

project {
  description = "For homework 09-ci-05-teamcity"

  vcsRoot(ExampleVcsRoot)

  buildType(ExampleBuildConfig)
  buildType(ExampleBuildFromMainBranch)
  buildType(ExampleBuildFromOtherBranches)
}

object ExampleVcsRoot : GitVcsRoot({
  name = "https://github.com/acider19/example-teamcity#refs/heads/master"
  url = "git@github.com:acider19/example-teamcity.git"
  branch = "refs/heads/master"
  branchSpec = "refs/heads/*"
  authMethod = privateKey {
    privateKeyFile = "/shared/tc_deploy_key"
  }
  ignoreKnownHosts = true
  agentCleanPolicy = AgentCleanPolicy.ON_BRANCH_CHANGE
  agentCleanFilesPolicy = AgentCleanFilesPolicy.ALL_UNTRACKED
  submoduleCheckout = SubmoduleCheckout.CHECKOUT
})

object ExampleBuildConfig : BuildType({
  name = "Build"

  vcs {
    root(ExampleVcsRoot)
  }

  steps {
    maven {
      goals = "clean test"
      runnerArgs = "-Dmaven.test.failure.ignore=true"
      localRepoScope = MavenBuildStep.LocalRepoScope.AGENT
      conditions {
        doesNotContain("teamcity.build.branch", "main")
      }
    }
    maven {
      name = "Maven Deploy in Nexus"
      goals = "clean deploy"
      runnerArgs = "-Dmaven.test.failure.ignore=true"
      userSettingsSelection = "settings.xml"
      localRepoScope = MavenBuildStep.LocalRepoScope.AGENT
      conditions {
        contains("teamcity.build.branch", "master")
      }
    }
  }

  triggers {
    vcs {
      branchFilter = "+:*"
      quietPeriodMode = QuietPeriodMode.DO_NOT_USE
    }
  }

  features {
    perfmon {}
  }
})

object ExampleBuildFromMainBranch : BuildType({
  name = "Build from Main Branch"
  paused = true

  vcs {
    root(ExampleVcsRoot)
  }

  steps {
    maven {
      goals = "clean deploy"
      runnerArgs = "-Dmaven.test.failure.ignore=true"
      localRepoScope = MavenBuildStep.LocalRepoScope.AGENT
    }
  }

  triggers {
    vcs {
      branchFilter = "+:refs/heads/master"
      quietPeriodMode = QuietPeriodMode.DO_NOT_USE
    }
  }

  features {
    perfmon {}
  }
})

object ExampleBuildFromOtherBranches : BuildType({
  name = "Build from Other Branches"
  paused = true

  vcs {
    root(ExampleVcsRoot)
  }

  steps {
    maven {
      goals = "clean test"
      runnerArgs = "-Dmaven.test.failure.ignore=true"
      localRepoScope = MavenBuildStep.LocalRepoScope.AGENT
    }
  }

  triggers {
    vcs {
      branchFilter = "+:refs/heads/*\n-:refs/heads/master"
      quietPeriodMode = QuietPeriodMode.DO_NOT_USE
    }
  }

  features {
    perfmon {}
  }
})
