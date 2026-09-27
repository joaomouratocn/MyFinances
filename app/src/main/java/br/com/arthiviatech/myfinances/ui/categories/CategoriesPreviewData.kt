package br.com.arthiviatech.myfinances.ui.categories

internal fun previewCategoriesUiState() = CategoriesUiState(
    categories = previewCategories(),
)

internal fun previewCategories() = listOf(
    CategoryUi(1, "Assinaturas e Serviços", systemDefined = true, enabled = true),
    CategoryUi(2, "Alimentação", systemDefined = true, enabled = true),
    CategoryUi(3, "Moradia", systemDefined = true, enabled = true),
    CategoryUi(4, "Diversos", systemDefined = true, enabled = true),
    CategoryUi(5, "Compras", systemDefined = true, enabled = true),
    CategoryUi(6, "Pet", systemDefined = true, enabled = true),
    CategoryUi(7, "Transporte", systemDefined = false, enabled = true),
)
