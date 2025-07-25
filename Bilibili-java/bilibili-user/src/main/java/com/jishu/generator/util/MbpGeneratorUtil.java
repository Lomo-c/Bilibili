package com.jishu.generator.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.jishu.generator.model.MbpGeneratorConfig;
import com.jishu.generator.mybatis.BaseMapperX;
import com.jishu.generator.pojo.BasePO;

import java.util.*;

/**
 * @author wnhyang
 * @date 2024/5/22
 **/
public class MbpGeneratorUtil {

    private static final String OUTPUT_PATH = System.getProperty("user.dir");

    private MbpGeneratorUtil() {
    }


    public static void generate(MbpGeneratorConfig config) {
        //1、配置数据源
        FastAutoGenerator.create(config.getFullJdbcUrl(), config.getUsername(), config.getPassword())
                //2、全局配置
                .globalConfig(builder -> {
                    builder.disableOpenDir() // 禁止打开输出目录 默认 true
                            .outputDir(OUTPUT_PATH + "/src/main/java")   // 设置输出路径：项目的 java 目录下
                            .author(config.getAuthor()) // 设置作者名p
                            // .enableKotlin() //开启 kotlin 模式 默认false
                            // .enableSwagger()   // 开启 swagger 模式 默认false
                            .dateType(DateType.TIME_PACK)   // 定义生成的实体类中日期的类型 TIME_PACK=LocalDateTime;ONLY_DATE=Date;
                            .commentDate("yyyy/MM/dd"); // 注释日期 默认值 yyyy-MM-dd
                })
                //3、包配置
                .packageConfig(builder -> {
                    builder.parent(config.getParent()) // 父包名 默认值 com.baomidou
                            .moduleName(config.getModuleName())   // 父包模块名 默认值 无
                            .entity("entity")   // Entity 包名 默认值 entity
                            .service("service") //Service 包名 默认值 service
                            .serviceImpl("service.impl") // Service Impl 包名 默认值:service.impl
                            .mapper("mapper")   // Mapper 包名 默认值 mapper
                            .xml("mapper")  // Mapper XML 包名 默认值 mapper.xml
                            .controller("controller") // Controller 包名 默认值 controller
                            .pathInfo(Collections.singletonMap(OutputFile.xml, OUTPUT_PATH + "/src/main/resources/mapper"));    //配置 mapper.xml 路径信息：项目的 resources 目录下
                })
                //4、模版配置
                .templateConfig(builder -> {
                    builder.entity("/templates/entity.java")
                            .service("/templates/service.java")
                            .serviceImpl("/templates/serviceImpl.java")
                            .mapper("/templates/mapper.java")
                            .xml("/templates/mapper.xml")
                            .controller("/templates/controller.java");

                })
                //5、策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(config.getTableNames()) // 设置需要生成的数据表名
                            .addTablePrefix(config.getTablePrefix().split(",")) // 设置过滤表前缀

                            //5.1、实体类策略配置
                            .entityBuilder()
                            .enableFileOverride() // 覆盖entity
                            .superClass(BasePO.class)
                            //.disableSerialVersionUID()  // 禁用生成 serialVersionUID 默认值 true
                            .enableLombok() // 开启 Lombok 默认值:false
                            .enableTableFieldAnnotation()       // 开启生成实体时生成字段注解 默认值 false
                            .logicDeleteColumnName("deleted")   // 逻辑删除字段名
                            .naming(NamingStrategy.underline_to_camel)  //数据库表映射到实体的命名策略：下划线转驼峰命
                            .columnNaming(NamingStrategy.underline_to_camel)    // 数据库表字段映射到实体的命名策略：下划线转驼峰命
                            // .addSuperEntityColumns("creator", "create_time", "updater", "update_time")
                            // .addTableFills(
                            //  new Column("creator", FieldFill.INSERT),
                            //  new Column("updater", FieldFill.INSERT_UPDATE)
                            // )   // 添加表字段填充，"create_time"字段自动填充为插入时间，"modify_time"字段自动填充为插入修改时间
                            .formatFileName("%s")

                            //5.2、Mapper策略配置
                            .mapperBuilder()
                            .enableFileOverride() // 覆盖mapper
                            .superClass(BaseMapperX.class)   // 设置父类
                            .mapperAnnotation(org.apache.ibatis.annotations.Mapper.class)      // 开启 @Mapper 注解
                            // .enableBaseResultMap() //启用 BaseResultMap 生成
                            .formatMapperFileName("%sMapper")   // 格式化 mapper 文件名称
                            .formatXmlFileName("%sMapper") // 格式化 Xml 文件名称

                            //5.3、service 策略配置
                            .serviceBuilder()
                            .enableFileOverride() // 覆盖service
                            .formatServiceFileName("%sService") // 格式化 service 接口文件名称，%s进行匹配表名，如 UserService
                            .formatServiceImplFileName("%sServiceImpl") // 格式化 service 实现类文件名称，%s进行匹配表名，如 UserServiceImpl

                            //5.4、Controller策略配置
                            .controllerBuilder()
                            .enableFileOverride() // 覆盖controller
                            .enableRestStyle()  // 开启生成 @RestController 控制器
                            //.enableHyphenStyle() //开启驼峰转连字符 默认false
                            .formatFileName("%sController"); // 格式化 Controller 类文件名称，%s进行匹配表名，如 UserController

                })
                //6、自定义配置
                .injectionConfig(consumer -> {
                    Map<String, Object> customMap = new HashMap<>();
                    customMap.put("dto", config.getParent() + ".dto");
                    customMap.put("create", config.getParent() + ".vo.create");
                    customMap.put("update", config.getParent() + ".vo.update");
                    customMap.put("vo", config.getParent() + ".vo");
                    customMap.put("page", config.getParent() + ".vo.page");
                    customMap.put("convert", config.getParent() + ".convert");

                    consumer.customMap(customMap);
                    // DTO
                    List<CustomFile> customFiles = new ArrayList<>();
                    customFiles.add(new CustomFile.Builder().packageName("dto").fileName("DTO.java")
                            .templatePath("/templates/dto/DTO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("vo/create").fileName("CreateVO.java")
                            .templatePath("/templates/vo/CreateVO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("vo/update").fileName("UpdateVO.java")
                            .templatePath("/templates/vo/UpdateVO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("vo").fileName("VO.java")
                            .templatePath("/templates/vo/VO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("vo/page").fileName("PageVO.java")
                            .templatePath("/templates/vo/PageVO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("convert").fileName("Convert.java")
                            .templatePath("/templates/convert/Convert.java.vm").enableFileOverride().build());
                    consumer.customFile(customFiles);
                })
                //7、模板
                .templateEngine(new VelocityTemplateEngine())

                /*
                    .templateEngine(new VelocityTemplateEngine())
                    .templateEngine(new FreemarkerTemplateEngine())
                    .templateEngine(new BeetlTemplateEngine())
                */

                //8、执行
                .execute();
    }

}
