kubectl delete service smrp
kubectl delete deployment smrp

mvnw clean package "-Dquarkus.profile=kubernetes" "-Dquarkus.container-image.build=true" "-Dquarkus.kubernetes.deploy=true"

kubectl apply -f target/kubernetes/kubernetes.yml