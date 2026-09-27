# Minhas Finanças — Fluxos do Usuário da V1

## 1. Objetivo

Este documento descreve como o usuário realizará as principais tarefas da V1. Os fluxos traduzem os requisitos do produto em sequências de interação e servirão de base para os wireframes.

## 2. Estrutura de navegação proposta

A navegação principal terá quatro destinos:

- **Início:** resumo e lançamentos do mês selecionado;
- **Contas futuras:** recorrências variáveis aguardando valor;
- **Relatórios:** totais e agrupamentos mensais;
- **Mais:** acesso a receitas, cartões e categorias.

Na tela Início, a ação **Nova despesa** abrirá diretamente o formulário de despesa. O cadastro de receitas ficará em **Mais > Receitas**, separado dessa ação rápida.

## 3. Entrada e retorno ao aplicativo

### Fluxo principal

1. O usuário abre o aplicativo.
2. O aplicativo verifica se o dispositivo possui bloqueio configurado.
3. Se houver, solicita a autenticação oferecida pelo Android.
4. Depois da autenticação, apresenta a tela Início no mês atual.
5. Se não houver bloqueio configurado, apresenta a tela Início diretamente.

### Retorno do segundo plano

1. O aplicativo registra quando deixou de estar ativo.
2. Se o usuário retornar em até cinco minutos, mantém a sessão aberta.
3. Se retornar depois de cinco minutos, solicita novamente a autenticação do dispositivo.
4. Depois da autenticação, retorna à tela que estava aberta.

## 4. Consultar o resumo mensal

1. O usuário acessa a tela Início.
2. O aplicativo seleciona o mês atual por padrão.
3. A tela apresenta receitas, despesas, saldo previsto, pendências e atrasos.
4. Se houver recorrências aguardando valor, apresenta um lembrete com acesso a Contas futuras.
5. O usuário pode avançar ou voltar entre os meses.
6. Ao acessar um mês pela primeira vez, o aplicativo materializa as receitas fixas aplicáveis a ele.
7. O usuário pode abrir um lançamento para consultar seus detalhes ou realizar uma ação.

## 5. Cadastrar uma despesa eventual

1. Na tela Início, o usuário aciona **Nova despesa**.
2. O aplicativo abre diretamente o formulário de despesa.
3. O usuário mantém desativadas as opções **Recorrente** e **Parcelada**.
4. Informa descrição, valor, categoria, data da compra ou contratação, vencimento e forma de pagamento.
5. Se a forma de pagamento for cartão, seleciona o cartão.
6. O aplicativo determina o vencimento pelo fechamento do cartão ou solicita o mês do vencimento quando o cartão não possuir fechamento.
7. O usuário salva.
8. O aplicativo valida os campos e inclui a despesa no mês do vencimento.
9. A tela apresenta uma confirmação e atualiza o resumo mensal.

### Validações

- descrição, valor maior que zero, categoria e vencimento são obrigatórios;
- um cartão deve ser selecionado quando essa for a forma de pagamento;
- o cartão deve estar ativo;
- datas configuradas para dias inexistentes usam o último dia do mês.

## 6. Cadastrar uma compra parcelada

1. O usuário inicia um novo lançamento do tipo Despesa.
2. Ativa a opção **Parcelada**.
3. Informa os dados comuns da despesa.
4. Informa a quantidade de parcelas e o valor de cada parcela.
5. Seleciona o cartão, quando aplicável.
6. O aplicativo calcula o primeiro vencimento conforme as regras do cartão. Sem dia de fechamento, o usuário escolhe o mês do primeiro vencimento.
7. O aplicativo apresenta um resumo com quantidade, valor de cada parcela, valor total calculado e primeiro vencimento.
8. O usuário confirma o cadastro.
9. O aplicativo cria todas as parcelas e apresenta a primeira no mês correspondente.

O valor total calculado será apenas informativo. O controle financeiro será realizado pelos valores das parcelas.

### Validações

- a quantidade de parcelas deve ser maior que zero;
- o valor da parcela deve ser maior que zero;
- cada parcela deve exibir sua posição, como `2/10`;
- dias inexistentes são ajustados para o último dia do mês sem alterar o dia original da série.

## 7. Editar ou desativar uma parcela

### Editar

1. O usuário abre a parcela.
2. Seleciona **Editar**.
3. Altera os dados desejados.
4. O aplicativo informa que a alteração afetará somente aquela parcela.
5. O usuário salva e o resumo do mês é atualizado.

### Desativar

1. O usuário abre a parcela e seleciona **Excluir**.
2. O aplicativo oferece **Somente esta parcela** ou **Esta e as futuras**.
3. O usuário escolhe o alcance e confirma.
4. O aplicativo aplica a desativação lógica aos registros selecionados.
5. Parcelas anteriores e demais dados históricos permanecem disponíveis no histórico.

## 8. Cadastrar e atualizar uma despesa recorrente variável

### Cadastrar a recorrência

1. O usuário inicia um novo lançamento do tipo Despesa.
2. Ativa a opção **Recorrente**.
3. Informa descrição, categoria, dia de vencimento, forma de pagamento e cartão, quando aplicável.
4. Salva a recorrência.
5. O modelo passa a ser apresentado em Contas futuras nos meses aplicáveis.

