<template>
    <el-container class="layout-container-demo">
        <el-header>
            <el-row>
                <el-col :span="12">
                    <h1>Mybatis Generator Web</h1>
                </el-col>
                <el-col :span="12">
                    <div style="float: right">
                        <el-button type="primary" @click="newConnectionVisible = true">数据库链接</el-button>
                    </div>
                </el-col>
            </el-row>
        </el-header>
        <el-divider />
        <el-container>
            <el-aside width="300px">
                <el-scrollbar>
                    <el-tree :data="databases" node-key="id" default-expand-all :expand-on-click-node="false">
                        <template #default="{ node, data }">
                            <span class="custom-tree-node">
                                <span class="custom-tree-node-item">
                                    <el-icon v-if="data.host"><Cellphone /></el-icon>
                                    <el-icon v-else><Platform /></el-icon>
                                    {{ data.name }}
                                </span>
                                <span v-if="data.host">
                                    <el-button-group>
                                        <el-button size="small" @click="editConn(data)"
                                            ><el-icon><Edit /></el-icon
                                        ></el-button>
                                        <el-button size="small" type="primary" @click="tables(data)"
                                            ><el-icon><Refresh /></el-icon
                                        ></el-button>
                                        <el-button size="small" type="danger" @click="deleteConn(data)"
                                            ><el-icon><Delete /></el-icon
                                        ></el-button>
                                    </el-button-group>
                                </span>
                                <span v-else>
                                    <el-button size="small" type="primary" @click="aim(data)"
                                        ><el-icon><Aim /></el-icon
                                    ></el-button>
                                </span>
                            </span>
                        </template>
                    </el-tree>
                </el-scrollbar>
            </el-aside>

            <el-main>
                <el-scrollbar>
                    <el-form :model="form" label-width="150px">
                        <el-form-item label="表名">
                            <el-input v-model="form.table" disabled />
                        </el-form-item>
                        <el-form-item label="Java实体类名">
                            <el-input v-model="form.bean" />
                        </el-form-item>
                        <el-form-item label="主键(选填)">
                            <el-input v-model="form.primaryKey" placeholder="primary key, such as id" />
                        </el-form-item>
                        <el-form-item label="项目所在目录">
                            <el-input v-model="form.path" placeholder="/User/xx/projects/example" />
                        </el-form-item>
                        <el-row>
                            <el-col :span="12">
                                <el-form-item label="实体类包名">
                                    <el-input v-model="form.beanPackage" placeholder="com.example.model" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="存放目录">
                                    <el-input v-model="form.beanDir" />
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="12">
                                <el-form-item label="Mapper类包名">
                                    <el-input v-model="form.mapperPackage" placeholder="com.example.mapper" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="存放目录">
                                    <el-input v-model="form.mapperDir" />
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-form-item label="自定义接口名称(选填)">
                            <el-input v-model="form.interfaceName" />
                        </el-form-item>
                        <el-row>
                            <el-col :span="12">
                                <el-form-item label="映射XML文件包名">
                                    <el-input v-model="form.xmlPackage" placeholder="com.example" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="存放目录">
                                    <el-input v-model="form.xmlDir" />
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-form-item label="文件编码">
                            <el-select v-model="form.encoding">
                                <el-option label="UTF-8" value="UTF-8" />
                            </el-select>
                        </el-form-item>
                        <el-form-item>
                            <el-checkbox label="使用Example" v-model="form.useExample" />
                            <el-checkbox label="分页插件(暂时只支持MySQL和PostgreSQL)" v-model="form.offsetLimitCheckBox" />
                            <el-checkbox label="生成实体域注释(来自表注释)" v-model="form.commentCheckBox" />
                            <el-checkbox label="覆盖原XML" v-model="form.overrideXML" />
                            <el-checkbox label="LombokPlugin" v-model="form.useLombokPlugin" />
                            <el-checkbox label="生成toString/hashCode/equals方法" v-model="form.needToStringHashcodeEquals" />
                            <el-checkbox label="使用Schema前缀" v-model="form.useSchemaPrefix" />
                            <el-checkbox label="select 增加ForUpdate" v-model="form.forUpdateCheckBox" />
                            <el-checkbox label="DAO使用 @Repository 注解" v-model="form.annotationDAOCheckBox" />
                            <el-checkbox label="DAO方法抽出到公共父接口" v-model="form.useDAOExtendStyle" />
                            <el-checkbox label="JSR310: Date and Time API" v-model="form.jsr310Support" />
                            <el-checkbox label="生成JPA注解" v-model="form.annotationCheckBox" />
                            <el-checkbox label="使用实际的列名" v-model="form.useActualColumnNamesCheckbox" />
                            <el-checkbox label="启用as别名查询" v-model="form.useTableNameAliasCheckbox" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="generate">代码生成</el-button>
                            <el-button type="info" @click="saveConf">保存配置</el-button>
                        </el-form-item>
                    </el-form>
                </el-scrollbar>
            </el-main>
        </el-container>

        <el-dialog v-model="newConnectionVisible" title="数据库链接" width="50%" center>
            <span>
                <el-form :model="conn" label-width="120px">
                    <el-form-item label="保存名称">
                        <el-input v-model="conn.name" />
                    </el-form-item>
                    <el-form-item label="数据库类型">
                        <el-select v-model="conn.type">
                            <el-option label="MySQL" value="MySQL" />
                            <el-option label="MySQL_8" value="MySQL_8" />
                            <el-option label="Oracle" value="Oracle" />
                            <el-option label="PostgreSQL" value="PostgreSQL" />
                            <el-option label="SQL Server" value="SQL Server" />
                            <el-option label="SQLite" value="SQLite" />
                            <el-option label="MariaDB" value="MariaDB" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="主机名或IP地址">
                        <el-input v-model="conn.host" />
                    </el-form-item>
                    <el-form-item label="端口号">
                        <el-input v-model="conn.port" />
                    </el-form-item>
                    <el-form-item label="用户名">
                        <el-input v-model="conn.username" />
                    </el-form-item>
                    <el-form-item label="密码">
                        <el-input v-model="conn.password" type="password" />
                    </el-form-item>
                    <el-form-item label="Schema/数据库">
                        <el-input v-model="conn.schema" />
                    </el-form-item>
                    <el-form-item label="编码">
                        <el-select v-model="conn.encoding">
                            <el-option label="UTF-8" value="UTF-8" />
                            <el-option label="GB2312" value="GB2312" />
                            <el-option label="GBK" value="GBK" />
                        </el-select>
                    </el-form-item>
                </el-form>
            </span>
            <template #footer>
                <span class="dialog-footer">
                    <el-button type="success" @click="testConnection"> 测试链接 </el-button>
                    <el-button type="primary" @click="saveConf"> 确定 </el-button>
                    <el-button @click="newConnectionVisible = false">关闭</el-button>
                </span>
            </template>
        </el-dialog>
    </el-container>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from "vue";
