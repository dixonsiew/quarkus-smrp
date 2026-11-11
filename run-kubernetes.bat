start "" kubernetes\kafka\run.bat
start "" kubernetes\mongodb\run.bat
start "" kubernetes\postgres\run.bat
start "" kubernetes\redis\run.bat
kubectl port-forward service/smrp 8300:80 --address="0.0.0.0"