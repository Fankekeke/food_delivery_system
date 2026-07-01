
<template>
  <div class="center-container" style="width: 100%">
    <a-row :gutter="24">
      <a-col :span="16">
        <a-card :bordered="false" class="order-card-wrapper">
          <div slot="title" class="card-title-section">
            <a-icon type="thunderbolt" class="title-icon" />
            <span>待接单订单</span>
            <a-badge :count="orderList.length" :numberStyle="{ backgroundColor: '#1890ff' }" />
          </div>

          <a-spin :spinning="loading">
            <div v-if="orderList.length === 0 && !loading" class="empty-state">
              <a-empty description="暂无可接订单" />
            </div>

            <div v-else class="order-list">
              <div v-for="order in orderList" :key="order.id" class="order-card">
                <div class="order-header">
                  <span class="order-code">
                    <a-icon type="shopping-cart" style="margin-right: 8px;" />
                    {{ order.code }}
                  </span>
                  <a-tag color="orange">{{ getStatusText(order.status) }}</a-tag>
                </div>
                <div class="order-body">
                  <a-row :gutter="16">
                    <a-col :span="12">
                      <p><a-icon type="user" style="margin-right: 6px; color: #1890ff;" /><strong>客户：</strong>{{ order.userName }}</p>
                      <p><a-icon type="shop" style="margin-right: 6px; color: #faad14;" /><strong>商家：</strong>{{ order.merchantName }}</p>
                    </a-col>
                    <a-col :span="12">
                      <p><a-icon type="clock-circle" style="margin-right: 6px; color: #52c41a;" /><strong>下单时间：</strong>{{ order.createDate }}</p>
                      <p><a-icon type="pay-circle" style="margin-right: 6px; color: #ff4d4f;" /><strong>支付时间：</strong>{{ order.payDate || '未支付' }}</p>
                    </a-col>
                  </a-row>
                  <a-row :gutter="16" style="margin-top: 8px;">
                    <a-col :span="12">
                      <div class="address-item">
                        <a-icon type="home" style="color: #52c41a;" />
                        <div class="address-content">
                          <span class="address-label">取餐地址</span>
                          <span class="address-text">{{ order.startAddress || '- -' }}</span>
                        </div>
                      </div>
                    </a-col>
                    <a-col :span="12">
                      <div class="address-item">
                        <a-icon type="environment" style="color: #ff4d4f;" />
                        <div class="address-content">
                          <span class="address-label">送餐地址</span>
                          <span class="address-text">{{ order.endAddress || '- -' }}</span>
                        </div>
                      </div>
                    </a-col>
                  </a-row>
                </div>
                <div class="order-footer">
                  <a-row :gutter="16">
                    <a-col :span="8">
                      <div class="price-info">
                        <div class="price-label">订单金额</div>
                        <div class="price-value">¥{{ order.orderPrice }}</div>
                      </div>
                    </a-col>
                    <a-col :span="8">
                      <div class="price-info">
                        <div class="price-label">优惠金额</div>
                        <div class="price-value discount">-¥{{ order.discount }}</div>
                      </div>
                    </a-col>
                    <a-col :span="8">
                      <div class="price-info">
                        <div class="price-label">实付金额</div>
                        <div class="price-value final">¥{{ order.afterOrderPrice }}</div>
                      </div>
                    </a-col>
                  </a-row>
                  <a-row :gutter="16" style="margin-top: 12px;">
                    <a-col :span="8">
                      <div class="info-item">
                        <a-icon type="environment" />
                        <span>{{ order.kilometre }} km</span>
                      </div>
                    </a-col>
                    <a-col :span="8">
                      <div class="info-item">
                        <a-icon type="car" />
                        <span>¥{{ order.distributionPrice }}</span>
                      </div>
                    </a-col>
                    <a-col :span="8">
                      <div class="info-item">
                        <a-icon type="gift" />
                        <span>{{ order.integral }}分</span>
                      </div>
                    </a-col>
                  </a-row>
                  <div class="order-action">
                    <a-button type="primary" icon="check" @click="acceptOrder(order)">
                      接单
                    </a-button>
                  </div>
                </div>
              </div>
            </div>
          </a-spin>
        </a-card>
      </a-col>

      <a-col :span="8">
        <a-card :bordered="false" class="location-card">
          <div slot="title" class="card-title-section">
            <a-icon type="compass" class="title-icon" />
            <span>我的位置</span>
          </div>
          <div class="location-info">
            <div class="location-status">
              <a-icon type="aim" style="font-size: 32px; color: #1890ff;" />
              <p v-if="currentLocation.lng && currentLocation.lat" class="location-text">
                经度：{{ currentLocation.lng }}<br/>
                纬度：{{ currentLocation.lat }}
              </p>
              <p v-else class="location-text">正在获取位置...</p>
            </div>
            <a-button type="primary" icon="reload" @click="getLocation" :loading="locating" block style="margin-top: 16px;">
              刷新位置
            </a-button>
          </div>
        </a-card>

        <a-card :bordered="false" class="recommend-card" style="margin-top: 16px;">
          <div slot="title" class="card-title-section">
            <a-icon type="fire" class="title-icon" />
            <span>推荐路线</span>
            <a-tooltip title="基于蚁群算法，综合考虑配送距离、配送收益，为配送员规划最优接单顺序">
              <a-icon type="question-circle" style="color: #999; cursor: pointer;" />
            </a-tooltip>
          </div>
          <div v-if="!currentLocation.lng || !currentLocation.lat" class="empty-state">
            <a-empty description="请先获取位置" />
          </div>
          <div v-else-if="recommendedOrders.length === 0" class="empty-state">
            <a-empty description="暂无推荐订单" />
          </div>
          <div v-else class="recommend-list">
            <div v-for="(order, index) in recommendedOrders" :key="'rec-' + order.id" class="recommend-item" :class="{ 'recommend-first': index === 0 }">
              <div class="recommend-header">
                <div class="recommend-rank">
                  <span class="rank-badge" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
                  <span class="recommend-code">{{ order.code }}</span>
                </div>
                <a-tag :color="index === 0 ? 'red' : 'blue'">{{ order._totalDist ? order._totalDist.toFixed(1) : order.kilometre }} km</a-tag>
              </div>
              <div class="recommend-body">
                <p><a-icon type="home" style="margin-right: 4px; color: #52c41a;" />{{ order.startAddress || '- -' }}</p>
                <p><a-icon type="environment" style="margin-right: 4px; color: #ff4d4f;" />{{ order.endAddress || '- -' }}</p>
                <div class="recommend-meta">
                  <span><a-icon type="car" /> {{ order.kilometre }}公里</span>
                  <span><a-icon type="dollar" /> ¥{{ order.distributionPrice }}</span>
                </div>
              </div>
