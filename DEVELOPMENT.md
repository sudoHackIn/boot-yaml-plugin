# Руководство по разработке

## Структура проекта

Проект создан как IntelliJ Platform Plugin для автодополнения Spring Boot YAML файлов.

### Основные компоненты

```
boot-yaml-plugin/
├── build.gradle.kts              # Основной build файл с IntelliJ Platform Plugin
├── build-simple.gradle.kts       # Упрощенный build для тестирования моделей
├── src/
│   ├── main/java/
│   │   └── com/github/sudohackin/bootyaml/
│   │       ├── model/            # Модели данных (независимые от IntelliJ)
│   │       ├── parser/           # JSON парсер (независимый от IntelliJ)
│   │       ├── service/          # Сервис загрузки метаданных (зависит от IntelliJ)
│   │       ├── completion/       # Completion contributor (зависит от IntelliJ)
│   │       └── listener/         # Event listeners (зависит от IntelliJ)
│   └── test/
│       ├── java/                 # Unit тесты
│       └── resources/
│           ├── test-metadata/    # Тестовые метаданные
│           └── test-yaml/        # Тестовые YAML файлы
```

## Сборка и тестирование

### Вариант 1: Полная сборка с IntelliJ Platform

Требуется доступ к интернету для загрузки зависимостей.

```bash
# Сборка плагина
./gradlew build

# Запуск тестов
./gradlew test

# Запуск IDE с плагином
./gradlew runIde

# Создание ZIP архива плагина
./gradlew buildPlugin
```

### Вариант 2: Упрощенное тестирование (без IntelliJ Platform)

Для тестирования базовых компонентов (модели и парсер) без зависимостей IntelliJ:

```bash
# Запуск только model и parser тестов
gradle -b build-simple.gradle.kts test --tests "*MetadataTest" --tests "*ParserTest"
```

## Тестирование

### Запуск всех тестов

```bash
./gradlew test
```

### Запуск конкретного теста

```bash
./gradlew test --tests ConfigurationMetadataTest
./gradlew test --tests PropertyMetadataTest
./gradlew test --tests MetadataParserTest
```

### Просмотр результатов

Отчет о тестировании создается в:
```
build/reports/tests/test/index.html
```

## Отладка

### В IntelliJ IDEA

1. Импортируйте проект как Gradle проект
2. Запустите задачу `runIde` для отладки
3. Установите breakpoints в коде
4. IDE запустится с плагином

### Логирование

Плагин использует IntelliJ Platform Logger:

```java
private static final Logger LOG = Logger.getInstance(ConfigurationMetadataService.class);
LOG.info("Message");
LOG.debug("Debug message");
LOG.warn("Warning", exception);
```

Логи можно найти в `idea.log` файле тестовой IDE.

## Как работает плагин

### 1. Загрузка метаданных

При открытии проекта:
1. `ConfigurationMetadataService` сканирует все JAR зависимости
2. Ищет файлы `META-INF/spring-configuration-metadata.json`
3. Парсит их с помощью `MetadataParser`
4. Объединяет в единый объект `ConfigurationMetadata`
5. Создает индекс для быстрого поиска

### 2. Автодополнение

При вводе в `application.yaml`:
1. `SpringBootYamlCompletionContributor` активируется
2. Определяет текущий путь в YAML (например, `server.`)
3. Запрашивает у `ConfigurationMetadataService` подходящие свойства
4. Формирует список предложений с типами и описаниями
5. Отображает их пользователю

### 3. Обновление при изменении зависимостей

1. `ProjectDependencyChangeListener` слушает события обновления зависимостей
2. При успешном обновлении вызывает `reloadMetadata()`
3. Метаданные перезагружаются из новых зависимостей

## Формат метаданных Spring Boot

Spring Boot хранит метаданные о конфигурационных свойствах в JSON файлах:

```json
{
  "groups": [
    {
      "name": "server",
      "type": "org.springframework.boot.autoconfigure.web.ServerProperties"
    }
  ],
  "properties": [
    {
      "name": "server.port",
      "type": "java.lang.Integer",
      "description": "Server HTTP port",
      "defaultValue": 8080
    }
  ],
  "hints": [
    {
      "name": "logging.level.root",
      "values": [
        {"value": "TRACE"},
        {"value": "DEBUG"},
        {"value": "INFO"}
      ]
    }
  ]
}
```

## Расширение функциональности

### Добавление валидации значений

1. Создать `ValidationService` в пакете `service`
2. Использовать `HintMetadata` для проверки допустимых значений
3. Интегрировать с IntelliJ Inspection API

### Добавление Quick Fixes

1. Создать `QuickFix` класс для deprecated свойств
2. Зарегистрировать в `plugin.xml`
3. Предлагать замену на новое свойство

### Поддержка @ConfigurationProperties

1. Создать сканер для аннотированных классов
2. Генерировать метаданные из полей класса
3. Объединять с существующими метаданными

## Публикация плагина

### Подготовка к релизу

1. Обновить версию в `gradle.properties`
2. Обновить `CHANGELOG.md`
3. Запустить `./gradlew buildPlugin`
4. Протестировать созданный ZIP

### Публикация в JetBrains Marketplace

```bash
# Настроить токен
export PUBLISH_TOKEN=your-token

# Опубликовать
./gradlew publishPlugin
```

## Troubleshooting

### Плагин не видит свойства

1. Проверьте, что в зависимостях есть Spring Boot библиотеки
2. Проверьте логи на наличие ошибок парсинга
3. Убедитесь, что в JAR есть файлы метаданных

### Тесты не запускаются

1. Убедитесь, что установлен JDK 17+
2. Проверьте доступность Maven Central
3. Очистите кэш: `./gradlew clean`

### IDE не запускается

1. Проверьте наличие свободной памяти
2. Попробуйте другую версию IntelliJ Platform в `build.gradle.kts`
3. Проверьте совместимость с вашей ОС
