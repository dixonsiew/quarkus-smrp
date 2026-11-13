kubectl delete service smrp-quarkus
kubectl delete deployment smrp-quarkus

mvnw clean package "-Dquarkus.profile=kubernetes" "-Dquarkus.container-image.build=true" "-Dquarkus.kubernetes.deploy=true"

@REM  kubectl apply -f target/kubernetes/kubernetes.yml