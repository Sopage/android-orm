# cube-orm
此项目是为Android简单封装的SQLite数据库ORM, 实现基本的单表CRUD，提高数据库开发效率。
## 使用方法
### 约定：
###### 所有的实体都要继承IDColumn.java类
1. 表名称必须以小写字母开始，单词之间用“_”下划线分开(当然也可以全部小写不分开):<br>
	`如：table_name、table_xxx、xx_xx`
2. 表名称和实体类对应如下:<br>
	`表：table_name 对应的实体类：TableName`
	`类：TableName 对应表名：table_name`
3. 表字段和实体类字段对应如下:<br>
	`表字段：field_name 对应实体类字段：fieldName.`
	`实体类字段：fieldName 对应实体类字段：field_name.`
4. 主键在IDColumn类里:<br>
	`主键为long类型自增长, 名称为_primary_key_id`
	
### 使用方法
    compile 'com.github.supersanders:cube-orm:1.5@aar'
##### 混淆配置
    -keep public class * extends com.sanders.db.IDColumn
##### 自动升级数据库说明
    如果使用自动升级，若表字段类型有变则会重新创建新表并备份旧表为 "表名_oldVersion",这需要手动将数据导入新表。
##### 推荐
	DBProxy db = new DBProxy.DBBuilder()
        .setDbName("db")
        .setDbVersion(1)
        .createTable(TableBean.class)
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
##### 实现自己的SQLiteOpenHelper
    //使用此方式要按约定来
    DBProxy db = new DBProxy(new SimpleOpenHelper(context));