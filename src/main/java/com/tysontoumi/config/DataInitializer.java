package com.tysontoumi.config;

import com.tysontoumi.model.Part;
import com.tysontoumi.model.PartCategory;
import com.tysontoumi.repository.PartRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    @Profile("!test")
    public CommandLineRunner seedData(PartRepository partRepository) {
        return args -> {
            if (partRepository.count() > 0) return;

            List<Part> parts = List.of(
                    new Part("Klassisk hvid urskive", PartCategory.DIAL, "Ren hvid skive med romerske tal", 349.0, 10),
                    new Part("Sort minimalistisk skive", PartCategory.DIAL, "Mat sort med streger", 299.0, 8),
                    new Part("Champagne guld skive", PartCategory.DIAL, "Luksuriøs champagne farve", 499.0, 5),
                    new Part("Rustfrit stål kasse 40mm", PartCategory.CASE, "Poleret stålkasse, vandtæt 50m", 899.0, 7),
                    new Part("Sort PVD kasse 42mm", PartCategory.CASE, "Sort belagt stål, sporty look", 999.0, 4),
                    new Part("Rose guld kasse 38mm", PartCategory.CASE, "Elegant rose guld finish", 1299.0, 3),
                    new Part("Stål mesh lænke", PartCategory.STRAP, "Milanaise mesh i sølv", 549.0, 12),
                    new Part("Sort læderrem", PartCategory.STRAP, "Ægte kalveskind, sort", 299.0, 15),
                    new Part("Brun vintage rem", PartCategory.STRAP, "Aged look, brun læder", 349.0, 10),
                    new Part("Gummirem til sport", PartCategory.STRAP, "Holdbar FKM gummi", 199.0, 20),
                    new Part("Miyota 8215 automatisk", PartCategory.MECHANISM, "Japansk automatisk urværk", 799.0, 6),
                    new Part("Seiko NH35 automatisk", PartCategory.MECHANISM, "Pålidelig automatisk bevægelse", 699.0, 8),
                    new Part("Kvartsværk Swiss", PartCategory.MECHANISM, "Schweizisk kvarts, præcis", 399.0, 15),
                    new Part("Sølv visersæt", PartCategory.HANDS, "Poleret sølv, klassisk sæt", 199.0, 20),
                    new Part("Sort visersæt", PartCategory.HANDS, "Mat sort med lume", 249.0, 18),
                    new Part("Safir krystal glas", PartCategory.GLASS, "Anti-refleks safir, 1mm", 399.0, 10),
                    new Part("Mineral glas", PartCategory.GLASS, "Klassisk mineralglas", 149.0, 20),
                    new Part("Stål krone", PartCategory.CROWN, "Standard trækrone i stål", 99.0, 25),
                    new Part("Sort krone", PartCategory.CROWN, "Sort PVD krone", 129.0, 15)
            );

            partRepository.saveAll(parts);
        };
    }
}
