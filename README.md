# Sistema RP GroupTree

### Sobre o programa:
* Foi utilizada a IDE IntelliJ
* O programa está na branch master do git
* Ao executar o programa, será solicitado que o usuário informe via console o caminho do arquivo que será lido e a máscara que será aplicada na classificação.
* O programa realiza as validações referentes ao arquivo e se não houver erros, irá ler o arquivo
* O programa armazena a lista de colunas e a lista de valores em cada linha
* A cada linha com valores que é lida, o sistema vai adicionando à arvore
	* A máscara é aplicada na classificação quando a quantidade de números da classificação não for maior do que a quantidade de números da máscara. Caso contrário printa um WARN no console e segue o arquivo
	* Se não houver uma descrição para a classificação, ela não é cadastrada na árvore(Ex: Se a classificação for 1.21, não será adicionado o nó 1 se ele não tem uma descrição)
* O programa ordena os nós da árvore pela classificação
* A árvore é salva em memória
* Para visualizar a árvore completa, foi criado o endpoint http://localhost:8081/api/grupos que é uma requisição do tipo GET 
	* No caso foi alterado no arquivo [properties](https://github.com/ttuscfc/rpGroupTree/blob/master/src/main/resources/application.properties) para utilizar a porta 8081
	* Ao executar o endpoint, será realizada a busca em memória da árvore completa
* Para filtrar a árvore pela classificação foi adicionado ao endpoint o parâmetro "classificacao". Portanto, se, por exemplo, for necessário filtrar a árvore pela classificação 3, o endpoint ficaria assim http://localhost:8081/api/grupos?classificacao=3
	* Ao executar o endpoint, será realizada a busca em memória do nó da árvore que possua a classificação enviada por parâmetro e irá retornar o nó com seus filhos(Ex: 3, 3.1, 3.1.1, 3.1.1.1)
* Foi adicionado tratamento de exceções
* Foram criados testes unitários para testar os métodos da service e obter 100% de cobertura dos testes



#### Arquivos txt utilizados para testes(inclusive os testes unitários) e que estão no diretório [arquivos](https://github.com/ttuscfc/rpGroupTree/tree/master/arquivos):
* [grupos.txt](https://github.com/ttuscfc/rpGroupTree/blob/master/arquivos/grupos.txt)
* [teste.txt](https://github.com/ttuscfc/rpGroupTree/blob/master/arquivos/teste.txt)
* [testeColunaErro](https://github.com/ttuscfc/rpGroupTree/blob/master/arquivos/testeColunaErro.txt)
* [testeSemClassificacao](https://github.com/ttuscfc/rpGroupTree/blob/master/arquivos/testeSemClassificacao.txt)
* [testeVazio](https://github.com/ttuscfc/rpGroupTree/blob/master/arquivos/testeVazio.txt)
