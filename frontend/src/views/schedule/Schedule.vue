
<template>
  <div class="schedule-container">
      <!-- 定时配送配置列表 -->
      <div v-if="scheduleList.length > 0" class="schedule-list-section">
        <div class="section-header-wrapper">
          <h3 class="section-title">
            <a-icon type="clock-circle" theme="filled" />
            我的时段配送配置
          </h3>
          <span class="section-subtitle">共 {{ scheduleList.length }} 个时段任务</span>
        </div>

        <div class="schedule-grid">
          <div
            v-for="schedule in scheduleList"
            :key="schedule.id"
            class="schedule-card"
          >
            <div class="card-header">
              <div class="header-left">
                <div class="goods-image">
                  <img :src="getGoodsImage(schedule.goodsImages)" :alt="schedule.goodsName">
                </div>
                <div class="goods-info">
                  <h4 class="goods-name">{{ schedule.goodsName }}</h4>
                  <div class="goods-spec">{{ schedule.portion }}</div>
                </div>
              </div>
              <a-popconfirm
                title="确定要删除这个定时配送配置吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="deleteSchedule(schedule.id)"
              >
                <a-button class="delete-btn" type="text" size="small">
                  <a-icon type="delete" />
                </a-button>
              </a-popconfirm>
            </div>

            <div class="card-body">
              <div class="info-row">
                <div class="info-label">
                  <a-icon type="shop" />
                  <span>商家</span>
                </div>
                <div class="info-value">{{ schedule.merchantName }}</div>
              </div>

              <div class="info-row">
                <div class="info-label">
                  <a-icon type="environment" />
                  <span>地址</span>
                </div>
                <div class="info-value">{{ schedule.address }}</div>
              </div>

              <div class="info-row">
                <div class="info-label">
                  <a-icon type="user" />
                  <span>联系人</span>
                </div>
                <div class="info-value">{{ schedule.contactPerson }} {{ schedule.contactMethod }}</div>
              </div>

              <div class="info-row">
                <div class="info-label">
                  <a-icon type="clock-circle" />
                  <span>配送时间</span>
                </div>
                <div class="info-value highlight">{{ schedule.taskTime }}</div>
              </div>

              <div class="info-row">
                <div class="info-label">
                  <a-icon type="sync" />
                  <span>配送频率</span>
                </div>
                <div class="info-value">
                  <a-tag color="blue">{{ getCronDescription(schedule.cronContent) }}</a-tag>
                </div>
              </div>

              <div class="info-row">
                <div class="info-label">
                  <a-icon type="shopping" />
                  <span>每次数量</span>
                </div>
                <div class="info-value">{{ schedule.takeNum }} 桶</div>
              </div>
            </div>

