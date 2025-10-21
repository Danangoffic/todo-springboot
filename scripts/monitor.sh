#!/bin/bash

# Monitor Script for Todo App
# This script shows the status of the deployed application

set -e

# Configuration
NAMESPACE="todo-app"

echo "📊 Todo App Monitoring Dashboard"
echo "================================"

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed or not in PATH"
    exit 1
fi

# Check if namespace exists
if ! kubectl get namespace ${NAMESPACE} &> /dev/null; then
    echo "❌ Namespace ${NAMESPACE} does not exist. Please deploy the application first."
    exit 1
fi

echo ""
echo "📋 Namespace Status:"
kubectl get namespace ${NAMESPACE}

echo ""
echo "🚀 Application Pods:"
kubectl get pods -n ${NAMESPACE} -l app=todo-app -o wide

echo ""
echo "🗄️  Database Pods:"
kubectl get pods -n ${NAMESPACE} -l app=mysql -o wide

echo ""
echo "🔗 Services:"
kubectl get services -n ${NAMESPACE}

echo ""
echo "📈 Horizontal Pod Autoscaler:"
kubectl get hpa -n ${NAMESPACE}

echo ""
echo "📊 Resource Usage:"
kubectl top pods -n ${NAMESPACE} --no-headers | head -10

echo ""
echo "🔍 Recent Events:"
kubectl get events -n ${NAMESPACE} --sort-by='.lastTimestamp' | tail -10

# Show application URL if using LoadBalancer
echo ""
echo "🌐 Application Access:"
SERVICE_TYPE=$(kubectl get service todo-app-service -n ${NAMESPACE} -o jsonpath='{.spec.type}' 2>/dev/null || echo "Not found")
if [ "$SERVICE_TYPE" = "LoadBalancer" ]; then
    SERVICE_IP=$(kubectl get service todo-app-service -n ${NAMESPACE} -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "Pending")
    SERVICE_HOST=$(kubectl get service todo-app-service -n ${NAMESPACE} -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' 2>/dev/null || echo "")
    if [ ! -z "$SERVICE_IP" ] && [ "$SERVICE_IP" != "Pending" ]; then
        echo "   📡 Main App: http://${SERVICE_IP}"
        echo "   📈 Health: http://${SERVICE_IP}/actuator/health"
        echo "   📊 Metrics: http://${SERVICE_IP}/actuator/metrics"
    elif [ ! -z "$SERVICE_HOST" ]; then
        echo "   📡 Main App: http://${SERVICE_HOST}"
        echo "   📈 Health: http://${SERVICE_HOST}/actuator/health"
        echo "   📊 Metrics: http://${SERVICE_HOST}/actuator/metrics"
    else
        echo "   ⏳ LoadBalancer IP is still being assigned..."
    fi
else
    echo "   💡 Use port-forwarding to access the application:"
    echo "   kubectl port-forward service/todo-app-service 8080:80 -n ${NAMESPACE}"
    echo "   Then access: http://localhost:8080"
fi

echo ""
echo "🧪 Health Check:"
kubectl exec -n ${NAMESPACE} deployment/todo-app -- curl -f http://localhost:8080/actuator/health 2>/dev/null && echo "   ✅ Application is healthy" || echo "   ❌ Application health check failed"

echo ""
echo "📝 Logs (last 20 lines):"
echo "Application logs:"
kubectl logs -n ${NAMESPACE} -l app=todo-app --tail=20 --timestamps=true