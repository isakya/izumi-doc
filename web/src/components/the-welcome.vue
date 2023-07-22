<template>
  <div class="welcome" >
    <div>
      <div class="tip">
        <div>1. 统计数据是真实的，一分钟左右延时，<b>用到了定时器、复杂SQL统计、echarts</b></div>
        <div>2. 有文档被别人点赞，你也会收到实时通知哦！<b>用到了websocket、异步化、RocketMQ、防重设计</b></div>
        <div>3. 文档树可无限级扩展，支持文字、图片、视频。<b>用到了无限级树设计知识</b></div>
        <div>4. 登录后可看到更多菜单。<b>用到了单点登录、前后端登录拦截、安全性设计</b></div>
      </div>
      <a-row>
        <a-col :span="24">
          <a-card>
            <a-row>
              <a-col :span="8">
                <a-statistic title="总阅读量"
                             :value="statistic.viewCount">
                  <template #suffix>
                    <UserOutlined/>
                  </template>
                </a-statistic>
              </a-col>
              <a-col :span="8">
                <a-statistic title="总点赞量"
                             :value="statistic.voteCount">
                  <template #suffix>
                    <like-outlined/>
                  </template>
                </a-statistic>
              </a-col>
              <a-col :span="8">
                <a-statistic title="点赞率" :value="statistic.voteCount / statistic.viewCount * 100"
                             :precision="2"
                             suffix="%"
                             :value-style="{ color: '#cf1322' }">
                </a-statistic>
              </a-col>
            </a-row>
          </a-card>
        </a-col>
      </a-row>
      <br>
      <a-row :gutter="16">
        <a-col :span="12">
          <a-card>
            <a-row>
              <a-col :span="12">
                <a-statistic title="今日阅读" :value="statistic.todayViewCount"
                             style="margin-right: 50px">
                  <template #suffix>
                    <UserOutlined/>
                  </template>
                </a-statistic>
              </a-col>
              <a-col :span="12">
                <a-statistic title="今日点赞" :value="statistic.todayVoteCount">
                  <template #suffix>
                    <like-outlined/>
                  </template>
                </a-statistic>
              </a-col>
            </a-row>
          </a-card>
        </a-col>
        <a-col :span="12">
          <a-card>
            <a-row>
              <a-col :span="12">
                <a-statistic title="预计今日阅读"
                             :value="statistic.todayViewIncrease"
                             :value-style="{color:'#0000ff'}">
                  <template #suffix>
                    <UserOutlined/>
                  </template>
                </a-statistic>
              </a-col>
              <a-col :span="12">
                <a-statistic title="预计今日阅读增长"
                             :value="statistic.todayViewIncreaseRateAbs"
                             :precision="2"
                             suffix="%"
                             class="demo-class"
                             :value-style="statistic.todayViewIncreaseRate < 0 ? {color: '#3f8600'} : {color: '#cf1322'}">
                  <template #prefix>
                    <arrow-down-outlined v-if="statistic.todayViewIncreaseRate < 0"/>
                    <arrow-up-outlined v-if="statistic.todayViewIncreaseRate >= 0"/>
                  </template>
                </a-statistic>
              </a-col>
            </a-row>
          </a-card>
        </a-col>
      </a-row>
      <br>
      <a-row>
        <a-col :span="24" id="main-col">
          <div id="main" style="width: 100%;height:300px;"></div>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from "axios";
import { message } from "ant-design-vue";
import {MessageOutlined, UserOutlined, LikeOutlined, ArrowDownOutlined, ArrowUpOutlined} from '@ant-design/icons-vue';


const statistic = ref({
  viewCount: 1,
  voteCount: 0,
  todayViewCount: 0,
  todayVoteCount: 0,
  todayViewIncrease: 0,
  todayViewIncreaseRate: 0,
  todayViewIncreaseRateAbs: 0
})
const getStatistic = () => {
  axios.get('/ebook-snapshot/get-statistic').then((response) => {
    const data = response.data;
    if (data.success) {
      const statisticResp = data.content;
      if(data.content?.length <= 1) {
        return
      }
      statistic.value.viewCount = statisticResp[1]?.viewCount;
      statistic.value.voteCount = statisticResp[1]?.voteCount;
      statistic.value.todayViewCount = statisticResp[1]?.viewIncrease;
      statistic.value.todayVoteCount = statisticResp[1]?.voteIncrease;
      // 按分钟计算当前时间点，占一天的百分比
      const now = new Date();
      const nowRate = (now.getHours() * 60 + now.getMinutes()) / (60 * 24);
      // console.log(nowRate)
      statistic.value.todayViewIncrease = parseInt(String(statisticResp[1].viewIncrease / nowRate));
      // todayViewIncreaseRate：今日预计增长率
      statistic.value.todayViewIncreaseRate = (statistic.value.todayViewIncrease - statisticResp[0].viewIncrease) / statisticResp[0].viewIncrease * 100;
      statistic.value.todayViewIncreaseRateAbs = Math.abs(statistic.value.todayViewIncreaseRate);
    } else {
      message.error(data.message)
    }
  });
};
onMounted(() => {
  getStatistic()
})

</script>

<style scoped>
/*.welcome {*/
/*  display: flex;*/
/*  justify-content: center;*/
/*  align-items: center;*/
/*}*/
/*.welcome h1,p {*/
/*  font-size: 30px;*/
/*  color: #ebab4b;*/
/*}*/
/*.welcome h1 {*/
/*  font-style: italic;*/
/*}*/

.tip {
  padding: 10px 5px;
  margin-bottom: 20px;
  border: 1px solid transparent;
  background: linear-gradient(white,white) padding-box,repeating-linear-gradient(-45deg, black 0, black 25%, white 0, white 50%) 0/.6em .6em;
  animation:ants 12s linear infinite;
}
.tip b{
  color: red;
}
</style>
