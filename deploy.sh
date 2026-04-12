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
#  【第一阶段：启动基础环境】
# =============================================================================
log_stage "第一阶段：启动基础环境 (MySQL + Nacos)"

log_info "检查 Docker 环境..."
check_command docker
check_command docker-compose
log_success "Docker 环境检查通过"

echo ""
log_info "启动基础环境容器 (MySQL + Nacos)..."
if docker-compose -f deploy/docker-compose-env.yml up -d; then
    log_success "基础环境容器启动成功"
else
    error_exit "基础环境容器启动失败"
fi

echo ""
log_info "准备图片资源..."
if [ -d "mall-store-web/public/images" ]; then
    mkdir -p deploy/images
    cp -r mall-store-web/public/images/* deploy/images/
    log_success "图片资源已复制到 deploy/images/"
else
    log_warn "mall-store-web/public/images 目录不存在，跳过图片复制"
fi

echo ""
log_info "MySQL 将自动执行 /docker-entrypoint-initdb.d/ 目录下的 SQL 脚本..."
log_info "SQL 文件: deploy/sql/mall_init.sql"

echo ""
log_warn "正在等待 MySQL 和 Nacos 初始化，休眠 30 秒..."
log_warn "这是为了确保数据库有足够的时间完成建表操作"
for i in $(seq 30 -1 1); do
    echo -ne "${YELLOW}  剩余等待时间: ${i} 秒...\r${NC}"
    sleep 1
done
echo -e "${GREEN}  等待完成，继续执行...             ${NC}"

# =============================================================================
#  【第二阶段：编译后端代码】
# =============================================================================
echo ""
log_stage "第二阶段：编译后端代码"

log_info "检查 Maven 环境..."
check_command mvn
log_success "Maven 环境检查通过"

log_info "Maven 版本: $(mvn -v | head -1)"

echo ""
log_info "开始编译后端代码 (mvn clean package -DskipTests)..."
log_info "编译过程可能需要 3-5 分钟，请耐心等待..."

if mvn clean package -DskipTests; then
    echo ""
    log_success "后端代码编译成功！"
else
    echo ""
    error_exit "后端代码编译失败！请检查 Maven 构建日志"
fi

# =============================================================================
#  【第三阶段：拉起业务集群】
# =============================================================================
echo ""
log_stage "第三阶段：拉起业务集群"

log_info "构建并启动所有业务服务..."
log_info "包含: Nginx前端(C端/B端)、Gateway网关、用户/商品/订单/购物车/支付/权限服务"

if docker-compose up -d --build; then
    echo ""
    log_success "业务集群启动成功！"
else
    echo ""
    error_exit "业务集群启动失败！请检查 Docker 日志"
fi

# =============================================================================
#  【第四阶段：完成提示】
# =============================================================================
echo ""
log_stage "第四阶段：部署完成"

echo ""
echo -e "${GREEN}${BOLD}========================================${NC}"
echo -e "${GREEN}${BOLD}      部署成功！系统已正常运行${NC}"
echo -e "${GREEN}${BOLD}========================================${NC}"
echo ""
echo -e "${CYAN}  系统访问地址:${NC}"
echo -e "    🛒 C端买家商城: ${YELLOW}http://localhost:80${NC}"
echo -e "    ⚙️  B端管理后台: ${YELLOW}http://localhost:8080${NC}"
echo -e "    🔌 API网关地址: ${YELLOW}http://localhost:8088${NC}"
echo ""
echo -e "${CYAN}  默认测试账号:${NC}"
echo -e "    👤 C端买家: username=zhangsan, password=123456"
echo -e "    👨‍💼 B端管理员: username=admin, password=admin123"
echo ""
echo -e "${CYAN}  常用命令:${NC}"
echo -e "    查看日志: ${YELLOW}docker-compose logs -f [服务名]${NC}"
echo -e "    停止服务: ${YELLOW}docker-compose down${NC}"
echo -e "    重启服务: ${YELLOW}docker-compose restart [服务名]${NC}"
echo ""
log_info "部署完成时间: $(date '+%Y-%m-%d %H:%M:%S')"
echo ""
echo -e "${GREEN}感谢使用 Mall SaaS 系统，祝您使用愉快！${NC}"
echo ""

exit 0
