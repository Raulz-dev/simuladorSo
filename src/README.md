# Simulador de Sistema de Arquivos em Java com Journaling

## Metodologia

O simulador será desenvolvido em linguagem de programação Java. Ele receberá as chamadas de métodos com os devidos parâmetros. Em seguida, serão implementados os métodos correspondentes aos comandos de um Sistema Operacional (SO).

O programa executará cada funcionalidade e exibirá o resultado na tela quando necessário.

## Parte 1: Introdução ao Sistema de Arquivos com Journaling

### Descrição do sistema de arquivos

Um **sistema de arquivos** é um componente crucial de um sistema operacional que controla como os dados são armazenados e recuperados. Ele organiza arquivos em uma estrutura hierárquica de diretórios (pastas), permitindo que usuários e aplicações gerenciem e acessem dados de forma eficiente em dispositivos de armazenamento secundário (como HDs ou SSDs). Sua importância reside na capacidade de fornecer persistência de dados, abstração do hardware de armazenamento e mecanismos para controle de acesso e organização da informação.

O **conceito de journaling** é uma técnica utilizada por sistemas de arquivos para aumentar sua resiliência contra falhas (como quedas de energia ou crashes do sistema). Antes de realizar qualquer modificação nos dados principais do sistema de arquivos, as alterações pretendidas são primeiramente registradas em uma área especial chamada "journal" ou "log".

### Journaling

**Propósito:**
O principal propósito do journaling é garantir a **consistência** do sistema de arquivos. Em caso de falha, o sistema pode verificar o journal para concluir operações pendentes ou reverter operações incompletas, evitando corrupção de dados e longos processos de verificação de integridade (como o `fsck`).

**Funcionamento:**
1.  **Registro (Write-ahead Logging):** Antes de uma operação que modifica o sistema de arquivos (ex: criar um arquivo, escrever dados) ser efetivamente aplicada aos metadados ou dados, uma descrição dessa operação é escrita no journal.
2.  **Aplicação (Commit):** Após o registro no journal ser confirmado como persistido, a operação é realizada no sistema de arquivos.
3.  **Marcação:** Uma vez que a operação é concluída com sucesso no sistema de arquivos, a entrada correspondente no journal pode ser marcada como completa ou removida.
4.  **Recuperação:** Se o sistema falhar, durante a reinicialização, ele verifica o journal. Operações registradas mas não completamente aplicadas podem ser refeitas (redo) ou desfeitas (undo) para levar o sistema a um estado consistente.

**Tipos de Journaling (exemplos):**
*   **Write-ahead logging (WAL):** O tipo mais comum, onde as alterações são escritas no log antes de serem aplicadas ao local principal dos dados.
*   **Data Journaling:** Registra tanto metadados quanto o conteúdo dos dados no journal. Oferece maior proteção, mas pode impactar o desempenho.
*   **Metadata Journaling (Ordered Journaling):** Registra apenas as alterações nos metadados no journal, mas garante que as escritas de dados ocorram antes das escritas de metadados associadas. É um bom compromisso entre segurança e desempenho.
*   **Log-structured File System:** Todo o disco é tratado como um log. Novas escritas são sempre sequenciais no final do log.

Para este simulador, focaremos nos princípios do write-ahead logging para metadados e operações.

## Parte 2: Arquitetura do Simulador

### Estrutura de Dados

O sistema de arquivos simulado é representado utilizando as seguintes classes Java principais:

*   **`Node.java`**: Uma classe abstrata (ou interface) que serve como base para arquivos e diretórios, contendo atributos comuns como nome, data de criação e data da última modificação.
*   **`File.java`**: Estende `Node` e representa um arquivo. Contém atributos específicos como tamanho e conteúdo (simulado, por exemplo, como uma `String` ou `byte[]`).
*   **`Directory.java`**: Estende `Node` e representa um diretório. Contém uma lista de `Node`s filhos (outros arquivos ou subdiretórios), permitindo a estrutura hierárquica.
*   **`FileSystemManager.java`**: A classe central que gerencia toda a lógica do sistema de arquivos. Ela mantém a referência ao diretório raiz, processa comandos como criação, exclusão, listagem de arquivos/diretórios, e interage com o `Journal`.
*   **`FileSystemSimulatorShell.java`**: Classe responsável pela interface com o usuário (linha de comando), lendo os comandos digitados e invocando os métodos apropriados no `FileSystemManager`.

