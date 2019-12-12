## Deployment Diagram

DevSpaces uses DevOps practices and automatically deploys the complete system to an environment using Jenkins CI through release pipelines and automation scripts which builds, tests and deploys all required services for Devspaces to operate.
The build pipeline is represented through the Jenkins pipeline GUI, all steps involved in the pipeline are shown below:



Deployment view of the DevSpace into the Container Management Platform



The minimum DevSpace deployment model is one Pod with one Container inside, but the number of Pods in the DevSpace, and the number of containers on every Pod is configured by the Software Developer through a k8s file.
Every Pod in the model is synchronized with only one local folder of the Local Development Workspace, and it is persisted into AWS S3 bucket using regular snapshots. It is then downloaded back on pod initialization
Caused by a Syncthing limitation there is no possible to synchronize subfolders of one synced folder.
