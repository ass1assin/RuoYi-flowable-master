<template>
  <div>
    <div>
      <div style="display: flex;justify-content: center;align-items: center">
        <el-card class="box-card" style="width: 80%;" v-if="middleOne">
          <div class="text item" style="margin: 2% 5% 0 0">
            <el-form ref="form" :model="form" label-width="150px" >

              <el-row style="" justify="center">
                <el-col :span="12">
                  <el-form-item label="id" prop="tenant">
                    <el-input class="my-input" v-model="form.id"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="承租人" prop="tenant">
                    <el-input class="my-input" v-model="form.tenant"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="承租人类型" prop="tenantType">
                    <el-select class="my-input" v-model="form.tenantType" placeholder="请选择">
                      <el-option v-for="item in tenantType" :key="item.value" :label="item.label" :value="item.value">
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row justify="center">
                <el-col :span="12">
                  <el-form-item label="支付方式" prop="paymentMethod">
                    <el-select class="my-input" v-model="form.paymentMethod" placeholder="请选择"
                               @change="form.payDay = ''">
                      <el-option v-for="item in paymentMethod" :key="item.value" :label="item.label"
                                 :value="item.value">
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="期数" prop="periods">
                    <el-input class="my-input" type="number" min="1" v-model="form.periods"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row justify="center">
                <el-col :span="12">
                  <el-form-item label="每期租金" prop="monthlyRent">
                    <el-input class="my-input" v-model="form.monthlyRent">
                      <template slot="append">元</template>
                    </el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12" v-if="form.paymentMethod === 0">
                  <el-form-item label="缴费日" prop="payDay">
                    <el-input style="width: 70%;" v-model="form.payDay" type="number" max="28" min="1">
                      <template slot="prepend">每月</template>
                      <template slot="append">日</template>
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row justify="center">
                <el-col :span="12">
                  <el-form-item label="押金" prop="deposit">
                    <el-input class="my-input" v-model="form.deposit">
                      <template slot="append">元</template>
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-row>

            </el-form>
          </div>
        </el-card>
      </div>

      <div class="foot-box" v-if="middleOne">
        <el-button @click="submitApply" type="primary">
          提交捏
        </el-button>
      </div>
    </div>


  </div>
</template>

<script>
import {definitionStart, flowXmlAndNode} from "@/api/flowable/definition";
import {getNextFlowNodeByStart} from "@/api/flowable/todo";

export default {
  name: 'RentAdd',
  data() {
    return {
      // lockTimeArray: [],
      // modifyPeriodRangesParams: {
      //   startPeriod: '',
      //   endPeriod: '',
      //   amount: ''
      // },
      paymentTimePeriods: [],
      //是否已经选中开始期数
      // isStartPeriodSelected: false,
      srcOne: '',
      srcTwo: '',
      srcThree: '',
      middleOne: false,
      middleTwoloding: false,
      form: {
        id: '',
        tenant: '',
        tenantType: '',
        paymentMethod: '',
        periods: '',
        monthlyRent: '',
        deposit: '',
        payDay: '',
        variables: "",
        auditType: '',
      },
      property: '',
      deployId: "",  // 流程定义编号
      procDefId: "",  // 流程实例编号
      formConf: {}, // 默认表单数据
      variables: [], // 流程变量数据
      taskTitle: null,
      taskOpen: false,
      checkSendUser: true, // 是否展示人员选择模块
      checkSendRole: true,// 是否展示角色选择模块
      checkType: '', // 选择类型
      checkValues: null, // 选中任务接收人员数据
      formData: {}, // 填写的表单数据,
      multiInstanceVars: '',// 会签节点
      tenantType: [
        {
          value: 0,
          label: '个人'
        },
        {
          value: 1,
          label: '企业'
        }
      ],
      paymentMethod: [
        {
          value: 0,
          label: '月付'
        },
        {
          value: 1,
          label: '季付'
        },
      ],
    }
  },
  created() {
    this.deployId = 7535;
    // 初始化表单
    this.procDefId = "flow_4muq3xka:4:7538",
      flowXmlAndNode({deployId: this.deployId}).then(res => {
        // console.log("sssssssssss"+JSON.stringify(res));
        this.flowData = res.data;
      })
  },

  mounted() {
    this.backInputInfo()
  },

  methods: {
    submitApply() {
      this.submitForm()
    },
    backInputInfo() {
      this.middleOne = true
    },

    submitForm() {
      const formData = {};
      const info = {};
      formData.formData = this.formConf
      formData.valData = info

      console.log("this.form"+JSON.stringify(this.form))
      // 根据当前任务或者流程设计配置的下一步节点 todo 暂时未涉及到考虑网关、表达式和多节点情况
      getNextFlowNodeByStart({deploymentId: this.deployId, variables: this.form}).then(res => {
        const data = res.data;
        if (data) {
          this.formData = formData;
          if (data.dataType === 'dynamic') {
            if (data.type === 'assignee') { // 指定人员
              this.checkSendUser = true;
              this.checkType = "single";
            } else if (data.type === 'candidateUsers') {  // 候选人员(多个)
              this.checkSendUser = true;
              this.checkType = "multiple";
            } else if (data.type === 'candidateGroups') { // 指定组(所属角色接收任务)
              this.checkSendRole = true;
            } else { // 会签
              // 流程设计指定的 elementVariable 作为会签人员列表
              this.multiInstanceVars = data.vars;
              this.checkSendUser = true;
              this.checkType = "multiple";
            }
            this.taskOpen = true;
            this.taskTitle = "选择任务接收";
          } else {
            const variables = {
              ...this.form,
              id: this.form.id
            }
            const formData = this.formData.formData;
            formData.disabled = true;
            formData.formBtns = false;
            if (this.procDefId) {
              variables.variables = formData;
              //启动流程并将表单数据加入流程变量
              // updateByAuditType(this.procDefId, JSON.stringify(variables)).then(res => {
              //   variables.auditType = res.msg

              // 在这，表放到了historyService里
              console.log("JSON.stringify(variables)"+JSON.stringify(variables))
                definitionStart(this.procDefId, JSON.stringify(variables)).then(res => {
                  this.$modal.msgSuccess(res.msg);
                  this.middleTwoloding = false
                })
              // })

            }
          }
        }
      })
    },
  }
}
</script>

