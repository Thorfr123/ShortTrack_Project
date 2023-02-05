# Sprint 4: Spring Preview

## Sprint Goal:

Implementar grande parte dos user cases referentes à parte online da aplicação, nomeadamente permitindo ao utilizador realizar login numa conta e criar listas pessoais e grupos para organizar tarefas.
Adicionalmente, os dados deverão ser guardados na database, para permitir ao utilizador reaver os dados num outro qualquer dispositivo numa nova inicialização da aplicação.
Além disso, a utilização de grupos permitirá interagir com outros utilizadores, permitindo organizar tarefas em projetos de equipa. 

## Sprint Planning:

Implementar os User Cases de 16 a 24, com a utilização de JavaFX para a interface e de JDBC para o armazenamento dos dados em database.

![Spring Planning](https://user-images.githubusercontent.com/109107004/216834017-f562d1ba-efa4-405d-8d16-28fe4df49c4b.PNG)

## Done User Stories:

Foram implementados os User Cases 16 a 24, assim como inicialmente planeado, contudo será necessário alterar a implementação do user case 19, uma vez que para adicionar membros num grupo este deverá receber uma notificação para decidir se quer ou não entrar no grupo.
Neste momento, a adição de membros no grupo é feita automaticamente, ou seja, o membro não tem a opção de recusar o "convite".
Além disso, devido à necessidade de implementar um sistema de notificações para realizar o user case acima referido, optou-se por adiar essa implementação, para o próximo spring, e implementar, neste spring, os user cases 26, 27 e 28.

![Done User Stories](https://user-images.githubusercontent.com/109107004/216834020-f77de74d-002d-4f96-a4fd-0b19d0c225b6.PNG)

## Sprint Retrospective:

A implementação da interface demorou pouco tempo, uma vez que parte da implementação já tinha sido realizada nos springs anteriores.
Além disso, a implementação da lógica da aplicação correu como esperado, uma vez que grande parte das funcionalidades são semelhantes às desenvolvidas no spring passado.
Por outro lado, a integração com a database demorou mais do que o esperado, visto que foram necessárias algumas alterações no código para permitir detetar algumas situações especificas.
Por exemplo, se um membro do grupo tem uma tarefa, a ele atribuída, e este é removido do grupo, então essa tarefa deve deixar de ter um membro a ela atribuída.
Assim, a existência de esta e outras situações semelhantes levou a necessidade de um estudo mais profundo de bases de dados, consumindo bastante tempo.
Por fim, durante este spring também foi necessário consumir algum tempo para a correção de bugs e reestruturação o código, de forma a minimizar o tempo de implementação para o próximo spring.

## Sprint Review:

Neste momento, a aplicação permite realizar logins, criar e deletar contas, utilizando uma ligação à base de dados.
Além disso, caso o utilizador não deseje criar uma conta, pode criar, editar e deletar listas e tarefas, sendo estas guardadas localmente num ficheiro encriptado.
Pode ainda, organizar, pesquisar e ordenar as suas tarefas, de forma prática e eficiente.
Por outro lado, caso o utilizador crie uma conta, este poderá realizar todas as tarefas acima referidas, além de criar grupos.
Assim, o utilizador que criar o grupo, será designado como o seu "manager", podendo adicionar outros utilizadores, com contas, e criar, editar, atribuir e apagar tarefas nesse grupo.
Por outro lado, os membros desse grupo, neste momento, poderão apenas alterar a descrição e o estado das tarefas a eles atribuídas. Obviamente também podem sair do grupo.
Por fim, os dados dos utilizadores com contas são guardados numa base de dados, o que permite que possam utilizar a aplicação em mais de um dispositivo.
Além disso, todas estas funcionalidades podem ser operadas através de uma interface gráfica.