import {Cellphone, Delete, Edit, Platform} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { request } from "@/utils/http";

const newConnectionVisible = ref(false);
const form = ref({
    id: "",
    connectionId: "",
    table: "",
    bean: "",
    primaryKey: "",
    path: "",
    beanPackage: "",
    beanDir: "src/main/java",
    mapperPackage: "",
    mapperDir: "src/main/java",
    interfaceName: "",
    xmlPackage: "",
    xmlDir: "src/main/resources",
    encoding: "UTF-8",
    useExample: false,
    offsetLimitCheckBox: true,
    commentCheckBox: true,
    overrideXML: true,
    useLombokPlugin: false,
    needToStringHashcodeEquals: true,
    useSchemaPrefix: false,
    forUpdateCheckBox: false,
    annotationDAOCheckBox: true,
    useDAOExtendStyle: true,
    jsr310Support: false,
    annotationCheckBox: false,
    useActualColumnNamesCheckbox: false,
    useTableNameAliasCheckbox: false,
});

const conn = ref({
    id: "",
    name: "",
    type: "MySQL",
    host: "",
    port: "",
    username: "",
    password: "",
    schema: "",
    encoding: "UTF-8",
});

const databases = ref([]);
const cache = ref({});

const refresh = () => {
    // (async () => {
    //     const res = await mock.getValue(mock.connectionListData);
    //     databases.value = res.data;
    // })();

    request.get("/api/connection/list").then(function (resp: any) {
        databases.value = [];
        databases.value.push(...resp.data);
        resp.data.forEach((elem) => {
            cache.value[elem.id] = elem;
        });
    });
};

onMounted(() => {
    refresh();
});

const generate = () => {
    request.post("/api/main/generate", form.value).then(function (resp: any) {
        ElMessage.success(resp.message);
    });
};

const saveConf = () => {
    request.post("/api/connection/save", conn.value).then(function (resp: any) {
        ElMessage.success(resp.message);
        newConnectionVisible.value = false;
        refresh();
    });
};

const testConnection = () => {
    request.post("/api/connection/test", conn.value).then(function (resp: any) {
        ElMessage.success(resp.message);
    });
};

const tables = (conn) => {
    request.get("/api/main/list/table?id=" + conn.id).then(function (resp: any) {
        cache.value[conn.id].children = [];
        resp.data.forEach((elem) => {
            cache.value[conn.id].children.push({
                id: elem,
                name: elem,
                connId: conn.id,
            });
        });
    });
};

const deleteConn = (conn) => {
    request
        .post("/api/connection/delete", {
            id: conn.id,
        })
        .then(function (resp: any) {
            ElMessage.success(resp.message);
            refresh();
        });
};

const aim = (data) => {
    const hump = toHump(data.name);
    form.value.connectionId = data.connId;
    form.value.table = data.name;
    form.value.bean = hump;
    form.value.interfaceName = hump + "Mapper";
};

const editConn = (data) => {
    conn.value = data
    newConnectionVisible.value = true;
}

const toHump = (name) => {
    const res = name.replace(/\_(\w)/g, function (all, letter) {
        return letter.toUpperCase();
    });
    return res.substring(0, 1).toUpperCase() + res.substring(1);
};
</script>

<style lang="less">
.custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;

    &-item {
        display: flex;
        align-items: center;
        justify-content: center;

        i {
            margin-right: 4px;
        }
    }
}
</style>
