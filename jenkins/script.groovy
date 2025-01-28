def testAPI(){
    echo "Testing API...."
}

// sh is used to run any commands in the shell
// commands must be in double quotes after sh
def configureGit(){
    echo "Configuring Git...."
    sh 'git config --global user.name "Jenkins"'
    sh 'git config --global user.email "jenkins@example.com"'
}
/*
  env is used to set environment variables
  env variable name must be in uppercase
  env variables are available in all stages
  env variables are accessible using ${variable_name} in double quotes
  few predefined env variables examples are BUILD_NUMBER, JOB_NAME, GIT_COMMIT


*/
def buildTag(){
    echo "Building Image Tag...."
    env.BUILD_TAG = "${JOB_NAME}-${BUILD_NUMBER}-${GIT_COMMIT}"
    echo "build tag is ${BUILD_TAG}"
}

/*
 withCredentials is a plugin which is pre intalled 
 used to access the credentials stored in jenkins


*/

def pushImage(){
    echo "Pushing Image...."
    withCredentials([usernamePassword(credentialsId: 'docker-hub-cred', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]){
       sh "docker build -t ${USERNAME}/automation:${BUILD_TAG} -t ${USERNAME}/automation:latest ."
        sh "echo $PASSWORD | docker login -u $USERNAME --password-stdin"
        sh "docker push ${USERNAME}/automation:${BUILD_TAG}"
        sh "docker push ${USERNAME}/automation:latest"

    }
}

/*
  sshagent is a plugin which we need to install ssh agent plugin
  used to login to the remote server using ssh key
  go to jenkins dashboard -> manage jenkins -> manage plugins -> available -> search for ssh agent plugin -> install
  go to credentials -> add credentials -> select ssh username with private key -> add the ssh key -> give the id as "aws-ec2-cred"
  paste the pem file content in the private key section

  In SSH commands, the second argument typically specifies the command to execute on the remote server after establishing the SSH connection. 


*/

def deployAPI(){
    echo "Deploying API....";
    sshagent(['aws-ec2-cred']){
        echo "logged in to aws ec2...."
      def serverCmds = "bash ./server-cmds-api.sh ${BUILD_TAG}"
      def server1 = "ubuntu@10.0.140.77"
      def server2 = "ubuntu@10.0.142.212"

      sh "scp -o StrictHostKeyChecking=no server-cmds-api.sh ${server1}:~"
      sh "scp -o StrictHostKeyChecking=no server-cmds-api.sh ${server2}:~"
      sh "scp -o StrictHostKeyChecking=no docker-compose.yml ${server1}:~"
      sh "scp -o StrictHostKeyChecking=no docker-compose.yml ${server2}:~"
      sh "ssh -o StrictHostKeyChecking=no ${server1} ${serverCmds}"
      sh "ssh -o StrictHostKeyChecking=no ${server2} ${serverCmds}"

    }
}

return this
