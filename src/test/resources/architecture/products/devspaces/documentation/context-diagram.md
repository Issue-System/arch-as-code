DevSpaces operates with the following elements in mind:

A **client running on the developer’s local workstation** and a server running in the cloud that works together to seamlessly run workloads in the cloud while keeping the developer experience local. 

The client initiates execution requests, synchronizes filesystem changes, and gives developers a view of the execution results.

The software developer is able to deploy one or more pods/containers in a DevSpace using a kubernetes configuration file. 

The **DevSpaces server** stores the configuration of the DevSpace definitions and synchronizes changes back to the client.

The **container management platform** composed of a highly scalable, high-performance Kubernetes container management service that supports Docker containers and allows you to easily run applications on a managed cluster.