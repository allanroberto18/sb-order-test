package br.com.alr.order.shared.infrastructure.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Intelligent Order Processing System API",
        version = "v1",
        description = "Order management, scheduler, and AI support endpoints.",
        contact = @Contact(name = "ALR"),
        license = @License(name = "Internal Use")),
    servers = @Server(url = "/", description = "Default server"))
public class OpenApiConfiguration {
}
