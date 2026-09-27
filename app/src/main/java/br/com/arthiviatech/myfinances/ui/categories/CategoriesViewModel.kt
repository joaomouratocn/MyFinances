package br.com.arthiviatech.myfinances.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arthiviatech.myfinances.data.repository.CategoryRepository
import br.com.arthiviatech.myfinances.data.room.entitys.CategoryEntity
import br.com.arthiviatech.myfinances.core.AppClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CategoryFilter { ENABLED, DISABLED }

data class CategoryUi(
    val id: Long,
    val name: String,
    val systemDefined: Boolean,
    val enabled: Boolean,
)

data class CategoryEditorUi(
    val categoryId: Long? = null,
    val name: String = "",
    val errorMessage: String? = null,
)

data class CategoriesUiState(
    val categories: List<CategoryUi> = emptyList(),
    val filter: CategoryFilter = CategoryFilter.ENABLED,
    val editor: CategoryEditorUi? = null,
    val pendingStatusChange: CategoryUi? = null,
    val message: String? = null,
)

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository,
    private val clock: AppClock,
) : ViewModel() {
    private val controls = MutableStateFlow(CategoriesUiState())

    val uiState = combine(categoryRepository.observeAll(), controls) { categories, state ->
        val filtered = categories
            .filter { category ->
                when (state.filter) {
                    CategoryFilter.ENABLED -> category.enabled
                    CategoryFilter.DISABLED -> !category.enabled
                }
            }
            .map { it.toUi() }
        state.copy(categories = filtered)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CategoriesUiState())

    init {
        viewModelScope.launch {
            categoryRepository.insertAll(
                InitialCategoryNames.map { name ->
                    CategoryEntity(name = name, systemDefined = true)
                },
            )
        }
    }

    fun selectFilter(filter: CategoryFilter) = update { copy(filter = filter, message = null) }
    fun openCreate() = update { copy(editor = CategoryEditorUi(), message = null) }
    fun dismissEditor() = update { copy(editor = null) }
    fun updateEditorName(name: String) = update {
        copy(editor = editor?.copy(name = name, errorMessage = null))
    }

    fun openEdit(category: CategoryUi) {
        if (category.systemDefined) {
            update { copy(message = "Categorias iniciais não podem ser renomeadas.") }
            return
        }
        update { copy(editor = CategoryEditorUi(category.id, category.name), message = null) }
    }

    fun saveEditor() {
        val editor = controls.value.editor ?: return
        val name = editor.name.trim()
        if (name.isBlank()) {
            update { copy(editor = editor.copy(errorMessage = "Informe o nome da categoria.")) }
            return
        }

        viewModelScope.launch {
            val existing: CategoryEntity? = editor.categoryId?.let { id ->
                categoryRepository.findById(id)
            }
            if (existing?.systemDefined == true) {
                update { copy(editor = null, message = "Categorias iniciais não podem ser renomeadas.") }
                return@launch
            }
            runCatching {
                if (existing == null) {
                    categoryRepository.insert(CategoryEntity(name = name))
                } else {
                    categoryRepository.update(existing.copy(name = name))
                }
            }.onSuccess {
                update { copy(editor = null, message = null) }
            }.onFailure {
                update {
                    copy(editor = editor.copy(errorMessage = "Já existe uma categoria com este nome."))
                }
            }
        }
    }

    fun requestStatusChange(category: CategoryUi) = update {
        copy(pendingStatusChange = category, message = null)
    }

    fun dismissStatusChange() = update { copy(pendingStatusChange = null) }

    fun confirmStatusChange() {
        val category = controls.value.pendingStatusChange ?: return
        viewModelScope.launch {
            val changed = if (category.enabled) {
                categoryRepository.disable(category.id, clock.nowMillis())
            } else {
                categoryRepository.enable(category.id)
            }
            update {
                copy(
                    pendingStatusChange = null,
                    message = if (changed) null else "Não foi possível alterar a categoria.",
                )
            }
        }
    }

    fun clearMessage() = update { copy(message = null) }

    private fun update(transform: CategoriesUiState.() -> CategoriesUiState) {
        controls.update(transform)
    }
}

private fun CategoryEntity.toUi() = CategoryUi(id, name, systemDefined, enabled)

private val InitialCategoryNames = listOf(
    "Assinaturas e Serviços",
    "Alimentação",
    "Moradia",
    "Diversos",
    "Compras",
    "Pet",
)
