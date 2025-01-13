what is helm chart ?
suppose we have created docker image of our api and that is deployed on ec2 , do you think that this single container can handle millions of request , answer is no , so for that handling millions of request we need multiple containers of the same image and for that we have k8s which is best in managing 1000 of containers.

so now we might have question why we need helm , we have docker for container and k8 for managing these containers.