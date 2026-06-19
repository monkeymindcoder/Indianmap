package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import kotlin.math.hypot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMapScreen(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val selectedState by viewModel.selectedState.collectAsStateWithLifecycle()
    val selectedDistrict by viewModel.selectedDistrict.collectAsStateWithLifecycle()
    val zoom by viewModel.zoom.collectAsStateWithLifecycle()
    val pan by viewModel.pan.collectAsStateWithLifecycle()
    val tilt by viewModel.tilt.collectAsStateWithLifecycle()
    val rotation by viewModel.rotation.collectAsStateWithLifecycle()
    val overlayType by viewModel.overlayType.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()

    var showAddBookmarkDialog by remember { mutableStateOf(false) }
    var bookmarkComment by remember { mutableStateOf("") }
    var showInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .drawBehind {
                        // High-contrast dark cartographic line at bottom
                        drawLine(
                            color = CartoTextDark,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 3f
                        )
                    }
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "भारताचा नकाशा",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CartoPrimary,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "INDIA INTERACTIVE MAP",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = CartoTextDark,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { showInfoDialog = true },
                            modifier = Modifier
                                .border(1.5.dp, CartoTextDark, RoundedCornerShape(8.dp))
                                .background(CartoSurface)
                                .size(40.dp)
                                .testTag("info_button")
                        ) {
                            Icon(Icons.Filled.Info, contentDescription = "Information Hub", tint = CartoTextDark)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { viewModel.resetView() },
                            modifier = Modifier
                                .border(1.5.dp, CartoTextDark, RoundedCornerShape(8.dp))
                                .background(CartoSurface)
                                .size(40.dp)
                                .testTag("reset_button")
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Reset Compass", tint = CartoTextDark)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom search input and filter overlay selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("search_input"),
                        placeholder = { Text("Search states, districts, or landmarks...", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search icon") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CartoTextDark,
                            unfocusedBorderColor = CartoTextDark.copy(alpha = 0.6f),
                            focusedContainerColor = CartoSurface,
                            unfocusedContainerColor = CartoSurface.copy(alpha = 0.5f)
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dynamic map thematic layer selection
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    val overlays = listOf("Topography", "Temperature", "Rainfall")
                    items(overlays) { layer ->
                        val isSelected = overlayType == layer
                        val chipEmoji = when (layer) {
                            "Topography" -> "🌋 "
                            "Temperature" -> "🌡️ "
                            else -> "🌧️ "
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setOverlayType(layer) },
                            label = { Text(chipEmoji + layer, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CartoPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = CartoSurface,
                                labelColor = CartoTextDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = CartoTextDark,
                                selectedBorderColor = CartoTextDark,
                                borderWidth = 1.5.dp,
                                selectedBorderWidth = 1.5.dp
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth > 650.dp

            if (isWideScreen) {
                // Tablet Landscape Mode: side by side split pane layout
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        MapCanvasWidget(
                            viewModel = viewModel,
                            selectedState = selectedState,
                            selectedDistrict = selectedDistrict,
                            zoom = zoom,
                            pan = pan,
                            tilt = tilt,
                            rotation = rotation,
                            overlayType = overlayType,
                            searchQuery = searchQuery
                        )
                    }

                    // Divider segment
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(2.dp)
                            .background(CartoTextDark)
                    )

                    // Details Panel side layout
                    Box(
                        modifier = Modifier
                            .weight(0.8f)
                            .fillMaxHeight()
                            .background(CartoSurface.copy(alpha = 0.3f))
                    ) {
                        DetailsPanel(
                            viewModel = viewModel,
                            selectedState = selectedState,
                            selectedDistrict = selectedDistrict,
                            bookmarks = bookmarks,
                            onAddBookmarkClick = { showAddBookmarkDialog = true }
                        )
                    }
                }
            } else {
                // Mobile Portrait Layout: Map Canvas taking top 55%, exploration sheet taking lower 45%
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.1f),
                        contentAlignment = Alignment.Center
                    ) {
                        MapCanvasWidget(
                            viewModel = viewModel,
                            selectedState = selectedState,
                            selectedDistrict = selectedDistrict,
                            zoom = zoom,
                            pan = pan,
                            tilt = tilt,
                            rotation = rotation,
                            overlayType = overlayType,
                            searchQuery = searchQuery
                        )
                    }

                    // Horizontal Cartographic Line divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(CartoTextDark)
                    )

                    // Exploration Details bottom tray
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.9f)
                            .background(CartoSurface.copy(alpha = 0.3f))
                    ) {
                        DetailsPanel(
                            viewModel = viewModel,
                            selectedState = selectedState,
                            selectedDistrict = selectedDistrict,
                            bookmarks = bookmarks,
                            onAddBookmarkClick = { showAddBookmarkDialog = true }
                        )
                    }
                }
            }
        }
    }

    // Modal dialogs
    if (showAddBookmarkDialog) {
        AlertDialog(
            onDismissRequest = { showAddBookmarkDialog = false },
            containerColor = CartoBackground,
            title = {
                Text(
                    "Bookmark Perspective",
                    fontWeight = FontWeight.Bold,
                    color = CartoTextDark
                )
            },
            text = {
                Column {
                    val stateLabel = selectedState?.name ?: "All India"
                    val districtLabel = selectedDistrict?.name
                    Text(
                        text = "Save your current view configuration of: ${if (districtLabel != null) "$districtLabel, $stateLabel" else stateLabel}",
                        fontSize = 14.sp,
                        color = CartoTextDark,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = bookmarkComment,
                        onValueChange = { bookmarkComment = it },
                        placeholder = { Text("Add custom note or historic reminder here...", fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bookmark_notes_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CartoTextDark,
                            unfocusedBorderColor = CartoTextDark.copy(alpha = 0.6f)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addBookmarkCurrentView(bookmarkComment)
                        bookmarkComment = ""
                        showAddBookmarkDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CartoPrimary)
                ) {
                    Text("Save Bookmark", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBookmarkDialog = false }) {
                    Text("Cancel", color = CartoTextDark)
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.border(2.dp, CartoTextDark, RoundedCornerShape(12.dp))
        )
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            containerColor = CartoBackground,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌋 ", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cartographic Guide", fontWeight = FontWeight.Black, color = CartoTextDark)
                }
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Text(
                            "This hand-crafted interactive app offers a real-time topographic and climatic exploration of the Indian Subcontinent.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CartoTextDark
                        )
                    }
                    item {
                        HorizontalDivider(color = CartoTextDark)
                    }
                    item {
                        Text(
                            "🔑 Quick Instructions:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CartoPrimary
                        )
                    }
                    item {
                        Text(
                            "- Panning / Zooming: Drag your finger to move the map canvas. Use double-touch pinch to zoom seamlessly into states.\n" +
                            "- 3D Topography View: Use the sliders next to the map to 'Tilt' the canvas relative to the horizon or tilt angle. This extrudes the landscape blocks in real-time pseudo-3D layers!\n" +
                            "- Layer Overlays: Switch between Topographic relief grids, local Isotherm temperature grids, and Monsoon rainfall distributions.\n" +
                            "- Search & Bookmarks: Query any town, state, or heritage site, and save specific view angles & comments directly into state memory.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = CartoTextDark
                        )
                    }
                    item {
                        Text(
                            "🎨 Aesthetic Design:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CartoPrimary
                        )
                    }
                    item {
                        Text(
                            "Styled specifically with parchment ivory tones and bold charcoal-washed lines, mimicking classical physical paper cartography blueprints with highly readable layout spacing.",
                            fontSize = 12.sp,
                            color = CartoTextDark
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CartoTextDark)
                ) {
                    Text("Understand", color = Color.White)
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.border(2.dp, CartoTextDark, RoundedCornerShape(12.dp))
        )
    }
}

