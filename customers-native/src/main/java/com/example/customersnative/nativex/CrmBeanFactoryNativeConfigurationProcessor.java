package com.example.customersnative.nativex;

import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.BeanFactoryNativeConfigurationProcessor;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeConfigurationRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class CrmBeanFactoryNativeConfigurationProcessor
        implements BeanFactoryNativeConfigurationProcessor {
    @Override
    public void process(ConfigurableListableBeanFactory beanFactory, NativeConfigurationRegistry registry) {

        var type = "com.example.customersnative.CustomerService";
        try {
            String[] beanNamesForType = beanFactory.getBeanNamesForType(Class.forName(type));
            for (var bn : beanNamesForType) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(bn);

            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
