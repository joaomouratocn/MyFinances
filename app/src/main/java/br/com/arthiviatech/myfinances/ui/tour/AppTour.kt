package br.com.arthiviatech.myfinances.ui.tour

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.navigation.MainTab
import kotlin.math.roundToInt

private data class TourStep(val target: String, val title: String, val message: String)

private val tourSteps = mapOf(
    MainTab.HOME to listOf(
        TourStep("home_month", "Escolha o mês", "Use as setas para consultar outros meses."),
        TourStep("home_balance", "Resumo financeiro", "Veja receitas, despesas e saldo previsto do mês."),
        TourStep("home_new_expense", "Nova despesa", "Toque aqui para registrar um gasto."),
    ),
    MainTab.FUTURE to listOf(
        TourStep("future_month", "Contas futuras", "Escolha o mês que deseja planejar."),
        TourStep("future_accounts", "Contas previstas", "Consulte as contas e atualize os valores quando necessário."),
    ),
    MainTab.REPORTS to listOf(
        TourStep("reports_month", "Mês do relatório", "Escolha o período que deseja analisar."),
        TourStep("reports_summary", "Resumo do mês", "Compare receitas, despesas e saldo previsto."),
    ),
    MainTab.MORE to listOf(
        TourStep("more_management", "Cadastros", "Acesse receitas, cartões, faturas e categorias."),
        TourStep("more_security", "Segurança", "Configure o bloqueio do aplicativo aqui."),
    ),
)

private val LocalTourTargets = staticCompositionLocalOf<MutableMap<String, Rect>> {
    mutableMapOf()
}
private val LocalActiveTourTarget = staticCompositionLocalOf<String?> { null }

@Composable
fun Modifier.tourTarget(key: String): Modifier {
    val targets = LocalTourTargets.current
    val activeTarget = LocalActiveTourTarget.current
    val requester = remember { BringIntoViewRequester() }
    LaunchedEffect(activeTarget) {
        if (activeTarget == key) requester.bringIntoView()
    }
    return bringIntoViewRequester(requester)
        .onGloballyPositioned { coordinates -> targets[key] = coordinates.boundsInWindow() }
}

@Composable
fun TourHost(activeTab: MainTab?, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val preferences = remember(context) { context.getSharedPreferences("app_tours", Context.MODE_PRIVATE) }
    val completed = remember(preferences) {
        mutableStateMapOf<MainTab, Boolean>().apply {
            MainTab.entries.forEach { tab -> put(tab, preferences.getBoolean("seen_${tab.name}", false)) }
        }
    }
    val targets = remember { mutableStateMapOf<String, Rect>() }
    var stepIndex by remember(activeTab) { mutableIntStateOf(0) }
    var hostBounds by remember { mutableStateOf<Rect?>(null) }

    val steps = activeTab?.let(tourSteps::get).orEmpty()
    val visible = activeTab != null && completed[activeTab] != true && steps.isNotEmpty()
    fun finish() {
        activeTab?.let { tab ->
            completed[tab] = true
            preferences.edit().putBoolean("seen_${tab.name}", true).apply()
        }
    }

    BackHandler(enabled = visible, onBack = ::finish)
    val activeTarget = if (visible) steps[stepIndex.coerceIn(0, steps.lastIndex)].target else null
    CompositionLocalProvider(
        LocalTourTargets provides targets,
        LocalActiveTourTarget provides activeTarget,
    ) {
        Box(Modifier.fillMaxSize().onGloballyPositioned { hostBounds = it.boundsInWindow() }) {
            content()
            if (visible) {
                val step = steps[stepIndex.coerceIn(0, steps.lastIndex)]
                val target = targets[step.target]
                val bounds = hostBounds
                if (target != null && bounds != null) {
                    TourOverlay(
                        step = step,
                        stepNumber = stepIndex + 1,
                        stepCount = steps.size,
                        target = target,
                        host = bounds,
                        onSkip = ::finish,
                        onNext = {
                            if (stepIndex == steps.lastIndex) finish() else stepIndex++
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun TourOverlay(
    step: TourStep,
    stepNumber: Int,
    stepCount: Int,
    target: Rect,
    host: Rect,
    onSkip: () -> Unit,
    onNext: () -> Unit,
) {
    val density = LocalDensity.current
    var measuredBubbleHeight by remember { mutableIntStateOf(0) }
    BoxWithConstraints(
        Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures { } }
            .semantics { paneTitle = "Apresentação do aplicativo" },
    ) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        val gapPx = with(density) { 12.dp.toPx() }
        val marginPx = with(density) { 16.dp.toPx() }
        val bubbleWidth = minOf(maxWidth - 32.dp, 320.dp)
        val bubbleWidthPx = with(density) { bubbleWidth.toPx() }
        val bubbleHeightPx = measuredBubbleHeight.takeIf { it > 0 }?.toFloat()
            ?: with(density) { 190.dp.toPx() }
        val left = (target.left - host.left).coerceIn(0f, widthPx)
        val top = (target.top - host.top).coerceIn(0f, heightPx)
        val right = (target.right - host.left).coerceIn(left, widthPx)
        val bottom = (target.bottom - host.top).coerceIn(top, heightPx)
        val bubbleX = (left + (right - left - bubbleWidthPx) / 2f)
            .coerceIn(marginPx, (widthPx - bubbleWidthPx - marginPx).coerceAtLeast(marginPx))
        val bubbleY = if (heightPx - bottom >= bubbleHeightPx + gapPx + marginPx) {
            bottom + gapPx
        } else {
            (top - bubbleHeightPx - gapPx).coerceAtLeast(marginPx)
        }

        Canvas(
            Modifier.fillMaxSize().graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            },
        ) {
            drawRect(Color.Black.copy(alpha = 0.76f))
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                cornerRadius = CornerRadius(12.dp.toPx()),
                blendMode = BlendMode.Clear,
            )
        }
        Card(
            modifier = Modifier.offset { IntOffset(bubbleX.roundToInt(), bubbleY.roundToInt()) }
                .width(bubbleWidth).heightIn(min = 140.dp)
                .onSizeChanged { measuredBubbleHeight = it.height },
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("$stepNumber de $stepCount", style = MaterialTheme.typography.labelSmall)
                Text(step.title, style = MaterialTheme.typography.titleMedium)
                Text(step.message, style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onSkip) { Text("Pular") }
                    TextButton(onClick = onNext) { Text(if (stepNumber == stepCount) "Concluir" else "Próximo") }
                }
            }
        }
    }
}
