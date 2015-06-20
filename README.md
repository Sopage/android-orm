# cube-orm
此项目是为Android简单封装的SQLite数据库ORM, 实现自动建表和自动更新升级，实现基本的单表CRUD，提高数据库开发效率。
## 使用方法
<b>Android Studio引用方式:</b>

`compile 'com.github.supersanders:cube-orm:3.0@aar'`

<b>混淆配置:</b>

`-keep public class * extends com.sanders.db.IDColumn`
### 约定：
#### 所有的实体都要继承IDColumn.java类，并遵守以下命名规范：
<b>表名称和Java类名称对应表：</b>

| 表名称 | Java类名 | 备注 |
| --- | --- | --- |
| table_name | TableName | 表名称必须以小写字母开始，单词之间用“_”下划线分开(当然也可以全部小写不分开) |

<b>表字段名称和Java类属性字段名称对应表：</b>

| 表字段 | Java字段 | 备注 |
| --- | --- | --- |
| field_name | fieldName | 表字段名称必须以小写字母开始，单词之间用“_”下划线分开(当然也可以全部小写不分开)。按照Java驼峰命名规范命名Java字段属性名称 |

<b>数据库创建方式：</b>

| 创建方式  | 说明 |
| --- | --- |
| 系统创建数据库 | 支持自动建表，自动升级。如果使用自动升级，若表字段类型有变则会重新创建新表并备份旧表为 `表名_oldVersion`,这需要手动将数据导入新表。 |
| 外部数据库 | 不支持自动建表和升级。另外主键名称必须是`primary_key` |

##### 系统创建表
	//使用DBContext自动化创建表，每个表对应一个继承IDColumn.java类的POJO实体
	DBContext dbContext = new DBContext("database", 1, new OnDBUpgrade() {
            @Override
            public boolean onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            	//return false 自己处理升级，return true 自动处理
                return false;
            }
        });
	dbContext.addTableBean(TableModel.class).addTableBean(TableBean.class);
	DBProxy db = dbContext.buildDBProxy(this);
	
	//使用DBContextUse创建数据库
	DBContextUse dbContextUse = new DBContextUse("dbdbdbdbdbd", 1, null);
    dbContextUse.addSql(create table sql).addSql(create table sql);
    DBProxy db = dbContextUse.buildDBProxy(getApplicationContext());
##### 设置外部数据库，主键名称必须是`primary_key`
	DBFile dbFile = new DBFile(file path or File);
	DBProxy db = dbFile.buildDBProxy();
##### `DBProxy.java`类包涵了所有数据库操作