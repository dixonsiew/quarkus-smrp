@REM  start "" kubernetes\kafka\run.bat
@REM  start "" kubernetes\mongodb\run.bat
@REM  start "" kubernetes\postgres\run.bat
@REM  start "" kubernetes\redis\run.bat
kubectl port-forward service/smrp 8300:80 --address="0.0.0.0"