## TESTANDO ##

Pré-requisito: Docker

Execute o seguinte comando para construir a imagem:

`docker build -t teste-gympass .`

Após a construção, para rodar, execute o seguinte:

`docker run -v [arquivo de entrada]:/var/teste/input teste-gympass`

Substitua [arquivo de entrada] pelo caminho do arquivo que servirá de entra para o programa

## CONSIDERAÇÕES ##

Para evitar a exigência de buildar o projeto localmente e exigir uma JVM, já estou versionando uma versão construída

Caso deseje construir localmente, é necessário ter a JDK 11 instalada (mínimo) e executar o seguinte comando:

`./sbt assembly`

Um jar será gerado na pasta `target/scala-2.12`