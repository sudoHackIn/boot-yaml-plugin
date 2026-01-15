import com.github.sudohackin.bootyaml.model.*;
import java.util.*;

/**
 * Простой тест моделей без JUnit - для быстрой проверки работоспособности.
 * Компилируется и запускается независимо от тестового фреймворка.
 */
public class SimpleModelTest {

    public static void main(String[] args) {
        System.out.println("=== Проверка моделей Spring Boot YAML Plugin ===\n");

        int passed = 0;
        int failed = 0;

        // Тест 1: PropertyMetadata
        try {
            PropertyMetadata prop = new PropertyMetadata();
            prop.setName("server.port");
            prop.setType("java.lang.Integer");
            prop.setDescription("Server HTTP port");
            prop.setDefaultValue(8080);

            assert prop.getName().equals("server.port") : "Name mismatch";
            assert prop.getType().equals("java.lang.Integer") : "Type mismatch";
            assert prop.getDefaultValue().equals(8080) : "Default value mismatch";

            System.out.println("✓ Test 1: PropertyMetadata creation and getters");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ Test 1: PropertyMetadata - " + e.getMessage());
            failed++;
        }

        // Тест 2: ConfigurationMetadata
        try {
            ConfigurationMetadata metadata = new ConfigurationMetadata();

            PropertyMetadata prop1 = new PropertyMetadata("server.port", "java.lang.Integer", "Server port");
            PropertyMetadata prop2 = new PropertyMetadata("server.address", "java.lang.String", "Server address");

            metadata.getProperties().add(prop1);
            metadata.getProperties().add(prop2);

            assert metadata.getProperties().size() == 2 : "Properties size mismatch";

            System.out.println("✓ Test 2: ConfigurationMetadata with properties");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ Test 2: ConfigurationMetadata - " + e.getMessage());
            failed++;
        }

        // Тест 3: GroupMetadata
        try {
            GroupMetadata group = new GroupMetadata();
            group.setName("server");
            group.setType("org.springframework.boot.ServerProperties");
            group.setDescription("Server configuration");

            assert group.getName().equals("server") : "Group name mismatch";

            System.out.println("✓ Test 3: GroupMetadata creation");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ Test 3: GroupMetadata - " + e.getMessage());
            failed++;
        }

        // Тест 4: HintMetadata
        try {
            HintMetadata hint = new HintMetadata();
            hint.setName("logging.level.root");

            HintMetadata.ValueHint vh1 = new HintMetadata.ValueHint();
            vh1.setValue("INFO");
            vh1.setDescription("Info level");

            HintMetadata.ValueHint vh2 = new HintMetadata.ValueHint();
            vh2.setValue("DEBUG");
            vh2.setDescription("Debug level");

            hint.getValues().add(vh1);
            hint.getValues().add(vh2);

            assert hint.getValues().size() == 2 : "Hints size mismatch";

            System.out.println("✓ Test 4: HintMetadata with values");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ Test 4: HintMetadata - " + e.getMessage());
            failed++;
        }

        // Тест 5: DeprecationMetadata
        try {
            DeprecationMetadata deprecation = new DeprecationMetadata();
            deprecation.setReason("Property no longer used");
            deprecation.setReplacement("new.property.name");
            deprecation.setLevel("warning");

            assert !deprecation.isError() : "Should not be error level";

            deprecation.setLevel("error");
            assert deprecation.isError() : "Should be error level";

            System.out.println("✓ Test 5: DeprecationMetadata");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ Test 5: DeprecationMetadata - " + e.getMessage());
            failed++;
        }

        // Тест 6: PropertyIndex
        try {
            ConfigurationMetadata metadata = new ConfigurationMetadata();

            PropertyMetadata prop1 = new PropertyMetadata("server.port", "java.lang.Integer", "Port");
            PropertyMetadata prop2 = new PropertyMetadata("server.address", "java.lang.String", "Address");
            PropertyMetadata prop3 = new PropertyMetadata("logging.level.root", "java.lang.String", "Log level");

            metadata.getProperties().add(prop1);
            metadata.getProperties().add(prop2);
            metadata.getProperties().add(prop3);

            Map<String, PropertyMetadata> index = metadata.createPropertyIndex();

            assert index.size() == 3 : "Index size mismatch";
            assert index.get("server.port") != null : "server.port not found in index";
            assert index.get("server.port").getType().equals("java.lang.Integer") : "Wrong type in index";

            System.out.println("✓ Test 6: Property indexing");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ Test 6: Property indexing - " + e.getMessage());
            failed++;
        }

        // Тест 7: Merge metadata
        try {
            ConfigurationMetadata metadata1 = new ConfigurationMetadata();
            metadata1.getProperties().add(new PropertyMetadata("prop1", "String", "First"));

            ConfigurationMetadata metadata2 = new ConfigurationMetadata();
            metadata2.getProperties().add(new PropertyMetadata("prop2", "String", "Second"));

            metadata1.merge(metadata2);

            assert metadata1.getProperties().size() == 2 : "Merge failed";

            System.out.println("✓ Test 7: Metadata merging");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ Test 7: Metadata merging - " + e.getMessage());
            failed++;
        }

        // Результаты
        System.out.println("\n=== Результаты ===");
        System.out.println("Пройдено: " + passed);
        System.out.println("Провалено: " + failed);
        System.out.println("Всего: " + (passed + failed));

        if (failed == 0) {
            System.out.println("\n✅ Все тесты пройдены успешно!");
            System.exit(0);
        } else {
            System.out.println("\n❌ Некоторые тесты провалены");
            System.exit(1);
        }
    }
}
