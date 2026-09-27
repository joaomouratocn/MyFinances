# Minhas Finanças — Requisitos da V1

## 1. Objetivo do documento

Este documento consolida os requisitos da primeira versão de um aplicativo Android para organização financeira pessoal. Ele descreve o comportamento esperado do produto, sem definir arquitetura ou iniciar sua implementação.

## 2. Visão do produto

O Minhas Finanças ajudará qualquer pessoa a organizar receitas e despesas mensais de forma simples no dia a dia. O aplicativo permitirá acompanhar gastos eventuais, contas recorrentes, compras parceladas e despesas feitas em cartões, além de apresentar o saldo previsto de cada mês.

## 3. Público-alvo

Pessoas que desejam controlar as próprias finanças e visualizar quanto recebem, quanto gastam e quais despesas estão pendentes ou atrasadas.

## 4. Escopo da V1

A V1 contemplará:

- um único perfil financeiro por instalação;
- valores exclusivamente em real brasileiro (BRL);
- período financeiro correspondente ao mês civil, do primeiro ao último dia;
- receitas fixas e eventuais;
- despesas eventuais, recorrentes variáveis e parceladas;
- despesas associadas a cartões;
- categorias predefinidas e personalizadas;
- acompanhamento individual de despesas;
- relatórios mensais por categoria e por cartão;
- armazenamento local no dispositivo;
- proteção pelo bloqueio configurado no aparelho.

## 5. Conceitos e classificações

### 5.1 Tipo de despesa

- **Eventual:** ocorre uma vez e não gera lançamentos futuros automaticamente.
- **Recorrente variável:** repete-se, mas seu valor deve ser informado ou confirmado em cada mês.
- **Parcelada:** possui quantidade e valor de parcelas definidos no cadastro e gera lançamentos futuros automaticamente.

### 5.2 Forma de pagamento

O cartão é uma forma de pagamento, e não um tipo de despesa. Uma despesa recorrente, eventual ou parcelada poderá estar associada a um cartão.

### 5.3 Categoria

A categoria representa a finalidade do gasto, como Moradia, Alimentação, Compras ou Pet. Ela é independente do tipo da despesa e da forma de pagamento.

## 6. Requisitos funcionais

### RF-01 — Receitas fixas

O usuário poderá cadastrar uma receita fixa com, no mínimo:

- descrição;
- valor padrão;
- categoria, quando aplicável;
- dia previsto para recebimento.

A receita deverá aparecer automaticamente nos meses seguintes. O usuário poderá alterar o valor recebido em um mês específico sem alterar o valor padrão nem os demais meses.

Deverá existir uma ação separada para alterar o valor padrão dos lançamentos futuros.

### RF-02 — Receitas eventuais

O usuário poderá cadastrar receitas que ocorram somente uma vez, informando descrição, valor e data.

A V1 não controlará estados de recebimento para receitas. Toda receita lançada participará diretamente do saldo previsto do respectivo mês.

### RF-03 — Despesas eventuais

O usuário poderá cadastrar uma despesa eventual com, no mínimo:

- descrição;
- valor;
- categoria;
- data da compra ou contratação;
- data de vencimento;
- forma de pagamento;
- cartão, quando a forma de pagamento for cartão.

### RF-04 — Despesas parceladas

Ao ativar a opção de compra parcelada, o usuário deverá informar:

- quantidade de parcelas;
- valor de cada parcela;
- data ou mês de vencimento da primeira parcela;
- demais dados comuns de uma despesa.

O valor total poderá ser calculado como `quantidade de parcelas × valor da parcela` e será apresentado somente como informação. O controle financeiro será realizado pelos valores das parcelas. As parcelas deverão ser criadas automaticamente nos meses correspondentes e aparecer nas despesas assim que o usuário consultar esses meses.

Cada parcela será uma despesa controlada individualmente. A edição de uma parcela não alterará as demais.

Ao excluir uma parcela, o aplicativo deverá oferecer duas opções:

- desativar somente a parcela selecionada;
- desativar a parcela selecionada e todas as parcelas futuras do mesmo parcelamento.

### RF-05 — Despesas recorrentes variáveis

O usuário poderá cadastrar modelos para despesas recorrentes cujo valor varia, como aluguel, água e energia elétrica.

No início de cada mês, esses itens deverão aparecer na tela **Contas futuras**, aguardando a atualização ou confirmação do valor. Enquanto o valor do mês não for confirmado:

- o lançamento não deverá aparecer entre as despesas do mês;
- o valor não deverá participar do saldo mensal;
- o aplicativo deverá exibir um lembrete interno sobre os itens pendentes de atualização.

O lembrete deverá sugerir que o usuário atualize o valor ou desative a recorrência caso ela não seja mais necessária. A V1 não enviará notificações pelo Android.

O usuário poderá pausar ou encerrar uma recorrência sem apagar os lançamentos anteriores.