### Journaling (Implementação no Simulador)

O journaling neste simulador é implementado para registrar as operações que modificam o estado do sistema de arquivos antes que elas sejam efetivamente aplicadas.

*   **Estrutura do Log (`Journal.java`)**:
    *   A classe `Journal` mantém uma lista de entradas de log (transações).
    *   Cada entrada de log é uma representação textual ou um objeto que descreve a operação a ser realizada (ex: "CREATE_FILE:/caminho/arquivo.txt", "DELETE_DIRECTORY:/caminho/diretorio", "WRITE_START:/caminho/arquivo.txt", "WRITE_COMMIT:/caminho/arquivo.txt").
    *   O log opera de forma sequencial (append-only).

*   **Operações Registradas**:
    As seguintes operações (e seus metadados relevantes) são candidatas a serem registradas no journal antes de sua execução pelo `FileSystemManager`:
    *   Criação de arquivo (`touch`, `createFile`)
    *   Criação de diretório (`mkdir`, `createDirectory`)
    *   Exclusão de arquivo (`rm`, `deleteFile`)
    *   Exclusão de diretório (`rmdir`, `deleteDirectory`)
    *   Escrita em arquivo (poderia ser simplificado para registrar o início e o fim da escrita)
    *   Renomeação de arquivo/diretório
    *   Movimentação de arquivo/diretório

No simulador, antes que o `FileSystemManager` modifique suas estruturas de dados internas (como adicionar um `File` a um `Directory`), ele primeiro adicionaria uma entrada ao `Journal`. Após a modificação ser bem-sucedida na memória, a entrada no journal poderia ser (conceitualmente) marcada como "concluída" ou o journal limpo periodicamente. Em uma simulação de recuperação, o `FileSystemManager` leria o `Journal` para reconstruir o estado.

## Parte 3: Implementação em Java

*   **Classe `FileSystemManager.java`**:
    *   Implementa a lógica central do simulador do sistema de arquivos.
    *   Contém métodos para cada operação do sistema de arquivos, como:
        *   `createFile(String path, String name)`
        *   `createDirectory(String path, String name)`
        *   `deleteFile(String path)`
        *   `deleteDirectory(String path)`
        *   `listDirectoryContents(String path)`
        *   `writeFile(String path, String content)` (simulado)
        *   `readFile(String path)` (simulado)
        *   Outros comandos como `cd`, `pwd`, `mv`, `cp`.
    *   Gerencia a estrutura hierárquica de `Directory` e `File` objetos, começando pelo diretório raiz.
    *   Interage com a classe `Journal` para registrar operações.

*   **Classes `File.java` e `Directory.java`**:
    *   **`File`**: Representa um arquivo.
        *   Atributos: `name` (String), `content` (String ou `byte[]` para simular dados), `size` (int/long), `creationDate` (Date/Timestamp), `lastModifiedDate` (Date/Timestamp).
        *   Métodos: getters e setters para seus atributos, talvez um método para atualizar conteúdo e tamanho.
    *   **`Directory`**: Representa um diretório.
        *   Atributos: `name` (String), `children` (List<Node>), `creationDate` (Date/Timestamp), `lastModifiedDate` (Date/Timestamp).
        *   Métodos: `addChild(Node node)`, `removeChild(Node node)`, `getChild(String name)`, `listChildren()`, getters e setters.

*   **Classe `Journal.java`**:
    *   Gerencia o log de operações do sistema de arquivos para fins de journaling.
    *   Atributos: `logEntries` (List<String> ou List<LogEntryObject>).
    *   Métodos:
        *   `addEntry(String operationDetails)`: Adiciona uma nova entrada ao log.
        *   `getEntries()`: Retorna todas as entradas do log.
        *   `clearLog()`: Limpa o log (simulando um checkpoint ou commit bem-sucedido).
        *   (Em um sistema real) `replayLog()`: Usado durante a recuperação para aplicar operações pendentes.

