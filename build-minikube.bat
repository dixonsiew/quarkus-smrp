kubectl delete service smrp-quarkus
kubectl delete deployment smrp-quarkus

@REM  mvnw clean package "-Dquarkus.profile=minikube" "-Dquarkus.container-image.build=true" "-Dquarkus.kubernetes.deploy=false"

@REM  kubectl apply -f target/kubernetes/minikube.yml

@REM  minikube cache delete docker.io/wfsiew888/smrp-quarkus:1.0.0
@REM  minikube image rm docker.io/wfsiew888/smrp-quarkus:1.0.0

@REM  minikube image load docker.io/wfsiew888/smrp-quarkus:1.0.0 --overwrite

mvnw clean package

docker build -f src/main/docker/Dockerfile.jvm -t smrp-quarkus:1.0.0 .

kubectl apply -f minikube.yaml

minikube cache delete smrp-quarkus:1.0.0
minikube image rm smrp-quarkus:1.0.0

minikube image load smrp-quarkus:1.0.0 --overwrite