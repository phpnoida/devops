# Kubernetes (K8s) Setup

## Local Machine vs Cloud Setup

| Local Machine        | Cloud   |
| -------------------- | ------- |
| Minikube             | AWS EKS |
| Kind (K8s in Docker) |         |

## Installation Steps

**Step 1**: Install Docker based on your OS.

**Step 2**: Install `kubectl` based on your OS (it's the `kubectl` which will help us interact with the Control Plane; hence, it is essential to have).

**Step 3**: Install Minikube based on your OS.

**Step 4**: Verify Minikube installation by running the following command:
- `minikube version`

## Useful `minikube` Commands

- `minikube start`
- `minikube stop`
- `minikube status`
- `minikube ip` (or EC2 IP on cloud)

### To Access the Dashboard in Detached Mode
```bash
minikube dashboard --url &
