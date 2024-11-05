# Docker vs. Kubernetes

- **Docker** is a container platform, while **Kubernetes** is a container orchestration platform.It means Docker is used to create containers while K8s is used to manage containers.
  
- Both are very different things and should not be compared with each other.

- **Docker**:
  - A portable artifact of your application.
  - A bundle of your code and project dependencies; nothing more than that.

- **Kubernetes**:
  - An enterprise-level solution; a production-ready solution.

## Deploying Node.js Applications

- If we have to deploy a Node.js application without Kubernetes, we must manually set up the following in production:
  - VPC (Virtual Private Cloud).
  - Spinning multiple EC2 instances for APIs in private subnets across multiple Availability Zones (AZs) for higher availability.
  - why multiple ec2 ? if one goes down other will be there to handle traffic
  - Spinning 3 to 4 EC2 instances for MongoDB, making replica sets.
  - Few EC2 instances for CMS (Content Management System).
  - Few EC2 instances for websites.
  - Most important pulling latest docker image of project in all ec2s
  - if any container goes down due to any reason , we need to fix it manually
  - Application Load Balancer (ALB).
  - Auto-scaling policies.
  - EBS (Elastic Block Store).
  - Snapshots.

- **Note**: Despite auto-scaling being attached, auto-healing is not there; we, as DevOps engineers, need to fix things manually.

## Benefits of Using Kubernetes or Why Kubernetes

- If we opt for Kubernetes:
  - It provides everything we need:
    - Auto-scaling.
    - Auto-healing.
    - Load balancers.
    - Automatic rollout and rollback
    - secret and configuraton management
  - No need to manually spin multiple EC2 instances.
  - Kubernetes is a cluster where we have more than one node/EC2/server.