*   **Classe `FileSystemSimulatorShell.java`**:
    *   Ponto de entrada do programa (`main` método).
    *   Provê uma interface de linha de comando (shell) para o usuário interagir com o simulador.
    *   Lê os comandos do usuário (ex: `mkdir`, `ls`, `touch`, `help`).
    *   Analisa os comandos e invoca os métodos correspondentes na instância de `FileSystemManager`.
    *   Exibe os resultados das operações ou mensagens de erro para o usuário.

## Parte 4: Instalação e Funcionamento

### Recursos Utilizados na Implementação:
*   **Linguagem de Programação:** Java (JDK 11 ou superior recomendado).
*   **Ambiente de Desenvolvimento Integrado (IDE):** IntelliJ IDEA (recomendado, mas qualquer IDE Java compatível como Eclipse ou VS Code pode ser usado).
*   **Sistema de Controle de Versão:** Git (para gerenciamento de código fonte, hospedado no GitHub).
*   **Build Tool:** Nenhuma ferramenta de build específica como Maven ou Gradle é estritamente necessária para este projeto, podendo ser compilado e executado diretamente pela IDE.
*   **Dependências Externas:** Nenhuma (o projeto utiliza apenas bibliotecas padrão do Java SE).

### Orientações sobre a Execução do Simulador:

1.  **Obter o Código:**
    *   Clone o repositório do GitHub para sua máquina local:
        ```bash
        git clone [URL_DO_SEU_REPOSITORIO_GIT]
        ```
    *   Ou baixe o arquivo ZIP do projeto e extraia-o.

2.  **Abrir o Projeto na IDE:**
    *   **IntelliJ IDEA:**
        *   Abra o IntelliJ IDEA.
        *   Selecione "Open" ou "Import Project".
        *   Navegue até a pasta onde você clonou ou extraiu o projeto e selecione-a.
        *   Aguarde a IDE indexar os arquivos e configurar o projeto.
    *   **Outras IDEs:** Siga os procedimentos específicos da sua IDE para abrir um projeto Java existente.

3.  **Compilar o Projeto:**
    *   A maioria das IDEs compila o projeto automaticamente ao abri-lo ou antes de executá-lo.
    *   Se necessário, procure por uma opção "Build" ou "Compile" no menu da IDE.

4.  **Executar o Simulador:**
    *   Localize a classe `br.edu.fs.simulator.FileSystemSimulatorShell.java` (ou o caminho correspondente no seu pacote).
    *   Clique com o botão direito sobre o arquivo `FileSystemSimulatorShell.java` e selecione "Run 'FileSystemSimulatorShell.main()'".
    *   Alternativamente, abra a classe e clique no botão de "Play" (Executar) ao lado do método `main`.

5.  **Interagindo com o Simulador:**
    *   Após a execução, o console da IDE exibirá o prompt do simulador (ex: `fs> `).
    *   Digite os comandos suportados e pressione Enter. Alguns comandos básicos incluem:
        *   `help`: Lista todos os comandos disponíveis e suas descrições.
        *   `mkdir <nome_diretorio>`: Cria um novo diretório no local atual.
        *   `touch <nome_arquivo>`: Cria um novo arquivo vazio no local atual.
        *   `ls`: Lista o conteúdo do diretório atual.
        *   `cd <caminho_diretorio>`: Muda o diretório atual.
        *   `pwd`: Mostra o caminho do diretório atual.
        *   `rm <nome_arquivo>`: Remove um arquivo.
        *   `rmdir <nome_diretorio>`: Remove um diretório vazio.
        *   `cat <nome_arquivo>`: Exibe o conteúdo de um arquivo (se implementado).
        *   `write <nome_arquivo> "<conteudo>"`: Escreve conteúdo em um arquivo (se implementado).
        *   `exit`: Encerra o simulador.

    *   Siga as instruções fornecidas pelo comando `help` para mais detalhes sobre o uso de cada comando.