@Composable
fun MapCanvasWidget(
    viewModel: MapViewModel,
    selectedState: StateData?,
    selectedDistrict: District?,
    zoom: Float,
    pan: Offset,
    tilt: Float,
    rotation: Float,
    overlayType: String,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density

    // Screen dimension checks for click transformations
    var canvasSize by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CartoBackground)
            .pointerInput(Unit) {
                detectTransformGestures { _, panAmount, zoomAmount, _ ->
                    viewModel.updateZoom(zoom * zoomAmount)
                    viewModel.updatePan(pan + panAmount)
                }
            }
            .pointerInput(canvasSize) {
                detectTapGestures(
                    onDoubleTap = {
                        viewModel.resetView()
                    },
                    onTap = { clickOffset ->
                        val w = canvasSize.x
                        val h = canvasSize.y
                        if (w > 0 && h > 0) {
                            var nearestState: StateData? = null
                            var minDistance = Float.MAX_VALUE

                            // Evaluate distance from tap to each state center, transformed by current zoom/pan
                            IndiaMapData.states.forEach { state ->
                                val centerOffset = MapPoint(state.centerX, state.centerY).toOffset(w, h, zoom, pan)
                                val distance = hypot(clickOffset.x - centerOffset.x, clickOffset.y - centerOffset.y)

                                if (distance < minDistance) {
                                    minDistance = distance
                                    nearestState = state
                                }
                            }

                            // If we click state-wide or select closely
                            val tapRadius = 55f * density * zoom // Dynamic tap response
                            if (minDistance < tapRadius) {
                                viewModel.selectState(nearestState)

                                // If already focusing on state, check if clicked near a district node!
                                val state = nearestState
                                if (state != null && selectedState?.id == state.id) {
                                    var nearestDistrict: District? = null
                                    var minDistD = Float.MAX_VALUE

                                    state.districts.forEach { dist ->
                                        // Calculate absolute map location of district
                                        val dx = state.centerX + dist.relativeX * 6f
                                        val dy = state.centerY + dist.relativeY * 6f
                                        val distOffset = MapPoint(dx, dy).toOffset(w, h, zoom, pan)
                                        val distanceD = hypot(clickOffset.x - distOffset.x, clickOffset.y - distOffset.y)
                                        if (distanceD < minDistD) {
                                            minDistD = distanceD
                                            nearestDistrict = dist
                                        }
                                    }

                                    val clickLimit = 32f * density
                                    if (minDistD < clickLimit) {
                                        viewModel.selectDistrict(nearestDistrict)
                                    } else {
                                        viewModel.selectDistrict(null)
                                    }
                                }
                            } else {
                                viewModel.selectState(null)
                            }
                        }
                    }
                )
            }
            .testTag("map_canvas_container"),
        contentAlignment = Alignment.Center
    ) {
        // High-performance custom map canvas drawing
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationX = tilt
                    rotationZ = rotation
                    cameraDistance = 14f * density
                }
                .testTag("india_vector_canvas")
        ) {
            val w = size.width
            val h = size.height
            canvasSize = Offset(w, h)

            // Draw a cartographic border grid behind
            drawCartoGrid(w, h)

            val filteredStates = IndiaMapData.states.filter { state ->
                searchQuery.isEmpty() || 
                state.name.contains(searchQuery, ignoreCase = true) || 
                state.culture.contains(searchQuery, ignoreCase = true) ||
                state.districts.any { it.name.contains(searchQuery, ignoreCase = true) }
            }

            // 3D TOPOGRAPHIC RELIEF EXTRUSION LOOP
            // We draw stacked layers downwards if tilted, creating an analog topographic block layout
            val extrusionLayers = if (tilt > 5f) 4 else 1
            for (layerIndex in extrusionLayers downTo 1) {
                // Offset calculation based on index
                val verticalShift = (layerIndex - 1) * 3f * density

                IndiaMapData.states.forEach { state ->
                    val isStateSelected = selectedState?.id == state.id
                    val isStateSearched = filteredStates.contains(state)

                    // Form the state path boundary
                    val statePath = Path().apply {
                        if (state.points.isNotEmpty()) {
                            val firstOffset = state.points[0].toOffset(w, h, zoom, pan)
                            moveTo(firstOffset.x, firstOffset.y + verticalShift)
                            
                            for (i in 1 until state.points.size) {
                                val pt = state.points[i].toOffset(w, h, zoom, pan)
                                lineTo(pt.x, pt.y + verticalShift)
                            }
                            close()
                        }
                    }

                    // Render background layer filling
                    if (layerIndex > 1) {
                        // Extruded body: dark terracotta shadow color
                        drawPath(
                            path = statePath,
                            color = Color(0xFF6E5E35).copy(alpha = 0.5f)
                        )
                        drawPath(
                            path = statePath,
                            color = CartoTextDark.copy(alpha = 0.3f),
                            style = Stroke(width = 1.5f * density)
                        )
                    } else {
                        // Top visible layer coloring based on chosen thematic overlay
                        val topFillColor = when (overlayType) {
                            "Topography" -> {
                                when {
                                    state.elevationMeters > 3000 -> Color(0xFFD7CCC8) // Snowy brown range
                                    state.elevationMeters > 1500 -> Color(0xFFE0C491) // High forest alpine
                                    state.elevationMeters > 500 -> Color(0xFFEFE5C2)  // Low central plateaus
                                    else -> Color(0xFFFFF9DF) // Low coastline fertile fields
                                }
                            }
                            "Temperature" -> {
                                when {
                                    state.averageTemp <= 15 -> Color(0xFFC8E6C9) // Cool green
                                    state.averageTemp in 16..25 -> Color(0xFFFFECB3) // Soft warm yellow
                                    else -> Color(0xFFFFCC80) // Fiery orange plain
                                }
                            }
                            "Rainfall" -> {
                                when {
                                    state.annualRainfall > 2000 -> Color(0xFFB3E5FC) // Deep monsoon teal
                                    state.annualRainfall > 1000 -> Color(0xFFE1F5FE) // Medium rain blue
                                    else -> Color(0xFFFFFDF7) // Arid bone sands
                                }
                            }
                            else -> CartoBackground
                        }

                        // Determine selection tinting
                        val finalFillColor = when {
                            isStateSelected -> CartoHighlight.copy(alpha = 0.45f)
                            isStateSearched && searchQuery.isNotEmpty() -> CartoSecondary.copy(alpha = 0.8f)
                            else -> topFillColor
                        }

                        // Draw filled base
                        drawPath(
                            path = statePath,
                            color = finalFillColor
                        )

                        // Draw neat topography contour lines if Topography overlay chosen
                        if (overlayType == "Topography" && state.elevationMeters > 1000) {
                            drawContourHatch(statePath, density)
                        }

                        // Draw state border line
                        val borderStrokeWidth = if (isStateSelected) 3.5f * density else 1.5f * density
                        val borderStrokeColor = if (isStateSelected) CartoHighlight else CartoTextDark
                        
                        drawPath(
                            path = statePath,
                            color = borderStrokeColor,
                            style = Stroke(width = borderStrokeWidth)
                        )
                    }
                }
            }

            // Draw primary landmarks & capital points on top of base layer
            IndiaMapData.states.forEach { state ->
                val centerOffset = MapPoint(state.centerX, state.centerY).toOffset(w, h, zoom, pan)
                
                // Draw State labels only if zoomed in or no state selected
                if (zoom > 1.2f || selectedState == null) {
                    val isStateSelected = selectedState?.id == state.id
                    val textPaint = android.graphics.Paint().apply {
                        color = if (isStateSelected) 0xFFE57373.toInt() else 0xFF1E1C11.toInt()
                        textSize = (9f + (zoom * 1.5f)).coerceIn(10f, 22f) * density
                        isFakeBoldText = true
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.SANS_SERIF
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        state.id,
                        centerOffset.x,
                        centerOffset.y + (4f * density), // simple center align adjustment
                        textPaint
                    )
                }

                // If zoomed into a selected state, draw the specific district nodes!
                if (selectedState?.id == state.id) {
                    state.districts.forEach { dist ->
                        val dx = state.centerX + dist.relativeX * 6f
                        val dy = state.centerY + dist.relativeY * 6f
                        val distOffset = MapPoint(dx, dy).toOffset(w, h, zoom, pan)

                        val isDistrictSelected = selectedDistrict?.id == dist.id
                        
                        // Draw outer target ring
                        drawCircle(
                            color = if (isDistrictSelected) CartoHighlight else CartoTextDark,
                            radius = if (isDistrictSelected) 10f * density else 6f * density,
                            center = distOffset,
                            style = Stroke(width = if (isDistrictSelected) 2.5f * density else 1.2f * density)
                        )
                        
                        // Inner focal point bullet
                        drawCircle(
                            color = if (isDistrictSelected) CartoHighlight else CartoPrimary,
                            radius = 3f * density,
                            center = distOffset
                        )

                        // District Text label
                        val distPaint = android.graphics.Paint().apply {
                            color = 0xFF1E1C11.toInt()
                            textSize = 10f * density
                            isFakeBoldText = isDistrictSelected
                            textAlign = android.graphics.Paint.Align.CENTER
                        }

                        drawContext.canvas.nativeCanvas.drawText(
                            dist.name,
                            distOffset.x,
                            distOffset.y - (10f * density),
                            distPaint
                        )
                    }
                }
            }
        }

        // COMPASS ROSE & 3D INTERFACE SLIDERS PANE
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(CartoBackground.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                .border(2.dp, CartoTextDark, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "3D TOPOGRAPHY CONTROLS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CartoTextDark,
                letterSpacing = 1.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("🌋 ", fontSize = 12.sp)
                Text("Tilt:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = tilt,
                    onValueChange = { viewModel.updateTilt(it) },
                    valueRange = 0f..45f,
                    modifier = Modifier
                        .width(100.dp)
                        .height(24.dp)
                        .testTag("tilt_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = CartoPrimary,
                        activeTrackColor = CartoTextDark,
                        inactiveTrackColor = CartoTextDark.copy(alpha = 0.2f)
                    )
                )
                Text("${tilt.toInt()}°", fontSize = 11.sp, modifier = Modifier.width(28.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("🔄 ", fontSize = 12.sp)
                Text("Rot:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = rotation,
                    onValueChange = { viewModel.updateRotation(it) },
                    valueRange = -30f..30f,
                    modifier = Modifier
                        .width(100.dp)
                        .height(24.dp)
                        .testTag("rotation_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = CartoPrimary,
                        activeTrackColor = CartoTextDark,
                        inactiveTrackColor = CartoTextDark.copy(alpha = 0.2f)
                    )
                )
                Text("${rotation.toInt()}°", fontSize = 11.sp, modifier = Modifier.width(28.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = { viewModel.updateZoom(zoom + 0.3f) },
                    modifier = Modifier
                        .size(32.dp)
                        .background(CartoSurface, RoundedCornerShape(4.dp))
                        .border(1.dp, CartoTextDark, RoundedCornerShape(4.dp))
                        .testTag("zoom_in_button")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Zoom In", tint = CartoTextDark, modifier = Modifier.size(16.dp))
                }
                IconButton(
                    onClick = { viewModel.updateZoom(zoom - 0.3f) },
                    modifier = Modifier
                        .size(32.dp)
                        .background(CartoSurface, RoundedCornerShape(4.dp))
                        .border(1.dp, CartoTextDark, RoundedCornerShape(4.dp))
                        .testTag("zoom_out_button")
                ) {
                    Text("—", fontWeight = FontWeight.Black, color = CartoTextDark, fontSize = 12.sp)
                }
            }
        }

        // DYNAMIC SCALE AND LEGEND LEGATIVE
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .background(CartoBackground.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .border(1.5.dp, CartoTextDark, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                val scaleKm = (200 / zoom).toInt()
                Text("SCALE INDICATOR", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CartoTextDark)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Draw mini cartographic scale block
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(6.dp)
                            .border(1.dp, CartoTextDark)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(CartoTextDark, Color.Transparent, CartoTextDark, Color.Transparent)
                                )
                            )
                    )
                    Text("$scaleKm km", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CartoTextDark)
                }
            }
        }
    }
}

