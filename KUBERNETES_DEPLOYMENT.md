# Kubernetes Deployment Guide

This guide provides comprehensive instructions for deploying the Todo Application to Kubernetes for both development and production environments.

## 📋 Prerequisites

### Required Tools
- **Docker** - For building container images
- **kubectl** - For Kubernetes cluster management
- **Minikube** or **Docker Desktop** - For local development
- **Helm** (optional) - For package management
- **Access to a Kubernetes cluster** - For production deployment

### Development Environment Setup

#### Using Minikube
```bash
# Start Minikube
minikube start --cpus=4 --memory=8192 --disk-size=20g

# Enable ingress addon
minikube addons enable ingress

# Verify cluster status
kubectl cluster-info
```

#### Using Docker Desktop
1. Enable Kubernetes in Docker Desktop settings
2. Increase resource allocation (recommended: 4+ CPU, 8+ GB RAM)

## 🏗️ Architecture Overview

The deployment consists of:

```
┌─────────────────┐    ┌─────────────────┐
│   Ingress       │    │   Ingress       │
│   (nginx)       │    │   (nginx)       │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
┌─────────▼───────┐    ┌─────────▼───────┐
│ Todo App Service │    │ Todo App Service │
│ (LoadBalancer)   │    │ (LoadBalancer)   │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
    ┌─────▼─────┐          ┌─────▼─────┐
    │ Todo Pods │          │ Todo Pods │
    │ (2-10)    │          │ (2-10)    │
    └───────────┘          └───────────┘
          │                      │
          └──────────┬───────────┘
                     │
            ┌────────▼─────────┐
            │ MySQL Service    │
            │ (ClusterIP)      │
            └────────┬─────────┘
                     │
               ┌─────▼─────┐
               │ MySQL Pod │
               │ (1)       │
               └───────────┘
```

## 🚀 Quick Start

### 1. Clone and Build
```bash
# Clone the repository
git clone <repository-url>
cd SpringBoot-1/first

# Build the application
./mvnw clean package -DskipTests
```

### 2. Deploy to Kubernetes
```bash
# Deploy the application
./scripts/build-and-deploy.sh
```

### 3. Monitor the Deployment
```bash
# Check deployment status
./scripts/monitor.sh
```

## 📁 Kubernetes Resources

### Resource Files
- `k8s/namespace.yaml` - Application namespace
- `k8s/configmap.yaml` - Application configuration
- `k8s/secret.yaml` - Database and JWT secrets
- `k8s/mysql-deployment.yaml` - MySQL database deployment
- `k8s/app-deployment.yaml` - Application deployment
- `k8s/ingress.yaml` - Ingress configuration (optional)
- `k8s/hpa.yaml` - Horizontal Pod Autoscaler

### Resource Details

#### Namespace
- **Name**: `todo-app`
- **Purpose**: Isolate application resources

#### ConfigMap
- **Name**: `todo-app-config`
- **Contains**: Application properties, database URL, actuator settings

#### Secrets
- **Name**: `todo-app-secrets`
- **Contains**: Database password, JWT secret
- **Note**: Update with your actual secrets before deployment

#### MySQL Deployment
- **Replicas**: 1
- **Storage**: 5Gi Persistent Volume
- **Database**: `todo_list`

#### Application Deployment
- **Replicas**: 2 (minimum)
- **Auto-scaling**: 2-10 pods
- **Resources**: 256Mi-512Mi memory, 250m-500m CPU
- **Health Checks**: Liveness and readiness probes

## ⚙️ Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | Database connection URL | MySQL service URL |
| `DB_USERNAME` | Database username | root |
| `DB_PASSWORD` | Database password | From secret |
| `JWT_SECRET` | JWT signing secret | From secret |

### Secrets Management

Update the secrets in `k8s/secret.yaml`:

```bash
# Generate base64 encoded values
echo -n "your_mysql_password" | base64
echo -n "your_jwt_secret_key" | base64

# Update the secret.yaml file with the encoded values
```

### Database Configuration

The application uses MySQL with the following settings:
- **Host**: `mysql-service` (within cluster)
- **Port**: `3306`
- **Database**: `todo_list`

## 📊 Monitoring and Health Checks

### Actuator Endpoints
- **Health**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus` (production)

### Health Probes
- **Liveness Probe**: `/actuator/health/liveness` (port 8081)
- **Readiness Probe**: `/actuator/health/readiness` (port 8081)

### Custom Health Indicators
- **Database Health**: Checks MySQL connection
- **Application Health**: Custom application status

## 🔄 Auto-scaling

### Horizontal Pod Autoscaler (HPA)
- **Min Replicas**: 2
- **Max Replicas**: 10
- **CPU Target**: 70%
- **Memory Target**: 80%

### Scaling Metrics
```bash
# Check HPA status
kubectl get hpa -n todo-app

# Check resource usage
kubectl top pods -n todo-app
```

## 🌐 Accessing the Application

### LoadBalancer (Recommended for Production)
```bash
# Get the external IP
kubectl get service todo-app-service -n todo-app

# Access the application
http://<EXTERNAL_IP>
```

### Port Forwarding (Development)
```bash
# Forward port to local machine
kubectl port-forward service/todo-app-service 8080:80 -n todo-app

# Access locally
http://localhost:8080
```

### Ingress (If configured)
```bash
# Check ingress status
kubectl get ingress -n todo-app

# Access via hostname
http://todo-app.example.com
```

## 📝 Scripts

### Build and Deploy
```bash
./scripts/build-and-deploy.sh
```
- Builds the application
- Creates Docker image
- Deploys to Kubernetes
- Waits for deployment completion

### Monitor
```bash
./scripts/monitor.sh
```
- Shows pod status
- Displays resource usage
- Shows recent events
- Provides access URLs

### Teardown
```bash
./scripts/teardown.sh
```
- Removes all Kubernetes resources
- Deletes namespace
- Cleans up deployments

## 🐛 Troubleshooting

### Common Issues

#### Pod Not Starting
```bash
# Check pod status
kubectl get pods -n todo-app

# Check pod events
kubectl describe pod <pod-name> -n todo-app

# Check logs
kubectl logs <pod-name> -n todo-app
```

#### Database Connection Issues
```bash
# Check MySQL pod
kubectl get pods -l app=mysql -n todo-app

# Test database connection
kubectl exec -it <mysql-pod> -n todo-app -- mysql -u root -p todo_list
```

#### Service Not Accessible
```bash
# Check service endpoints
kubectl get endpoints todo-app-service -n todo-app

# Check service configuration
kubectl describe service todo-app-service -n todo-app
```

### Debug Commands

```bash
# Get cluster info
kubectl cluster-info

# Check all resources in namespace
kubectl get all -n todo-app

# Port forward to pod for debugging
kubectl port-forward <pod-name> 8080:8080 -n todo-app

# Exec into pod
kubectl exec -it <pod-name> -n todo-app -- /bin/sh
```

## 🔒 Security Considerations

### Network Policies
Consider implementing network policies to restrict traffic between pods.

### RBAC
Use Role-Based Access Control for cluster operations.

### Secrets
- Use proper secret management (e.g., HashiCorp Vault, AWS Secrets Manager)
- Rotate secrets regularly
- Don't commit secrets to version control

### Image Security
- Use signed images
- Regularly scan images for vulnerabilities
- Use specific image tags instead of `latest`

## 🚀 Production Deployment

### Pre-deployment Checklist
- [ ] Update secrets with production values
- [ ] Configure proper ingress with TLS
- [ ] Set up monitoring and alerting
- [ ] Configure backup for database
- [ ] Set up CI/CD pipeline
- [ ] Configure logging aggregation
- [ ] Set up disaster recovery procedures

### Production Modifications

#### Update Image Registry
```bash
export DOCKER_REGISTRY="your-registry.com"
./scripts/build-and-deploy.sh
```

#### Configure Production Ingress
Update `k8s/ingress.yaml` with your domain and TLS configuration.

#### Set up Monitoring
- Install Prometheus and Grafana
- Configure alerting rules
- Set up log aggregation with ELK stack

## 📚 Additional Resources

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Spring Boot on Kubernetes](https://spring.io/guides/gs/spring-boot-kubernetes/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

## 🆘 Support

For issues related to:
- **Application**: Check application logs and configuration
- **Kubernetes**: Check cluster status and resource limits
- **Database**: Verify MySQL connectivity and configuration
- **Networking**: Check service and ingress configuration