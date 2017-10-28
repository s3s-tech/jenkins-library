def call(body) {

        def config = [:]
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = config
        body()

node("${config.slave}") {
//{
   //git 'https://github.com/spring-projects/spring-petclinic.git'
   //git '${config.git_url}'
   checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/spring-projects/spring-petclinic.git']]])

   
   def server = Artifactory.server("${config.artifactoryServer}")
   def rtMaven = Artifactory.newMavenBuild()
   rtMaven.resolver server: server, releaseRepo: 'libs-release', snapshotRepo: 'libs-snapshot'
   rtMaven.deployer server: server, releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local'
   
   rtMaven.deployer.artifactDeploymentPatterns.addInclude("*.jar").addExclude("*.zip")
rtMaven.tool = "${config.mavenName}"
// Run Maven:
def buildInfo = rtMaven.run pom: 'pom.xml', goals: 'clean install sonar:sonar'
server.publishBuildInfo buildInfo


}

}
