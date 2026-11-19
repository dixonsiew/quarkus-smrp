@echo off
setlocal EnableDelayedExpansion

REM ============================================
REM Configuration
REM ============================================
set IMAGE_NAME=smrp-quarkus
set IMAGE_TAG=1.0.0
set LOCAL_IMAGE=%IMAGE_NAME%:%IMAGE_TAG%
set SERVICE_NAME=smrp-quarkus
set DEPLOYMENT_NAME=smrp-quarkus
set DOCKERFILE=src/main/docker/Dockerfile.jvm
set DEPLOY_FILE=minikube.yaml

echo ============================================
echo  Starting deployment of %SERVICE_NAME%
echo ============================================
echo.

REM ============================================
REM Step 1: Delete Service
REM ============================================
echo [1/8] Deleting service %SERVICE_NAME%...
kubectl delete service %SERVICE_NAME% --ignore-not-found=true
if errorlevel 1 (
    echo [WARN] Failed to delete service. Continuing...
)

REM ============================================
REM Step 2: Delete Deployment
REM ============================================
echo.
echo [2/8] Deleting deployment %DEPLOYMENT_NAME%...
kubectl delete deployment %DEPLOYMENT_NAME% --ignore-not-found=true
if errorlevel 1 (
    echo [WARN] Failed to delete deployment. Continuing...
)

REM ============================================
REM Step 3: Remove Local Docker Image
REM ============================================
echo.
echo [3/8] Removing local Docker image %LOCAL_IMAGE%...
docker rmi -f %LOCAL_IMAGE% 2>nul
if errorlevel 1 (
    echo [WARN] Local image not found or already removed. Continuing...
) else (
    echo [OK] Local image removed.
)

REM ============================================
REM Step 4: Maven Clean Package
REM ============================================
echo.
echo [4/8] Running mvnw clean package...
call mvnw.cmd clean package
if errorlevel 1 (
    echo [ERROR] Maven build failed. Aborting.
    exit /b 1
)

REM ============================================
REM Step 5: Docker Build
REM ============================================
echo.
echo [5/8] Building Docker image %LOCAL_IMAGE%...
docker build -f %DOCKERFILE% -t %LOCAL_IMAGE% .
if errorlevel 1 (
    echo [ERROR] Docker build failed. Aborting.
    exit /b 1
)

REM ============================================
REM Step 6: Apply Kubernetes Manifest
REM ============================================
echo.
echo [6/8] Applying %DEPLOY_FILE%...
kubectl apply -f %DEPLOY_FILE%
if errorlevel 1 (
    echo [ERROR] kubectl apply failed. Aborting.
    exit /b 1
)

REM ============================================
REM Step 7: Clean Minikube Image Cache
REM ============================================
echo.
echo [7/8] Clearing minikube cache and old image...
minikube cache delete %LOCAL_IMAGE% 2>nul
if errorlevel 1 (
    echo [WARN] minikube cache delete failed or cache empty. Continuing...
) else (
    echo [OK] Minikube cache deleted.
)

minikube image rm %LOCAL_IMAGE% 2>nul
if errorlevel 1 (
    echo [WARN] minikube image rm failed or image not present. Continuing...
) else (
    echo [OK] Image removed from minikube.
)

REM ============================================
REM Step 8: Load Image into Minikube
REM ============================================
echo.
echo [8/8] Loading image into minikube...
minikube image load %LOCAL_IMAGE% --overwrite
if errorlevel 1 (
    echo [ERROR] minikube image load failed.
    exit /b 1
)

REM ============================================
REM Restart Deployment to Pick Up New Image
REM ============================================
echo.
echo Restarting deployment to use freshly loaded image...
kubectl rollout restart deployment/%DEPLOYMENT_NAME%
if errorlevel 1 (
    echo [WARN] Rollout restart failed. You may need to manually delete pods.
) else (
    echo [OK] Rollout restarted.
)

echo.
echo Waiting for rollout to complete...
kubectl rollout status deployment/%DEPLOYMENT_NAME% --timeout=120s
if errorlevel 1 (
    echo [WARN] Rollout did not complete within timeout. Check pod status.
)

echo.
echo ============================================
echo  Deployment completed!
echo ============================================
endlocal
exit /b 0