<!--            <div class="card-footer">-->
<!--              <div class="cron-expression">-->
<!--                <a-icon type="code" />-->
<!--                <span>Cron: {{ schedule.cronContent }}</span>-->
<!--              </div>-->
<!--            </div>-->
          </div>
        </div>
      </div>
    <!-- 商家列表视图 -->
    <div v-if="!selectedMerchant" class="merchants-view">
      <div class="page-header">
        <div class="header-decoration">
          <div class="deco-circle deco-1"></div>
          <div class="deco-circle deco-2"></div>
          <div class="deco-circle deco-3"></div>
        </div>
        <h2 class="page-title">
          <a-icon type="shop" theme="filled" />
          商家列表
        </h2>
        <p class="page-subtitle">选择您喜欢的商家，开启便捷配送服务</p>
      </div>

      <div class="merchants-grid">
        <div
          v-for="item in merchants"
          :key="item.merchant.id"
          class="merchant-card"
          @click="selectMerchant(item)"
        >
          <div class="merchant-image-wrapper">
            <div class="image-overlay"></div>
            <img :src="getMerchantImage(item.merchant.images)" :alt="item.merchant.name">
            <div class="merchant-status" :class="item.merchant.status === '1' ? 'online' : 'offline'">
              <a-icon :type="item.merchant.status === '1' ? 'check-circle' : 'clock-circle'" />
              {{ item.merchant.status === '1' ? '营业中' : '休息中' }}
            </div>
          </div>

          <div class="merchant-info">
            <h3 class="merchant-name">{{ item.merchant.name }}</h3>

            <div class="merchant-tags">
              <div class="tag-item">
                <a-icon type="environment" theme="filled" />
                <span>{{ item.merchant.address }}</span>
              </div>
              <div class="tag-item">
                <a-icon type="phone" theme="filled" />
                <span>{{ item.merchant.phone }}</span>
              </div>
              <div class="tag-item">
                <a-icon type="clock-circle" theme="filled" />
                <span>{{ item.merchant.operateStartTime }} - {{ item.merchant.operateEndTime }}</span>
              </div>
            </div>

            <div class="merchant-footer">
              <div class="product-count">
                <a-icon type="shopping" theme="filled" />
                <span class="count-number">{{ item.dishes.length }}</span>
                <span class="count-text">件餐品</span>
              </div>
              <div class="action-btn">
                <span>立即选购</span>
                <a-icon type="arrow-right" />
              </div>
            </div>
          </div>

          <div class="card-shine"></div>
        </div>
      </div>

      <div v-if="merchants.length === 0" class="empty-state">
        <div class="empty-icon">
          <a-icon type="shop" theme="outlined" />
        </div>
        <p>暂无商家信息</p>
      </div>
    </div>

    <!-- 餐品详情视图 -->
    <div v-else class="products-view">
      <div class="page-header compact">
        <a-button class="back-btn" @click="backToMerchants">
          <a-icon type="arrow-left" slot="icon" />
          返回商家列表
        </a-button>

        <div class="merchant-banner">
          <div class="banner-bg">
            <img :src="getMerchantImage(selectedMerchant.merchant.images)" alt="">
          </div>
          <div class="banner-content">
            <h2 class="page-title">{{ selectedMerchant.merchant.name }}</h2>
            <div class="merchant-meta">
              <span class="meta-item">
                <a-icon type="phone" theme="filled" />
                {{ selectedMerchant.merchant.phone }}
              </span>
              <span class="meta-item">
                <a-icon type="clock-circle" theme="filled" />
                {{ selectedMerchant.merchant.operateStartTime }} - {{ selectedMerchant.merchant.operateEndTime }}
              </span>
              <span class="meta-item status-badge" :class="selectedMerchant.merchant.status === '1' ? 'online' : 'offline'">
                <a-icon :type="selectedMerchant.merchant.status === '1' ? 'check-circle' : 'clock-circle'" />
                {{ selectedMerchant.merchant.status === '1' ? '营业中' : '休息中' }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div class="products-layout">
        <!-- 餐品列表 -->
        <div class="products-list">
          <div class="section-header">
            <div class="section-title">
              <div class="title-icon">
                <a-icon type="shopping" theme="filled" />
              </div>
              <span>餐品列表</span>
            </div>
            <div class="section-stats">
              <span class="stat-item">
                <a-icon type="shopping-cart" />
                已选 {{ totalQuantity }} 件
              </span>
            </div>
          </div>

          <div v-if="selectedMerchant.dishes.length === 0" class="empty-products">
            <div class="empty-icon">
              <a-icon type="inbox" theme="outlined" />
            </div>
            <p>该商家暂无餐品</p>
          </div>

          <div
            v-for="product in selectedMerchant.dishes"
            :key="product.id"
            class="product-card"
            :class="{ 'in-cart': cart[product.id] && cart[product.id] > 0 }"
          >
            <div class="product-badge" v-if="cart[product.id] && cart[product.id] > 0">
              <span class="badge-count">{{ cart[product.id] }}</span>
            </div>

            <div class="product-image-wrapper">
              <img :src="getProductImage(product.images)" :alt="product.name">
            </div>

            <div class="product-details">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-desc">{{ product.content }}</p>

              <div class="product-specs">
                <div class="spec-tag">
                  <a-icon type="container" />
                  <span>{{ product.portion }}</span>
                </div>
                <div class="spec-tag">
                  <a-icon type="database" />
                  <span>库存 {{ product.laveNum }}</span>
                </div>
              </div>

              <div class="product-bottom">
                <div class="product-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ product.unitPrice }}</span>
                </div>

                <div class="quantity-control">
                  <a-button
                    class="qty-btn"
                    @click="decreaseQuantity(product)"
                    :disabled="!cart[product.id] || cart[product.id] <= 0"
                    shape="circle"
                  >
                    <a-icon type="minus" />
                  </a-button>

                  <div class="qty-display">
                    <input
                      type="number"
                      class="qty-input"
                      v-model.number="cart[product.id]"
                      min="0"
                      :max="product.laveNum"
                      @change="validateQuantity(product)"
                    >
                  </div>

                  <a-button
                    class="qty-btn primary"
                    @click="increaseQuantity(product)"
                    :disabled="cart[product.id] >= product.laveNum"
                    shape="circle"
                  >
                    <a-icon type="plus" />
                  </a-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 购物车侧边栏 -->
        <div class="cart-sidebar">
          <div class="cart-header">
            <div class="header-left">
              <div class="cart-icon-wrapper">
                <a-icon type="shopping-cart" theme="filled" />
                <span class="cart-badge" v-if="totalQuantity > 0">{{ totalQuantity }}</span>
              </div>
              <div class="header-text">
                <h3>购物车</h3>
                <span class="header-subtitle">{{ cartItems.length }} 种餐品</span>
              </div>
            </div>
          </div>

          <!-- 餐品列表（可滚动） -->
          <div class="cart-items">
            <div v-if="cartItems.length === 0" class="empty-cart">
              <div class="empty-icon">
                <a-icon type="shopping-cart" theme="outlined" />
              </div>
              <p>购物车是空的</p>
              <span class="empty-hint">快去挑选心仪的餐品吧</span>
            </div>

            <transition-group name="cart-item">
              <div
                v-for="item in cartItems"
                :key="item.product.id"
                class="cart-item"
              >
                <div class="cart-item-image">
                  <img :src="getProductImage(item.product.images)" :alt="item.product.name">
                </div>

                <div class="cart-item-content">
                  <div class="cart-item-header">
                    <div class="cart-item-name">{{ item.product.name }}</div>
                    <a-button
                      class="remove-btn"
                      @click="removeFromCart(item.product.id)"
                      type="text"
                      size="small"
                    >
                      <a-icon type="delete" />
                    </a-button>
                  </div>

                  <div class="cart-item-footer">
                    <div class="cart-item-price">
                      <span class="unit-price">¥{{ item.product.unitPrice }}</span>
                      <span class="total-price">¥{{ (item.product.unitPrice * item.quantity).toFixed(2) }}</span>
                    </div>

                    <div class="cart-item-qty">
                      <a-button
                        class="cart-qty-btn"
                        @click="decreaseQuantity(item.product)"
                        size="small"
                        shape="circle"
                      >
                        <a-icon type="minus" />
                      </a-button>

                      <span class="cart-qty-value">{{ item.quantity }}</span>

                      <a-button
                        class="cart-qty-btn"
                        @click="increaseQuantity(item.product)"
                        size="small"
                        shape="circle"
                      >
                        <a-icon type="plus" />
                      </a-button>
                    </div>
                  </div>
                </div>
              </div>
            </transition-group>
          </div>

          <!-- 配送配置（可滚动） -->
          <div class="delivery-config" v-if="cartItems.length > 0">
            <div class="config-section">
              <div class="section-label">
                <div class="label-icon">
                  <a-icon type="environment" theme="filled" />
                </div>
                <span>配送地址</span>
              </div>
              <a-select
                v-model="selectedAddressId"
                placeholder="请选择配送地址"
                style="width: 100%"
                size="large"
              >
                <a-select-option
                  v-for="addr in addressList"
                  :key="addr.id"
                  :value="addr.id"
                >
                  <div class="address-option">
                    <div class="address-header">
                      <a-tag v-if="addr.isDefault === '1'" color="red" size="small">默认</a-tag>
                      <span class="address-text">{{ addr.address }}</span>
                    </div>
<!--                    <div class="address-contact">-->
<!--                      <a-icon type="user" />-->
<!--                      {{ addr.contactName }}-->
<!--                      <a-icon type="phone" />-->
<!--                      {{ addr.contactPhone }}-->
<!--                    </div>-->
                  </div>
                </a-select-option>
              </a-select>
            </div>

            <div class="config-section">
              <div class="section-label">
                <div class="label-icon">
                  <a-icon type="clock-circle" theme="filled" />
                </div>
                <span>配送频率</span>
              </div>

              <div class="schedule-presets">
                <a-radio-group v-model="selectedSchedule">
                  <a-radio-button
                    v-for="preset in schedulePresets"
                    :key="preset.value"
                    :value="preset.value"
                    class="schedule-radio"
                  >
                    <div class="schedule-content">
                      <div class="schedule-name">{{ preset.name }}</div>
                      <div class="schedule-desc">{{ preset.description }}</div>
                    </div>
                  </a-radio-button>
                </a-radio-group>
              </div>

              <div class="custom-schedule" v-if="selectedSchedule === 'custom'">
                <a-input
                  v-model="customCron"
                  placeholder="请输入Cron表达式"
                  size="large"
                >
                  <a-icon slot="prefix" type="edit" />
                </a-input>
                <div class="cron-help">
                  <a-icon type="info-circle" theme="filled" />
                  <span>格式：秒 分 时 日 月 周</span>
                </div>
              </div>

              <div class="delivery-time-section">
                <div class="time-label">
                  <a-icon type="clock-circle" />
                  期望配送时间
                </div>
                <a-time-picker
                  v-model="deliveryTime"
                  format="HH:mm"
                  placeholder="选择配送时间"
                  size="large"
                />
              </div>
            </div>
          </div>

          <!-- 订单摘要和结算按钮（固定在底部） -->
          <div class="cart-footer" v-if="cartItems.length > 0">
            <div class="order-summary">
              <div class="summary-card">
                <div class="summary-row">
                  <span class="row-label">
                    <a-icon type="shopping" />
                    餐品总数
                  </span>
                  <span class="row-value">{{ totalQuantity }} 件</span>
                </div>
                <div class="summary-row">
                  <span class="row-label">
                    <a-icon type="clock-circle" />
                    配送频率
                  </span>
                  <span class="row-value highlight">{{ currentScheduleName }}</span>
                </div>
                <div class="summary-divider"></div>
                <div class="summary-row total">
                  <span class="row-label">每批合计金额</span>
                  <span class="row-value price">¥{{ totalPrice.toFixed(2) }}</span>
                </div>
              </div>
            </div>

            <a-button
              class="checkout-btn"
              @click="checkout"
              :disabled="!selectedAddressId"
              type="primary"
              size="large"
              block
            >
              <a-icon type="wallet" slot="icon" />
              确认下单
            </a-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>import { mapState } from 'vuex'
import moment from 'moment'

export default {
  name: 'Schedule',
  data () {
    return {
      scheduleList: [],
      merchants: [],
      addressList: [],
      selectedMerchant: null,
      cart: {},

      // 配送相关
      selectedAddressId: null,
      selectedSchedule: 'every2days',
      customCron: '',
      deliveryTime: null,

      // 预设配送方案
      schedulePresets: [
        {
          value: 'every2days',
          name: '每2天配送',
          description: '每隔1天送一次',
          cron: '0 0 8 */2 * ?'
        },
        {
          value: 'every3days',
          name: '每3天配送',
          description: '每隔2天送一次',
          cron: '0 0 8 */3 * ?'
        },
        {
          value: 'every5days',
          name: '每5天配送',
          description: '每隔4天送一次',
          cron: '0 0 8 */5 * ?'
        },
        {
          value: 'weekly',
          name: '每周配送',
          description: '每周固定时间送',
          cron: '0 0 8 ? * MON'
        },
        {
          value: 'biweekly',
          name: '每半月配送',
          description: '每月1号和15号送',
          cron: '0 0 8 1,15 * ?'
        },
        {
          value: 'monthly',
          name: '每月配送',
          description: '每月1号送',
          cron: '0 0 8 1 * ?'
        }
      ]
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),

    cartItems () {
      const items = []
      if (this.selectedMerchant && this.selectedMerchant.dishes) {
        this.selectedMerchant.dishes.forEach(product => {
          const quantity = this.cart[product.id]
          if (quantity && quantity > 0) {
            items.push({
              product,
              quantity
            })
          }
        })
      }
      return items
    },

    totalQuantity () {
      return this.cartItems.reduce((sum, item) => sum + item.quantity, 0)
    },

    totalPrice () {
      return this.cartItems.reduce((sum, item) => {
        return sum + (item.product.unitPrice * item.quantity)
      }, 0)
    },

    currentScheduleName () {
      const preset = this.schedulePresets.find(p => p.value === this.selectedSchedule)
      return preset ? preset.name : '自定义'
    },

    currentCron () {
      const preset = this.schedulePresets.find(p => p.value === this.selectedSchedule)
      return this.selectedSchedule === 'custom' ? this.customCron : (preset ? preset.cron : '')
    },

    selectedAddressText () {
      const addr = this.addressList.find(a => a.id === this.selectedAddressId)
      if (!addr) return ''
      return `${addr.address} - ${addr.contactName} ${addr.contactPhone}`
    }
  },
  mounted () {
    this.queryByUserId()
    this.queryGooodsGroup()
    this.queryUserAddressList()
  },

  methods: {
    queryByUserId () {
      this.$get('/cos/schedule-order-info/queryByUserId', {
        userId: this.currentUser.userId
      }).then((r) => {
        this.scheduleList = r.data.data
      })
    },
    deleteSchedule (id) {
      this.$delete(`/cos/schedule-order-info/${id}`).then(() => {
        this.$message.success('删除成功')
        this.queryByUserId()
      }).catch(err => {
        console.error('删除失败:', err)
        this.$message.error('删除失败')
      })
    },
    getCronDescription (cron) {
      const preset = this.schedulePresets.find(p => p.cron === cron)
      if (preset) {
        return preset.name
      }
      return '自定义'
    },

    getGoodsImage (images) {
      if (!images) return '/static/img/default.jpg'
      const imageArray = images.split(',')
      return 'http://127.0.0.1:9527/imagesWeb/' + imageArray[0]
    },
    queryGooodsGroup () {
      this.$get('/cos/dishes-info/queryGooodsGroup').then((r) => {
        this.merchants = r.data.merchants
      }).catch(err => {
        console.error('获取商家列表失败:', err)
        this.$message.error('获取商家列表失败')
      })
    },

    queryUserAddressList () {
      this.$get(`/cos/address-info/listByUserId/${this.currentUser.userId}`).then((r) => {
        this.addressList = r.data.data
        const defaultAddr = this.addressList.find(addr => addr.isDefault === '1')
        if (defaultAddr) {
          this.selectedAddressId = defaultAddr.id
        } else if (this.addressList.length > 0) {
          this.selectedAddressId = this.addressList[0].id
        }
      })
    },

    selectMerchant (item) {
      this.selectedMerchant = item
      this.cart = {}
      window.scrollTo(0, 0)
    },

    backToMerchants () {
      this.selectedMerchant = null
      this.cart = {}
    },

    getMerchantImage (images) {
      if (!images) return '/static/img/default.jpg'
      const imageArray = images.split(',')
      return 'http://127.0.0.1:9527/imagesWeb/' + imageArray[0]
    },

    getProductImage (images) {
      if (!images) return '/static/img/default.jpg'
      const imageArray = images.split(',')
      return 'http://127.0.0.1:9527/imagesWeb/' + imageArray[0]
    },

    increaseQuantity (product) {
      if (!this.cart[product.id]) {
        this.$set(this.cart, product.id, 0)
      }
      if (this.cart[product.id] < product.laveNum) {
        this.cart[product.id]++
      } else {
        this.$message.warning('已达到最大库存')
      }
    },

    decreaseQuantity (product) {
      if (this.cart[product.id] && this.cart[product.id] > 0) {
        this.cart[product.id]--
      }
    },

    validateQuantity (product) {
      if (!this.cart[product.id] || this.cart[product.id] < 0) {
        this.$set(this.cart, product.id, 0)
      }
      if (this.cart[product.id] > product.laveNum) {
        this.$set(this.cart, product.id, product.laveNum)
        this.$message.warning('已达到最大库存')
      }
    },

    removeFromCart (productId) {
      this.$set(this.cart, productId, 0)
    },

    checkout () {
      if (this.cartItems.length === 0) {
        this.$message.warning('购物车是空的')
        return
      }
      if (!this.selectedAddressId) {
        this.$message.warning('请选择配送地址')
        return
      }
      if (!this.currentCron) {
        this.$message.warning('请设置配送频率')
        return
      }

      const taskTime = this.deliveryTime
        ? moment(this.deliveryTime).format('HH:mm:ss')
        : '08:00:00'

      let itemList = this.cartItems.map(item => ({
        dishesId: item.product.id,
        quantity: item.quantity,
        unitPrice: item.product.unitPrice
      }))
      const orderData = {
        merchantId: this.selectedMerchant.merchant.id,
        userId: this.currentUser.userId,
        addressId: this.selectedAddressId,
        cronContent: this.currentCron,
        taskTime: taskTime,
        items: JSON.stringify(itemList),
        totalAmount: this.totalPrice
      }

      console.log(JSON.stringify(orderData))

      this.$confirm({
        title: '确认订单',
        content: `是否确认时段配送`,
        okText: '确认下单',
        cancelText: '取消',
        iconType: 'info-circle',
        dangerouslyUseHTMLString: true,
        onOk: () => {
          this.$post('/cos/schedule-order-info', orderData).then(() => {
            this.$message.success('定时配送订单提交成功！')
            this.cart = {}
          })
        },
        onCancel: () => {
          console.log('用户取消下单')
        }
      })
    }
  }
}
</script>

<style scoped>/* ========== 全局样式 ========== */
.schedule-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 100px);
}

