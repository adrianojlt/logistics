


## docker

```Bash
./builder.sh all
```

### BackEnd

```Bash
sudo docker buildx build --platform linux/amd64 -t dachser-backend .
```
```Bash
sudo docker tag dachser-backend 192.168.1.3:5005/dachser-backend:latest
```
```Bash
sudo docker push 192.168.1.3:5005/dachser-backend:latest
```
Command to run on the machine that will run the backend app:
```Bash
sudo docker run -e SPRING_PROFILES_ACTIVE=prod -e DB_USERNAME=logistics -e DB_PASSWORD=logistics1pass -e JWT_SECRET=dachser-logistics-super-secret-key-32-chars-min --network mariadb_cdc_default -d -p 8333:8333 --name dachser-backend 192.168.1.3:5005/dachser-backend
```
### FrontEnd

```Bash
sudo docker buildx build --platform linux/amd64 -t dachser-frontend ./frontend
```
```Bash
sudo docker tag dachser-frontend 192.168.1.3:5005/dachser-frontend:latest
```
```Bash
sudo docker push 192.168.1.3:5005/dachser-frontend:latest
```
Command to run on the machine that will run the frontend app:
```Bash
sudo docker run -d -p 4200:80 --name dachser-frontend 192.168.1.3:5005/dachser-frontend
```