// Draw a beautiful topographic contour alignment line inside states
fun DrawScope.drawContourHatch(path: Path, density: Float) {
    drawPath(
        path = path,
        color = CartoTextDark.copy(alpha = 0.08f),
        style = Stroke(
            width = 4f * density,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 15f), 0f)
        )
    )
}

// Background Grid
fun DrawScope.drawCartoGrid(width: Float, height: Float) {
    val step = 80f
    var x = 0f
    while (x < width) {
        drawLine(
            color = CartoTextDark.copy(alpha = 0.04f),
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1f
        )
        x += step
    }
    var y = 0f
    while (y < height) {
        drawLine(
            color = CartoTextDark.copy(alpha = 0.04f),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
        )
        y += step
    }
}

@Composable
fun DetailsPanel(
    viewModel: MapViewModel,
    selectedState: StateData?,
    selectedDistrict: District?,
    bookmarks: List<Bookmark>,
    onAddBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (selectedState == null) {
            // INTRODUCTORY DEFAULT LANDING & BOOKMARKS VIEW
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CartoSurface.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.5.dp, CartoTextDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⛰️", fontSize = 18.sp)
                            Text("Subcontinent Geography", fontSize = 16.sp, fontWeight = FontWeight.Black, color = CartoTextDark)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "India stretches from the snowy glided Himalayan peaks in the north to the tropical, spice-scented oceans of the Indian Ocean south. Explore detailed topography heights, climate layers, and historic landmarks.",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = CartoTextDark
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "📌 SAVED BOOKMARKS & VIEWS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CartoPrimary
                    )
                    IconButton(
                        onClick = onAddBookmarkClick,
                        modifier = Modifier
                            .size(28.dp)
                            .background(CartoPrimary, RoundedCornerShape(4.dp))
                            .testTag("add_bookmark_button")
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add bookmark", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (bookmarks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CartoTextDark.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = "None", tint = CartoTextDark.copy(alpha = 0.4f), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("No saved regional perspective bookmarks yet.", fontSize = 12.sp, color = CartoTextDark.copy(alpha = 0.5f))
                        Text("Tap + above to bookmark your current map viewpoint.", fontSize = 11.sp, color = CartoTextDark.copy(alpha = 0.4f))
                    }
                }
            } else {
                items(bookmarks) { bookmark ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateToBookmark(bookmark) }
                            .testTag("bookmark_item"),
                        colors = CardDefaults.cardColors(containerColor = CartoSurface),
                        border = BorderStroke(1.5.dp, CartoTextDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (bookmark.regionType == "District") Icons.Filled.LocationOn else Icons.Filled.LocationOn,
                                        contentDescription = "Bookmark Type",
                                        tint = CartoPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = bookmark.regionName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = CartoTextDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = bookmark.notes,
                                    fontSize = 12.sp,
                                    color = CartoTextDark.copy(alpha = 0.8f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            IconButton(
                                onClick = { viewModel.removeBookmark(bookmark) },
                                modifier = Modifier.testTag("delete_bookmark_${bookmark.id}")
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Remove Bookmark", tint = CartoHighlight)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "🗺️ GEOMETRIC STATES LIST",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CartoPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(IndiaMapData.states) { state ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectState(state) }
                        .testTag("state_item_${state.id}"),
                    colors = CardDefaults.cardColors(containerColor = CartoSurface.copy(alpha = 0.7f)),
                    border = BorderStroke(1.5.dp, CartoTextDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(state.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CartoTextDark)
                            Text("Capital: ${state.capital}", fontSize = 12.sp, color = CartoTextDark.copy(alpha = 0.7f))
                        }
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Open State", tint = CartoTextDark)
                    }
                }
            }
        } else {
            // SPECIFIC STATE DETAILED EXPLORATION PANEL
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { viewModel.selectState(null) },
                        modifier = Modifier
                            .border(1.dp, CartoTextDark, RoundedCornerShape(4.dp))
                            .background(CartoSurface)
                            .size(32.dp)
                            .testTag("back_to_india")
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back to India", tint = CartoTextDark, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        "STATE METADATA PROFILE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CartoPrimary
                    )
                    IconButton(
                        onClick = onAddBookmarkClick,
                        modifier = Modifier
                            .size(32.dp)
                            .background(CartoPrimary, RoundedCornerShape(4.dp))
                            .testTag("state_bookmark_plus")
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = "Bookmark State", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            item {
                Column {
                    Text(
                        text = selectedState.name.uppercase(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = CartoTextDark
                    )
                    Text(
                        text = "Capital Centre: ${selectedState.capital}  |  Elevation: ${selectedState.elevationMeters}m",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CartoPrimary
                    )
                }
            }

            // LOCALIZED CLIMATE RATINGS OVERLAYS (CARD WITH DETAILS)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CartoBackground),
                    border = BorderStroke(1.5.dp, CartoTextDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("📉 LOCALIZED CLIMATE OVERLAYS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CartoPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🌡️ ", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Avg Temp", fontSize = 11.sp, color = CartoTextDark.copy(alpha = 0.6f))
                                }
                                Text("${selectedState.averageTemp}°C", fontSize = 18.sp, fontWeight = FontWeight.Black, color = CartoTextDark)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🌧️ ", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Monsoon Rain", fontSize = 11.sp, color = CartoTextDark.copy(alpha = 0.6f))
                                }
                                Text("${selectedState.annualRainfall} mm", fontSize = 18.sp, fontWeight = FontWeight.Black, color = CartoTextDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = CartoTextDark.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Text("🌋 ", fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedState.topography,
                                fontSize = 12.sp,
                                color = CartoTextDark
                            )
                        }
                    }
                }
            }

            // CULTURAL & HISTORICAL INSIGHTS
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CartoSurface),
                    border = BorderStroke(1.5.dp, CartoTextDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📜 ", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("HISTORICAL & CULTURAL INSIGHTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CartoTextDark)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = selectedState.culture,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = CartoTextDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Legacy: ${selectedState.historicalLegacy}",
                            fontSize = 11.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = CartoTextDark.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // DISTRICT-LEVEL EXPLORATION
            item {
                Text(
                    "📌 DISTRICT-LEVEL EXPLORATION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CartoPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(selectedState.districts) { district ->
                val isSelected = selectedDistrict?.id == district.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectDistrict(if (isSelected) null else district) }
                        .testTag("district_item_${district.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) CartoHighlight.copy(alpha = 0.15f) else CartoSurface.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) CartoHighlight else CartoTextDark
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = district.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isSelected) CartoHighlight else CartoTextDark
                            )
                            Badge(
                                containerColor = CartoPrimary,
                                contentColor = Color.White
                            ) {
                                Text("${district.elevationMeters}m", fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp))
                            }
                        }

                        AnimatedVisibility(visible = isSelected) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                HorizontalDivider(color = CartoTextDark.copy(alpha = 0.1f))
                                Text("🌡️ Local Temp: ${district.temperatureCelsius}°C  |  🌧️ Rainfall: ${district.annualRainfallMm}mm", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CartoTextDark)
                                Text("⛅ Climate: ${district.climateSummary}", fontSize = 12.sp, color = CartoTextDark)
                                Text("🏛️ Star Attraction: ${district.primaryAttraction}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CartoPrimary)
                                Text("📜 History: ${district.historicInsight}", fontSize = 12.sp, color = CartoTextDark.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }
        }
    }
}