Ao pausar uma recorrência, o aplicativo deverá registrar a data da pausa e deixar de apresentá-la em Contas futuras. Ao reativá-la, ela voltará a ser apresentada a partir do mês atual. Os meses transcorridos durante a pausa não deverão gerar lançamentos retroativos.

Encerrar uma recorrência deverá aplicar a desativação lógica, definindo `enabled = false` e preenchendo `disabledAt`. A reativação de recorrências encerradas ficará fora do escopo da V1.

### RF-06 — Situação das despesas

Cada despesa terá uma das seguintes situações:

- **Pendente:** ainda não foi paga e não passou do vencimento;
- **Paga:** teve o pagamento registrado;
- **Atrasada:** permanece pendente após a data de vencimento.

A situação atrasada deverá ser determinada automaticamente pela data de vencimento. Marcar uma despesa como paga deverá exigir confirmação. Ao confirmar o pagamento, o aplicativo deverá armazenar a data e a hora em que a ação foi realizada.

### RF-07 — Cartões

O usuário poderá cadastrar cartões contendo:

- nome, obrigatório;
- dia de vencimento, obrigatório;
- dia de fechamento, opcional.

A V1 não armazenará o limite do cartão e não controlará uma fatura como entidade separada. Cada despesa do cartão será acompanhada individualmente.

Quando o cartão possuir dia de fechamento, o aplicativo deverá determinar o vencimento da despesa conforme a data da compra:

- compra feita até o fechamento: entra no próximo vencimento;
- compra feita após o fechamento: entra no vencimento subsequente.

Quando o cartão não possuir dia de fechamento, o usuário deverá escolher o mês do primeiro vencimento ao cadastrar a despesa.

No relatório mensal, o aplicativo deverá somar as despesas por cartão, por exemplo: **Cartão A — R$ 1.000,00**.

### RF-08 — Categorias

O aplicativo deverá oferecer categorias iniciais e permitir que o usuário crie, edite e desative categorias personalizadas. Categorias iniciais poderão ser desativadas, mas não renomeadas.

As categorias iniciais da V1 serão:

- Assinaturas e Serviços;
- Alimentação;
- Moradia;
- Diversos;
- Compras;
- Pet.

A desativação de uma categoria não deverá alterar nem remover os lançamentos históricos associados a ela.

### RF-09 — Saldo e resumo mensal

O saldo previsto do mês deverá ser calculado da seguinte forma:

`saldo previsto = total de receitas do mês − total de despesas do mês`

O cálculo incluirá despesas pagas, pendentes e atrasadas lançadas no mês. Contas recorrentes variáveis ainda não confirmadas não participarão do cálculo.

A tela inicial deverá apresentar, para o mês selecionado:

- saldo previsto;
- total de receitas;
- total de despesas;
- despesas pendentes;
- despesas atrasadas;
- lembrete de contas futuras aguardando atualização.

### RF-10 — Navegação mensal

O usuário poderá navegar entre o mês atual, meses anteriores e meses futuros. Parcelas e receitas fixas já programadas deverão aparecer nos respectivos meses.

Os lançamentos futuros deverão seguir estas regras:

- todas as parcelas serão criadas no cadastro do parcelamento;
- a ocorrência de uma receita fixa será criada quando o usuário acessar o respectivo mês pela primeira vez;
- uma recorrência variável somente gerará uma despesa depois que o usuário confirmar seu valor em Contas futuras;
- o cadastro de uma nova receita fixa ou recorrência não criará lançamentos retroativos;
- a desativação impedirá novos lançamentos, preservando os já existentes.

### RF-11 — Pesquisa e filtros

O usuário poderá pesquisar lançamentos e aplicar filtros por:

- período;
- categoria;
- cartão;
- situação da despesa.

Por padrão, as despesas deverão ser ordenadas nesta sequência:

1. atrasadas, do vencimento mais antigo para o mais recente;
2. pendentes, do vencimento mais próximo para o mais distante;
3. pagas, do vencimento mais recente para o mais antigo.

As receitas deverão ser ordenadas pela data prevista mais próxima e, em caso de empate, pela descrição em ordem alfabética.

O usuário poderá escolher outras ordenações durante a consulta, incluindo valor, data e descrição.

### RF-12 — Relatórios

A V1 deverá oferecer relatórios mensais com:

- comparação entre receitas e despesas;
- despesas agrupadas por categoria;
- despesas agrupadas por cartão.

Os relatórios serão exibidos somente no aplicativo.

### RF-13 — Desativação lógica

Os registros de negócio deverão utilizar desativação lógica em vez de exclusão física. Cada registro aplicável deverá possuir:

- indicador de registro ativo (`enabled`);
- data e hora da desativação (`disabledAt`), vazia enquanto o registro estiver ativo.

Ao desativar um registro, o aplicativo deverá definir o indicador como inativo e preencher a data e a hora da desativação.

Essa regra se aplica a receitas, despesas, parcelas, recorrências, cartões e categorias. Registros desativados não deverão aparecer nas operações comuns, mas deverão permanecer armazenados para preservar o histórico e os relacionamentos. Lançamentos históricos continuarão exibindo corretamente cartões e categorias desativados.