<style scoped lang="scss">
.head-box {
  width: 100%;
  height: 100px;
  display: flex;
  justify-content: space-between;
}

.head-box-small {
  width: 100%;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.head-box img {
  width: 30px;
  height: 30px;
  margin-right: 20px;
}

.foot-box {
  display: flex;
  width: 100%;
  margin-top: 50px;
  justify-content: space-evenly;
  margin-bottom: 100px;
}

.body-box-one-small {
  width: 100%;
  display: flex;
}

.neck-box {
  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: center;
  margin-bottom: 20px;
  padding: 0px 100px 0px 100px;
}

.neck-box-small-send {
  display: flex;
  width: 100%;
  height: 50px;
  justify-content: center;
  align-items: center;
}

.neck-box-small-send img {
  width: 50px;
  margin-right: 20px;
}

.my-input {
  width: 100%;
}

.neck-box-small {
  display: flex;
  width: 100%;
  height: 50px;
  justify-content: space-between;
  align-items: center;
  background-color: #F0F0F0;
  padding: 0px 20px 0px 20px;
}

.neck-box-small-person {
  width: 100%;
  height: 50px;
  padding: 0px 100px 0px 100px;
  display: flex;
  margin-top: 20px;
  align-items: center;
}

.neck-box-small span {
  display: inline-block;
  height: 50px;
  line-height: 50px;
  font-size: 17px;
  font-weight: bold;
}

.body-box-three {
  padding: 0px 100px 0px 100px;
}

.middleThree-box {
  width: 100%;
  display: flex;
  margin-top: 100px;
}

.middleThree-box span {
  margin-right: 20px;
}

.statusImg {
  margin-top: 100px;
  width: 100%;
}

.my-status-img {
  display: flex;
  width: 100%;
  height: 130px;
  justify-content: center;
  align-items: center;
}

.my-status-img img {
  width: 100px;
  height: 100px;
}

.my-status-text {
  width: 100%;
  height: 100px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.my-status-button {
  width: 100%;
  display: flex;
  height: 100px;
  justify-content: center;
  align-items: center;
}

.my-status-text .my-text {
  display: inline-block;
  margin-top: 10px;
  font-size: 10px;

}

.period-container {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-gap: 15px;
  justify-content: space-evenly;
  margin-bottom: 20px;

  .title {
    white-space: nowrap;
  }

  .content .el-input {
    max-width: 12em;
  }

  .el-form-item {
    margin-bottom: 2px;
  }

  .period-card.active {
    border-color: #409eff;
    box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.5);
  }

  .content {
    padding-left: 16px;
  }

  //.el-checkbox ::v-deep .el-checkbox__input {
  //  padding-top: 11px;
  //}
}

@media screen and (max-width: 768px) {
  .period-container {
    grid-template-columns: 1fr;
  }
}

@media screen and (min-width: 768px) {
  .period-container {
    grid-template-columns: 1fr 1fr;
  }
}

@media screen and (min-width: 1200px) {
  .period-container {
    grid-template-columns: 1fr 1fr 1fr;
  }
}

@media screen and (min-width: 1680px) {
  .period-container {
    grid-template-columns: 1fr 1fr 1fr 1fr;
  }
}

@media screen and (min-width: 2160px) {
  .period-container {
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
  }
}

@media screen and (min-width: 2560px) {
  .period-container {
    grid-template-columns: repeat(auto-fill, 320px);
  }
}
</style>
