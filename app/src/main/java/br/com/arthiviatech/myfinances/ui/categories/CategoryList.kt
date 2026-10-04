package br.com.arthiviatech.myfinances.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
internal fun CategoryList(
    categories: List<CategoryUi>,
    filter: CategoryFilter,
    onEdit: (CategoryUi) -> Unit,
    onStatusChange: (CategoryUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (categories.isEmpty()) {
        CategoryEmptyState(filter = filter, modifier = modifier)
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(categories, key = { it.id }) { category ->
            CategoryCard(category, onEdit, onStatusChange)
        }
    }
}

@Composable
private fun CategoryCard(
    category: CategoryUi,
    onEdit: (CategoryUi) -> Unit,
    onStatusChange: (CategoryUi) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Category,
                contentDescription = null,
                tint = if (category.enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            )
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(category.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                if (category.systemDefined) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(
                            text = "Inicial",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                } else {
                    Text("Personalizada", style = MaterialTheme.typography.bodySmall)
                }
            }
            if (!category.systemDefined && category.enabled) {
                IconButton(onClick = { onEdit(category) }) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Editar ${category.name}")
                }
            }
            IconButton(onClick = { onStatusChange(category) }) {
                Icon(
                    Icons.Rounded.PowerSettingsNew,
                    contentDescription = if (category.enabled) "Desativar ${category.name}" else "Reativar ${category.name}",
                    tint = if (category.enabled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun CategoryEmptyState(filter: CategoryFilter, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Rounded.Category, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(
            text = if (filter == CategoryFilter.ENABLED) "Nenhuma categoria ativa" else "Nenhuma categoria desativada",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Preview(name = "Lista de categorias", group = "Componentes", showBackground = true, widthDp = 390, heightDp = 500)
@Composable
private fun CategoryListPreview() {
    MyFinancesTheme {
        CategoryList(
            categories = previewCategories(),
            filter = CategoryFilter.ENABLED,
            onEdit = {},
            onStatusChange = {},
        )
    }
}

@Preview(name = "Categorias desativadas vazias", group = "Estados", showBackground = true, widthDp = 390, heightDp = 300)
@Composable
private fun CategoryEmptyListPreview() {
    MyFinancesTheme {
        CategoryList(
            categories = emptyList(),
            filter = CategoryFilter.DISABLED,
            onEdit = {},
            onStatusChange = {},
        )
    }
}
