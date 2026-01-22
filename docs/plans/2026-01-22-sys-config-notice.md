# 模块二：系统配置与公告 实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 实现公告管理、轮播图管理、系统配置功能，并在首页展示轮播图和公告列表

**Architecture:** 后端采用SpringBoot + MyBatis-Plus实现CRUD接口，文件上传保存到本地目录；前端使用Vue3 + Element Plus实现管理页面和首页展示组件

**Tech Stack:** SpringBoot 2.7, MyBatis-Plus 3.5, Vue 3, Element Plus, Vite

---

## Phase 1: 后端基础设施

### Task 1: 创建公告实体类

**Files:**
- Create: `hangfuzhulingxit/src/main/java/com/hanfu/entity/SysNotice.java`

**Step 1: 创建实体类**

```java
package com.hanfu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_notice")
public class SysNotice {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private String content;
    private Integer type;  // 1公告, 2轮播图
    private String imgUrl;
    private LocalDateTime createTime;
}
```

**Step 2: 验证编译通过**

在IDEA中确认无编译错误

---

### Task 2: 创建系统配置实体类

**Files:**
- Create: `hangfuzhulingxit/src/main/java/com/hanfu/entity/SysConfig.java`

**Step 1: 创建实体类**

```java
package com.hanfu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_config")
public class SysConfig {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String paramKey;
    private String paramValue;
    private String paramDesc;
    private LocalDateTime createTime;
}
```

---

### Task 3: 创建Mapper接口

**Files:**
- Create: `hangfuzhulingxit/src/main/java/com/hanfu/mapper/SysNoticeMapper.java`
- Create: `hangfuzhulingxit/src/main/java/com/hanfu/mapper/SysConfigMapper.java`

**Step 1: 创建SysNoticeMapper**

```java
package com.hanfu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanfu.entity.SysNotice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysNoticeMapper extends BaseMapper<SysNotice> {
}
```

**Step 2: 创建SysConfigMapper**

```java
package com.hanfu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanfu.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {
}
```

---

### Task 4: 创建Service层

**Files:**
- Create: `hangfuzhulingxit/src/main/java/com/hanfu/service/NoticeService.java`
- Create: `hangfuzhulingxit/src/main/java/com/hanfu/service/impl/NoticeServiceImpl.java`
- Create: `hangfuzhulingxit/src/main/java/com/hanfu/service/ConfigService.java`
- Create: `hangfuzhulingxit/src/main/java/com/hanfu/service/impl/ConfigServiceImpl.java`

**Step 1: NoticeService接口**

```java
package com.hanfu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanfu.entity.SysNotice;
import java.util.List;

public interface NoticeService {
    // 分页查询（后台）
    IPage<SysNotice> getPage(Integer pageNum, Integer pageSize, Integer type);
    // 获取公告列表（前台）
    List<SysNotice> getNoticeList();
    // 获取轮播图列表（前台）
    List<SysNotice> getBannerList();
    // 获取详情
    SysNotice getById(Integer id);
    // 新增
    void add(SysNotice notice);
    // 更新
    void update(SysNotice notice);
    // 删除
    void delete(Integer id);
}
```

**Step 2: NoticeServiceImpl实现**

```java
package com.hanfu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanfu.entity.SysNotice;
import com.hanfu.mapper.SysNoticeMapper;
import com.hanfu.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private final SysNoticeMapper noticeMapper;

    @Override
    public IPage<SysNotice> getPage(Integer pageNum, Integer pageSize, Integer type) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(SysNotice::getType, type);
        }
        wrapper.orderByDesc(SysNotice::getCreateTime);
        return noticeMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<SysNotice> getNoticeList() {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotice::getType, 1).orderByDesc(SysNotice::getCreateTime).last("LIMIT 10");
        return noticeMapper.selectList(wrapper);
    }

    @Override
    public List<SysNotice> getBannerList() {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotice::getType, 2).orderByDesc(SysNotice::getCreateTime);
        return noticeMapper.selectList(wrapper);
    }

    @Override
    public SysNotice getById(Integer id) {
        return noticeMapper.selectById(id);
    }

    @Override
    public void add(SysNotice notice) {
        noticeMapper.insert(notice);
    }

    @Override
    public void update(SysNotice notice) {
        noticeMapper.updateById(notice);
    }

    @Override
    public void delete(Integer id) {
        noticeMapper.deleteById(id);
    }
}
```

**Step 3: ConfigService接口和实现**

