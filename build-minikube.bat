kubectl delete service smrp-quarkus
kubectl delete deployment smrp-quarkus

mvnw clean package "-Dquarkus.profile=minikube" "-Dquarkus.container-image.build=true" "-Dquarkus.kubernetes.deploy=false"

kubectl apply -f target/kubernetes/minikube.yml

minikube cache delete docker.io/wfsiew888/smrp-quarkus:1.0.0
minikube image rm docker.io/wfsiew888/smrp-quarkus:1.0.0

minikube image load docker.io/wfsiew888/smrp-quarkus:1.0.0 --overwrite