### What is Jenkins or GithubActions ?
it is a software which is used to automate the build process

### what is build process?
code commited to git -> connect git & jenkins  -> run test -> create docker image -> push to dockerhub/ecr -> deploy to ec2 -> send notifications to team

### What does CI/CD means?
CI(continious integrations) like pulling code from git -> running tests -> create docker image -> sending mails to developer if test fail
CD(continious delivery) like pushing docker image to registry -> deploying image to ec2/k8s -> sending email 

### Install Jenkins on machine:
#### Note before installation: 
 - There will be 1 dedicated ec2(for all our projects) on which Jenkins will be installed.Say server name as Jenkins
 - We will not use the master and slave concept of Jenkins as it has drawbacks,the most important drawback is company has to pay for 1 master and say 3 slave means total 4 ec2
 - we will use the concept of docker agent where each project pipeline will be running inside docker container and as soon as pipeline execution is done container will be deleted automatically.
 - If incase loads increases a lot on this particular Jenkins server we can opt for vertical scaling or horizontal scaling , we can think of deploying this on k8s clutser too.

### Install Jenkins.

Pre-Requisites:
 - Java (JDK)

### Run the below commands to install Java and Jenkins

Install Java

```
sudo apt update
sudo apt install openjdk-17-jre
```

Verify Java is Installed

```
java -version
```

Now, you can proceed with installing Jenkins

```
curl -fsSL https://pkg.jenkins.io/debian/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
sudo apt update
sudo apt install jenkins
```

**Note: ** By default, Jenkins will not be accessible to the external world due to the inbound traffic restriction by AWS. Open port 8080 in the inbound traffic rules as show below.

- EC2 > Instances > Click on <Instance-ID>
- In the bottom tabs -> Click on Security
- Security groups
- Add inbound traffic rules as shown in the image (you can just allow TCP 8080 as well, in my case, I allowed `All traffic`).

<img width="1187" alt="Screenshot 2023-02-01 at 12 42 01 PM" src="https://user-images.githubusercontent.com/43399466/215975712-2fc569cb-9d76-49b4-9345-d8b62187aa22.png">


### Login to Jenkins using the below URL:

http://<ec2-instance-public-ip-address>:8080    [You can get the ec2-instance-public-ip-address from your AWS EC2 console page]

Note: If you are not interested in allowing `All Traffic` to your EC2 instance
      1. Delete the inbound traffic rule for your instance
      2. Edit the inbound traffic rule to only allow custom TCP port `8080`
  
After you login to Jenkins, 
      - Run the command to copy the Jenkins Admin Password - `sudo cat /var/lib/jenkins/secrets/initialAdminPassword`
      - Enter the Administrator password
      
<img width="1291" alt="Screenshot 2023-02-01 at 10 56 25 AM" src="https://user-images.githubusercontent.com/43399466/215959008-3ebca431-1f14-4d81-9f12-6bb232bfbee3.png">

### Click on Install suggested plugins

<img width="1291" alt="Screenshot 2023-02-01 at 10 58 40 AM" src="https://user-images.githubusercontent.com/43399466/215959294-047eadef-7e64-4795-bd3b-b1efb0375988.png">

Wait for the Jenkins to Install suggested plugins

<img width="1291" alt="Screenshot 2023-02-01 at 10 59 31 AM" src="https://user-images.githubusercontent.com/43399466/215959398-344b5721-28ec-47a5-8908-b698e435608d.png">

Create First Admin User or Skip the step [If you want to use this Jenkins instance for future use-cases as well, better to create admin user]

<img width="990" alt="Screenshot 2023-02-01 at 11 02 09 AM" src="https://user-images.githubusercontent.com/43399466/215959757-403246c8-e739-4103-9265-6bdab418013e.png">

Jenkins Installation is Successful. You can now starting using the Jenkins 

<img width="990" alt="Screenshot 2023-02-01 at 11 14 13 AM" src="https://user-images.githubusercontent.com/43399466/215961440-3f13f82b-61a2-4117-88bc-0da265a67fa7.png">

## Install the Docker Pipeline plugin in Jenkins:

   - Log in to Jenkins.
   - Go to Manage Jenkins > Manage Plugins.
   - In the Available tab, search for "Docker Pipeline".
   - Select the plugin and click the Install button.
   - Restart Jenkins after the plugin is installed.
   
<img width="1392" alt="Screenshot 2023-02-01 at 12 17 02 PM" src="https://user-images.githubusercontent.com/43399466/215973898-7c366525-15db-4876-bd71-49522ecb267d.png">

Wait for the Jenkins to be restarted.


## Docker Slave Configuration

Run the below command to Install Docker

```
sudo apt update
sudo apt install docker.io
```
 
### Grant Jenkins user and Ubuntu user permission to docker deamon.

```
sudo su - 
usermod -aG docker jenkins
usermod -aG docker ubuntu
systemctl restart docker
```

Once you are done with the above steps, it is better to restart Jenkins.

```
http://<ec2-instance-public-ip>:8080/restart
```

The docker agent configuration is now successful.


### Install Build Tools in Jenkins:
For Java, Maven and Gradle is build tools and For Js, npm is build tool.

Jenkins UI/dashboard
Click on Manage Jenkins -> plugin -> available -> search for plugin which you need to install and after installing click on installed to cross verify whether installed or not

Note:If plugin is your build tool then in that case we need to configure it so that it is available for you in pipelines

install NodeJs plugin first and once installed go to
Tools -> go to NodeJs installation section and feed name and version you want.


## In Jenkins jobs can be created in 3 types :
1.Freestyle
2.Pipeline
3.Multibranch pipeline
For setting CI/CD we will use Pipeline or Multibranch and will not use Freestyle approach.

