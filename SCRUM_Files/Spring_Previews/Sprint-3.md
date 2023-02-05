# Sprint Goal:

Implementar toda a parte offline da aplicação, nomeadamente permitindo ao utilizador criar listas pessoais e organizar tarefas nas listas.
Adicionalmente, os dados deverão ser guardados localmente para permitir ao utilizador reaver os dados numa nova inicialização da aplicação.

## Sprint Planning:

Implementar os User Cases de 5 a 13, com a utilização de JavaFX para a interface e de ficheiros .dat para o armazenamento dos dados.

![Spring Planning](https://user-images.githubusercontent.com/109107004/216833920-d0d49b71-498e-4265-997f-f7a511ced5e8.PNG)

## Done User Stories:

Foram implementados os User Cases 5 a 13, assim como inicialmente planeado.
Além disso, durante a implementação surgiu a necessidade da adição de 2 novos User Cases, nomeadamente para o ordenamento das tarefas nas listas, e para a pesquisa de tarefas globalmente.
Estes dois User Cases foram igualmente implementados.

![Done User Stories](https://user-images.githubusercontent.com/109107004/216833922-eea5407d-11f2-4b24-ab78-a2eeeef00172.PNG)

## Sprint Retrospective:

A implementação da interface demorou pouco tempo, uma vez que parte da implementação já tinha sido realizada no spring anterior.
Por outro lado, a implementação da lógica da aplicação consumiu grande parte do spring, uma vez que foram necessárias alterações ao código para incluir os User Cases adicionais.
Além disso, a implementação do código que permitiu armazenar os dados localmente correu como o esperado.
Por fim, como o tempo consumido nos pontos anteriores, na sua globalidade, foi menor do que o esperado, foi possível começar a implementação do código para o próximo spring.
Será importante ainda referir que a estrutura do projeto foi alterada para um projeto maven, com o objetivo de permitir criar um ficheiro .jar executável, sem a necessidade de instalar as bibliotecas de JavaFX e JDBC manualmente.

## Sprint Review:

Neste momento, a aplicação permite realizar logins, criar e deletar contas, apesar de não terem qualquer utilidade, por enquanto.
Além disso, permite ainda criar, editar e apagar listas e tarefas.
Por outro lado, também é possível ordenar e pesquisar determinadas tarefas, pelo seu nome, pela sua data de criação e pelo seu prazo.
Por fim, quando a aplicação é fechada, os dados são guardados localmente num ficheiro encriptado, de forma que o utilizador possa reaver os dados da última utilização.
Todas estas funcionalidades podem ser operadas através de uma interface gráfica.

