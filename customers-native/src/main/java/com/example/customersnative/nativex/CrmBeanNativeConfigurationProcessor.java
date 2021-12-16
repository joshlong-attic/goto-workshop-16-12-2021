package com.example.customersnative.nativex;

import org.springframework.aot.context.bootstrap.generator.bean.descriptor.BeanInstanceDescriptor;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.BeanNativeConfigurationProcessor;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeConfigurationRegistry;
import org.springframework.core.ResolvableType;

public class CrmBeanNativeConfigurationProcessor
        implements BeanNativeConfigurationProcessor {

    @Override
    public void process(BeanInstanceDescriptor descriptor, NativeConfigurationRegistry registry) {
        ResolvableType beanType = descriptor.getBeanType();
        System.out.println("processing " + beanType.getType().getTypeName());

    }
}
