# Why is a Service Required?

## Gist:
1. **Pods can’t be accessed directly, so our application can’t be accessed directly either.**
   
2. **Pods need to communicate with each other within the cluster**, but this communication can’t rely on IP addresses assigned to pods since pods are ephemeral (they come and go). Therefore, we need a **static IP address that doesn’t change** — and this is provided by the **Service**.

   **Example:**  
   Suppose we have 100 microservices, each running on multiple pods across various worker nodes. Each microservice’s pods need to communicate with others, but this can’t happen based on pod IP addresses alone. Here, Services play a crucial role.

3. **Service** is a component of the cluster that allows access to pods/applications, whether within the cluster network or from outside it.

4. **Service acts as a LoadBalancer for pods.**

5. Services identify their associated pods using **labels and selectors**.

6. **There are 3 types of Services:**
   - **ClusterIP:** Accessible only within the cluster. This allows pods to communicate with each other using the Service. This is the default and most commonly used type.
   - **NodePort:** Allows access to the Service outside the cluster via the worker node’s IP address and a specific NodePort (in the range 30000–32767). This type is generally not recommended for production, as the node could be terminated or restarted with a new IP address.
   - **LoadBalancer:** Provides a public IP address, making the Service accessible externally. However, instead of using this directly, **Ingress** is commonly used for external access.

   > **Note:** `kube-proxy` running on each worker node is responsible for forwarding requests from the Service to the appropriate pod.