```java
// ConfigService.java
package com.hanfu.service;

import com.hanfu.entity.SysConfig;
import java.util.List;

public interface ConfigService {
    List<SysConfig> getAll();
    String getValue(String key);
    void update(SysConfig config);
}
```

```java
// ConfigServiceImpl.java
package com.hanfu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hanfu.entity.SysConfig;
import com.hanfu.mapper.SysConfigMapper;
import com.hanfu.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {
    private final SysConfigMapper configMapper;

    @Override
    public List<SysConfig> getAll() {
        return configMapper.selectList(null);
    }

    @Override
    public String getValue(String key) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getParamKey, key);
        SysConfig config = configMapper.selectOne(wrapper);
        return config != null ? config.getParamValue() : null;
    }

    @Override
    public void update(SysConfig config) {
        configMapper.updateById(config);
    }
}
```

---

### Task 5: 创建文件上传服务

**Files:**
- Create: `hangfuzhulingxit/src/main/java/com/hanfu/service/FileService.java`
- Create: `hangfuzhulingxit/src/main/java/com/hanfu/service/impl/FileServiceImpl.java`

**Step 1: 创建文件上传服务**

```java
// FileService.java
package com.hanfu.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String upload(MultipartFile file);
}
```

```java
// FileServiceImpl.java
package com.hanfu.service.impl;

import com.hanfu.common.BusinessException;
import com.hanfu.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 获取原始文件名和扩展名
        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf("."));
        
        // 生成新文件名
        String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;
        
        // 创建目录
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 保存文件
        File dest = new File(uploadPath + newFileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }
        
        // 返回访问路径
        return "/uploads/" + newFileName;
    }
}
```

---

### Task 6: 创建Controller层

**Files:**
- Create: `hangfuzhulingxit/src/main/java/com/hanfu/controller/FileController.java`
- Create: `hangfuzhulingxit/src/main/java/com/hanfu/controller/NoticeController.java`
- Create: `hangfuzhulingxit/src/main/java/com/hanfu/controller/ConfigController.java`

**Step 1: FileController**

```java
package com.hanfu.controller;

import com.hanfu.common.Result;
import com.hanfu.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        return Result.success("上传成功", url);
    }
}
```

**Step 2: NoticeController**

```java
package com.hanfu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanfu.common.Result;
import com.hanfu.entity.SysNotice;
import com.hanfu.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 前台接口 - 获取公告列表
    @GetMapping("/list")
    public Result<List<SysNotice>> getNoticeList() {
        return Result.success(noticeService.getNoticeList());
    }

    // 前台接口 - 获取轮播图
    @GetMapping("/banners")
    public Result<List<SysNotice>> getBanners() {
        return Result.success(noticeService.getBannerList());
    }

    // 前台接口 - 公告详情
    @GetMapping("/{id}")
    public Result<SysNotice> getDetail(@PathVariable Integer id) {
        return Result.success(noticeService.getById(id));
    }

    // 后台接口 - 分页查询
    @GetMapping("/admin/page")
    public Result<IPage<SysNotice>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type) {
        return Result.success(noticeService.getPage(pageNum, pageSize, type));
    }

    // 后台接口 - 新增
    @PostMapping("/admin")
    public Result<Void> add(@RequestBody SysNotice notice) {
        noticeService.add(notice);
        return Result.success("添加成功", null);
    }

    // 后台接口 - 更新
    @PutMapping("/admin")
    public Result<Void> update(@RequestBody SysNotice notice) {
        noticeService.update(notice);
        return Result.success("更新成功", null);
    }

    // 后台接口 - 删除
    @DeleteMapping("/admin/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        noticeService.delete(id);
        return Result.success("删除成功", null);
    }
}
```

**Step 3: ConfigController**

```java
package com.hanfu.controller;

import com.hanfu.common.Result;
import com.hanfu.entity.SysConfig;
import com.hanfu.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @GetMapping("/list")
    public Result<List<SysConfig>> getAll() {
        return Result.success(configService.getAll());
    }

    @PutMapping
    public Result<Void> update(@RequestBody SysConfig config) {
        configService.update(config);
        return Result.success("更新成功", null);
    }
}
```

---

### Task 7: 更新拦截器配置

**Files:**
- Modify: `hangfuzhulingxit/src/main/java/com/hanfu/config/WebConfig.java`

**Step 1: 添加公开接口白名单**

在excludePathPatterns中添加：
- `/api/notice/list`
- `/api/notice/banners`
- `/api/notice/{id}`
- `/api/file/upload`

---

