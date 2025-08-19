<template>
  <div id="viewQuestionsView">
    <a-row>
      <a-col :md="12" :xs="24">
        <a-tabs default-active-key="question">
          <a-tab-pane key="question" title="题目">
            <a-card v-if="question" :title="question.title">
              <a-descriptions
                :column="{ xs: 1, md: 2, lg: 3 }"
                title="判题条件"
              >
                <a-descriptions-item label="时间限制"
                  >{{ question.judgeConfig?.memoryLimit ?? 0 }}
                </a-descriptions-item>
                <a-descriptions-item label="空间限制"
                  >{{ question.judgeConfig?.memoryLimit ?? 0 }}
                </a-descriptions-item>
                <a-descriptions-item label="堆栈限制"
                  >{{ question.judgeConfig?.stackLimit ?? 0 }}
                </a-descriptions-item>
              </a-descriptions>
              <MdViewer :value="question.content || ''" />
              <template #extra>
                <a-space wrap>
                  <a-tag
                    v-for="(tag, index) of question.tags"
                    :key="index"
                    color="green"
                    >{{ tag }}
                  </a-tag>
                </a-space>
              </template>
            </a-card>
          </a-tab-pane>
          <a-tab-pane key="2" title="评论"></a-tab-pane>
        </a-tabs>
      </a-col>
      <a-col :md="12" :xs="24">
        <div style="display: flex; align-items: center; gap: 10px">
          <a-form :model="form" layout="inline">
            <a-form-item field="language" label="编程语言">
              <a-select
                v-model="form.language"
                :style="{ width: '200px' }"
                placeholder="选择编程语言"
              >
                <a-option value="cpp">C++</a-option>
                <a-option value="java">Java</a-option>
                <a-option value="go">Go</a-option>
              </a-select>
            </a-form-item>
          </a-form>
          <a-button style="min-width: 200px" type="primary" @click="doSubmit">
            提交代码
          </a-button>
        </div>

        <CodeEditor
          :handleChange="onCodeChange"
          :language="form.language"
          :value="form.code"
        />
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { defineProps, onMounted, ref, withDefaults } from "vue";
import {
  QuestionControllerService,
  QuestionSubmitAddRequest,
  QuestionVO,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import CodeEditor from "@/components/CodeEditor.vue";
import MdViewer from "@/components/MdViewer.vue";

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => "",
});

const question = ref<QuestionVO>();

const loadData = async () => {
  const res = await QuestionControllerService.getQuestionVoByIdUsingGet(
    props.id as any
  );
  if (res.code === 0) {
    question.value = res.data;
  } else {
    message.error("获取失败，" + res.message);
  }
};

/**
 * 页面加载时，请求数据
 */
onMounted(() => {
  loadData();
});

const onCodeChange = (code: string) => {
  form.value.code = code;
};

const form = ref<QuestionSubmitAddRequest>({
  code: "",
  language: "java",
  questionId: props.id as any,
});
/**
 * 提交代码
 */
const doSubmit = async () => {
  const res = await QuestionControllerService.doQuestionSubmitUsingPost(
    form.value
  );
  if (res.code === 0) {
    message.success("提交成功");
  } else {
    message.error("提交失败，" + res.message);
  }
};
</script>

<style>
#viewQuestionsView {
  margin: 0 auto;
}

#viewQuestionsView .arco-space-horizontal .arco-space-horizontal {
  margin-bottom: 0 !important;
}
</style>
