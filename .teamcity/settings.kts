import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.projectFeatures.versionedSettings

version = "2024.12"

project {
  id("NetologyExample")
  name = "netology example"
  description = "For homework 09-ci-05-teamcity"
  
  vcsRoot(HttpsGithubComAcider19exampleTeamcityRefsHeadsMaster)
  
  buildType(Build)
  
  features {
    versionedSettings {
      id = "PROJECT_EXT_2"
      mode = VersionedSettingsMode.USE_CURRENT_AND_STORE_TO_VCS
      format = VersionedSettingsFormat.KOTLIN
      rootId = "NetologyExample_HttpsGithubComAcider19exampleTeamcityRefsHeadsMaster"
      showChanges = false
      useRelativeIds = true
    }
  }
}

object HttpsGithubComAcider19exampleTeamcityRefsHeadsMaster : GitVcsRoot({
  name = "https://github.com/acider19/example-teamcity#refs/heads/master"
  url = "git@github.com:acider19/example-teamcity.git"
  branch = "refs/heads/master"
  branchSpec = "refs/heads/*"
  authMethod = privateKeyFile {
    privateKeyPath = "/home/buildagent/.ssh/tc_deploy_key"
  }
  ignoreKnownHosts = true
  agentCleanPolicy = AgentCleanPolicy.ON_BRANCH_CHANGE
  agentCleanFilesPolicy = AgentCleanFilesPolicy.ALL_UNTRACKED
})

object Build : BuildType({
  id("NetologyExample_Build_2")
  name = "Build"
  
  vcs {
    root(HttpsGithubComAcider19exampleTeamcityRefsHeadsMaster)
  }
  
  steps {
    maven {
      id = "Maven2"
      name = "Maven Tests"
      goals = "clean test"
      pomLocation = "pom.xml"
      localRepoScope = agent()
      jvmArgs = "-Dmaven.test.failure.ignore=true"
    }
    maven {
      id = "Maven_Deploy"
      name = "Maven Deploy in Nexus"
      goals = "clean deploy"
      pomLocation = "pom.xml"
      localRepoScope = agent()
      jvmArgs = "-Dmaven.test.failure.ignore=true"
      userSettingsSelection = "settings.xml"
    }
  }
  
  triggers {
    vcs {
      branchFilter = "+:*"
      enableQueueOptimization = true
      quietPeriodMode = QuietPeriodMode.DO_NOT_USE
    }
  }
  
  features {
    perfmon {
    }
  }
})