/* ========== 页面头部 ========== */
.page-header {
  margin-bottom: 24px;
}

.page-header.compact {
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title i {
  font-size: 26px;
  color: #1890ff;
}

.page-subtitle {
  font-size: 14px;
  color: #999;
  margin: 0;
}

/* ========== 返回按钮 ========== */
.back-btn {
  margin-bottom: 16px;
  font-size: 13px;
}

/* ========== 商家横幅 ========== */
.merchant-banner {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  background: white;
}

.banner-bg {
  height: 120px;
  overflow: hidden;
}

.banner-bg img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-content {
  padding: 16px 20px;
}

.merchant-meta {
  display: flex;
  gap: 20px;
  margin-top: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 13px;
}

.meta-item i {
  color: #1890ff;
  font-size: 14px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-weight: 500;
  font-size: 12px;
}

.status-badge.online {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.status-badge.offline {
  background: #f5f5f5;
  color: #999;
  border: 1px solid #d9d9d9;
}

/* ========== 商家网格 ========== */
.merchants-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.merchant-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.merchant-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  border-color: #1890ff;
}

.merchant-image-wrapper {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.merchant-image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.merchant-card:hover .merchant-image-wrapper img {
  transform: scale(1.05);
}

.merchant-status {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  align-items: center;
  gap: 4px;
}

.merchant-status.online {
  color: #52c41a;
}

.merchant-status.offline {
  color: #999;
}

.merchant-info {
  padding: 16px;
}

.merchant-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.merchant-tags {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 14px;
}

.tag-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #666;
  padding: 6px 10px;
  background: #fafafa;
  border-radius: 6px;
}

.tag-item i {
  color: #1890ff;
  font-size: 13px;
}

.merchant-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 14px;
  border-top: 1px solid #f0f0f0;
}

