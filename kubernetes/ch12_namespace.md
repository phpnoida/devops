By default k8s creates following namespaces
default
kubectl-node-lease (v impt, it holds the heartbeat of node , it tells the control plane about the status of each node)
kube-public
kube-system

Common Practices for Using Namespaces
Namespace per Environment (most common):

DevOps teams often create separate namespaces for each environment, like dev, staging, and production, for each project.
This helps keep resources isolated by environment, making it easier to apply policies or configurations specific to each environment.
Example: For a project called "dsobs", you might have namespaces like dsobs-dev, dsobs-staging, and dsobs-prod.


