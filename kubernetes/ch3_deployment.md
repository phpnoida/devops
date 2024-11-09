# Why Deployment is Required?

The **Deployment** component in Kubernetes is essential for running applications. It is responsible for managing and maintaining application pods, providing critical features like **auto-healing** and **auto-scaling**. These capabilities ensure that applications experience zero downtime and can automatically adjust to varying loads.

Note:For each microservice there will be respective deployment.yml file i.e if there are 50 microservices in our project then there will be 50 deployment.yml file i.e emaildeployment.yml , orderdeployment.yml etc

---

## Docker World vs. Kubernetes World

| Docker World         | Kubernetes World                       | Kubernetes World                    |
|----------------------|----------------------------------------|-------------------------------------|
| **Container**        | **Pod**                                |  **Deployment**                     |
| 1. The main artifact | 1. A wrapper around a container        | 1. A wrapper around a pod           |
| 2. Running program   | 2. The smallest component in Kubernetes| 2. Deploys pods through deployments |
|                      | 3. Responsible for running applications| 3. Defined in a `deployment.yml` file |
|                      | 4. Typically, 1 pod runs 1 container   | 4. Specifies replica requirements (e.g., 2 or 100) |
|                      |                                        | 5. In practice, we **never deploy pods directly**; we always deploy through a **Deployment** to ensure scalability and resilience. |

---

### How Many Pods Are Needed in a ReplicaSet?

1. The number of pods required in a ReplicaSet depends on the **concurrent user load** the application is expected to handle.
2. For example, if a single pod can handle **10 concurrent users** and the application needs to manage **100 requests per second**, we would calculate the required number of pods as:
