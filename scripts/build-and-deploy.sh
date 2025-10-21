#!/bin/bash

# Build and Deploy Script for Todo App
# This script builds the Docker image and deploys it to Kubernetes

set -e

# Configuration
APP_NAME="todo-app"
DOCKER_REGISTRY="${DOCKER_REGISTRY:-your-registry.com}"
DOCKER_IMAGE="${DOCKER_REGISTRY}/${APP_NAME}:latest"
NAMESPACE="todo-app"

echo "🚀 Starting deployment process..."

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed or not in PATH"
    exit 1
fi

# Check if Docker is available
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed or not in PATH"
    exit 1
fi

# Step 1: Build the application
echo "📦 Building the application..."
./mvnw clean package -DskipTests

# Step 2: Build Docker image
echo "🐳 Building Docker image..."
docker build -t ${APP_NAME}:latest .
docker tag ${APP_NAME}:latest ${DOCKER_IMAGE}

# Step 3: Push Docker image (if registry is specified)
if [ "$DOCKER_REGISTRY" != "your-registry.com" ]; then
    echo "📤 Pushing Docker image to registry..."
    docker push ${DOCKER_IMAGE}
else
    echo "⚠️  Skipping push (no registry configured)"
    echo "   To push to a registry, set DOCKER_REGISTRY environment variable"
fi

# Step 4: Deploy to Kubernetes
echo "☸️  Deploying to Kubernetes..."

# Create namespace if it doesn't exist
kubectl create namespace ${NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -

# Apply secrets (you might want to update the secrets first)
echo "🔐 Applying secrets..."
kubectl apply -f k8s/secret.yaml -n ${NAMESPACE}

# Apply configmap
echo "⚙️  Applying configuration..."
kubectl apply -f k8s/configmap.yaml -n ${NAMESPACE}

# Deploy MySQL
echo "🗄️  Deploying MySQL..."
kubectl apply -f k8s/mysql-deployment.yaml -n ${NAMESPACE}

# Wait for MySQL to be ready
echo "⏳ Waiting for MySQL to be ready..."
kubectl wait --for=condition=ready pod -l app=mysql -n ${NAMESPACE} --timeout=300s

# Deploy application
echo "🚀 Deploying application..."
if [ "$DOCKER_REGISTRY" != "your-registry.com" ]; then
    # Update the deployment with the new image
    kubectl set image deployment/todo-app todo-app=${DOCKER_IMAGE} -n ${NAMESPACE}
else
    # Use local image
    kubectl apply -f k8s/app-deployment.yaml -n ${NAMESPACE}
fi

# Apply HPA
echo "📈 Applying Horizontal Pod Autoscaler..."
kubectl apply -f k8s/hpa.yaml -n ${NAMESPACE}

# Apply Ingress (optional)
if [ -f "k8s/ingress.yaml" ]; then
    echo "🌐 Applying Ingress..."
    kubectl apply -f k8s/ingress.yaml -n ${NAMESPACE}
fi

# Step 5: Wait for deployment to be ready
echo "⏳ Waiting for deployment to be ready..."
kubectl wait --for=condition=available deployment/todo-app -n ${NAMESPACE} --timeout=600s

# Step 6: Show status
echo "✅ Deployment completed successfully!"
echo ""
echo "📊 Deployment status:"
kubectl get pods -n ${NAMESPACE}
echo ""
echo "🔗 Services:"
kubectl get services -n ${NAMESPACE}
echo ""
echo "📈 HPA status:"
kubectl get hpa -n ${NAMESPACE}

# Show application URL if using LoadBalancer
SERVICE_IP=$(kubectl get service todo-app-service -n ${NAMESPACE} -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "")
if [ ! -z "$SERVICE_IP" ]; then
    echo ""
    echo "🌐 Application URL: http://${SERVICE_IP}"
fi

echo ""
echo "🎉 Todo App is now deployed and ready!"