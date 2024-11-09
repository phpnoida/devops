# Important `kubectl` Commands for CRUD Operations

## Create Resources
To create resources from configuration files:
- `kubectl apply -f nginx-deployment.yml`
- `kubectl apply -f nginx-service.yml`

## Delete Resources
To delete resources:
- `kubectl delete -f nginx-deployment.yml`
- `kubectl delete -f nginx-service.yml`
- `kubectl delete all --all`

## Edit Resources
To edit a configuration file, then apply the changes:
- `vim nginx-deployment.yml` (edit the file in editor)
- `kubectl apply -f nginx-deployment.yml`

## Get Resources
To retrieve various resources in the cluster:
- `kubectl get all`
- `kubectl get all -o wide`
- `kubectl get all -o yaml`
- `kubectl get pods` or `kubectl get po`
- `kubectl get deployment`
- `kubectl get service` or `kubectl get svc`
- `kubectl get ing`
- `kubectl get cm` (ConfigMaps)
- `kubectl get secrets`

## Debugging
To troubleshoot and find reasons why resources haven’t started:
- `kubectl describe podName|deploymentName|serviceName`

### Port Forwarding for Debugging
To forward a port for in-depth debugging, especially for `ClusterIP` services:
- `kubectl port-forward podName|serviceName localMachinePort:containerPort`
  - Example: `kubectl port-forward nginx-pod 8083:80`

## Logs
To view logs for a specific pod:
- `kubectl logs podName`

## Access Pod Terminal
To open an interactive terminal inside a pod:
- `kubectl exec -it podName -- /bin/sh`

## Rollback (if anything goes wrong)
To check deployment history and roll back to a specific revision:
- `kubectl rollout history deployment/your_depl_name`
- `kubectl rollout undo deployment/your_depl_name --to-revision=revisionNumber`
  - Example: `kubectl rollout undo deployment/nginx-deployment --to-revision=1`

## Cross-Check Deployment Status
To check the rollout status of a deployment:
- `kubectl rollout status deployment/nginx-deployment`
