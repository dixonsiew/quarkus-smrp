@echo off
setlocal EnableDelayedExpansion

REM ============================================
REM Configuration
REM ============================================
set IMAGE_NAME=docker.io/wfsiew888/smrp-quarkus
set IMAGE_TAG=1.0.0
set LOCAL_IMAGE=wfsiew888/smrp-quarkus:%IMAGE_TAG%
set SERVICE_NAME=smrp-quarkus
set DEPLOYMENT_NAME=smrp-quarkus
set DOCKERFILE=src/main/docker/Dockerfile.jvm
set DEPLOY_FILE=deployment.yaml

echo ============================================
echo  Starting deployment of %SERVICE_NAME%
echo ============================================
echo.

REM ============================================
REM Step 1: Delete Service
REM ============================================
echo [1/7] Deleting service %SERVICE_NAME%...
kubectl delete service %SERVICE_NAME% --ignore-not-found=true
if errorlevel 1 (
    echo [WARN] Failed to delete service. Continuing...
)

REM ============================================
REM Step 2: Delete Deployment
REM ============================================
echo.
echo [2/7] Deleting deployment %DEPLOYMENT_NAME%...
kubectl delete deployment %DEPLOYMENT_NAME% --ignore-not-found=true
if errorlevel 1 (
    echo [WARN] Failed to delete deployment. Continuing...
)

REM ============================================
REM Step 3: Remove Local Docker Image
REM ============================================
echo.
echo [3/7] Removing local Docker image %LOCAL_IMAGE%...
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
echo [4/7] Running mvnw clean package...
call mvnw.cmd clean package
if errorlevel 1 (
    echo [ERROR] Maven build failed. Aborting.
    exit /b 1
)

REM ============================================
REM Step 5: Docker Build
REM ============================================
echo.
echo [5/7] Building Docker image %IMAGE_NAME%:%IMAGE_TAG%...
docker build -f %DOCKERFILE% -t %IMAGE_NAME%:%IMAGE_TAG% .
if errorlevel 1 (
    echo [ERROR] Docker build failed. Aborting.
    exit /b 1
)

REM ============================================
REM Step 6: Docker Push
REM ============================================
echo.
echo [6/7] Pushing Docker image to registry...
docker push %IMAGE_NAME%:%IMAGE_TAG%
if errorlevel 1 (
    echo [ERROR] Docker push failed. Aborting.
    exit /b 1
)

REM ============================================
REM Step 7: Apply Deployment
REM ============================================
echo.
echo [7/7] Applying %DEPLOY_FILE%...
kubectl apply -f %DEPLOY_FILE%
if errorlevel 1 (
    echo [ERROR] kubectl apply failed.
    exit /b 1
)

echo.
echo ============================================
echo  Deployment completed successfully!
echo ============================================
endlocal
exit /b 0