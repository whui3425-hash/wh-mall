# wh-mall

## Install build dependencies

```shell
# Ubuntu
sudo apt install -y maven openjdk-17-jdk

# CentOS
sudo yum install -y maven java-17-openjdk-devel
```

## Install Git and clone the repository

```shell
# Ubuntu/Debian
sudo apt install -y git
# CentOS/RHEL
sudo yum install -y git

git clone https://github.com/whui3425-hash/wh-mall.git /opt/wh-mall
cd /opt/wh-mall
```

## Configure environment variables

![img.png](mall-store-web%2Fpublic%2Fimg.png)

```shell
cd /opt/wh-mall
vi .env
```

## Deploy

```shell
sed -i 's/\r$//' deploy.sh
chmod +x deploy.sh
./deploy.sh
```

## Multi-tenant frontend (hosts)

Add the following lines to `/etc/hosts` (or your OS equivalent):

```shell
127.0.0.1 shop1.whmall.test
127.0.0.1 shop2.whmall.test
```

## Clean reinstall (after errors)

```shell
cd /opt/wh-mall

# 1. Stop and remove all containers
docker-compose down
docker-compose -f deploy/docker-compose-env.yml down

# 2. Remove data volumes (reinitialize the database)
rm -rf deploy/mysql-data

# 3. Remove old images (optional; forces a rebuild)
docker rmi $(docker images -q wh-mall-* 2>/dev/null) 2>/dev/null || true

# 4. Prune unused resources
docker system prune -f

# 5. Redeploy
./deploy.sh
```
