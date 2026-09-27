package br.com.arthiviatech.myfinances.ui.expense

internal fun previewNewExpenseUiState() = NewExpenseUiState(
    description = "Supermercado",
    amount = "450,00",
    categoryId = 2,
    purchaseDate = "22/09/2026",
    dueDate = "05/10/2026",
    paymentMethod = ExpensePaymentMethod.PIX,
    categories = listOf(
        ExpenseOptionUi(1, "Assinaturas e Serviços"),
        ExpenseOptionUi(2, "Alimentação"),
        ExpenseOptionUi(3, "Moradia"),
        ExpenseOptionUi(4, "Diversos"),
        ExpenseOptionUi(5, "Compras"),
        ExpenseOptionUi(6, "Pet"),
    ),
    cards = listOf(
        ExpenseOptionUi(1, "Cartão A", dueDay = 10, closingDay = 3),
        ExpenseOptionUi(2, "Cartão B", dueDay = 22),
    ),
)
