package com.yourschool.campussystem.config;

import com.yourschool.campussystem.interceptor.PermissionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 用于配置静态资源访问路径、拦截器等
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final PermissionInterceptor permissionInterceptor;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 配置 Knife4j 静态资源路径（4.5.0版本基于SpringDoc）
        // doc.html 是 Knife4j 的主页面
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        // webjars 包含所有前端静态资源（JS、CSS等）
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        // 配置 Swagger UI 静态资源
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/");
        // 配置其他 HTML 静态资源
        registry.addResourceHandler("/*.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        // 配置 Knife4j 的 favicon 等资源
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/META-INF/resources/");
        
        // 配置上传文件的静态资源访问路径
        // 支持多种访问路径：/uploads/** 和 /files/**
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadPath + "/files/");
    }

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // 配置默认视图控制器，确保可以直接访问 /doc.html
        registry.addRedirectViewController("/", "/doc.html");
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // 配置跨域，允许访问API文档
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 注册权限验证拦截器
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有API请求
                .excludePathPatterns(
                        "/api/user/login",      // 登录接口不需要权限验证
                        "/api/user/register",  // 注册接口不需要权限验证
                        "/api/common/**",       // 通用接口（帮助中心、公告等）不需要权限验证
                        "/api/auth/visitor/**", // 游客相关接口不需要登录验证
                        "/api/auth/email/**",   // 邮箱登录相关接口不需要登录验证
                        "/doc.html",            // API文档页面
                        "/swagger-ui/**",        // Swagger UI
                        "/v3/api-docs/**",       // API文档JSON
                        "/webjars/**",           // 静态资源
                        "/uploads/**",           // 上传文件访问
                        "/files/**"              // 文件访问
                );
    }
}