.product-count {
  display: flex;
  align-items: center;
  gap: 6px;
}

.product-count i {
  color: #1890ff;
  font-size: 16px;
}

.count-number {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.count-text {
  font-size: 12px;
  color: #999;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #1890ff;
  font-weight: 500;
  font-size: 13px;
  padding: 6px 14px;
  background: #e6f7ff;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.merchant-card:hover .action-btn {
  background: #1890ff;
  color: white;
}

/* ========== 餐品布局 ========== */
.products-layout {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 20px;
}

.products-list {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 14px;
  border-bottom: 1px solid #f0f0f0;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  width: 28px;
  height: 28px;
  background: #1890ff;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
}

.section-stats {
  display: flex;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #f5f5f5;
  border-radius: 14px;
  font-size: 12px;
  font-weight: 500;
  color: #666;
}

/* ========== 餐品卡片 ========== */
.product-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  margin-bottom: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
  background: white;
}

.product-card:hover {
  border-color: #1890ff;
  box-shadow: 0 2px 12px rgba(24, 144, 255, 0.1);
}

.product-card.in-cart {
  border-color: #1890ff;
  background: #f0f9ff;
}

.product-badge {
  position: absolute;
  top: -6px;
  right: 16px;
  background: #ff4d4f;
  color: white;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 12px;
}

