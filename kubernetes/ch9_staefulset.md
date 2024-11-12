# Kubernetes StatefulSet Guide

## When to Use StatefulSet?

StatefulSets are essential when deploying databases and applications that require stable storage and persistent identity, such as **MySQL** or **MongoDB**. In contrast, for deploying stateless applications (such as APIs or CMSs), the **Deployment** component should be used.

### Why Not Use Deployment for Databases?

Databases in a Kubernetes cluster have specific requirements that a Deployment cannot meet:
1. **Separate Persistent Volumes** for Each Pod: To avoid data inconsistency, each database pod should have its own storage.
2. **Ordered Pod Creation and Deletion**: For instance, if deploying 3 database pods, the second pod should be created only after the first pod is ready, and the third only after the second.
3. **Sticky Identity and Storage**: If a database pod dies, it should restart with the same name and reattach to its original storage.

These needs are met with StatefulSet rather than Deployment.

## Deployment vs. StatefulSet

| Feature                        | Deployment                                                    | StatefulSet                                                                 |
|--------------------------------|----------------------------------------------------------------|----------------------------------------------------------------------------|
| **Pod Naming**                 | Pods receive random names (e.g., `nginx-12er`).               | Pods have predictable, sequential names (e.g., `mongo-0`, `mongo-1`).      |
| **Pod Deletion Order**         | Any pod can be deleted at random.                             | Last created pod is deleted first, ensuring ordered deletion.              |
| **Persistent Volume Claims**   | All pods can share a single PVC, leading to data inconsistency. | Uses `volumeClaimTemplates` to create a unique PVC for each pod, ensuring each pod has its own storage and avoids data inconsistency. |

## What is a Headless Service?

In typical services, Kubernetes provides a load-balancing layer to distribute requests across pods. However, for databases, we do not want random pod selection or load balancing by the service. Instead, we need:
- **Unique DNS for Each Pod**: This allows database clients to connect to specific pods directly.
- **Database-Managed Load Balancing**: Load balancing should be handled by the database driver, especially in setups involving master-slave configurations or replica sets.

Setting `ClusterIP: None` creates a **Headless Service** instead of a standard load-balanced service, providing each pod with a unique DNS entry.

### Headless Service vs. ClusterIP Service

- **ClusterIP Service**: Acts as a load balancer within the cluster, with subtypes such as NodePort, LoadBalancer, and ClusterIP.
- **Headless Service**: Used for databases, it avoids random pod selection and load balancing, giving each pod a stable DNS.

### DNS Format for Pods with Headless Service
The DNS format for a pod in a StatefulSet with a Headless Service is:
<mongo-0 (podname from StatefulSet name + ordinal)>.mongo(servicename).default(namespace).svc.cluster.local:27017


## Summary

- Use **StatefulSet** for databases to ensure stable storage and identity.
- Use a **Headless Service** instead of a standard ClusterIP service for database pods, ensuring a unique DNS and avoiding automatic load balancing.
