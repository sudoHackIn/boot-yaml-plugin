#!/bin/bash

# Скрипт для быстрой проверки проекта без зависимостей от Maven
# Компилирует и тестирует модели локально

set -e

echo "=========================================="
echo " Spring Boot YAML Plugin - Verification"
echo "=========================================="
echo ""

# Очистка
rm -rf /tmp/plugin-verify
mkdir -p /tmp/plugin-verify

# Компиляция моделей
echo "1. Компиляция моделей..."
javac -d /tmp/plugin-verify \
    src/main/java/com/github/sudohackin/bootyaml/model/*.java
echo "   ✓ Модели скомпилированы"

# Компиляция простого теста
echo ""
echo "2. Компиляция тестов..."
javac -cp /tmp/plugin-verify:src/main/java \
    -d /tmp/plugin-verify \
    SimpleModelTest.java
echo "   ✓ Тесты скомпилированы"

# Запуск тестов
echo ""
echo "3. Запуск тестов..."
echo "   ─────────────────────────────────"
cd /tmp/plugin-verify
java -ea SimpleModelTest
cd - > /dev/null
echo "   ─────────────────────────────────"

# Проверка структуры
echo ""
echo "4. Проверка структуры проекта..."

check_exists() {
    if [ -f "$1" ]; then
        echo "   ✓ $1"
        return 0
    else
        echo "   ✗ $1 - НЕ НАЙДЕН"
        return 1
    fi
}

check_exists "build.gradle.kts"
check_exists "src/main/resources/META-INF/plugin.xml"
check_exists "src/test/resources/test-metadata/spring-configuration-metadata.json"
check_exists "src/test/resources/test-yaml/application.yaml"

echo ""
echo "5. Статистика..."
echo "   Production классов: $(find src/main/java -name "*.java" | wc -l)"
echo "   Тестовых классов:   $(find src/test/java -name "*.java" | wc -l)"
echo "   Строк кода:         $(find src -name "*.java" -exec cat {} \; | wc -l)"
echo "   Документов MD:      $(ls *.md 2>/dev/null | wc -l)"

echo ""
echo "=========================================="
echo " ✅ Проект успешно верифицирован!"
echo "=========================================="
echo ""
echo "Для полной сборки с IntelliJ Platform:"
echo "  ./gradlew build"
echo ""
echo "Для запуска в IntelliJ IDEA:"
echo "  ./gradlew runIde"
echo ""
