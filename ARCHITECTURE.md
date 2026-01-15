# Архитектура плагина Spring Boot YAML Autocomplete

## Обзор

Плагин для IntelliJ IDEA, предоставляющий интеллектуальное автодополнение для Spring Boot `application.yaml` файлов на основе доступных автоконфигураций.

## Архитектурные принципы

1. **Разделение ответственности**: Четкое разделение на слои - модель, парсинг, сервисы, UI
2. **Кэширование**: Метаданные кэшируются для быстрого доступа
3. **Реактивность**: Автоматическое обновление при изменении зависимостей
4. **Тестируемость**: Независимые компоненты с полным покрытием тестами

## Компоненты системы

### 1. Model Layer (Модельный слой)

**Назначение**: Представление данных о конфигурационных свойствах Spring Boot

**Классы**:
- `ConfigurationMetadata` - корневой объект, содержащий все метаданные
- `PropertyMetadata` - описание одного конфигурационного свойства
- `GroupMetadata` - группа связанных свойств
- `HintMetadata` - подсказки для значений свойств
- `DeprecationMetadata` - информация об устаревших свойствах

**Особенности**:
- POJO классы без зависимостей от IntelliJ Platform
- Легко тестируются
- Сериализуются/десериализуются через Gson

**Пример использования**:
```java
PropertyMetadata property = new PropertyMetadata();
property.setName("server.port");
property.setType("java.lang.Integer");
property.setDefaultValue(8080);
```

### 2. Parser Layer (Слой парсинга)

**Назначение**: Парсинг JSON файлов с метаданными Spring Boot

**Классы**:
- `MetadataParser` - парсер JSON файлов в объекты модели

**Особенности**:
- Использует Gson для десериализации
- Независим от IntelliJ Platform
- Обрабатывает ошибки парсинга

**Формат входных данных**:
```json
{
  "properties": [
    {
      "name": "server.port",
      "type": "java.lang.Integer",
      "description": "Server HTTP port",
      "defaultValue": 8080
    }
  ]
}
```

### 3. Service Layer (Сервисный слой)

**Назначение**: Бизнес-логика работы с метаданными

**Классы**:
- `ConfigurationMetadataService` - центральный сервис для работы с метаданными

**Функции**:
- Сканирование JAR зависимостей проекта
- Поиск и парсинг файлов `META-INF/spring-configuration-metadata.json`
- Объединение метаданных из разных источников
- Кэширование для производительности
- Индексирование для быстрого поиска

**Жизненный цикл**:
```
1. Инициализация при открытии проекта
2. Сканирование всех JAR файлов в classpath
3. Парсинг найденных метаданных
4. Создание объединенного индекса
5. Предоставление API для поиска свойств
```

**API**:
```java
// Получить все метаданные
ConfigurationMetadata metadata = service.getMetadata();

// Найти конкретное свойство
PropertyMetadata prop = service.getProperty("server.port");

// Найти свойства по префиксу
List<PropertyMetadata> props = service.findPropertiesWithPrefix("server.");
```

### 4. Completion Layer (Слой автодополнения)

**Назначение**: Предоставление автодополнения в YAML файлах

**Классы**:
- `SpringBootYamlCompletionContributor` - contributor для IntelliJ completion system

**Алгоритм работы**:
```
1. Пользователь начинает ввод в application.yaml
2. IntelliJ вызывает CompletionContributor
3. Определяется текущий контекст (путь в YAML дереве)
4. Запрашиваются подходящие свойства из ConfigurationMetadataService
5. Формируются LookupElements с:
   - Названием свойства
   - Типом данных
   - Описанием
   - Значением по умолчанию
   - Иконкой
6. Отображаются пользователю
```

**Пример контекста**:
```yaml
server:
  port: 8080
  servlet:
    |  <- курсор здесь
```
Префикс: `server.servlet.`
Предложения: `context-path`, `session`, и т.д.

### 5. Listener Layer (Слой слушателей)

**Назначение**: Реагирование на события в проекте

**Классы**:
- `ProjectDependencyChangeListener` - отслеживание изменений зависимостей

**События**:
- Обновление Gradle/Maven зависимостей
- Добавление/удаление библиотек
- Изменение classpath

**Действия**:
- Перезагрузка метаданных
- Инвалидация кэша
- Обновление индекса

## Поток данных