## Phase 2: 后端测试

### Task 8: 启动后端并测试接口

**Step 1: 启动后端应用**

在IDEA中运行 HanfuApplication

**Step 2: 测试轮播图接口**

```
GET http://localhost:8080/api/notice/banners
Expected: {"code":200,"data":[],"message":"success"}
```

**Step 3: 测试公告列表接口**

```
GET http://localhost:8080/api/notice/list
Expected: {"code":200,"data":[],"message":"success"}
```

---

## Phase 3: 前端API层

### Task 9: 创建前端API文件

**Files:**
- Create: `vue/src/api/notice.js`
- Create: `vue/src/api/config.js`
- Create: `vue/src/api/file.js`

**Step 1: notice.js**

```javascript
import request from '@/utils/request'

// 前台 - 获取公告列表
export function getNoticeList() {
  return request({ url: '/notice/list', method: 'get' })
}

// 前台 - 获取轮播图
export function getBanners() {
  return request({ url: '/notice/banners', method: 'get' })
}

// 前台 - 公告详情
export function getNoticeDetail(id) {
  return request({ url: `/notice/${id}`, method: 'get' })
}

// 后台 - 分页查询
export function getNoticePage(params) {
  return request({ url: '/notice/admin/page', method: 'get', params })
}

// 后台 - 新增
export function addNotice(data) {
  return request({ url: '/notice/admin', method: 'post', data })
}

// 后台 - 更新
export function updateNotice(data) {
  return request({ url: '/notice/admin', method: 'put', data })
}

// 后台 - 删除
export function deleteNotice(id) {
  return request({ url: `/notice/admin/${id}`, method: 'delete' })
}
```

**Step 2: config.js**

```javascript
import request from '@/utils/request'

export function getConfigList() {
  return request({ url: '/config/list', method: 'get' })
}

export function updateConfig(data) {
  return request({ url: '/config', method: 'put', data })
}
```

**Step 3: file.js**

```javascript
import request from '@/utils/request'

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/file/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 获取上传URL（用于el-upload组件）
export const uploadUrl = '/api/file/upload'
```

---

## Phase 4: 前端页面（使用frontend-design skill）

### Task 10: 重写首页展示轮播图和公告

**Files:**
- Modify: `vue/src/views/home/Home.vue`

使用 frontend-design skill 创建中国风首页，包含轮播图和公告列表

---

### Task 11: 创建公告管理页面

**Files:**
- Create: `vue/src/views/admin/NoticeManage.vue`

使用 frontend-design skill 创建后台公告管理页面

---

### Task 12: 创建轮播图管理页面

**Files:**
- Create: `vue/src/views/admin/BannerManage.vue`

使用 frontend-design skill 创建后台轮播图管理页面（含图片上传）

**注意：** el-upload组件必须显式携带token请求头

---

### Task 13: 创建系统配置页面

**Files:**
- Create: `vue/src/views/admin/ConfigManage.vue`

使用 frontend-design skill 创建系统配置管理页面

---

### Task 14: 更新路由和菜单

**Files:**
- Modify: `vue/src/router/index.js`
- Modify: `vue/src/layout/AdminLayout.vue`

添加公告管理、轮播图管理、系统配置的路由和菜单项

---

## Phase 5: 集成测试

### Task 15: 前后端联调测试

**测试清单:**

| 功能 | 测试步骤 | 预期结果 |
|------|----------|----------|
| 首页轮播图 | 访问 /home | 显示轮播图组件 |
| 首页公告 | 访问 /home | 显示公告列表 |
| 公告管理 | 访问 /admin/notice | 显示公告列表，支持CRUD |
| 轮播图管理 | 访问 /admin/banner | 支持图片上传和管理 |
| 系统配置 | 访问 /admin/config | 可修改仓库地址等配置 |

---

### Task 16: 提交代码

**Step 1: 创建功能分支**

```bash
git checkout -b feature/sys-config
```

**Step 2: 提交代码**

```bash
git add .
git commit -m "[feat] 完成模块二：系统配置与公告功能"
```

**Step 3: 推送到远程**

```bash
git push origin feature/sys-config
```

---

## 执行顺序总结

1. **Phase 1** (Task 1-7): 后端实体、Service、Controller
2. **Phase 2** (Task 8): 后端接口测试
3. **Phase 3** (Task 9): 前端API层
4. **Phase 4** (Task 10-14): 前端页面（使用frontend-design skill）
5. **Phase 5** (Task 15-16): 集成测试和代码提交
