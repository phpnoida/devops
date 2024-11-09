# Why Ingress & Ingress Controller?

1. **Service component is the load balancer of our cluster, right?**
2. However, this load balancer is not enterprise-level, meaning it doesn’t support path-based, port-based, header-based, or query-based routing.
3. If we have 100 microservices, we would create 100 services. This means 100 load balancers with 100 public IP addresses, which can be very costly.

4. **How can we bring enterprise-level load balancers, like NGINX, Apache, or AWS ELB, into our clusters?**  
   The answer is by using an **Ingress Controller**.
5. Major load balancers in the market, like NGINX, Apache, and AWS ALB, have written their logic to allow us to use any of them within a Kubernetes (K8s) cluster.
6. As DevOps professionals, we bring these load balancers into our cluster using Helm charts or YAML files.
7. Now, suppose ALB (Application Load Balancer) is installed in our cluster. To specify rules for it, we have another component called **Ingress**.
8. **Both Ingress and the Ingress Controller are essential** components in Kubernetes.
9. We can think of it this way: the Ingress Controller balances loads among nodes, while the service balances loads among pods. This setup is powerful and efficient.
10. A single Ingress file exists for the whole cluster, where path-based routing rules and TLS configurations are defined. The Ingress Controller continuously monitors this file, so any changes made are reflected and passed to ALB.
11. The Ingress Controller is installed within a pod.
12. Since it’s installed in a pod, it will be accessible through a service. This service will be of **LoadBalancer** type with one public IP, which will serve as the entry point to the cluster for end users.
