pipeline {
  agent {
    ecs {
      inheritFrom 'python3-builder-agent'
    }
  }
  options {
    timeout(time: 45, unit: "MINUTES")
    disableConcurrentBuilds()
  }
  stages {
    stage('Hello World') {
      steps {
        script {
          echo 'Hello World!'
        }
      }
    }
  }
}
