<template>
  <n-form
    :label-width="90"
    :model="formValue"
    :rules="rules"
    label-placement="left"
    ref="form1Ref"
    style="max-width: 500px; margin: 40px auto 0 80px"
  >
    <n-form-item label="原密码" path="prePassword">
      <n-input type="password" v-model:value="formValue.prePassword" />
    </n-form-item>
    <n-form-item label="新密码" path="newPassword">
      <n-input type="password" v-model:value="formValue.newPassword" />
    </n-form-item>
    <n-divider />
    <div style="margin-left: 80px">
      <n-space>
        <n-button type="primary" :loading="loading" @click="formSubmit">提交</n-button>
      </n-space>
    </div>
  </n-form>
</template>

<script lang="ts" setup>
  import { ref, reactive, defineEmits } from 'vue';
  import { useMessage } from 'naive-ui';
  import { changePassword } from '@/./api/system/user';
  import { storage } from '@/utils/Storage';

  const emit = defineEmits(['prevStep', 'nextStep']);

  const form1Ref: any = ref(null);
  const message = useMessage();

  let url = window.location.href;
  let p = url.split('=')[1];

  const formValue = reactive({
    prePassword: '',
    newPassword: '',
  });

  const rules = {
    newPassword: {
      required: true,
      message: '请输入新密码',
      trigger: 'blur',
    },
    prePassword: {
      required: true,
      message: '请输入原密码',
      trigger: 'blur',
    },
  };

  function prevStep() {
    emit('prevStep');
  }

  function formSubmit() {
    //console.log(this.$route.path);
    form1Ref.value.validate(async (errors) => {
      if (!errors) {
        let response = await changePassword(p, { ...formValue });
        // await changePassword({...formValue});
        if (response.code != 200) message.error(response.message.toString());
        else if (response.code == 200) {
          message.success('修改成功！');
          emit('nextStep');
        }
      } else {
        message.error('验证失败，请填写完整信息');
      }
    });
  }
  // axios.defaults.baseURL="/api";
  //
  // function formSubmit() {
  //   loading.value = true;
  //   form2Ref.value.validate((errors) => {
  //     if (!errors) {
  //       axios.post('http://yemaoluo.top:7000/api/admin/account?verifyToken=56a417031ac34ddf9835cc84299feee1&newpassword=' +
  //         formValue.value.newPassword)
  //         .then(resp=>{
  //           console.log(resp.data)
  //         })
  //         .catch(err=>{
  //           console.log(err)
  //         })
  //     } else {
  //       message.error('验证失败，请填写完整信息');
  //     }
  //   });
  // }
</script>
