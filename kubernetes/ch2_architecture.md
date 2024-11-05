# Understanding Kubernetes Architecture

## What is a Container Runtime?

Before diving into Kubernetes architecture, let’s first understand what a container runtime is.

- Without a Java runtime, we can’t run Java code, right? Similarly, to run container-based projects, we need a **container runtime**. Without it, the project cannot run.
- when we installed docker , docker container also gets installed along with docker.
- **Example**: Docker is one of the popular containers, and it uses `dockershim` by default as the container runtime. Other container runtimes include **CRI-O** and **containerd**.

---

## Kubernetes Architecture

Kubernetes operates on two types of nodes/machines:

1. **Control Plane(set of master node)**
2. **Data Plane(set of worker node)**

- **Control Plane**: Used by Kubernetes administrators for managing the cluster; it controls the worker nodes.
- **Data Planes**: Set of worker node is called data planes.Worker node are the one where our application runs , which does the main work that's why its called as worker node.

- The control plane is the entry point for Kubernetes administrators.
- Worker nodes (where **ALB** will be deployed) serve as the entry point for end users.

what is K8s Cluster ?
cluster is comprised of control plane means collection of master nodes and data planes means collection of worker node.

---

## Worker Nodes

There are multiple worker nodes in a Kubernetes cluster. Each worker node hosts multiple **pods**, and our application runs inside containers within these pods.

Each worker node must have three key processes installed:

1. **Container Runtime**

   - Responsible for running containers.

2. **Kubelet**

   - Manages pod-related tasks, such as starting pods.
   - Reports pod status to the control plane (e.g., if a pod goes down).
   - Allocates resources like CPU and RAM to pods on the node.

3. **Kube-proxy**
   - Manages networking for services.
   - Forwards requests from services to the appropriate pods.

---

## Control Plane Nodes

Each control plane node must have five main processes installed, in the following sequence:

1. **API Server**

   - The only entry point for administrators.
   - Acts as the heart of the cluster.
   - Administrators or DevOps can interact with the API Server via the Kubernetes dashboard, Kubernetes API, or CLI (`kubectl`).
   - Acts as a gatekeeper, allowing only authorized and authenticated requests into the cluster.

2. **Scheduler**

   - Decides on which worker node a pod should be scheduled.
   - this decision is based on how much cpu and memmory we have requested for and in which nodes it is available

3. **Controller Manager**

   - Manages various controllers in Kubernetes, like ReplicaSets, ensuring they work properly.
   - it mainly see our yml file and look for any chnages we did in these yml file 

4. **Cloud Controller Manager**

   - Allows Kubernetes, an open-source software, to be deployed on various cloud providers like AWS, Azure, and GCP.

5. **etcd**
   - Acts as a database for the cluster.
   - Considered the "brain" of the cluster.
   - Stores values (e.g., pod status, node data) in a key-value format.
   - The scheduler, controller manager, and others use data stored in etcd to perform their roles.

---

## Realistic Example of Cluster Setup for Production

For a production setup:

- A minimum of **2 control plane nodes** and **3 worker nodes** is recommended.
- The exact number may vary depending on the type of application and expected load.
