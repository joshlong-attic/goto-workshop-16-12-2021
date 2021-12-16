package hints;

import org.reflections.Reflections;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeConfigurationRegistry;
import org.springframework.nativex.AotOptions;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.NativeConfiguration;


@TypeHint(typeNames = "com.example.customersnative.CustomerService")
public class MyCrmHints implements NativeConfiguration {
/*
    @Override
    public void computeHints(NativeConfigurationRegistry registry, AotOptions aotOptions) {
        try {

            var csClass = Class.forName("com.example.customersnative.CustomerService");
            registry.reflection().forType(csClass).withAccess(TypeAccess.values()).build();

        } catch (ClassNotFoundException e) {

        }

    }*/
}
