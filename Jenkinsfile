pipeline {
    agent any
    stages {

        stage('Commit Stage') {
            parallel {
                stage('build') {
                    steps {
                        sh 'mvn -s /var/jenkins_home/mvn/settings.xml -gs /var/jenkins_home/mvn/settings.xml clean package'
                        junit(allowEmptyResults: true, testResults: '**/surefire-reports/*.xml')
                        archiveArtifacts(artifacts: '**/*.jar', onlyIfSuccessful: true)
                    }
                }
                stage('Sonar') {
                    steps {
                        sh 'mvn -s /var/jenkins_home/mvn/settings.xml -gs /var/jenkins_home/mvn/settings.xml sonar:sonar -Dsonar.host.url=http://192.168.1.77:29000'
                    }
                }
            }
        }

        stage('Snapshot') {

            steps {
                sh 'mvn -s /var/jenkins_home/mvn/settings.xml -gs /var/jenkins_home/mvn/settings.xml -Dmaven.test.skip=true deploy '
            }
        }

        stage('Release') {
            when {
                branch 'master'
            }
            steps {
                releaseCurrentVersion()
            }

        }
    }

    tools {
        maven 'apache-maven-3.5.2'
        jdk 'jdk8'
    }
    options {
        timeout(time: 1, unit: 'HOURS')
    }

    }


def releaseCurrentVersion(){

    def currentVersion = getCurrentVersion().minus('-SNAPSHOT')

    input "Are you sure to Release ? ${currentVersion}"

    prepareAndDeploy(currentVersion)

}

def prepareAndDeploy(newVersion) {
    echo  "######### deploy To nexus"

    sh "mvn -s /var/jenkins_home/mvn/settings.xml -gs /var/jenkins_home/mvn/settings.xml -Dmaven.test.skip=true versions:set -DnewVersion=\"${newVersion}.RELEASE\" versions:commit"

    sh 'mvn -s /var/jenkins_home/mvn/settings.xml -gs /var/jenkins_home/mvn/settings.xml -Dmaven.test.skip=true clean deploy '

    echo  "########## branching Tag"

    sh "git config user.email \"s.raja@wafaassurance.co.ma\""
    String credentialsId="jenkins-generated-ssh-key"
    sh "git config  user.name \"s.raja@192.168.1.77:28081\""

    sh "git commit --allow-empty -am \"Pass to new Version : ${newVersion}\""

    sh "git tag -a \"${newVersion}\" -m \"Pass to new Tag : ${newVersion}\""


    pushCode("git push --tags")

    nextDevSnapshot()

}
def pushCode(cmd){
    String credentialsId="jenkins-generated-ssh-key"
    sshagent([credentialsId]) {
        sh(script: "${cmd}")
    }
}
def getCurrentVersion(){
    def pom = readMavenPom file: 'pom.xml'
    def currentVersion = pom.version

    return currentVersion
}

def nextDevSnapshot(){
    echo "######### prepare next version SNAPSHOT for developpement branch"

    def n0 = "\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion}"
    def newDevVersionID = input id: 'newDevVersionID', message: 'Specify version or let empty to be autoincrement', parameters: [string(defaultValue: '', description: 'write version like M.m.p ex: 1.0.0', name: 'newDevVersion')]

    if (newDevVersionID?.trim()) {
        n0=newDevVersionID
    }


    sh "git checkout master"

    sh "mvn build-helper:parse-version -s /var/jenkins_home/mvn/settings.xml -gs /var/jenkins_home/mvn/settings.xml   versions:set -DnewVersion=${n0}-SNAPSHOT versions:commit "
    sh "git commit --allow-empty -am \"new Snaphot\""

    pushCode('git push')
}