### RF-14 — Ajuste de datas ao calendário

Quando um vencimento ou recebimento estiver configurado para um dia inexistente em determinado mês, o aplicativo deverá usar o último dia disponível naquele mês.

Por exemplo, uma recorrência configurada para o dia 31 vencerá em 30 de abril e em 28 ou 29 de fevereiro. Nos meses seguintes, o aplicativo deverá voltar a usar o dia originalmente configurado sempre que ele existir.

Essa regra deverá ser aplicada a receitas, despesas, parcelas, recorrências e vencimentos de cartões.

## 7. Telas previstas

### 7.1 Acesso

Solicita a autenticação disponibilizada pelo bloqueio do dispositivo, quando houver.

### 7.2 Início

Exibe o resumo financeiro do mês, acesso à navegação mensal, despesas pendentes e atrasadas e o lembrete de contas futuras.

### 7.3 Receitas

Lista, cadastra e edita receitas fixas e eventuais.

### 7.4 Despesas

Lista, pesquisa, filtra, cadastra e edita despesas eventuais e parceladas.

### 7.5 Contas futuras

Apresenta recorrências variáveis aguardando confirmação de valor e permite atualizar, pausar ou encerrar uma recorrência.

### 7.6 Cartões

Permite cadastrar e gerenciar cartões e consultar suas despesas agrupadas por mês.

### 7.7 Categorias

Permite consultar categorias iniciais e criar, editar ou desativar categorias personalizadas.

### 7.8 Relatórios

Apresenta os totais mensais e os agrupamentos por categoria e cartão.

## 8. Requisitos não funcionais

### RNF-01 — Plataforma

O produto será um aplicativo Android.

### RNF-02 — Facilidade de uso

As operações frequentes, especialmente cadastro e pagamento de despesas, deverão exigir poucos passos e usar linguagem simples.

### RNF-03 — Armazenamento

Os dados serão armazenados apenas no dispositivo por meio do Room.

### RNF-04 — Autenticação local

Se o dispositivo possuir bloqueio configurado, o aplicativo deverá usar a autenticação oferecida pelo Android, como biometria, PIN, padrão ou senha. Se não houver bloqueio configurado, o acesso será direto.

Ao retornar do segundo plano, uma nova autenticação deverá ser solicitada depois de cinco minutos de ausência.

### RNF-05 — Localização

A V1 usará real brasileiro, formato brasileiro de valores e formato brasileiro de datas.

### RNF-06 — Integridade histórica

Alterações e desativações não deverão quebrar a visualização de lançamentos históricos nem seus vínculos com categorias, cartões, recorrências ou parcelamentos.

## 9. Critérios gerais de aceite

1. O usuário consegue cadastrar uma receita fixa e visualizá-la nos meses seguintes.
2. A alteração do valor de uma receita fixa em um mês não modifica os demais meses.
3. O usuário consegue cadastrar uma receita eventual em uma única data.
4. O usuário consegue cadastrar uma despesa parcelada e encontrar cada parcela no mês correto.
5. A edição de uma parcela não altera as outras parcelas.
6. Uma despesa pendente passa a atrasada depois do vencimento.
7. O pagamento de uma despesa registra a data e a hora da ação.
8. Uma recorrência variável não confirmada permanece em Contas futuras e não afeta o saldo.
9. O lembrete interno informa quando existem recorrências aguardando atualização.
10. O saldo mensal corresponde às receitas menos as despesas lançadas no mês.
11. Os relatórios agrupam corretamente as despesas por categoria e cartão.
12. Um registro desativado deixa de aparecer nas operações normais sem desaparecer do histórico.
13. O aplicativo solicita o bloqueio do dispositivo após permanecer cinco minutos em segundo plano, quando esse bloqueio estiver configurado.
14. Um lançamento configurado para um dia inexistente no mês usa o último dia desse mês e volta ao dia original no mês seguinte, quando possível.
15. Uma recorrência pausada não gera pendências durante a pausa nem cria lançamentos retroativos ao ser reativada.
16. Parcelas são criadas no cadastro; receitas fixas são materializadas ao acessar o mês; e recorrências variáveis somente são lançadas após a confirmação do valor.

## 10. Fora do escopo da V1

- cadastro de usuário, e-mail ou senha próprios do aplicativo;
- múltiplos perfis;
- sincronização entre dispositivos;
- armazenamento em nuvem;
- backup e restauração;
- exportação para PDF, planilha ou CSV;
- notificações do Android;
- controle e pagamento de faturas de cartão;
- armazenamento ou acompanhamento do limite do cartão;
- pagamentos parciais, juros ou multas;
- suporte a outras moedas;
- integração com bancos ou instituições financeiras.

## 11. Decisões de produto concluídas

As regras de calendário, categorias iniciais, pausa e reativação de recorrências, ordenação e criação dos lançamentos futuros estão definidas para a V1.
