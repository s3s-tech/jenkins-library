def call(body) {
    def config = [:]
    //def slave = ${config.slave}
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    def $slave = ${config.slave}
    body()

node(${slave}) {
   //git 'https://github.com/spring-projects/spring-petclinic.git'
   git '${config.git_url}'
   
   def server = Artifactory.server('${config.artifactory_server}')
   def rtMaven = Artifactory.newMavenBuild()
   rtMaven.resolver server: server, releaseRepo: 'libs-release', snapshotRepo: 'libs-snapshot'
   rtMaven.deployer server: server, releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local'
   
   rtMaven.deployer.artifactDeploymentPatterns.addInclude("*.jar").addExclude("*.zip")
rtMaven.tool = "${config.maven_name}"
// Run Maven:
def buildInfo = rtMaven.run pom: 'pom.xml', goals: 'clean install sonar:sonar'
server.publishBuildInfo buildInfo


}
    
}