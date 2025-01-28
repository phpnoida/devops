# Docker Notes

## Virtual Machine:
**Problem Solved By VM**: Resource under-utilization of host machine & Costs

1. VM has solved the issues of utilizing resources of the host machine to its maximum. How? By installing multiple VMs on the same machine.
2. **Best example**: EC2 instances are VMs running on AWS physical machines.
3. There can be a maximum number of VMs installed on the same machine, reducing costs for companies and maximizing resource utilization of the host machine.
4. Each VM has its own OS.
5. **Hypervisor** is the technology that helps in spinning up VMs.
6. On a personal computer, we can use a Hypervisor like Oracle VirtualBox.
7. On EC2, AWS uses the Hypervisor Xen.

---

## Containers:
**Problem Solved By Containers**: "It works on my machine" & better resource utilization of VMs

1. We can run multiple applications/containers on the same VM. For example:
   - Node.js in one container
   - React.js in another container
   - Redis in its own container
   - MongoDB in its own container
   - RabbitMQ in its own container
2. A container is a portable artifact or bundle of application code + application libraries + few system dependencies.
3. Containers are lightweight as they do not include a full OS. Instead, they have a base OS and use resources from the host machine (VM or personal computer).
4. To containerize any application, we need a container runtime such as **Docker**, **Buildah**, or **Podman**.

---

## Container vs Image:
- **Image**: A program.
- **Container**: A running instance of the program.

---

## Few Terms to Know:
- **Docker Daemon**: The heart of Docker. It interprets all Docker commands and performs the required actions.
- **Registry**: A storage for images, which can be public or private. Examples include:
  - DockerHub
  - AWS ECR
  - CRIO.D
  
  We can think of registries as being similar to Git repositories.

---

## Install Docker:
### Step 1: Set up Docker's apt repository.
1. Add Docker's official GPG key:
   ```bash
   sudo apt-get update
   sudo apt-get install ca-certificates curl
   sudo install -m 0755 -d /etc/apt/keyrings
   sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
   sudo chmod a+r /etc/apt/keyrings/docker.asc
   ```
2. Add the repository to Apt sources:
   ```bash
   echo \  
     "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \  
     $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \  
     sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
   sudo apt-get update
   ```

### Step 2: Install the Docker packages.
```bash
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

### Step 3: Add Docker Repository
```bash
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

### Step 4: Verify
```bash
sudo docker --version
```

### Step 5: (Optional) Add Current User to Docker Group to Run Docker Without sudo
```bash
sudo usermod -aG docker $USER
```
> **Note**: Exit the terminal and log back in to see the effects.

### Step 6: Test Docker Installation
```bash
docker run hello-world
```

---

## Important Commands:
```bash
docker pull redis
docker ps
docker ps -a
docker start <containerId>
docker stop <containerId>
docker run <imageName>
docker images
docker rmi <imageId>
docker rm <containerId>
docker logs <containerId>
docker exec -it <containerId/containerName> /bin/sh
docker inspect <containerId/containerName>
docker volume ls
docker volume inspect jenkins_data

```

---
## Why Dockerfile is created?
To create custom image or to make our application containerized we have to create Dockerfile


## Docker Compose:
Docker Compose is a tool used to manage multiple containers.

```bash
docker compose up
docker compose down
docker compose -f docker-compose-dev.yml up
docker compose -f docker-compose-dev.yml down
```

---

## Docker Init:
docker init 
- A utility that generates:
  - `Dockerfile`
  - `docker-compose.yml`
  - `.dockerignore` file


## Docker Network:
3 Types:
Bridge
Host
Overlay

By default Bridge type is created which allows container to talk to host.