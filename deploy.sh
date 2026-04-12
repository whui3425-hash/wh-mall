#!/bin/bash

# =============================================================================
#  Mall SaaS 系统一键部署脚本
# =============================================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'
BOLD='\033[1m'

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_stage() {
    echo -e ""
    echo -e "${CYAN}========================================${NC}"
    echo -e "${CYAN}  $1${NC}"
    echo -e "${CYAN}========================================${NC}"
}

error_exit() {
    log_error "$1"
    echo -e "${RED}========================================${NC}"
    echo -e "${RED}  部署失败！${NC}"
    echo -e "${RED}========================================${NC}"
    exit 1
}

check_command() {
    if ! command -v "$1" &> /dev/null; then
        error_exit "$1 未安装"
    fi
}

# 获取服务器 IP
SERVER_IP=$(hostname -I | awk '{print $1}')

# =============================================================================
#  主程序开始
# =============================================================================

clear
echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}     Mall SaaS 系统一键部署脚本${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""
log_info "部署开始时间: $(date '+%Y-%m-%d %H:%M:%S')"
echo ""

# =============================================================================
#  【第一阶段：环境检查】
# =============================================================================
log_stage "第一阶段：环境检查"

log_info "检查 Docker..."
check_command docker
check_command docker-compose
log_success "Docker 检查通过"

log_info "检查 Maven..."
check_command mvn
log_success "Maven 检查通过"

# =============================================================================
#  【第二阶段：创建配置文件】
# =============================================================================
log_stage "第二阶段：创建配置文件"

# 创建 .env 文件（使用 IP 地址）
cat > .env << EOF
NACOS_HOST=${SERVER_IP}
MYSQL_HOST=${SERVER_IP}
MYSQL_USERNAME=root
MYSQL_PASSWORD=123456
EOF
log_success ".env 文件已创建 (IP: ${SERVER_IP})"

# 创建 docker-compose-env.yml（Nacos 开启认证）
cat > deploy/docker-compose-env.yml << EOF
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: mall-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      TZ: Asia/Shanghai
    ports:
      - "3306:3306"
    volumes:
      - ./mysql-data:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d
    networks:
      - mall-net
  nacos:
    image: nacos/nacos-server:v2.2.3
    container_name: mall-nacos
    restart: always
    environment:
      - PREFER_HOST_MODE=hostname
      - MODE=standalone
      - JVM_XMS=512m
      - JVM_XMX=512m
      - NACOS_AUTH_ENABLE=false
    ports:
      - "8848:8848"
      - "9848:9848"
      - "9849:9849"
    networks:
      - mall-net
networks:
  mall-net:
    driver: bridge
EOF
log_success "docker-compose-env.yml 已创建"

# =============================================================================
#  【第三阶段：清理旧环境】
# =============================================================================
log_stage "第三阶段：清理旧环境"

log_info "停止旧容器..."
docker-compose down 2>/dev/null || true
docker-compose -f deploy/docker-compose-env.yml down 2>/dev/null || true

log_info "清理旧数据..."
rm -rf deploy/mysql-data 2>/dev/null || true
docker network rm mall-net 2>/dev/null || true

log_success "旧环境清理完成"

# =============================================================================
#  【第四阶段：启动基础服务】
# =============================================================================
log_stage "第四阶段：启动基础服务 (MySQL + Nacos)"

log_info "复制图片资源..."
if [ -d "mall-store-web/public/images" ]; then
    mkdir -p deploy/images
    cp -r mall-store-web/public/images/* deploy/images/ 2>/dev/null || true
    log_success "图片资源已复制"
fi

log_info "启动 MySQL 和 Nacos..."
docker-compose -f deploy/docker-compose-env.yml up -d

log_info "等待 MySQL 初始化 (60秒)..."
for i in $(seq 60 -1 1); do
    echo -ne "${YELLOW}  剩余 ${i} 秒...\r${NC}"
    sleep 1
done
echo -e "${GREEN}  MySQL 初始化完成                     ${NC}"

log_info "等待 Nacos 启动 (最多120秒)..."
for i in $(seq 1 24); do
    if curl -s http://${SERVER_IP}:8848/nacos > /dev/null 2>&1; then
        log_success "Nacos 已就绪"
        break
    fi
    echo -ne "${YELLOW}  尝试 ${i}/24...\r${NC}"
    sleep 5
done

# 检查 Nacos 是否真的启动了
if ! curl -s http://${SERVER_IP}:8848/nacos > /dev/null 2>&1; then
    error_exit "Nacos 启动超时，请检查日志: docker logs mall-nacos"
fi

# =============================================================================
#  【第五阶段：编译后端代码】
# =============================================================================
log_stage "第六阶段：编译后端代码"

log_info "开始 Maven 编译 (可能需要 5-10 分钟)..."
if mvn clean package -DskipTests -q; then
    log_success "后端代码编译成功"
else
    error_exit "Maven 编译失败"
fi

# =============================================================================
#  【第七阶段：启动业务集群】
# =============================================================================
log_stage "第七阶段：启动业务集群"

log_info "构建并启动所有业务服务..."
docker-compose up -d --build

log_info "等待服务注册 (60秒)..."
for i in $(seq 60 -1 1); do
    echo -ne "${YELLOW}  剩余 ${i} 秒...\r${NC}"
    sleep 1
done
echo -e "${GREEN}  服务注册完成                     ${NC}"

# =============================================================================
#  【第八阶段：完成提示】
# =============================================================================
log_stage "部署完成"

echo ""
echo -e "${GREEN}${BOLD}========================================${NC}"
echo -e "${GREEN}${BOLD}      部署成功！${NC}"
echo -e "${GREEN}${BOLD}========================================${NC}"
echo ""
echo -e "${CYAN}  系统访问地址:${NC}"
echo -e "    🛒 C端买家商城: ${YELLOW}http://${SERVER_IP}:80${NC}"
echo -e "    ⚙️  B端管理后台: ${YELLOW}http://${SERVER_IP}:8080${NC}"
echo -e "    🔌 API网关地址: ${YELLOW}http://${SERVER_IP}:8088${NC}"
echo -e "    📊 Nacos控制台:   ${YELLOW}http://${SERVER_IP}:8848/nacos${NC} (nacos/nacos)"
echo ""
echo -e "${CYAN}  测试账号:${NC}"
echo -e "    👤 C端买家: zhangsan / 123456"
echo -e "    👨‍💼 B端管理员: admin / admin123"
echo ""
echo -e "${CYAN}  常用命令:${NC}"
echo -e "    查看日志: ${YELLOW}docker-compose logs -f [服务名]${NC}"
echo -e "    停止服务: ${YELLOW}docker-compose down${NC}"
echo ""
log_info "部署完成时间: $(date '+%Y-%m-%d %H:%M:%S')"
echo ""

exit 0
