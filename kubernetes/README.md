# Kubernetes configuration for the Auth Server

In the deployment.yaml, ensure that you replace your-image-name with the actual name of your Docker image.

In the deployment.yaml, envFrom with secretRef is used to inject environment variables from a secret into your
container. Ensure that your-secret-name matches the name of the Secret you want to use.

In the service.yaml, make sure auth-server matches the app label in your deployment, so that the service can correctly
select the pods.

In the auth-server-secret.yaml, the data section should include the actual base64-encoded values for
spring.datasource.password, key, and spring.mail.password.

In the managed-cert-ingress.yaml, replace your-domain-name.com with your actual domain name, and ensure that
auth-server-service-np matches the name of your service.

Ensure that the referenced resources like the Docker image, static IP name, and domain name are correctly configured and
accessible.