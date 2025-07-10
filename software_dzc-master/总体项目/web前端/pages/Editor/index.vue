<template>
  <div>
    <QuillEditor ref="myQuillEditor"
                 theme="snow"
                 v-model:content="content"
                 :options="data.editorOption"
                 contentType="html"
                 @update:content="setValue"
    />
    <input type="file" hidden accept=".jpg,.png" ref="fileBtn" @change="handleUpload" />
  </div>
</template>

<script setup>
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import { reactive, onMounted, ref, toRaw, watch } from 'vue'
import axios from '../../utils/request.js'

const props = defineProps(['value'])
const emit = defineEmits(['updateValue'])
const content = ref(props.value || '')
const myQuillEditor = ref()

watch(() => props.value, (val) => {
  content.value = val
  toRaw(myQuillEditor.value).setHTML(val)
}, { deep: true })

const fileBtn = ref()
const data = reactive({
  content: '',
  editorOption: {
    modules: {
      toolbar: [
        ['bold', 'italic', 'underline', 'strike'],
        [{ 'size': ['small', false, 'large', 'huge'] }],
        [{ 'font': [] }],
        [{ 'align': [] }],
        [{ 'list': 'ordered' }, { 'list': 'bullet' }],
        [{ 'indent': '-1' }, { 'indent': '+1' }],
        [{ 'header': 1 }, { 'header': 2 }],
        ['image'],
        [{ 'direction': 'rtl' }],
        [{ 'color': [] }, { 'background': [] }]
      ]
    },
    placeholder: '请输入内容...'
  }
})

const imgHandler = (state) => {
  if (state) {
    fileBtn.value.click()
  }
}

const setValue = () => {
  content.value = toRaw(myQuillEditor.value).getHTML()
  emit('updateValue', content.value)
}

const handleUpload = async (e) => {
  const file = e.target.files[0];
  if (!file) return;

  const isImage = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isImage) {
    console.error('上传图片只能是 JPG/PNG 格式!');
    return;
  }

  if (!isLt5M) {
    console.error('上传图片大小不能超过 5MB!');
    return;
  }

  const formData = new FormData();
  formData.append('file', file);

  try {
    const res = await axios.post('http://localhost:9049/api/upload', formData);
    if (res.data.path) {
      const localPath = res.data.path;
      insertImage(localPath);
    } else {
      console.error('服务器返回的路径无效:', res.data);
    }
  } catch (error) {
    console.error('图片上传失败:', error);
  }
}

const insertImage = (path) => {
  const quill = toRaw(myQuillEditor.value).getQuill();
  const length = quill.getSelection()?.index || quill.getLength();
  const imageUrl = `file://${path.replace(/\\/g, '/')}`;
  quill.insertEmbed(length, 'image', imageUrl);
  quill.setSelection(length + 1);
  setValue();
}

onMounted(() => {
  const quill = toRaw(myQuillEditor.value).getQuill();
  quill.getModule('toolbar').addHandler('image', imgHandler);
});
</script>

<style scoped lang="scss">
:deep(.ql-editor) {
  min-height: 180px;
}

:deep(.ql-formats) {
  height: 21px;
  line-height: 21px;
}
</style>
