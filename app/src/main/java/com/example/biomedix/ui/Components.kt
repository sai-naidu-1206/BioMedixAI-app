package com.example.biomedix.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.biomedix.common.HubResult
import com.example.biomedix.common.PocketFeatureVector
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Interactive Force-Directed PPI Network Graph Canvas.
 */
@Composable
fun InteractivePpiNetworkGraph(
    hubResult: HubResult,
    modifier: Modifier = Modifier,
    onNodeSelected: (String) -> Unit = {}
) {
    val textMeasurer = rememberTextMeasurer()
    val nodes = remember(hubResult) { hubResult.candidateGenes.map { it.symbol } }
    val edges = remember(hubResult) { hubResult.ppiEdges }
    val hubSymbol = hubResult.hubGeneSymbol

    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    // Dynamic rotation animation for network nodes
    val transition = rememberInfiniteTransition(label = "ppi_pulse")
    val pulse by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .background(Color(0xFF0F172A), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    dragOffset += dragAmount
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f + dragOffset.x, height / 2f + dragOffset.y)

            // Compute positions
            val nodePositions = mutableMapOf<String, Offset>()
            // Hub gene in center
            nodePositions[hubSymbol] = center

            // Other genes arranged in concentric circles
            val otherNodes = nodes.filter { it != hubSymbol }
            val radius1 = minOf(width, height) * 0.32f
            val radius2 = minOf(width, height) * 0.42f

            otherNodes.forEachIndexed { i, node ->
                val r = if (i % 2 == 0) radius1 else radius2
                val angle = (i.toFloat() / otherNodes.size.toFloat()) * 2f * Math.PI.toFloat()
                val x = center.x + r * cos(angle)
                val y = center.y + r * sin(angle)
                nodePositions[node] = Offset(x, y)
            }

            // Draw edges
            edges.forEach { edge ->
                val p1 = nodePositions[edge.source]
                val p2 = nodePositions[edge.target]
                if (p1 != null && p2 != null) {
                    val isHubEdge = edge.source == hubSymbol || edge.target == hubSymbol
                    val edgeAlpha = (edge.combinedScore / 1000f).coerceIn(0.2f, 0.9f)
                    val strokeColor = if (isHubEdge) {
                        Color(0xFF38BDF8).copy(alpha = edgeAlpha)
                    } else {
                        Color(0xFF64748B).copy(alpha = edgeAlpha * 0.5f)
                    }
                    val strokeWidth = if (isHubEdge) 3.5f else 1.8f

                    drawLine(
                        color = strokeColor,
                        start = p1,
                        end = p2,
                        strokeWidth = strokeWidth
                    )
                }
            }

            // Draw nodes
            nodePositions.forEach { (symbol, pos) ->
                val isHub = symbol == hubSymbol
                val centrality = hubResult.centralityScores[symbol] ?: 0.5f
                val baseRadius = if (isHub) 28f * pulse else 16f + (centrality * 10f)

                // Outer halo for hub
                if (isHub) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.5f), Color.Transparent),
                            center = pos,
                            radius = baseRadius * 2.2f
                        ),
                        radius = baseRadius * 2.2f,
                        center = pos
                    )
                }

                val nodeBrush = if (isHub) {
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
                        center = pos,
                        radius = baseRadius
                    )
                } else {
                    val c = if (centrality > 0.5f) Color(0xFF10B981) else Color(0xFF6366F1)
                    Brush.radialGradient(
                        colors = listOf(c, c.copy(alpha = 0.7f)),
                        center = pos,
                        radius = baseRadius
                    )
                }

                drawCircle(
                    brush = nodeBrush,
                    radius = baseRadius,
                    center = pos
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = baseRadius,
                    center = pos,
                    style = Stroke(width = if (isHub) 2.5f else 1.5f)
                )

                // Label
                val textLayout = textMeasurer.measure(
                    text = symbol,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = if (isHub) 12.sp else 9.sp,
                        fontWeight = if (isHub) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = symbol,
                    topLeft = Offset(
                        pos.x - textLayout.size.width / 2f,
                        pos.y + baseRadius + 4f
                    ),
                    style = TextStyle(
                        color = if (isHub) Color(0xFF38BDF8) else Color(0xFFE2E8F0),
                        fontSize = if (isHub) 11.sp else 9.sp,
                        fontWeight = if (isHub) FontWeight.Bold else FontWeight.Normal
                    ),
                    size = textLayout.size.toSize()
                )
            }
        }
    }
}

/**
 * 3D Protein Structure & Binding Pocket Viewer Canvas.
 */