### Confirmar o valor do mês

1. O aplicativo mostra na tela Início que existem contas aguardando atualização.
2. O usuário abre **Contas futuras**.
3. Seleciona uma recorrência pendente.
4. Informa ou confirma o valor e o vencimento daquele mês.
5. Confirma o lançamento.
6. O aplicativo cria a despesa mensal e remove o item da lista de pendências daquele mês.
7. A despesa passa a integrar a lista e o saldo do mês.

### Pausar, reativar ou encerrar

1. O usuário abre o modelo da recorrência.
2. Escolhe pausar ou encerrar.
3. Pausar registra a data da pausa e interrompe novas pendências.
4. Uma recorrência pausada pode ser reativada a partir do mês atual, sem gerar meses retroativos.
5. Encerrar aplica a desativação lógica e preserva os lançamentos anteriores.

## 9. Cadastrar e ajustar receitas

### Receita fixa

1. O usuário acessa **Mais > Receitas**.
2. Seleciona **Nova receita** e, em seguida, **Fixa**.
3. Informa descrição, valor padrão e dia previsto de recebimento.
4. Salva.
5. A receita passa a aparecer automaticamente nos meses aplicáveis.

### Alterar somente um mês

1. O usuário abre a ocorrência mensal da receita.
2. Seleciona **Editar este mês**.
3. Altera o valor ou a data.
4. Salva sem modificar o padrão nem outros meses.

### Alterar os meses futuros

1. O usuário abre a ocorrência ou o cadastro da receita fixa.
2. Seleciona **Editar padrão futuro**.
3. Altera o valor padrão ou o dia previsto.
4. Confirma que a mudança valerá a partir do mês escolhido.
5. Ocorrências anteriores permanecem inalteradas.

### Receita eventual

1. O usuário acessa **Mais > Receitas**.
2. Seleciona **Nova receita** e, em seguida, **Eventual**.
3. Informa descrição, valor e data.
4. Salva e visualiza a receita no respectivo mês.

## 10. Registrar o pagamento de uma despesa

1. O usuário abre uma despesa pendente ou atrasada.
2. Seleciona **Marcar como paga**.
3. O aplicativo solicita a confirmação do pagamento.
4. O usuário confirma a ação.
5. O aplicativo altera a situação para Paga e registra a data e a hora da ação.
6. A despesa permanece no mês de seu vencimento e passa para o grupo de despesas pagas.

## 11. Gerenciar cartões

### Cadastrar

1. O usuário acessa **Mais > Cartões**.
2. Seleciona **Novo cartão**.
3. Informa o nome e o dia de vencimento.
4. Opcionalmente, informa o dia de fechamento.
5. Salva o cartão.

### Editar ou desativar

1. O usuário abre um cartão.
2. Edita seus dados ou escolhe desativá-lo.
3. A desativação impede novas associações.
4. Despesas históricas mantêm a identificação do cartão.

## 12. Gerenciar categorias

1. O usuário acessa **Mais > Categorias**.
2. Visualiza as categorias iniciais e personalizadas.
3. Pode criar uma categoria informando seu nome.
4. Pode editar ou desativar uma categoria personalizada.
5. Pode desativar uma categoria inicial, mas não pode renomeá-la.
6. Categorias desativadas deixam de aparecer em novos lançamentos.
7. Lançamentos históricos preservam a categoria original.

## 13. Pesquisar e filtrar lançamentos

1. O usuário abre a lista de lançamentos.
2. Pode pesquisar pela descrição.
3. Pode filtrar por período, categoria, cartão e situação.
4. Pode ordenar por data, valor ou descrição.
5. O aplicativo atualiza a lista e mantém visíveis os filtros aplicados.
6. O usuário pode limpar os filtros e retornar à ordenação padrão.

## 14. Consultar relatórios

1. O usuário acessa Relatórios.
2. O aplicativo usa o mês selecionado na navegação principal.
3. Apresenta receitas, despesas e saldo previsto.
4. Apresenta as despesas agrupadas por categoria.
5. Apresenta as despesas agrupadas por cartão.
6. O usuário pode navegar para outro mês.
7. O aplicativo recalcula e atualiza os agrupamentos.

## 15. Estados vazios e erros recuperáveis

- Sem lançamentos no mês, a tela Início deverá explicar como criar o primeiro lançamento.
- Sem cartões cadastrados, a seleção de cartão deverá oferecer acesso ao cadastro de um novo cartão.
- Sem recorrências pendentes, Contas futuras deverá informar que não há valores aguardando atualização.
- Um formulário inválido deverá indicar o campo que precisa ser corrigido sem perder os demais dados digitados.
- Se a autenticação for cancelada, os dados financeiros não deverão ser exibidos.

## 16. Decisões para os wireframes

- Início, Contas futuras, Relatórios e Mais serão os destinos principais.
- A ação rápida da tela Início abrirá diretamente o cadastro de despesa.
- Receitas serão cadastradas e gerenciadas por Mais > Receitas.
- Marcar uma despesa como paga exigirá confirmação.
- O valor total de uma compra parcelada será apenas informativo.
- Categorias iniciais poderão ser desativadas, mas não renomeadas.
