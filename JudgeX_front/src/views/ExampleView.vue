<template>
  <div class="home">
    <CodeEditor :handle-change="onCodeChange" :value="codeValue" />
    <MdEditor :handle-change="onMdChange" :value="mdValue" />
  </div>
</template>
<script lang="ts" setup>
import MdEditor from "@/components/MdEditor.vue"; // @ is an alias to /src
import { ref } from "vue";
import CodeEditor from "@/components/CodeEditor.vue";

const codeValue = ref("");
const mdValue = ref("");

const onCodeChange = (v: string) => {
  codeValue.value = v;
  console.log(v);
};
const onMdChange = (v: string) => {
  mdValue.value = v;
  console.log(v);
};
</script>
