#!/bin/bash

# =============================================================================
#  Mall SaaS 系统一键部署脚本
# =============================================================================
# 使用说明:
#   1. 赋予执行权限: chmod +x deploy.sh
#   2. 执行部署: ./deploy.sh
# =============================================================================

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color
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

# 错误处理函数
error_exit() {
    log_error "$1"
    echo -e "${RED}========================================${NC}"
    echo -e "${RED}  部署失败！请检查上述错误信息${NC}"
    echo -e "${RED}========================================${NC}"
    exit 1
}

# 检查命令是否存在
check_command() {
    if ! command -v "$1" &> /dev/null; then
        error_exit "$1 未安装，请先安装 $1"
    fi
}

# 等待服务健康
wait_for_service() {
    local service_name=$1
    local url=$2
    local max_attempts=${3:-30}
    
    log_info "等待 $service_name 就绪..."
    for i in $(seq 1 $max_attempts); do
        if curl -s "$url" > /dev/null 2>&1; then
            log_success "$service_name 已就绪！"
            return 0
        fi
        echo -ne "${YELLOW}  尝试 ${i}/${max_attempts}...\r${NC}"
        sleep 2
    done
    error_exit "$service_name 启动超时"
}

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

log_info "检查 Docker 环境..."
check_command docker
check_command docker-compose
log_success "Docker 环境检查通过"

log_info "检查 Maven 环境..."
check_command mvn
log_success "Maven 环境检查通过"

# 检查 .env 文件
if [ ! -f ".env" ]; then
    log_warn ".env 文件不存在，创建默认配置..."
    cat > .env << 'EOF'
NACOS_HOST=mall-nacos
MYSQL_HOST=mall-mysql
MYSQL_USERNAME=root
MYSQL_PASSWORD=123456
EOF
    log_success ".env 文件已创建"
fi

# =============================================================================
#  【第二阶段：清理旧环境】
# =============================================================================
log_stage "第二阶段：清理旧环境"

log_info "停止旧容器..."
docker-compose down 2>/dev/null || true
docker-compose -f deploy/docker-compose-env.yml down 2>/dev/null || true

log_info "清理旧数据..."
rm -rf deploy/mysql-data 2>/dev/null || true

docker network rm mall-net 2>/dev/null || true
log_success "旧环境清理完成"

# =============================================================================
#  【第三阶段：启动基础服务】
# =============================================================================
log_stage "第三阶段：启动基础服务 (MySQL + Nacos)"

log_info "复制图片资源..."
if [ -d "mall-store-web/public/images" ]; then
    mkdir -p deploy/images
    cp -r mall-store-web/public/images/* deploy/images/ 2>/dev/null || true
    log_success "图片资源已复制"
else
    log_warn "图片目录不存在，跳过"
fi

log_info "启动 MySQL 和 Nacos..."
docker-compose -f deploy/docker-compose-env.yml up -d

log_info "等待 MySQL 初始化 (60秒)..."
for i in $(seq 60 -1 1); do
    echo -ne "${YELLOW}  剩余 ${i} 秒...\r${NC}"
    sleep 1
done
echo -e "${GREEN}  MySQL 初始化完成                     ${NC}"

# 等待 Nacos 就绪
wait_for_service "Nacos" "http://localhost:8848/nacos" 30

# 创建 Nacos 默认用户
log_info "创建 Nacos 默认用户..."
sleep 5
if docker exec mall-nacos curl -s -X POST 'http://localhost:8848/nacos/v1/auth/users/register' \
    -d 'username=nacos&password=nacos' > /dev/null 2>&1; then
    log_success "Nacos 用户创建成功"
else
    log_warn "Nacos 用户可能已存在或注册接口不可用，继续部署..."
fi

# =============================================================================
#  【第四阶段：编译后端代码】
# =============================================================================
log_stage "第四阶段：编译后端代码"

log_info "开始 Maven 编译 (可能需要 5-10 分钟)..."
if mvn clean package -DskipTests -q; then
    log_success "后端代码编译成功！"
else
    error_exit "Maven 编译失败"
fi

# =============================================================================
#  【第五阶段：启动业务集群】
# =============================================================================
log_stage "第五阶段：启动业务集群"

log_info "构建并启动所有业务服务..."
docker-compose up -d --build

log_info "等待服务注册到 Nacos (60秒)..."
for i in $(seq 60 -1 1); do
    echo -ne "${YELLOW}  剩余 ${i} 秒...\r${NC}"
    sleep 1
done
echo -e "${GREEN}  服务注册完成                     ${NC}"

# 检查关键服务是否注册
log_info "检查服务注册状态..."
SERVICES=("mall-gateway" "mall-user" "mall-goods" "mall-order" "mall-cart" "mall-pay" "mall-permission")
for service in "${SERVICES[@]}"; do
    if curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=${service}" 2>/dev/null | grep -q "ip"; then
        log_success "  ✓ ${service} 已注册"
    else
        log_warn "  ✗ ${service} 未注册 (可能仍在启动中)"
    fi
done

# =============================================================================
#  【第六阶段：完成提示】
# =============================================================================
log_stage "部署完成"

SERVER_IP=$(hostname -I | awk '{print $1}')

echo ""
echo -e "${GREEN}${BOLD}========================================${NC}"
echo -e "${GREEN}${BOLD}      部署完成！${NC}"
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
echo -e "    重启服务: ${YELLOW}docker-compose restart [服务名]${NC}"
echo ""
log_info "部署完成时间: $(date '+%Y-%m-%d %H:%M:%S')"
echo ""

exit 0
