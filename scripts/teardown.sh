#!/bin/bash

# Teardown Script for Todo App
# This script removes all Kubernetes resources for the Todo App

set -e

# Configuration
APP_NAME="todo-app"
NAMESPACE="todo-app"

echo "🧹 Starting teardown process..."

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed or not in PATH"
    exit 1
fi

# Confirm deletion
read -p "❓ Are you sure you want to delete all resources for ${APP_NAME}? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Teardown cancelled"
    exit 1
fi

echo "🗑️  Deleting Kubernetes resources..."

# Delete all resources in reverse order of creation
if [ -f "k8s/ingress.yaml" ]; then
    echo "🌐 Deleting Ingress..."
    kubectl delete -f k8s/ingress.yaml -n ${NAMESPACE} --ignore-not-found=true
fi

echo "📈 Deleting Horizontal Pod Autoscaler..."
kubectl delete -f k8s/hpa.yaml -n ${NAMESPACE} --ignore-not-found=true

echo "🚀 Deleting application deployment..."
kubectl delete -f k8s/app-deployment.yaml -n ${NAMESPACE} --ignore-not-found=true

echo "🗄️  Deleting MySQL deployment..."
kubectl delete -f k8s/mysql-deployment.yaml -n ${NAMESPACE} --ignore-not-found=true

echo "⚙️  Deleting ConfigMap..."
kubectl delete -f k8s/configmap.yaml -n ${NAMESPACE} --ignore-not-found=true

echo "🔐 Deleting Secrets..."
kubectl delete -f k8s/secret.yaml -n ${NAMESPACE} --ignore-not-found=true

# Wait for pods to be deleted
echo "⏳ Waiting for pods to be terminated..."
kubectl wait --for=delete pod -l app=todo-app -n ${NAMESPACE} --timeout=300s || true
kubectl wait --for=delete pod -l app=mysql -n ${NAMESPACE} --timeout=300s || true

# Delete namespace
echo "🗂️  Deleting namespace..."
kubectl delete namespace ${NAMESPACE} --ignore-not-found=true

echo ""
echo "✅ Teardown completed successfully!"
echo "🎯 All resources for ${APP_NAME} have been removed from Kubernetes"