@Composable
fun Protein3DStructureViewer(
    geneSymbol: String,
    pdbId: String,
    pocketFeatures: PocketFeatureVector?,
    viewMode: String,
    modifier: Modifier = Modifier
) {
    var rotX by remember { mutableFloatStateOf(20f) }
    var rotY by remember { mutableFloatStateOf(35f) }
    var zoom by remember { mutableFloatStateOf(1.0f) }

    val transition = rememberInfiniteTransition(label = "rot_spin")
    val autoSpin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Box(
        modifier = modifier
            .background(Color(0xFF090D16), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    rotY += dragAmount.x * 0.5f
                    rotX -= dragAmount.y * 0.5f
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height / 2f)

            val effectiveRotY = Math.toRadians((rotY + autoSpin).toDouble()).toFloat()
            val effectiveRotX = Math.toRadians(rotX.toDouble()).toFloat()

            // 3D coordinate transform helper
            fun project3D(x: Float, y: Float, z: Float): Triple<Float, Float, Float> {
                // Rotate around Y
                val x1 = x * cos(effectiveRotY) + z * sin(effectiveRotY)
                val z1 = -x * sin(effectiveRotY) + z * cos(effectiveRotY)

                // Rotate around X
                val y2 = y * cos(effectiveRotX) - z1 * sin(effectiveRotX)
                val z2 = y * sin(effectiveRotX) + z1 * cos(effectiveRotX)

                val scale = (width * 0.007f * zoom) / (1f - (z2 * 0.002f))
                val px = center.x + x1 * scale
                val py = center.y + y2 * scale
                return Triple(px, py, z2)
            }

            // Generate helical protein backbone points
            val numResidues = 75
            val backbonePoints = mutableListOf<Triple<Float, Float, Float>>()

            for (i in 0 until numResidues) {
                val t = i * 0.35f
                val r = 24f + 8f * sin(i * 0.2f)
                val x = r * cos(t)
                val y = r * sin(t)
                val z = (i - numResidues / 2f) * 3.2f

                val proj = project3D(x, y, z)
                backbonePoints.add(proj)
            }

            // Draw Backbone Ribbon or Atomic Spheres
            if (viewMode == "Backbone Ribbon" || viewMode == "Binding Pocket") {
                val path = Path()
                backbonePoints.forEachIndexed { idx, pt ->
                    if (idx == 0) path.moveTo(pt.first, pt.second)
                    else path.lineTo(pt.first, pt.second)
                }

                // Ribbon tube background
                drawPath(
                    path = path,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF6366F1), Color(0xFF38BDF8), Color(0xFF10B981), Color(0xFFA855F7))
                    ),
                    style = Stroke(width = 6.5f)
                )
            }

            // Draw Residue Nodes / Atoms sorted by depth (Z)
            val sortedPoints = backbonePoints.mapIndexed { idx, pt -> Pair(idx, pt) }
                .sortedBy { it.second.third }

            sortedPoints.forEach { (idx, pt) ->
                val isLiningPocket = pocketFeatures != null && (idx in 25..45)
                val atomRadius = when {
                    viewMode == "Atomic Spheres" -> 8f
                    isLiningPocket -> 7f
                    else -> 4f
                }

                val atomColor = when {
                    isLiningPocket -> Color(0xFFF59E0B) // Amber pocket cavity
                    idx % 4 == 0 -> Color(0xFF38BDF8)  // Alpha Carbon (Cyan)
                    idx % 4 == 1 -> Color(0xFF3B82F6)  // Nitrogen (Blue)
                    idx % 4 == 2 -> Color(0xFFEF4444)  // Oxygen (Red)
                    else -> Color(0xFF10B981)          // Carbon (Emerald)
                }

                drawCircle(
                    color = atomColor,
                    radius = atomRadius,
                    center = Offset(pt.first, pt.second)
                )
            }

            // Binding Pocket Cavity Glow
            if (pocketFeatures != null && (viewMode == "Binding Pocket" || viewMode == "Backbone Ribbon")) {
                val pCenter = project3D(
                    pocketFeatures.pocketCenter.first,
                    pocketFeatures.pocketCenter.second,
                    pocketFeatures.pocketCenter.third
                )

                val pocketGlowRadius = minOf(width, height) * 0.18f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFF59E0B).copy(alpha = 0.45f),
                            Color(0xFFF59E0B).copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(pCenter.first, pCenter.second),
                        radius = pocketGlowRadius
                    ),
                    radius = pocketGlowRadius,
                    center = Offset(pCenter.first, pCenter.second)
                )

                // Pocket Label
                drawCircle(
                    color = Color(0xFFF59E0B),
                    radius = 5f,
                    center = Offset(pCenter.first, pCenter.second)
                )
            }
        }

        // Overlay Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(Color(0xFF1E293B).copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "PDB: $pdbId | $geneSymbol | $viewMode",
                color = Color(0xFF38BDF8),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Score Card Gauge
 */
@Composable
fun ScoreGaugeCard(
    title: String,
    score: Float,
    category: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(76.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Background track
                    drawArc(
                        color = Color(0xFF334155),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx())
                    )
                    // Progress arc
                    drawArc(
                        color = accentColor,
                        startAngle = 135f,
                        sweepAngle = 270f * score.coerceIn(0f, 1f),
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx())
                    )
                }
                Text(
                    text = "${(score * 100).toInt()}%",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = category,
                color = accentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DescriptorItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(text = label, color = Color(0xFF94A3B8), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
