# test with k8s (kind) on M1 Mac

## pre requirements

- Docker
- kind

## setup cluster

```bash
cat <<EOF | kind create cluster --image rossgeorgiev/kind-node-arm64:v1.20.0 --config -
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
- role: control-plane
  extraPortMappings:
  - containerPort: 30443
    hostPort: 30443
- role: worker
- role: worker
EOF
```

## import images

```
cd $BOOK_HOME/Chapter16
./gradlew build && docker-compose build
for i in `docker images | grep hands-on | awk '{print $1}'`;do kind load docker-image ${i}:latest ;done
```

## create app for dev env

install
```
for f in kubernetes/helm/components/*; do helm dep up $f; done
for f in kubernetes/helm/environments/*; do helm dep up $f; done
helm install hands-on-dev-env kubernetes/helm/environments/dev-env -n hands-on --create-namespace
```

check result
```
kubectl get pod -n hands-on
curl https://127.0.0.1:30443/actuator/health -k | jq .
HOST=127.0.0.1 PORT=30443 USE_K8S=true ./test-em-all.bash
```
