import { createStore } from 'vuex'

const store = createStore({
  state: {
    // 保存全局的用户信息
    user: {}
  },
  getters: {
  },
  mutations: {
    // 对变量的操作
    // 同步
    setUser(state, user) {
      state.user = user
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