.product-image-wrapper {
  width: 140px;
  height: 140px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.product-image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-details {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.product-desc {
  font-size: 12px;
  color: #999;
  line-height: 1.5;
  margin: 0 0 12px 0;
  flex: 1;
}

.product-specs {
  display: flex;
  gap: 8px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}

.spec-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: #f5f5f5;
  border-radius: 12px;
  font-size: 11px;
  color: #666;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.price-symbol {
  font-size: 14px;
  color: #ff4d4f;
  font-weight: 600;
}

.price-value {
  font-size: 22px;
  color: #ff4d4f;
  font-weight: 600;
}

/* ========== 数量控制 ========== */
.quantity-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.qty-btn {
  width: 28px !important;
  height: 28px !important;
  min-width: 28px !important;
  padding: 0 !important;
  border: 1px solid #d9d9d9 !important;
  background: white !important;
  border-radius: 4px !important;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666 !important;
  font-size: 12px;
  transition: all 0.2s ease;
}

.qty-btn:hover:not(:disabled) {
  border-color: #1890ff !important;
  color: #1890ff !important;
}

.qty-btn.primary {
  background: #1890ff !important;
  border-color: #1890ff !important;
  color: white !important;
}

.qty-btn.primary:hover:not(:disabled) {
  background: #40a9ff !important;
  border-color: #40a9ff !important;
}

.qty-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.qty-display {
  width: 50px;
  height: 28px;
  background: white;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qty-input {
  width: 100%;
  height: 100%;
  border: none;
  text-align: center;
  font-size: 13px;
  font-weight: 600;
  color: #333;
  background: transparent;
  outline: none;
}

/* ========== 购物车侧边栏 ========== */
.cart-sidebar {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 160px);
  position: sticky;
  top: 20px;
  overflow: hidden;
  border: 1px solid #f0f0f0;
}

.cart-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cart-icon-wrapper {
  position: relative;
  width: 36px;
  height: 36px;
  background: #1890ff;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
}

.cart-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #ff4d4f;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 600;
}

