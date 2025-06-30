<template>
  <el-container>
    <el-main>
      <!-- 搜索栏 -->
      <el-row :gutter="20" class="search-bar">
        <el-col :span="4">
          <el-input
              placeholder="请输入新闻标题"
              v-model="searchForm.title"
              class="search-box"
          ></el-input>
        </el-col>
        <el-col :span="4">
          <el-input
              placeholder="请输入作者"
              v-model="searchForm.author"
              class="search-box"
          ></el-input>
        </el-col>
        <el-col :span="4">
          <el-input
              placeholder="请输入新闻简介"
              v-model="searchForm.introduction"
              class="search-box"
          ></el-input>
        </el-col>
        <el-col :span="2">
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        </el-col>
        <el-col :span="2">
          <el-button type="default" icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>

      <!-- 功能按钮栏 -->
      <el-row :gutter="20" style="margin-top: 10px;">
        <el-col :span="24" class="button-bar">
          <el-button type="primary" icon="el-icon-plus" @click="openDialog">新建</el-button>
          <el-button type="success" icon="el-icon-edit" @click="handleEdit">修改</el-button>
          <el-button type="danger" icon="el-icon-delete" @click="handleDelete">删除</el-button>
          <el-button type="warning" icon="el-icon-upload2" @click="handleExport">导出</el-button>
        </el-col>
      </el-row>

      <!-- 资讯列表表格 -->
      <el-table :data="paginatedInformationList" style="width: 100%; margin-top: 20px;" @row-click="handleRowClick">
        <el-table-column prop="title" label="新闻标题"></el-table-column>
        <el-table-column prop="introduction" label="新闻简介"></el-table-column>
        <el-table-column prop="author" label="作者"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-link @click="editInformation(scope.row)" type="primary" class="action-link">修改</el-link>
            <el-link @click="confirmDelete(scope.row.id)" type="danger" class="action-link">删除</el-link>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredInformationList.length"
          :page-sizes="[10, 20, 30, 40]"
          :page-size.sync="pageSize"
          :current-page.sync="currentPage"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
          style="margin-top: 20px; text-align: right;"
      >
      </el-pagination>

      <!-- 新建/编辑资讯弹窗 -->
      <el-dialog v-model="dialogVisible" title="添加资讯管理" width="50%">
        <el-form :model="form" :rules="formRules" ref="informationForm">
          <el-form-item label="新闻标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入新闻标题"></el-input>
          </el-form-item>
          <el-form-item label="新闻图片路径" prop="picture">
            <el-upload
                class="upload-demo"
                action="/api/upload"
                :on-success="handleSuccess"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :before-upload="beforeUpload"
                :file-list="fileList"
            >
              <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
              <div slot="tip" class="el-upload__tip">请上传大小不超过 5MB 格式为 png/jpg/jpeg 的文件</div>
            </el-upload>
          </el-form-item>
          <el-form-item label="新闻内容" prop="content">
            <Editor v-model:value="form.content" @updateValue="updateContent" />
          </el-form-item>
          <el-form-item label="作者">
            <el-input v-model="form.author" placeholder="请输入作者"></el-input>
          </el-form-item>
          <el-form-item label="新闻简介" prop="introduction">
            <el-input v-model="form.introduction" type="textarea" placeholder="请输入新闻简介"></el-input>
          </el-form-item>
          <el-form-item label="选择租户">
            <el-select v-model="form.tenantId" placeholder="请选择" @change="handleTenantChange">
              <el-option
                  v-for="item in tenants"
                  :key="item.id"
                  :label="item.tenantName"
                  :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveInformation">确定</el-button>
        </span>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ref } from 'vue';
import Editor from './Editor/index.vue';

