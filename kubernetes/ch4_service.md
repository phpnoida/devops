why service is required ?

we can't access our application by accessing pods ,our application which is running inside pods is accessible through service only.

Kubeproxy is worker node is the one which forward the request from service to pod.

We can specify 10 or 100 pods in deployment.yml file right ?so service is that component of k8 which acts as LoadBalancer for these 10 or 100 pods which has been created by deployemnt hence we can say autohealing and autoscaling we get from deployment and load balancer from service.

Note:
1.For each microservice there will be respective service.yml file i.e if there are 50 microservices in our project then there will be 50 service.yml file i.e emailservice.yml , orderservice.yml etc
2.more refined way is that we mainly create service in deployment.yml file itself becuase deployment and service are closely related to each other.

Types of Service:
1.ClusterIP(default)
2.NodePort
3.LoadBalancer