.header-text h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.header-subtitle {
  font-size: 11px;
  color: #999;
  margin-top: 2px;
}

.cart-items {
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px;
  min-height: 0;
}

.cart-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  margin-bottom: 12px;
  background: #fafafa;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
}

.cart-item-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
}

.cart-item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cart-item-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cart-item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 6px;
}

.cart-item-name {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
}

.remove-btn {
  color: #ff4d4f !important;
  padding: 2px !important;
  font-size: 12px !important;
}

.cart-item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cart-item-price {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.unit-price {
  font-size: 11px;
  color: #999;
}

.total-price {
  font-size: 14px;
  font-weight: 600;
  color: #ff4d4f;
}

.cart-item-qty {
  display: flex;
  align-items: center;
  gap: 6px;
}

.cart-qty-btn {
  width: 24px !important;
  height: 24px !important;
  min-width: 24px !important;
  padding: 0 !important;
  border: 1px solid #d9d9d9 !important;
  background: white !important;
  border-radius: 4px !important;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666 !important;
  font-size: 10px;
  transition: all 0.2s ease;
}

.cart-qty-btn:hover {
  border-color: #1890ff !important;
  color: #1890ff !important;
}

.cart-qty-value {
  min-width: 28px;
  text-align: center;
  font-weight: 600;
  color: #333;
  font-size: 13px;
}

.empty-cart {
  text-align: center;
  padding: 40px 20px;
  color: #ccc;
}

.empty-cart .empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
  opacity: 0.5;
}

