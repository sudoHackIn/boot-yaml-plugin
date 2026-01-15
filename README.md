# Spring Boot YAML Autocomplete Plugin

IntelliJ IDEA плагин для автодополнения Spring Boot `application.yaml` файлов с учетом подключенных автоконфигураций.

## Функциональность

- ✅ Автодополнение для Spring Boot конфигурационных свойств
- ✅ Парсинг `spring-configuration-metadata.json` из зависимостей
- ✅ Поддержка вложенных свойств
- ✅ Отображение типов данных и значений по умолчанию
- ✅ Поддержка deprecated свойств
- ✅ Автоматическое обновление при изменении зависимостей

## Архитектура

### Компоненты

1. **Model Layer** (`com.github.sudohackin.bootyaml.model`)
   - `ConfigurationMetadata` - корневая модель метаданных
   - `PropertyMetadata` - модель конфигурационного свойства
   - `GroupMetadata` - модель группы свойств
   - `HintMetadata` - модель подсказок для значений
   - `DeprecationMetadata` - информация об устаревших свойствах

2. **Parser Layer** (`com.github.sudohackin.bootyaml.parser`)
   - `MetadataParser` - парсер JSON метаданных из Spring Boot

3. **Service Layer** (`com.github.sudohackin.bootyaml.service`)
   - `ConfigurationMetadataService` - сервис для загрузки и кэширования метаданных
   - Сканирует все JAR зависимости проекта
   - Кэширует метаданные для быстрого доступа

4. **Completion Layer** (`com.github.sudohackin.bootyaml.completion`)
   - `SpringBootYamlCompletionContributor` - предоставляет автодополнение в YAML файлах

5. **Listener Layer** (`com.github.sudohackin.bootyaml.listener`)
   - `ProjectDependencyChangeListener` - отслеживает изменения зависимостей

## Структура проекта

```
boot-yaml-plugin/
├── src/
│   ├── main/
│   │   ├── java/com/github/sudohackin/bootyaml/
│   │   │   ├── model/              # Модели данных
│   │   │   ├── parser/             # Парсеры
│   │   │   ├── service/            # Бизнес-логика
│   │   │   ├── completion/         # Автодополнение
│   │   │   └── listener/           # Event listeners
│   │   └── resources/
│   │       └── META-INF/
│   │           └── plugin.xml      # Конфигурация плагина
│   └── test/
│       ├── java/                   # Unit тесты
│       └── resources/
│           ├── test-metadata/      # Тестовые метаданные
│           └── test-yaml/          # Тестовые YAML файлы
├── build.gradle.kts
└── README.md
```

## Разработка

### Требования

- JDK 17+
- Gradle 8.5+
- IntelliJ IDEA 2023.2+

### Сборка

```bash
./gradlew build
```

### Запуск тестов

```bash
./gradlew test
```

### Запуск плагина в IDE

```bash
./gradlew runIde
```

## Тестирование

### Unit тесты

Проект включает комплексные unit тесты для всех компонентов:

- `ConfigurationMetadataTest` - тесты модели метаданных
- `PropertyMetadataTest` - тесты модели свойств
- `MetadataParserTest` - тесты парсера

### Тестовые данные

В `src/test/resources/` находятся:
- `test-metadata/spring-configuration-metadata.json` - примеры метаданных
- `test-yaml/application.yaml` - примеры YAML конфигураций

### Запуск тестов

```bash
# Все тесты
./gradlew test

# Конкретный тест
./gradlew test --tests ConfigurationMetadataTest

# С отчетом покрытия
./gradlew test jacocoTestReport
```

## Как работает автодополнение

1. **Инициализация**: При открытии проекта `ConfigurationMetadataService` сканирует все JAR зависимости
2. **Парсинг**: Находит и парсит файлы `META-INF/spring-configuration-metadata.json`
3. **Кэширование**: Объединяет все метаданные и создает индекс для быстрого поиска
4. **Автодополнение**: При вводе в YAML файле `SpringBootYamlCompletionContributor` предлагает подходящие свойства
5. **Обновление**: При изменении зависимостей метаданные автоматически перезагружаются

## Пример использования

При наборе в `application.yaml`:

```yaml
server:
  po<cursor>
```

Плагин предложит:
- `port` (Integer) - Server HTTP port (default: 8080)
- ... другие свойства начинающиеся с `server.po`

## План дальнейшей разработки

- [ ] Валидация значений свойств
- [ ] Поддержка профилей (application-{profile}.yaml)
- [ ] Интеграция с @ConfigurationProperties классами
- [ ] Quick fixes для deprecated свойств
- [ ] Документация при hover
- [ ] Навигация к source type

## Лицензия

MIT License
