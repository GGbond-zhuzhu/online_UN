package com.yourschool.campussystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI campusOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校园集成系统 API 文档")
                        .description("校园集成系统后端接口文档，包含用户管理、二手交易、校园卡、兼职、行程管理等五大模块")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@yourschool.com")
                                .url("https://github.com/your-repo"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT令牌，格式：Bearer {token}")));
                // 注意：不添加全局安全要求，这样文档页面可以直接访问
                // 各个接口可以在方法上单独添加 @SecurityRequirement 注解来要求认证
    }

    // 按模块分组，便于管理
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("01-用户管理")
                .pathsToMatch("/api/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi ecardApi() {
        return GroupedOpenApi.builder()
                .group("02-校园卡管理")
                .pathsToMatch("/api/ecard/**")
                .build();
    }

    @Bean
    public GroupedOpenApi secondhandApi() {
        return GroupedOpenApi.builder()
                .group("03-二手交易平台")
                .pathsToMatch("/api/secondhand/**")
                .build();
    }

    @Bean
    public GroupedOpenApi parttimeApi() {
        return GroupedOpenApi.builder()
                .group("04-兼职管理")
                .pathsToMatch("/api/parttime/**")
                .build();
    }

    @Bean
    public GroupedOpenApi scheduleApi() {
        return GroupedOpenApi.builder()
                .group("05-行程管理")
                .pathsToMatch("/api/schedule/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("06-身份认证")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
                .group("07-通用功能")
                .pathsToMatch("/api/common/**")
                .build();
    }

    @Bean
    public GroupedOpenApi accountBookApi() {
        return GroupedOpenApi.builder()
                .group("13-记账本管理")
                .pathsToMatch("/api/account-book/**")
                .build();
    }

    @Bean
    public GroupedOpenApi dietRecordApi() {
        return GroupedOpenApi.builder()
                .group("14-饮食记录管理")
                .pathsToMatch("/api/diet-record/**")
                .build();
    }

    @Bean
    public GroupedOpenApi mapApi() {
        return GroupedOpenApi.builder()
                .group("15-地图服务")
                .pathsToMatch("/api/map/**")
                .build();
    }

    @Bean
    public GroupedOpenApi messageApi() {
        return GroupedOpenApi.builder()
                .group("08-消息中心")
                .pathsToMatch("/api/messages/**")
                .build();
    }

    @Bean
    public GroupedOpenApi chatApi() {
        return GroupedOpenApi.builder()
                .group("09-聊天功能")
                .pathsToMatch("/api/chat/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("10-管理员功能")
                .pathsToMatch("/api/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi merchantApi() {
        return GroupedOpenApi.builder()
                .group("11-商户管理")
                .pathsToMatch("/api/merchant/**")
                .build();
    }

    @Bean
    public GroupedOpenApi universityApi() {
        return GroupedOpenApi.builder()
                .group("12-高校管理")
                .pathsToMatch("/api/university/**")
                .build();
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("00-全部接口")
                .pathsToMatch("/api/**")
                .build();
    }
}