<!--              <a-button size="small" type="primary" icon="check" @click="acceptOrder(order)" block style="margin-top: 15px">-->
<!--                接单-->
<!--              </a-button>-->
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>import {mapState} from 'vuex'

export default {
  name: "Center",
  data() {
    return {
      orderList: [],
      loading: false,
      locating: false,
      currentLocation: {
        lng: null,
        lat: null
      },
      recommendedOrders: []
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  watch: {
    orderList: {
      handler() {
        this.computeRecommendation()
      },
      deep: true
    }
  },
  mounted() {
    this.queryOrderList()
    this.getLocation()
  },
  methods: {
    queryOrderList() {
      this.loading = true
      this.$get(`/cos/order-info/queryOrderWorkList`).then((r) => {
        if (r.data && r.data.data) {
          this.orderList = r.data.data
        }
      }).catch(() => {
        this.$message.error('获取订单列表失败')
      }).finally(() => {
        this.loading = false
      })
    },
    getLocation() {
      this.locating = true
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            this.currentLocation.lng = position.coords.longitude
            this.currentLocation.lat = position.coords.latitude
            this.locating = false
            this.$message.success('定位成功')
            this.computeRecommendation()
          },
          (error) => {
            console.error('定位失败:', error)
            this.$message.warning('定位失败，请检查浏览器定位权限')
            this.locating = false
          },
          {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
          }
        )
      } else {
        this.$message.warning('您的浏览器不支持定位功能')
        this.locating = false
      }
    },
    haversineDistance(lng1, lat1, lng2, lat2) {
      if (!lng1 || !lat1 || !lng2 || !lat2) return Infinity
      const R = 6371
      const dLat = (lat2 - lat1) * Math.PI / 180
      const dLng = (lng2 - lng1) * Math.PI / 180
      const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
        Math.sin(dLng / 2) * Math.sin(dLng / 2)
      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
      return R * c
    },
    computeRecommendation() {
      if (!this.currentLocation.lng || !this.currentLocation.lat || this.orderList.length === 0) {
        this.recommendedOrders = []
        return
      }
      const validOrders = this.orderList.filter(o => o.startLongitude && o.startLatitude)
      if (validOrders.length === 0) {
        this.recommendedOrders = []
        return
      }
      this.recommendedOrders = this.antColonyOptimization(validOrders)
    },
    antColonyOptimization(orders) {
      const antCount = 20
      const maxIterations = 50
      const alpha = 1.0
      const beta = 2.0
      const rho = 0.1
      const Q = 100

      const curLng = this.currentLocation.lng
      const curLat = this.currentLocation.lat
      const n = orders.length

      const distToStart = []
      const startToEnd = []
      const startToStart = []
      for (let i = 0; i < n; i++) {
        distToStart[i] = this.haversineDistance(curLng, curLat, orders[i].startLongitude, orders[i].startLatitude)
        startToEnd[i] = this.haversineDistance(orders[i].startLongitude, orders[i].startLatitude, orders[i].endLongitude, orders[i].endLatitude)
        startToStart[i] = []
        for (let j = 0; j < n; j++) {
          if (i === j) {
            startToStart[i][j] = 0
          } else {
            startToStart[i][j] = this.haversineDistance(orders[i].endLongitude, orders[i].endLatitude, orders[j].startLongitude, orders[j].startLatitude)
          }
        }
      }

      let pheromone = []
      for (let i = 0; i < n; i++) {
        pheromone[i] = []
        for (let j = 0; j < n; j++) {
          pheromone[i][j] = 1.0
        }
      }
      let pheromoneFromCur = new Array(n).fill(1.0)

      let bestPath = null
      let bestScore = -Infinity

      for (let iter = 0; iter < maxIterations; iter++) {
        let allPaths = []
        let allScores = []

        for (let ant = 0; ant < antCount; ant++) {
          let visited = new Array(n).fill(false)
          let path = []
          let current = -1
          let totalDist = 0
          let totalEarning = 0

          for (let step = 0; step < n; step++) {
            let probs = []
            let totalProb = 0

            for (let j = 0; j < n; j++) {
              if (visited[j]) {
                probs.push(0)
                continue
              }
              let dist
              if (current === -1) {
                dist = distToStart[j] + startToEnd[j]
              } else {
                dist = startToStart[current][j] + startToEnd[j]
              }
              dist = Math.max(dist, 0.01)

              let earning = orders[j].distributionPrice + orders[j].afterOrderPrice * 0.05
              let eta = earning / dist

              let tau
              if (current === -1) {
                tau = pheromoneFromCur[j]
              } else {
                tau = pheromone[current][j]
              }

              let prob = Math.pow(tau, alpha) * Math.pow(eta, beta)
              probs.push(prob)
              totalProb += prob
            }

            let next = -1
            if (totalProb > 0) {
              let r = Math.random() * totalProb
              let cumProb = 0
              for (let j = 0; j < n; j++) {
                cumProb += probs[j]
                if (cumProb >= r) {
                  next = j
                  break
                }
              }
            } else {
              let unvisited = []
              for (let j = 0; j < n; j++) {
                if (!visited[j]) unvisited.push(j)
              }
              next = unvisited[Math.floor(Math.random() * unvisited.length)]
            }

            if (current === -1) {
              totalDist += distToStart[next]
            } else {
              totalDist += startToStart[current][next]
            }
            totalDist += startToEnd[next]
            totalEarning += orders[next].distributionPrice

            visited[next] = true
            path.push(next)
            current = next
          }

          let score = totalEarning / Math.max(totalDist, 0.01)
          allPaths.push(path)
          allScores.push(score)

          if (score > bestScore) {
            bestScore = score
            bestPath = [...path]
          }
        }

        pheromoneFromCur = pheromoneFromCur.map(t => t * (1 - rho))
        for (let i = 0; i < n; i++) {
          pheromone[i] = pheromone[i].map(t => t * (1 - rho))
        }

        for (let a = 0; a < antCount; a++) {
          let path = allPaths[a]
          let delta = Q * allScores[a] / antCount

          pheromoneFromCur[path[0]] += delta
          for (let i = 0; i < path.length - 1; i++) {
            pheromone[path[i]][path[i + 1]] += delta
          }
        }
      }

      if (!bestPath) return []

      let currentIdx = -1
      let totalDist = 0
      let result = []
      for (let i = 0; i < bestPath.length; i++) {
        let orderIdx = bestPath[i]
        let d
        if (currentIdx === -1) {
          d = distToStart[orderIdx]
        } else {
          d = startToStart[currentIdx][orderIdx]
        }
        totalDist += d + startToEnd[orderIdx]
        currentIdx = orderIdx
        result.push({
          ...orders[orderIdx],
          _totalDist: totalDist
        })
      }

      return result.slice(0, 5)
    },
    acceptOrder(order) {
      this.$confirm({
        title: '确认接单',
        content: `是否接受订单 ${order.code} 的配送任务？`,
        centered: true,
        onOk: () => {
          this.$get('/cos/order-info/acceptOrder', {
            orderId: order.id,
            staffId: this.currentUser.userId
          }).then((r) => {
            this.$message.success('接单成功')
            this.queryOrderList()
          })
        }
      })
    },
    getStatusText(status) {
      const statusMap = {
        '0': '待支付',
        '1': '已支付',
        '2': '配送中',
        '3': '已完成',
        '4': '已取消',
        '5': '退款中'
      }
      return statusMap[status] || '未知状态'
    }
  },
}
</script>

