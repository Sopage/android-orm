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
	`主键为long类型自增长, 名称为_key_id`
	
### 使用方法
    compile 'com.github.supersanders:cube-orm:1.2.2@aar'
##### 推荐
	DBProxy db = new DBProxy.DBBuilder()
        .builderDbName("test")
        .builderDbVersion(1)
        .builderTable(Table1.class)
        .builderTable(Table_2.class)
        .builderTable(TableBean.class)
        .setOnDBUpgrade(new DefaultSQLiteOpenHelper.OnDBUpgrade() {
                            @Override
                            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

                            }
                        })
        .build(this);
##### 实现自己的SQLiteOpenHelper
    //使用此方式要按约定来
    DBProxy db = new DBProxy(new SimpleOpenHelper(this));
### 下次更新：
1. 再议~！
