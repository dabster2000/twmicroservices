package dk.trustworks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EnableDiscoveryClient
@EntityScan(
		basePackageClasses = {InvoiceApplication.class, Jsr310JpaConverters.class}
)
public class InvoiceApplication {

	public final static String queueName = "update-queue";

	public static void main(String[] args) {
		SpringApplication.run(InvoiceApplication.class, args);
	}

}