.empty-cart p {
  font-size: 14px;
  margin: 0 0 6px 0;
  color: #999;
}

.empty-hint {
  font-size: 12px;
  color: #ccc;
}

/* ========== 配送配置 ========== */
.delivery-config {
  padding: 0 16px 16px;
  overflow-y: auto;
  flex-shrink: 1;
}

.config-section {
  margin-bottom: 16px;
  padding: 14px;
  background: #fafafa;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
}

.section-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.label-icon {
  width: 24px;
  height: 24px;
  background: #1890ff;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
}

.address-option {
  padding: 6px 0;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.address-text {
  font-size: 13px;
  color: #333;
  font-weight: 500;
}

.address-contact {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 11px;
  color: #999;
}

.address-contact i {
  color: #1890ff;
}

.schedule-presets {
  margin-bottom: 10px;
}

.schedule-radio {
  width: 100%;
  margin-bottom: 6px !important;
}

.schedule-content {
  text-align: left;
  padding: 2px 0;
}

.schedule-name {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  margin-bottom: 2px;
}

.schedule-desc {
  font-size: 11px;
  color: #999;
}

.custom-schedule {
  margin-top: 10px;
}

.cron-help {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 6px;
  font-size: 11px;
  color: #999;
  padding: 8px 10px;
  background: #fff;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
}

.cron-help i {
  color: #1890ff;
}

.delivery-time-section {
  margin-top: 12px;
}

.time-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 500;
  color: #666;
  margin-bottom: 8px;
}

