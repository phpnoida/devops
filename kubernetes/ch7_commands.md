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
- `kubectl delete pvc --all`
- `kubectl delete pod --all`

## Edit Resources
To edit a configuration file, then apply the changes:
- `vim nginx-deployment.yml` (edit the file in editor)
- `kubectl apply -f nginx-deployment.yml`

## Get Resources
To retrieve various resources in the cluster:
- `kubectl get all -n dsobs-dev`
- `kubectl get all -o wide -n dosbs-prod`
- `kubectl get all -o yaml`
- `kubectl get pods` or `kubectl get po`
- `kubectl get deployment -n dsobs-dev`
- `kubectl get statefulset -n dsobs-dev`
- `kuebctl get pvc -n dsobs-dev`
- `kubectl get pv` (pv belongs to whole cluster)
- `kubectl get service -n dsobs-dev` or `kubectl get svc -n dsobs-dev`
- `kubectl get ing -n dsobs-dev`
- `kubectl get cm -n dsobs-dev` (ConfigMaps)
- `kubectl get secrets -n dsobs-dev`

## Debugging
To troubleshoot and find reasons why resources haven’t started:
- `kubectl get po -w `
- `kubectl describe pod|svc|depl podName|deploymentName|serviceName`

### Port Forwarding for Debugging
To forward a port for in-depth debugging, especially for `ClusterIP` services:
- `kubectl port-forward podName|serviceName localMachinePort:containerPort`
  - Example: `kubectl port-forward nginx-pod 8083:80`

## More Debugging Logs
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


rs.initiate({
  _id: "rs0",              
  members: [
    { _id: 0, host: "mongo-statefulset-0.mongo-headless-svc.default.svc.cluster.local:27017" },
    { _id: 1, host: "mongo-statefulset-1.mongo-headless-svc.default.svc.cluster.local:27017" },
    { _id: 2, host: "mongo-statefulset-2.mongo-headless-svc.default.svc.cluster.local:27017" }
  ]
})

