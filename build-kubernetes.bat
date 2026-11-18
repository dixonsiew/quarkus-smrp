kubectl delete service smrp-quarkus
kubectl delete deployment smrp-quarkus

mvnw clean package

docker build -f src/main/docker/Dockerfile.jvm -t docker.io/wfsiew888/smrp-quarkus:1.0.0 .

docker push docker.io/wfsiew888/smrp-quarkus:1.0.0

kubectl apply -f deployment.yaml