.time-label i {
  color: #1890ff;
}

/* ========== 订单摘要 ========== */
.cart-footer {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
  flex-shrink: 0;
}

.order-summary {
  margin-bottom: 14px;
}

.summary-card {
  padding: 14px;
  background: white;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
  color: #666;
}

.summary-row:last-child {
  margin-bottom: 0;
}

.row-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.row-label i {
  color: #1890ff;
}

.row-value {
  font-weight: 600;
  color: #333;
}

.row-value.highlight {
  color: #1890ff;
}

.summary-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 10px 0;
}

.summary-row.total {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.row-value.price {
  color: #ff4d4f;
  font-size: 20px;
}

.checkout-btn {
  height: 40px !important;
  border: none !important;
  background: #1890ff !important;
  color: white !important;
  border-radius: 6px !important;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.checkout-btn:hover:not(:disabled) {
  background: #40a9ff !important;
}

.checkout-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ========== 空状态 ========== */
.empty-state, .empty-products {
  text-align: center;
  padding: 80px 20px;
  color: #999;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.4;
}

.empty-state p, .empty-products p {
  font-size: 14px;
  margin: 0;
}

/* ========== 响应式 ========== */
@media (max-width: 1200px) {
  .products-layout {
    grid-template-columns: 1fr;
  }

  .cart-sidebar {
    position: static;
    max-height: none;
  }
}

@media (max-width: 768px) {
  .schedule-container {
    padding: 12px;
  }

  .merchants-grid {
    grid-template-columns: 1fr;
  }

  .product-card {
    flex-direction: column;
  }

  .product-image-wrapper {
    width: 100%;
    height: 180px;
  }

  .page-title {
    font-size: 20px;
  }

  .products-layout {
    gap: 16px;
  }
}

/* ========== 滚动条美化 ========== */
.cart-items::-webkit-scrollbar,
.delivery-config::-webkit-scrollbar {
  width: 6px;
}

.cart-items::-webkit-scrollbar-track,
.delivery-config::-webkit-scrollbar-track {
  background: transparent;
}

.cart-items::-webkit-scrollbar-thumb,
.delivery-config::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 3px;
}

.cart-items::-webkit-scrollbar-thumb:hover,
.delivery-config::-webkit-scrollbar-thumb:hover {
  background: #999;
}

/* ========== 购物车动画 ========== */
.cart-item-enter-active {
  transition: all 0.3s ease;
}

.cart-item-leave-active {
  transition: all 0.2s ease;
}

.cart-item-enter {
  opacity: 0;
  transform: translateX(-20px);
}

.cart-item-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

/* ========== 确认对话框样式 ========== */
.confirm-content {
  text-align: left;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
  margin: 12px 0;
}

.confirm-content p {
  margin: 8px 0;
  font-size: 13px;
  color: #333;
}

.confirm-content .total-price {
  font-size: 16px;
  color: #ff4d4f;
  font-weight: 600;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}


/* ========== 定时配送配置列表 ========== */
.schedule-list-section {
  margin-bottom: 32px;
}

.section-header-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title i {
  font-size: 20px;
  color: #1890ff;
}

.section-subtitle {
  font-size: 13px;
  color: #999;
}

.schedule-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.schedule-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.schedule-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  border-color: #1890ff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #e6f7ff 0%, #f0f9ff 100%);
}

.header-left {
  display: flex;
  gap: 12px;
  align-items: center;
  flex: 1;
}

.goods-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.goods-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-info {
  flex: 1;
  min-width: 0;
}

.goods-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
  line-height: 1.4;
}

.goods-spec {
  font-size: 12px;
  color: #999;
}

.delete-btn {
  color: #ff4d4f !important;
  padding: 4px 8px !important;
  font-size: 16px;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  background: rgba(255, 77, 79, 0.1) !important;
}

.card-body {
  padding: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 12px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
  min-width: 80px;
}

.info-label i {
  color: #1890ff;
  font-size: 13px;
}

.info-value {
  font-size: 13px;
  color: #333;
  text-align: right;
  flex: 1;
  word-break: break-all;
}

.info-value.highlight {
  color: #1890ff;
  font-weight: 600;
}

.card-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.cron-expression {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: #999;
  font-family: 'Courier New', monospace;
}

.cron-expression i {
  color: #1890ff;
}
</style>
