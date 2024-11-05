# Kubernetes (K8s) Setup

## Local Machine vs Cloud Setup

| Local Machine       | Cloud    |
|---------------------|----------|
| Minikube            | AWS EKS  |
| Kind (K8s in Docker)|          |

## Installation Steps

**Step 1**: Install Docker based on your OS.

**Step 2**: Install `kubectl` based on your OS (it's the `kubectl` which will help us interact with the Control Plane; hence, it is essential to have).

**Step 3**: Install Minikube based on your OS.

**Step 4**: Verify Minikube installation by running the following command:
`minikube version`

**Step 5**: Create the cluster by running the command:
`minikube start --nodes 2 -p dsobs-local-cluster --driver=docker`

**Step 6**: Check the status of the cluster you created by running the command:
`minikube status -p dsobs-local-cluster`

## Useful `kubectl` Commands

- **List all clusters:**
  `kubectl config get-contexts`

- **Switch between clusters:**
  `kubectl config set-context dsobs-local-cluster`

- **Add a node to a particular cluster:**
  `minikube node add --worker -p dsobs-local-cluster`

- **Delete a node from a particular cluster:**
  `minikube node delete node_name -p dsobs-local-cluster`

- **Get all nodes:**
  `kubectl get nodes`

- **Access the Minikube dashboard:**
  `minikube dashboard --url -p dsobs-local-cluster`

---

