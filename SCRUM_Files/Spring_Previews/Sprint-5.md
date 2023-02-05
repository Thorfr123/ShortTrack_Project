# Sprint 5: Spring Preview

## Sprint Goal:

Implementar os **restantes User Cases**, nomeadamente permitindo ao utilizador receber e enviar notificações para participar de um grupo e pedir ajuda nas suas tarefas dentro do grupo. Adicionalmente, deverão ser **corrigidos diversos "bugs"** que ocorrem devido a situações específicas de acesso à database. Por exemplo, se um membro estiver a editar uma tarefa e o manager eliminar essa tarefa, este deve ser alertado e não poderá alterar uma tarefa que já não existe. Por fim, deverão ser realizadas algumas **otimizações temporais na obtenção dos dados da database**.

## Sprint Planning:

Implementar os **User Cases de 19 e 25**, que não foram terminados no spring anterior, e os **User Cases de 29 a 32**, com a utilização de JavaFX para a interface e de JDBC para o armazenamento dos dados em database.

![Spring Planning](https://user-images.githubusercontent.com/109107004/216834101-d5b06a1c-29c2-4127-aec4-f757fdd35b7a.PNG)

## Done User Stories:

Foram implementados todos os **User Cases planeados (19, 25 e 29 a 32)**. Além disso, também foram **corrigidos diversos "bugs"** e consideradas várias situações específicas como referido no Sprint Goal. Por fim, foram também realizadas **otimizações nas queries da database** que permitiram ter um aumento de velocidade de aproximadamente 10 vezes, nos pedidos à database.

![Done User Stories](https://user-images.githubusercontent.com/109107004/216834105-b94d3c41-5b62-4e67-90c7-d3ddcad27a6e.PNG)

## Sprint Retrospective:

A **implementação dos User Cases** decorreu como esperado, uma vez que a equipa já tem um bom conhecimento das bibliotecas utilizadas (JavaFX e JBDC). Por outro lado, a **correção de "bugs"** e deteção/correção de situações específicas de funcionamento da aplicação demorou um pouco mais do que o esperado, uma vez que, ao contrário do que pensávamos, essas situações são numerosas. Por fim, a **otimização dos pedidos à database** foi também demorosa, uma vez que foi necessário explorar os comandos de database, de forma mais precisa, para operar com arrays. Contudo, assim como referido acima, obtivemos um resultado satisfatório (10x mais rápido).

## Sprint Review:

A aplicação encontra-se neste momento concluída, uma vez que foram realizados todos os User Cases inicialmente definidos. Desta forma, o utilizador tem a possibilidade de, através de uma interface gráfica (GUI):

- Na parte offline da aplicação (sem a criação de conta e utilização de internet):
  1. Criar, editar e eliminar listas pessoais
  2. Criar, editar e eliminar tarefas pessoais
  3. Ordenar as tarefas nas listas por nome, data de criação, prazo e estado (completa ou não)
  4. Pesquisar tarefas específicas por nome, data de criação e prazo
  5. Guardar os dados em ficheiros locais de modo a ser recuperados numa próxima execução do programa
- Na parte online da aplicação (com a utilização de conta e internet/ligação à rede da FEUP):
   1. Criar, editar e eliminar contas
   2. Criar, editar e eliminar listas pessoais
   3. Criar, editar e eliminar tarefas pessoais
   4. Criar, editar e eliminar grupos
   5. Criar, editar e eliminar tarefas de grupo
   6. Adicionar e remover membros dos seus grupos
   7. Atribuir tarefas aos membros do grupo
   8. Aceitar e recusar pedidos de acesso ao grupo, através de notificações
   9. Enviar, aceitar e Recusar pedidos de ajuda em tarefas de outros membros do grupo, através de notificações
  10. Ordenar as tarefas nas listas/grupos por nome, data de criação, prazo e estado (completa ou não)
  11. Pesquisar tarefas específicas por nome, data de criação e prazo
  12. Guardar os dados numa database permitindo acedê-los em outro dispositivo