<style scoped>.center-container {
  padding: 16px;
}

.card-title-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 20px;
  color: #1890ff;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.order-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.order-card {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-code {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.order-body {
  padding: 12px 0;
}

.order-body p {
  margin-bottom: 6px;
  font-size: 13px;
  color: #555;
}

.address-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 12px;
  color: #666;
  padding: 8px;
  background-color: #fafafa;
  border-radius: 4px;
}

.address-item .anticon {
  margin-top: 2px;
  font-size: 14px;
}

.address-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.address-label {
  font-size: 11px;
  color: #999;
  font-weight: 500;
}

.address-text {
  font-size: 12px;
  color: #555;
  word-break: break-all;
  line-height: 1.4;
}

.order-footer {
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.price-info {
  text-align: center;
  padding: 10px;
  background-color: #fafafa;
  border-radius: 6px;
}

.price-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 6px;
}

.price-value {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.price-value.discount {
  color: #ff4d4f;
}

.price-value.final {
  color: #52c41a;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 13px;
}

.info-item i {
  color: #1890ff;
}

.order-action {
  margin-top: 16px;
  text-align: right;
}

.location-card {
  margin-bottom: 0;
}

.location-info {
  text-align: center;
  padding: 16px 0;
}

.location-status {
  padding: 20px;
  background: linear-gradient(135deg, #e6f7ff 0%, #f0f5ff 100%);
  border-radius: 8px;
}

.location-text {
  margin-top: 12px;
  font-size: 14px;
  color: #555;
  line-height: 1.8;
}

.recommend-card {
  margin-top: 16px;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommend-item {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 12px;
  transition: all 0.3s;
}

.recommend-item:hover {
  border-color: #1890ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.15);
}

.recommend-first {
  border-color: #52c41a;
  background-color: #f6ffed;
}

.recommend-first:hover {
  border-color: #52c41a;
  box-shadow: 0 2px 8px rgba(82, 196, 23, 0.25);
}

.recommend-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.recommend-rank {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  font-size: 12px;
  font-weight: bold;
  color: #fff;
  background-color: #1890ff;
}

.rank-1 {
  background-color: #f5222d;
}

.rank-2 {
  background-color: #fa8c16;
}

.rank-3 {
  background-color: #faad14;
}

.recommend-code {
  font-size: 13px;
  font-weight: 500;
  color: #333;
}

.recommend-body p {
  margin-bottom: 4px;
  font-size: 12px;
  color: #666;
  word-break: break-all;
}

.recommend-meta {
  display: flex;
  gap: 16px;
  margin-top: 6px;
  font-size: 12px;
  color: #888;
}

.recommend-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
