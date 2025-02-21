# Smart_Home_Controller
Repo to host application code for the rest controller that will manage all oft the traffic inbound and outbound of my custom smart home. This is strictly a backend applicaiton and built on Java spring boot

## Smart Home Controller
The smart home controller is responsable for managing all traffic in and out of the smart home, directing requests and serving as the controller for the IoT Devices

### Endpoints
1. /testec2 This tests the smarthome controller itself
2. /testpi This tests the rasberi pi device


### AWS IoT Authenitcation
The applicaiton(Controller) will authenticate with the AWS IOT Core Topic via AWS Secrets manager where we have our credential files stored
IN order to authenticate to AWS IOT via MQTT Protocol, we need IAM based authentication on the AWS IOT Core side, IAM Authentication on the EC2 Instcnce side, as well as inherit the credentials from aws iot core in our application code


