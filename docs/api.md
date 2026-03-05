# 目前项目的接口已经实现的如下

## 班级管理接口 (classApi)

| 方法   | 接口路径                    | 说明         | 参数                                                         |
| ------ | --------------------------- | ------------ | ------------------------------------------------------------ |
| GET    | `/api/v1/classes`           | 获取班级列表 | `{ page: number; size: number }`                             |
| POST   | `/api/v1/classes`           | 创建班级     | `{ name: string; createTime?: string; description?: string }` |
| PUT    | `/api/v1/classes/{classId}` | 更新班级     | `{ name: string; createTime?: string; description?: string }` |
| DELETE | `/api/v1/classes/{classId}` | 删除班级     | classId: number                                              |

## 学生管理接口 (studentApi)

| 方法   | 接口路径                       | 说明         | 参数                                               |
| ------ | ------------------------------ | ------------ | -------------------------------------------------- |
| GET    | `/api/v1/students`             | 获取学生列表 | `{ classId?: number; page: number; size: number }` |
| POST   | `/api/v1/students`             | 创建学生     | `{ basic, contact, clazzId, message, avatar }`     |
| PUT    | `/api/v1/students/{studentId}` | 更新学生     | `{ basic, contact, clazzId, message, avatar }`     |
| DELETE | `/api/v1/students/{studentId}` | 删除学生     | studentId: number                                  |

# 广场(squareApi)



## 通用配置

- **Base URL**: `/api/v1`
- **超时时间**: 10000ms
- **Content-Type**: `application/json`

## 响应格式

所有接口返回统一的响应格式：

```typescript
{
  code: number;      // 状态码，0 表示成功
  message: string;   // 响应消息
  data: T;           // 响应数据
}
```

分页数据格式：

```typescript
{
  items: T[];        // 数据列表
  page: number;      // 当前页码
  pageSize: number;  // 每页大小
  total: number;     // 总记录数
}
```