export default {
  components: {
    Editor
  },
  data() {
    return {
      informationList: [],
      dialogVisible: false,
      isEditing: false,
      form: {
        id: '',
        title: '',
        introduction: '',
        author: '',
        content: '<p>这里是默认的新闻内容。你可以在此编辑或添加新的内容。</p>',
        company: '',  // 用于存储公司名称
        tenantId: '',  // 用于存储租户ID
        picture: ''  // 用于存储图片路径
      },
      formRules: {
        title: [
          { required: true, message: '请输入新闻标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入新闻内容', trigger: 'blur' }
        ],
        introduction: [
          { required: true, message: '请输入新闻简介', trigger: 'blur' }
        ],
        picture: [
          { required: true, message: '请上传新闻图片', trigger: 'change' }
        ]
      },
      tenants: [],  // 用于存储租户信息
      fileList: [],
      selectedInformation: null,
      searchForm: {
        title: '',
        author: '',
        introduction: '',
      },
      filteredInformationList: [],
      currentPage: 1,
      pageSize: 10,
    };
  },
  mounted() {
    this.fetchInformation();
    this.fetchTenants();  // 在 mounted 生命周期钩子中调用获取租户信息的方法
  },
  computed: {
    paginatedInformationList() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredInformationList.slice(start, end);
    }
  },
  methods: {
    fetchInformation() {
      axios.get('/api/information')
          .then(response => {
            if (Array.isArray(response.data)) {
              this.informationList = response.data;
              this.filteredInformationList = response.data;
            } else {
              console.error('API response is not an array:', response.data);
            }
          })
          .catch(error => {
            console.error('获取资讯时出错:', error);
          });
    },
    fetchTenants() {
      axios.get('/api/tenants/list')  // 更新为新的路径
          .then(response => {
            this.tenants = response.data.tenantList;
          })
          .catch(error => {
            console.error('获取租户信息时出错:', error);
          });
    },
    openDialog() {
      this.resetForm();
      this.dialogVisible = true;
      this.isEditing = false;
    },
    editInformation(info) {
      this.isEditing = true;
      this.form = { ...info };
      this.dialogVisible = true;
    },
    saveInformation() {
      // 表单验证
      this.$refs.informationForm.validate((valid) => {
        if (!valid) {
          ElMessage.error('请完善必填信息');
          return;
        }

        // 检查必填字段
        if (!this.form.title || !this.form.content || !this.form.introduction) {
          ElMessage.error('新闻标题、新闻内容、新闻简介不能为空');
          return;
        }

        if (!this.form.picture && !this.isEditing) {
          ElMessage.error('请上传新闻图片');
          return;
        }

        const operation = this.isEditing ? 
          axios.put(`/api/information/${this.form.id}`, this.form) :
          axios.post('/api/information', this.form);

        operation
          .then(response => {
            this.fetchInformation();
            this.dialogVisible = false;
            this.resetForm();
            ElMessage({
              type: 'success',
              message: this.isEditing ? '资讯更新成功' : '资讯添加成功'
            });
          })
          .catch(error => {
            console.error(`${this.isEditing ? '更新' : '添加'}资讯时出错:`, error);
            ElMessage({
              type: 'error',
              message: `${this.isEditing ? '更新' : '添加'}资讯失败，请重试`
            });
          });
      });
    },
    handleTenantChange(value) {
      const selectedTenant = this.tenants.find(tenant => tenant.id === value);
      this.form.company = selectedTenant ? selectedTenant.tenantName : '';
      this.form.tenantId = value;
    },
    updateContent(value) {
      this.form.content = value;
    },
    handleSuccess(response, file) {
      this.form.picture = response.path;
      const imageUrl = response.path.replace(/\\/g, '/');
      const imgTag = `<img src="${imageUrl}" alt="新闻图片" style="max-width: 100%;" />`;
      this.form.content += imgTag;
      
      // 触发表单验证，清除图片必填的错误提示
      this.$nextTick(() => {
        this.$refs.informationForm.validateField('picture');
      });
    },
    confirmDelete(id) {
      ElMessageBox.confirm('此操作将永久删除该资讯, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        this.deleteInformation(id);
      }).catch(() => {
        ElMessage({
          type: 'info',
          message: '已取消删除',
        });
      });
    },
    deleteInformation(id) {
      axios.delete(`/api/information/${id}`)
          .then(response => {
            this.fetchInformation();
          })
          .catch(error => {
            console.error('删除资讯时出错:', error);
          });
    },
    handleEdit() {
      if (this.selectedInformation) {
        this.editInformation(this.selectedInformation);
      } else {
        ElMessage({
          type: 'warning',
          message: '请先选择一条记录',
        });
      }
    },
    handleDelete() {
      if (this.selectedInformation) {
        this.confirmDelete(this.selectedInformation.id);
      } else {
        ElMessage({
          type: 'warning',
          message: '请先选择一条记录',
        });
      }
    },
    handleExport() {
      ElMessage({
        type: 'success',
        message: '导出成功',
      });
    },
    handleSearch() {
      this.filteredInformationList = this.informationList.filter(info => {
        return (
            (!this.searchForm.title || info.title.includes(this.searchForm.title)) &&
            (!this.searchForm.author || info.author.includes(this.searchForm.author)) &&
            (!this.searchForm.introduction || info.introduction.includes(this.searchForm.introduction))
        );
      });
      this.currentPage = 1;
    },
    resetSearch() {
      this.searchForm = {
        title: '',
        author: '',
        introduction: '',
      };
      this.filteredInformationList = this.informationList;
      this.currentPage = 1;
    },
    handlePageChange(page) {
      this.currentPage = page;
    },
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
    },
    handleRowClick(row) {
      this.selectedInformation = row;
    },
    resetForm() {
      this.form = {
        id: '',
        title: '',
        introduction: '',
        author: '',
        content: '<p>这里是默认的新闻内容。你可以在此编辑或添加新的内容。</p>',
        company: '',  // 重置公司字段
        tenantId: '',  // 重置租户ID字段
        picture: ''  // 重置图片路径字段
      };
      this.fileList = [];
      
      // 清除表单验证状态
      if (this.$refs.informationForm) {
        this.$refs.informationForm.resetFields();
      }
    },
    handlePreview(file) {
      console.log(file);
    },
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    beforeUpload(file) {
      const isJPG = file.type === 'image/jpeg';
      const isPNG = file.type === 'image/png';
      const isLt5M = file.size / 1024 / 1024 < 5;

      if (!isJPG && !isPNG) {
        this.$message.error('上传图片只能是 JPG/PNG 格式!');
      }
      if (!isLt5M) {
        this.$message.error('上传图片大小不能超过 5MB!');
      }
      return (isJPG || isPNG) && isLt5M;
    }
  }
};
</script>

<style>
.el-table {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.el-button + .el-button {
  margin-left: 10px;
}

.upload-demo {
  width: 200px;
  margin: 0 auto;
}

.search-bar {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-top: 10px;
}

.search-box {
  margin-right: 10px;
  width: 200px;
}

.action-link {
  margin-right: 10px;
  color: blue;
}
</style>
