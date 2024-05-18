# Задача "о китайском почтальне". Вычислительная часть.
Данная программа написана на языке Kotlin (1.9.22) и реализует несколько метаэвристических алгоритмов. Все эти алгоритмы направлены на нахождение кратчайшего замкнутого пути в графе, который будет проходить по каждому ребру минимум один раз.

## Запуск программы
### Пример команды запуска
```console
java -jar ./chinese-postman-problem.jar -Dlogfile-path=.\logfile.log --config .\configuration.json --graph .\graph.json --output .\result.json 
```
### Параметры запуска
Для запуска программы потребуется установить Java Virtual Machine на ваше устройство. Параметры запуска:
- --config - путь к json-файлу с настройками для алгоритма. В нём должен быть находиться объект, в котором указано кодовое название используемого метода (поле <b>type</b>), а также приведена конфигурация к нему (для каждого из алгоритмов она своя)
- --graph - путь к json-файлу с информацией о структуре графа. В файле должен находиться объект, с полями edges и nodes соответственно. Оба поля должны содержать массив объектов (вершин и рёбер).
- --output - путь к файлу, в который будет записан результат работы программы. Вывод представляет собой json-объект.
- (необязательный) -Dlogfile-path - путь к файлу, в который программа будет записывать свои логи. В случае отсутствия параметра будет создан .log-файл в домашней директории пользователя.
### Примеры входных файлов
#### Файл с настройкой алгоритма (на примере ГА)
```JSON
{
  "type": "GENETIC",
  "genetic": {
    "iterationsCount": 200,
    "populationSize": 100,
    "startNodeId": "f6788ca0-08e3-48ff-8ce0-0569f0b323f6",
    "parents": {
      "selection": "TOURNAMENT",
      "chooser": "PANMIXIA"
    },
    "recombinationType": "DISCRETE",
    "mutation": {
      "type": "REPLACING",
      "rate": 0.5
    },
    "newPopulation": {
      "type": "TRUNCATION",
      "rate": 0.5
    }
  }
}
```
#### Файл со структурой графа
```JSON
{
  "nodes": [
    {
      "label": "Node-0",
      "id": "4f44e9c0-827f-49fd-9876-c9cdd3343824"
    },
    {
      "label": "Node-1",
      "id": "895f4f27-837c-4540-a940-0126a708a868"
    },
    {
      "label": "Node-2",
      "id": "4a749fcf-32f6-4aa8-9d23-f37c905d16ce"
    }
  ],
  "edges": [
    {
      "source": {
        "label": "Node-0",
        "id": "4f44e9c0-827f-49fd-9876-c9cdd3343824"
      },
      "destination": {
        "label": "Node-1",
        "id": "895f4f27-837c-4540-a940-0126a708a868"
      },
      "weight": 100,
      "id": "5e505e0f-2cea-46a0-9160-9bc40bd79102"
    },
    {
      "source": {
        "label": "Node-0",
        "id": "4f44e9c0-827f-49fd-9876-c9cdd3343824"
      },
      "destination": {
        "label": "Node-1",
        "id": "895f4f27-837c-4540-a940-0126a708a868"
      },
      "weight": 100,
      "id": "674e14b0-013d-414d-8c9a-f97ad7198934"
    },
    {
      "source": {
        "label": "Node-0",
        "id": "4f44e9c0-827f-49fd-9876-c9cdd3343824"
      },
      "destination": {
        "label": "Node-1",
        "id": "895f4f27-837c-4540-a940-0126a708a868"
      },
      "weight": 100,
      "id": "9ac12d1f-4e2f-4845-908c-c28f9ea986e4"
    },
    {
      "source": {
        "label": "Node-1",
        "id": "895f4f27-837c-4540-a940-0126a708a868"
      },
      "destination": {
        "label": "Node-2",
        "id": "4a749fcf-32f6-4aa8-9d23-f37c905d16ce"
      },
      "weight": 100,
      "id": "af9a26a1-a357-4f69-b25f-a265a65b61b0"
    },
    {
      "source": {
        "label": "Node-2",
        "id": "4a749fcf-32f6-4aa8-9d23-f37c905d16ce"
      },
      "destination": {
        "label": "Node-0",
        "id": "4f44e9c0-827f-49fd-9876-c9cdd3343824"
      },
      "weight": 100,
      "id": "329db316-3545-4e7c-a223-bb422d13fcd4"
    }
  ]
}
```

## Вывод программы
Основной результат работы программы записывается в файл, путь к которому передан через параметр --output. Для информативности программа в процессе своей работы ведёт запись логов. Путь к файлу с логами программы можно задать через параметр -Dlogfile-path. В случае если такой параметр не передан, логи сохраняются в файл <b>chinese-postman-problem-program.log</b> в домашнем каталоге пользователя.
### Пример файла с результатами работы программы
```JSON
{
    "path": [
        "674e14b0-013d-414d-8c9a-f97ad7198934",
        "af9a26a1-a357-4f69-b25f-a265a65b61b0",
        "329db316-3545-4e7c-a223-bb422d13fcd4",
        "9ac12d1f-4e2f-4845-908c-c28f9ea986e4",
        "af9a26a1-a357-4f69-b25f-a265a65b61b0",
        "329db316-3545-4e7c-a223-bb422d13fcd4"
    ],
    "length": 600.0,
    "executionTimeMs": 324
}
```
### Пример файла с логами программы (на примере ГА)
```log
21:19:44.718 [main] INFO  ROOT - A custom log path is used: C:\Users\jigal\chinese-postman-problem-program.log
21:19:44.965 [main] INFO  FileHelper - Data from the C:\Users\jigal\config.json has been successfully read
21:19:44.967 [main] INFO  CommandLineHelper - The following parameter was successfully received from the command line: --config
21:19:44.982 [main] INFO  FileHelper - Data from the C:\Users\jigal\data.json has been successfully read
21:19:44.983 [main] INFO  CommandLineHelper - The following parameter was successfully received from the command line: --graph
21:19:45.159 [main] INFO  CommandLineHelper - The following parameter was successfully received from the command line: --output
21:19:45.168 [main] INFO  GeneticAlgorithm - Launching the genetic algorithm
21:19:45.279 [main] INFO  GeneticAlgorithm - A starting population has been created (id: 37e2e7ea-4aa2-417d-9791-e72ce112edec)
21:19:58.554 [main] INFO  FileHelper - Writing to the C:\Users\jigal\Downloads\results.json has been completed successfully
```
