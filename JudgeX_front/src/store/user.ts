// 存储的状态信息，比如用户信息
import { StoreOptions } from "vuex";
import ACCESS_ENUM from "@/access/ACCESS_ENUM";
import { UserControllerService } from "../../generated";

export default {
  namespaced: true,
  state: () => ({
    loginUser: {
      userName: "未登录",
    },
  }),
  // 执行异步操作，并触发mutation的更改（actions调用mutation）
  actions: {
    async getLoginUser({ commit, state }, payload) {
      // 远程请求获取登录信息
      const res = await UserControllerService.getLoginUserUsingGet();
      if (res.code === 0) {
        commit("updateUser", res.data);
      } else {
        commit("updateUser", {
          access: ACCESS_ENUM.NOT_LOGIN,
        });
      }
    },
  },
  // 定于了对变量进行更新的方法
  mutations: {
    updateUser(state, payload) {
      state.loginUser = payload;
    },
  },
} as StoreOptions<any>;
