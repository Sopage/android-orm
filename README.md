# cube-orm
此项目是为Android简单封装的SQLite数据库ORM, 实现自动建表和自动更新升级，实现基本的单表CRUD，提高数据库开发效率。
## 使用方法
### 约定：
#### 所有的实体都要继承IDColumn.java类，并总受一下命名规范：
<b>表名称和Java类名称对应表</b>

| 表名称 | Java类名 | 备注 |
| --- | --- | --- |
| table_name | TableName | 表名称必须以小写字母开始，单词之间用“_”下划线分开(当然也可以全部小写不分开) |

<b>表字段名称和Java类属性字段名称对应表</b>

| 表字段 | Java字段 | 备注 |
| --- | --- | --- |
| field_name | fieldName | 表字段名称必须以小写字母开始，单词之间用“_”下划线分开(当然也可以全部小写不分开)。按照Java驼峰命名规范命名Java字段属性名称 |

### 使用方法
    compile 'com.github.supersanders:cube-orm:2.0@aar'
##### 混淆配置
    -keep public class * extends com.sanders.db.IDColumn
##### 说明
	系统创建数据库：支持自动建表，自动升级。如果使用自动升级，若表字段类型有变则会重新创建新表并备份旧表为 "表名_oldVersion",这需要手动将数据导入新表。
	设置外部数据库：不支持自动建表和升级。另外主键名称必须是_primary_key
##### 系统创建表
	DBProxy db = new DBProxy.DBBuilder()
        .setDbName("db")//数据库名称
        .setDbVersion(1)//版本
        .createTable(TableBean.class)//建表
        .setOnDBUpgrade(new OnDBUpgrade() {//不设置此值则代表自动升级

                            @Override
                            public boolean beginUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                                //开始升级数据库之前调用此方法
                                return super.beginUpgrade(db, oldVersion, newVersion);
                            }

                            @Override
                            public boolean onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                                //数据库升级调用此方法 返回true是自己处理升级，false则自动升级
                                return super.onUpgrade(db, oldVersion, newVersion);
                            }

                            @Override
                            public boolean endUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                                //数据库升级结束调用此方法
                                return super.endUpgrade(db, oldVersion, newVersion);
                            }
                        })
        .build(this);
##### 设置外部数据库，主键名称必须是`_primary_key`
    DBProxy db = new DBProxy.DBBuilder().setDbFilePath("path").build(this);
    DBProxy db = new DBProxy.DBBuilder().setDbFile(File).build(this);
##### 实现自己的SQLiteOpenHelper
    //使用此方式要按约定来
    DBProxy db = new DBProxy(new SimpleOpenHelper(context));
