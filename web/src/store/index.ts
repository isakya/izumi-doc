import { createStore } from 'vuex'

declare let SessionStorage: any
const USER = "USER"

const store = createStore({
  state: {
    // 保存全局的用户信息
    // 先去缓存里获取信息
    user: SessionStorage.get(USER) || {}
  },
  getters: {
  },
  mutations: {
    // 对变量的操作
    // 同步
    setUser (state, user) {
      console.log('store user: ', user)
      state.user = user
      SessionStorage.set(USER, user)
    }
  },
  actions: {
    // 对变量的操作
    // 异步
  },
  modules: {
  }
})
export default store
