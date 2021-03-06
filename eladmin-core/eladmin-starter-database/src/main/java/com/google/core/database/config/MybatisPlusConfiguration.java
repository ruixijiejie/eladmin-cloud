package com.google.core.database.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.google.core.common.factory.YmlPropertySourceFactory;
import com.google.core.database.handler.ElAdminMetaObjectHandler;
import com.google.core.database.props.TenantProperties;
import com.google.core.mybatis.injector.SqlInjector;
import com.google.core.mybatis.interceptor.SqlLogInterceptor;
import lombok.AllArgsConstructor;
import org.apache.ibatis.type.EnumTypeHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author iris
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@AllArgsConstructor
@EnableTransactionManagement
@EnableConfigurationProperties(MybatisPlusConfiguration.class)
@PropertySource(factory = YmlPropertySourceFactory.class, value = "classpath:eladmin-db.yml")
@MapperScan("com.google.**.mapper.**")
public class MybatisPlusConfiguration {

    private final TenantProperties tenantProperties;

    private final TenantLineInnerInterceptor tenantLineInnerInterceptor;

    /**
     * ????????????????????????(???????????????,?????? ??????#handlerLimit ??????)
     */
    private static final Long MAX_LIMIT = 1000L;

    /**
     * sql ??????
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new SqlInjector();
    }

    /**
     * sql ??????
     */
    @Bean
    @Profile({"local", "dev", "test"})
    @ConditionalOnProperty(value = "mybatis-plus.sql-log.enable", matchIfMissing = true)
    public SqlLogInterceptor sqlLogInterceptor() {
        return new SqlLogInterceptor();
    }

    /**
     * ??????????????????,?????????????????????mybatis?????????,
     * ???????????? MybatisConfiguration#useDeprecatedExecutor = false
     * ????????????????????????(?????????????????????????????????????????????)
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        boolean enableTenant = tenantProperties.getEnable();
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (enableTenant) {
            interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        }
        //????????????: PaginationInnerInterceptor
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setMaxLimit(MAX_LIMIT);
        //?????????????????????????????????: BlockAttackInnerInterceptor
        // BlockAttackInnerInterceptor blockAttackInnerInterceptor = new BlockAttackInnerInterceptor();
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        // interceptor.addInnerInterceptor(blockAttackInnerInterceptor);
        return interceptor;
    }


    /**
     * ??????????????????
     */
    @Bean
    @ConditionalOnMissingBean(ElAdminMetaObjectHandler.class)
    public ElAdminMetaObjectHandler mateMetaObjectHandler() {
        return new ElAdminMetaObjectHandler();
    }


    /**
     * IEnum ????????????
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            configuration.setDefaultEnumTypeHandler(EnumTypeHandler.class);
            // ?????? mybatis ???????????????
            configuration.setLogPrefix("log.mybatis");
        };
    }

    /**
     * mybatis-plus ??????????????????
     */
    @Bean
    public OptimisticLockerInnerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }
}
