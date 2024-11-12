# Kubernetes Probes Guide

## What is a Probe in Kubernetes?

In Kubernetes, probing is a method to monitor the health and status of containers to ensure they are functioning correctly. Although a pod may be in a `Running` state, issues can still arise within the container where our application is running. Kubernetes uses probes to detect such issues.

### Common Reasons for Pod Failures
- Bugs in the application code running inside the container
- Out of memory issues
- Database connection failures
- Timeouts while communicating with external services

## Types of Probing Mechanisms

1. **Exec Probe**
   - Executes a command inside the container.
   - If the command exits with code `0`, it indicates success; `1` indicates failure.

2. **HTTP Probe**
   - Sends an HTTP request to a specified endpoint (e.g., `/health`).
   - Response codes between `200-399` indicate success; other codes indicate failure.

3. **TCP Probe**
   - Opens a specified port to check if it is accepting connections.
   - Success is indicated if the port is open and accepting requests.

> For API deployments, HTTP probes are commonly used, whereas TCP probes are typically used for databases.

## Types of Probes in Kubernetes

Kubernetes provides three types of probes:

1. **Liveness Probe**
   - Checks if the container is running. If it fails, the container is restarted.
   - This process continues until the issue is resolved.

2. **Readiness Probe**
   - Checks if the container is ready to serve traffic. If it fails, the pod's IP is removed from the service, so no requests are routed to it.

3. **Startup Probe**
   - Runs before liveness and readiness probes.
   - If it fails, the pod is marked as `Stopped`, and liveness and readiness probes will not execute.

### Summary
- **Startup Probe Fails**: Pod is marked as `Stopped`.
- **Readiness Probe Fails**: No traffic is routed to the pod, and its IP is removed from the service.
- **Liveness Probe Fails**: Container is restarted.

## Practical Use Cases

### Example: Node.js API Deployment [Please do probing syntax at container level]

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nodejs-api-deployment
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nodejs-api
  template:
    metadata:
      labels:
        app: nodejs-api
    spec:
      containers:
      - name: nodejs-api
        image: your-nodejs-image:latest
        ports:
        - containerPort: 3000
        readinessProbe:
          httpGet:
            path: /healthz           # Health check endpoint in your Node.js app
            port: 3000
          initialDelaySeconds: 5      # Delay before probe starts
          periodSeconds: 10           # Frequency of probe checks
          timeoutSeconds: 2           # Timeout for each probe attempt
        livenessProbe:
          httpGet:
            path: /healthz            # Same endpoint can be used for liveness
            port: 3000
          initialDelaySeconds: 10
          periodSeconds: 15
          timeoutSeconds: 3
```
### Example: mongodb statefulset
```yaml
livenessProbe:
          tcpSocket:
            port: 27017               # Port where MongoDB listens
          initialDelaySeconds: 15      # Wait time before probe starts
          periodSeconds: 20            # Frequency of probe checks
          timeoutSeconds: 5            # Timeout for each probe attempt
readinessProbe:
          tcpSocket:
            port: 27017               # Port for checking MongoDB readiness
          initialDelaySeconds: 10
          periodSeconds: 15
          timeoutSeconds: 3
 