```
┌─────────────────────┐
│   Project opened    │
└──────────┬──────────┘
           │
           v
┌─────────────────────────────────┐
│  ConfigurationMetadataService   │
│         initializes             │
└──────────┬──────────────────────┘
           │
           v
┌─────────────────────────────────┐
│   Scan project dependencies     │
│   (JAR files in classpath)      │
└──────────┬──────────────────────┘
           │
           v
┌─────────────────────────────────┐
│ For each JAR:                   │
│  - Check for metadata file      │
│  - Parse with MetadataParser    │
│  - Merge into aggregated model  │
└──────────┬──────────────────────┘
           │
           v
┌─────────────────────────────────┐
│  Create property index          │
│  (Map<String, PropertyMetadata>)│
└──────────┬──────────────────────┘
           │
           v
┌─────────────────────────────────┐
│     Cache ready for use         │
└─────────────────────────────────┘

User types in YAML:
           │
           v
┌─────────────────────────────────┐
│  SpringBootYamlCompletion       │
│  Contributor triggered          │
└──────────┬──────────────────────┘
           │
           v
┌─────────────────────────────────┐
│  Extract YAML context path      │
│  (e.g., "server.servlet.")      │
└──────────┬──────────────────────┘
           │
           v
┌─────────────────────────────────┐
│  Query service for matching     │
│  properties                     │
└──────────┬──────────────────────┘
           │
           v
┌─────────────────────────────────┐
│  Build LookupElements           │
│  with metadata                  │
└──────────┬──────────────────────┘
           │
           v
┌─────────────────────────────────┐
│  Display to user                │
└─────────────────────────────────┘
```

## Интеграция с IntelliJ Platform

### Extension Points

Плагин регистрируется через `plugin.xml`:

```xml
<extensions defaultExtensionNs="com.intellij">
  <!-- Автодополнение для YAML -->
  <completion.contributor
    language="yaml"
    implementationClass="...SpringBootYamlCompletionContributor"/>

  <!-- Сервис на уровне проекта -->
  <projectService
    serviceImplementation="...ConfigurationMetadataService"/>
</extensions>

<projectListeners>
  <!-- Слушатель изменений зависимостей -->
  <listener class="...ProjectDependencyChangeListener"
    topic="...ExternalSystemTaskNotificationListener"/>
</projectListeners>
```

### Зависимости плагина

- `com.intellij.modules.platform` - базовая платформа
- `com.intellij.modules.java` - поддержка Java проектов
- `org.jetbrains.plugins.yaml` - работа с YAML
- `com.intellij.gradle` - интеграция с Gradle

## Производительность

### Оптимизации

1. **Ленивая загрузка**: Метаданные загружаются только при первом обращении
2. **Индексирование**: Создается HashMap для O(1) поиска по имени свойства
3. **Кэширование**: Метаданные хранятся в памяти и не перечитываются
4. **Инкрементальное обновление**: Перезагрузка только при изменении зависимостей

### Потребление памяти

Типичный Spring Boot проект с ~50 зависимостями:
- ~2000 свойств
- ~100 групп
- ~200 hints
- Общий размер в памяти: ~1-2 MB

## Тестирование

### Уровни тестирования

1. **Unit тесты**:
   - Модели (ConfigurationMetadataTest, PropertyMetadataTest)
   - Парсер (MetadataParserTest)
   - Независимы от IntelliJ Platform

2. **Integration тесты** (требуют IntelliJ Platform):
   - ConfigurationMetadataService
   - SpringBootYamlCompletionContributor

3. **E2E тесты** (требуют IDE):
   - Полный цикл: ввод → автодополнение → вставка

### Тестовые данные

- `test-metadata/spring-configuration-metadata.json` - примеры метаданных
- `test-yaml/application.yaml` - примеры конфигураций

## Расширяемость

### Планируемые расширения

1. **Валидация значений**:
   - Проверка типов
   - Проверка допустимых значений из hints
   - Проверка форматов (Duration, DataSize, etc.)

2. **Quick Fixes**:
   - Замена deprecated свойств
   - Исправление опечаток
   - Конвертация типов

3. **Навигация**:
   - Переход к source type класса
   - Поиск использований свойства

4. **Documentation**:
   - Показ документации при hover
   - Ссылки на Spring Boot reference

5. **@ConfigurationProperties интеграция**:
   - Сканирование аннотированных классов проекта
   - Генерация метаданных из полей
   - Поддержка вложенных классов

## Безопасность и надежность

### Обработка ошибок

- Graceful degradation при ошибках парсинга
- Логирование проблем
- Продолжение работы с частичными данными

### Thread Safety

- Synchronized блоки в ConfigurationMetadataService
- Immutable модели (где возможно)
- Thread-safe кэширование

## Совместимость

- IntelliJ IDEA 2023.2+
- JDK 17+
- Spring Boot 2.x и 3.x
- Gradle и Maven проекты