Pipeline jobs is used when we have to create pipelines for single branch only, we can add more branches but whenever new branch is added in git we have to come to jenkins to add this branch manually.

We try to go with Multibranch pipeline approach rather than Pipeline because in real projects we need followings:
- Pipeline for each branch
- Different behaviour(CI/CD) based on branch (like we want deployment for master and dev branch but for features and hotfix branch we just want to test and skip the rest,if test fail we want to inform developers)
- each time when branch is added we want to dynamically create pipeline 

## How to create Multibranch pipeline
- Enter item name ex: DSOBS-API 
- select pipeline type i.e Multibranch pipeline
- select Branch sources as git not github 
- enter project repo 
- select creds
- in behaviours select filter by name with regular expression option from dropdown
- rest all fields are optional and save it

## Jenkins Dashboard for various projects will look like this
- DSOBS-API
    - main
    - dev
    - features/auth
    - features/events
    - release1.0
    - bugfix/videos
- DSOBS-CMS
- QUORUM-API
- QUORUM-CMS
- IH-API


## Credentials store (place where we save secrets which will be used in pipeline)
# There are 3 types of credentials store:
- Global (Everywhere accessible)
- System (only avaivlable on jenkins server but not for jobs) 
- limited to project which comes with multibranch pipeline only

# steps to create Global Creds
- Manage Jenkins -> click credentials
- select global not system
- select username with password or ssh username with private key as in creds type/kind
- enter username and password
- enter ID (very important as this name will be shown while selecting creds) 
- ex github-creds , aws-ec2-creds

# steps to create creds scoped to specific project only
- this is available for multibranch pipeline only
- DSOBS-API -> credentials 
- select stores scoped to DSOBS-API and click on gloabl
- Add in the same way as explained in Global creds section
- Now all creds will be only specific to this project 
- creds stored here will infact not be visible in other multibranch pipeline


## How to connect Git & Jenkins (Trigger pipeline automatically on code push)
# Add webhook url in Git
- Go to Github -> select project repo -> settings -> webhooks -> in payload add jenkins url -> http://ec2-ip/:8080/github-webhook/


## Shared Library
- used for resusality and make methods more dynamic
- as in code we use to write reusable methods in utils, we can think like this

# Steps to create Shared Library
- create its repository
- write the groovy methods
- make the shared library  available globally or for specif project 
- we can now use the methods defined in shared library in our Jenkinsfile

# structure of Shared Library 
- vars folder (main folder where reusable functions we will write, which we will call from Jenkinsfile of different project)
  - configureGit.groovy
  - runUnitTests.groovy
  - validateCode.groovy
  - buildDockerImage.groovy
  - dockerLogin.groovy
  - awsEcrLogin.groovy
  - pushDockerImage.groovy
  - deployToEc2.groovy
  - notifySlacke.groovy

- src folder (will not use for now ,contain helper code)
- resources folder (third party library or non groovy code)

# Example of various reusable code
configureGit.groovy
def call(String userName, String userEmail) {
    echo "Configuring Git with user: ${userName} and email: ${userEmail}"
    sh "git config --global user.name '${userName}'"
    sh "git config --global user.email '${userEmail}'"
}

runUnitTests.groovy
def call(String testCommand) {
    echo "Running Unit Tests..."
    sh "${testCommand}"
}

buildDockerImage.groovy
def call(String imageName, String imageTag = "latest") {
    echo "Building Docker Image: ${imageName}:${imageTag}"
    sh "docker build -t ${imageName}:${imageTag} ."
}

dockerLogin.groovy
def call(String credentialsId) {
    echo "Logging in to Docker..."
    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
        sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
    }
}

pushDockerImage.groovy
def call(String imageName, String imageTag = "latest") {
    echo "Pushing Docker Image: ${imageName}:${imageTag}"
    sh "docker push ${imageName}:${imageTag}"
}


deployToEc2.groovy
def call(){

}

# Now How to Make This SharedLibrary Globally Available for all Projects
- Manage Jenkins -> system
- on this page scroll down to section Global Trusted Pipeline Libraries
- Enter Name:Jenkins-shared-library
- Default Version: main (means branch name)
- github url where Jenkins-shared-library is hosted

# How to use now in our Project Jenkinsfile
- in our Jenkinsfile we must import the library first
- @Library('Jenkins-shared-library')
- @Library('Jenkins-shared-library)_  //use this when there is no def gv 
- buildDockerImage()   //now we can call resusable method directly
- buildDockerImage 'dsobs/image-2' (if we have to pass arguments)
- Note:method name must be exactly the same as filename given in jenkins-shared-library

### Custom image containing Docker and Node js, this image will be used in docker agent
Dockerfile
```
FROM node:22-alpine
RUN apk add --no-cache docker
```

### commands to build image and push to registry
```
docker build -t node-docker .
docker push your-registry/node-docker:latest
```

Explanation:
The line `RUN apk add --no-cache docker` is part of a Dockerfile and is used to install the Docker CLI (Command Line Interface) in the container. Here's a breakdown of what it does:

- **`apk`**: This is the package manager used in Alpine Linux, which is the lightweight distribution often used in Docker images. It's similar to `apt` in Debian/Ubuntu or `yum` in CentOS/RHEL.

- **`add`**: This command is used by `apk` to install software packages from the available repositories.

- **`--no-cache`**: This flag tells `apk` not to store any cache of the downloaded package indexes. This reduces the size of the Docker image by preventing unnecessary package metadata from being saved. This is often used in Dockerfiles to keep the image size small.

- **`docker`**: This specifies the package to be


