package com.example.ui

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = BookmarkRepository(db.bookmarkDao())

    val bookmarks: StateFlow<List<Bookmark>> = repository.allBookmarks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedState = MutableStateFlow<StateData?>(null)
    val selectedState = _selectedState.asStateFlow()

    private val _selectedDistrict = MutableStateFlow<District?>(null)
    val selectedDistrict = _selectedDistrict.asStateFlow()

    private val _zoom = MutableStateFlow(1.0f)
    val zoom = _zoom.asStateFlow()

    private val _pan = MutableStateFlow(Offset.Zero)
    val pan = _pan.asStateFlow()

    // 3D perspective slant states
    private val _tilt = MutableStateFlow(12f)
    val tilt = _tilt.asStateFlow()

    private val _rotation = MutableStateFlow(-6f)
    val rotation = _rotation.asStateFlow()

    private val _overlayType = MutableStateFlow("Topography") // "Topography" | "Temperature" | "Rainfall"
    val overlayType = _overlayType.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun selectState(state: StateData?) {
        _selectedState.value = state
        _selectedDistrict.value = null
        if (state != null) {
            _zoom.value = 2.2f
            _pan.value = Offset(
                x = -(state.centerX - 50f) * 10f,
                y = -(state.centerY - 50f) * 10f
            )
        } else {
            resetView()
        }
    }

    fun selectDistrict(district: District?) {
        _selectedDistrict.value = district
    }

    fun updateZoom(newZoom: Float) {
        _zoom.value = newZoom.coerceIn(0.5f, 6.0f)
    }

    fun updatePan(newPan: Offset) {
        _pan.value = newPan
    }

    fun updateTilt(newTilt: Float) {
        _tilt.value = newTilt.coerceIn(0f, 45f)
    }

    fun updateRotation(newRotation: Float) {
        _rotation.value = newRotation.coerceIn(-30f, 30f)
    }

    fun setOverlayType(type: String) {
        _overlayType.value = type
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun resetView() {
        _zoom.value = 1.0f
        _pan.value = Offset.Zero
        _selectedState.value = null
        _selectedDistrict.value = null
        _tilt.value = 12f
        _rotation.value = -6f
    }

    fun addBookmarkCurrentView(comment: String = "") {
        viewModelScope.launch {
            val stateName = _selectedState.value?.name ?: "All India"
            val stateId = _selectedState.value?.id ?: "ALL"
            val districtName = _selectedDistrict.value?.name
            
            val title = if (districtName != null) "$districtName, $stateName" else stateName
            val type = if (districtName != null) "District" else "State"
            val notesText = comment.ifBlank { "Custom perspective of $title" }
            
            val bookmark = Bookmark(
                regionId = districtName ?: stateId,
                regionName = title,
                regionType = type,
                notes = notesText,
                customTilt = _tilt.value,
                customZoom = _zoom.value
            )
            repository.addBookmark(bookmark)
        }
    }

    fun removeBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.removeBookmark(bookmark)
        }
    }

    fun navigateToBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            _tilt.value = bookmark.customTilt
            _zoom.value = bookmark.customZoom
            
            // Search matching state or district in local datasets
            val foundState = IndiaMapData.states.find { state ->
                state.id == bookmark.regionId || 
                bookmark.regionName.contains(state.name) || 
                state.districts.any { it.name == bookmark.regionId }
            }
            if (foundState != null) {
                _selectedState.value = foundState
                _selectedDistrict.value = foundState.districts.find { 
                    it.name == bookmark.regionId || bookmark.regionName.startsWith(it.name) 
                }
                _pan.value = Offset(
                    x = -(foundState.centerX - 50f) * 10f,
                    y = -(foundState.centerY - 50f) * 10f
                )
            } else {
                _selectedState.value = null
                _selectedDistrict.value = null
                _pan.value = Offset.Zero
            }
        }
    }
}
