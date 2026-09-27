# Minhas Finanças — Paleta e Diretrizes Visuais da V1

**Status:** proposta para validação.

**Logo aprovada:** [logo_app.png](logo_app.png).

## 1. Direção visual

A identidade combina azul e laranja:

- **Azul:** ações principais, navegação selecionada, foco e dados de receitas;
- **Laranja:** ações secundárias, destaques e estados pendentes;
- **Verde:** pagamentos concluídos e resultados positivos;
- **Vermelho:** atrasos e erros.

Os temas claro e escuro usam os mesmos papéis semânticos com tons diferentes. O aplicativo seguirá o tema do dispositivo por padrão.

## 2. Tema claro

| Papel | Cor | Uso principal |
|---|---|---|
| Primary | `#005AC1` | Botões principais, seleção e links |
| On Primary | `#FFFFFF` | Conteúdo sobre Primary |
| Primary Container | `#D8E2FF` | Cartões e destaques azuis suaves |
| On Primary Container | `#001A41` | Conteúdo sobre Primary Container |
| Secondary | `#9C4400` | Ações secundárias e destaque laranja |
| On Secondary | `#FFFFFF` | Conteúdo sobre Secondary |
| Secondary Container | `#FFDBC8` | Avisos e destaques laranja suaves |
| On Secondary Container | `#341100` | Conteúdo sobre Secondary Container |
| Background | `#FAF8FF` | Fundo geral |
| On Background | `#1A1B20` | Texto principal |
| Surface | `#FFFFFF` | Cartões, diálogos e campos |
| On Surface | `#1A1B20` | Conteúdo sobre superfícies |
| Surface Variant | `#E1E2EC` | Áreas e controles discretos |
| On Surface Variant | `#44474F` | Texto secundário |
| Outline | `#74777F` | Bordas e divisores |
| Success | `#146C2E` | Pago e saldo positivo |
| On Success | `#FFFFFF` | Conteúdo sobre Success |
| Error | `#BA1A1A` | Atrasado, erro e ação destrutiva |
| On Error | `#FFFFFF` | Conteúdo sobre Error |

## 3. Tema escuro

| Papel | Cor | Uso principal |
|---|---|---|
| Primary | `#ADC7FF` | Botões principais, seleção e links |
| On Primary | `#002F67` | Conteúdo sobre Primary |
| Primary Container | `#00458F` | Cartões e destaques azuis |
| On Primary Container | `#D8E2FF` | Conteúdo sobre Primary Container |
| Secondary | `#FFB68F` | Ações secundárias e destaque laranja |
| On Secondary | `#552100` | Conteúdo sobre Secondary |
| Secondary Container | `#783200` | Avisos e destaques laranja |
| On Secondary Container | `#FFDBC8` | Conteúdo sobre Secondary Container |
| Background | `#111318` | Fundo geral |
| On Background | `#E2E2E9` | Texto principal |
| Surface | `#191C20` | Cartões, diálogos e campos |
| On Surface | `#E2E2E9` | Conteúdo sobre superfícies |
| Surface Variant | `#44474F` | Áreas e controles discretos |
| On Surface Variant | `#C4C6D0` | Texto secundário |
| Outline | `#8E9099` | Bordas e divisores |
| Success | `#7DDA95` | Pago e saldo positivo |
| On Success | `#003916` | Conteúdo sobre Success |
| Error | `#FFB4AB` | Atrasado, erro e ação destrutiva |
| On Error | `#690005` | Conteúdo sobre Error |

## 4. Cores por significado financeiro

| Informação | Tema claro | Tema escuro |
|---|---|---|
| Receita | `#005AC1` | `#ADC7FF` |
| Despesa | `#9C4400` | `#FFB68F` |
| Paga | `#146C2E` | `#7DDA95` |
| Pendente | `#9C4400` | `#FFB68F` |
| Atrasada | `#BA1A1A` | `#FFB4AB` |
| Saldo positivo | `#146C2E` | `#7DDA95` |
| Saldo negativo | `#BA1A1A` | `#FFB4AB` |

A cor nunca será o único indicador de situação. Rótulos e ícones acompanharão Pago, Pendente e Atrasado.

## 5. Gráficos

Sequência recomendada para categorias e séries:

| Ordem | Tema claro | Tema escuro |
|---:|---|---|
| 1 | `#005AC1` | `#ADC7FF` |
| 2 | `#9C4400` | `#FFB68F` |
| 3 | `#006B5F` | `#7AD9C9` |
| 4 | `#76558F` | `#DDB7F5` |
| 5 | `#5C5F67` | `#C4C6D0` |
| 6 | `#8A5100` | `#FFC56E` |

Gráficos devem usar também legenda, texto ou padrão visual, pois diferenças de cor isoladas não são suficientes para acessibilidade.

## 6. Aplicação nas telas

- **Início:** saldo em Primary quando neutro, Success quando positivo e Error quando negativo.
- **Nova despesa:** botão Salvar em Primary; opção de parcelamento selecionada em Primary Container.
- **Contas futuras:** lembrete em Secondary Container; ação Informar valor em Secondary.
- **Relatórios:** receitas em azul e despesas em laranja; categorias usam a sequência de gráficos.
- **Pagamento:** ação de confirmação em Success; cancelar usa botão textual neutro.
- **Exclusão lógica:** confirmação e indicação visual em Error.

## 7. Orientação e adaptação

- Em retrato, telas usam uma coluna principal.
- Em paisagem compacta, resumo e lista podem ocupar duas colunas.
- Formulários em paisagem podem dividir campos relacionados em duas colunas, mantendo a ordem de leitura.
- A barra inferior permanece para janelas compactas; uma barra lateral poderá substituí-la quando a largura disponível justificar.
- Nenhum conteúdo ou ação dependerá exclusivamente de uma orientação.

## 8. Nome do produto

O nome exibido do aplicativo será **Minhas Finanças**.

Antes da publicação, o nome deverá passar por verificação de disponibilidade na Google Play e de marca.

## 9. Logo e ícones do aplicativo

A imagem `logo_app.png` é a referência visual aprovada para o produto. O arquivo original possui transparência e resolução aproximada de 1024 × 1028 pixels. Os ativos normalizados estão documentados em [design/logo/README.md](design/logo/README.md).

Foram derivados os seguintes ativos:

- **foreground adaptativo:** carteira isolada, ampliada e posicionada na área segura;
- **background adaptativo:** cor ou gradiente simples separado do foreground;
- **ícone monocromático:** versão simplificada de uma cor para ícones temáticos do Android;
- **ícone legado:** composição quadrada para aparelhos e launchers sem máscara adaptativa;
- **ícone da loja:** PNG de 512 × 512 pixels para a ficha da Google Play;
- **marca interna:** versão transparente para telas institucionais, quando necessária.

O arquivo original foi normalizado para uma tela quadrada. O símbolo foi ampliado e reposicionado para continuar legível em tamanhos pequenos.

A versão monocromática preserva a silhueta da carteira e simplifica o gráfico circular, o fecho e as cédulas. Detalhes tridimensionais, sombras e gradientes não são usados como única forma de reconhecer o símbolo.

As cédulas foram ajustadas para uma aparência abstrata, sem símbolos nem denominações, evitando associação direta com dólar.

O verde e o azul presentes na logo podem coexistir com a paleta definida. O azul permanece como cor principal da interface e o laranja deve ser usado como destaque funcional, sem necessidade de aparecer obrigatoriamente na logo.

## 10. Artefato visual

A paleta completa está representada em [paleta-v1.svg](paleta-v1.svg).
