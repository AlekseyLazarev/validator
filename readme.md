 Validator
 Задание:
 1. Сделать runnable jar файл который бы получал как входные данные:
 имя xml файла, имя xsd файла(по этой схеме должен быть сформирован xml файл), имя xslt файла и имя результирующего файла(тоже хмл).
 Приложение должно взять xml файл по имени, провалидировать его по xsd схеме, затем произвести xslt трансформацию по xslt,
 затем результат провалидировать по xsd схеме(результирующий хмл также должен соответствовать некоему классу их xsd схемы) и сохранить результирующий xml в файл.
 Нужно предусмотреть обработку эксепшенов и ведение лога по каждой операции(валидация входящего хмл, xslt трансформация, валидация результата).
 2. Сделать юнит тесты на JUnit 5.
 3. Вычислить покрытие кода тестами используя JaCoCo.
 4. Покрытие должно быть 100%, либо объяснено почему 100% недостижимо.