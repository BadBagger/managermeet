package com.smithware.managermeet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smithware.managermeet.data.ManagerMeetDatabase
import com.smithware.managermeet.data.ManagerMeetRepository
import com.smithware.managermeet.data.PreferencesStore
import com.smithware.managermeet.data.ProjectEntity
import com.smithware.managermeet.data.SettingsEntity
import com.smithware.managermeet.data.defaultChecklist
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ManagerMeetState(
    val projects: List<ProjectEntity> = emptyList(),
    val settings: SettingsEntity = SettingsEntity(),
    val darkMode: Boolean = false,
    val compactCards: Boolean = false
) {
    val activeProject: ProjectEntity? = projects.firstOrNull()
    val averageProgress: Int =
        if (projects.isEmpty()) 0 else projects.map { it.progress }.average().toInt()
}

class ManagerMeetViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ManagerMeetRepository(
        ManagerMeetDatabase.get(application).dao()
    )
    private val preferences = PreferencesStore(application)

    val state: StateFlow<ManagerMeetState> = combine(
        repository.projects,
        repository.settings,
        preferences.darkMode,
        preferences.compactCards
    ) { projects, settings, darkMode, compact ->
        ManagerMeetState(
            projects = projects,
            settings = settings ?: SettingsEntity(),
            darkMode = darkMode,
            compactCards = compact
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ManagerMeetState())

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
    }

    fun saveProject(
        existing: ProjectEntity?,
        name: String,
        idea: String,
        audience: String,
        coreProblem: String,
        status: String,
        notes: String,
        checklist: List<String>,
        progress: Int
    ): String? {
        if (name.isBlank() || idea.isBlank() || coreProblem.isBlank()) return "Name, idea, and core problem are required."
        viewModelScope.launch {
            repository.saveProject(
                (existing ?: ProjectEntity(
                    name = name.trim(),
                    idea = idea.trim(),
                    audience = audience.trim(),
                    coreProblem = coreProblem.trim(),
                    status = status.trim().ifBlank { "Draft" },
                    notes = notes.trim(),
                    checklist = checklist.joinToString("|"),
                    progress = progress.coerceIn(0, 100)
                )).copy(
                    name = name.trim(),
                    idea = idea.trim(),
                    audience = audience.trim().ifBlank { "Managers" },
                    coreProblem = coreProblem.trim(),
                    status = status.trim().ifBlank { "Draft" },
                    notes = notes.trim(),
                    checklist = checklist.filter { it.isNotBlank() }.joinToString("|"),
                    progress = progress.coerceIn(0, 100)
                )
            )
        }
        return null
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch { repository.deleteProject(project) }
    }

    fun archiveProject(projectId: String) {
        viewModelScope.launch { repository.archiveProject(projectId) }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch { preferences.setDarkMode(enabled) }
    }

    fun toggleCompactCards(enabled: Boolean) {
        viewModelScope.launch { preferences.setCompactCards(enabled) }
    }

    fun updateSettings(title: String, notes: String) {
        viewModelScope.launch {
            repository.saveSettings(SettingsEntity(title = title.trim(), notes = notes.trim()))
        }
    }
}

fun ProjectEntity.checklistItems(): List<String> =
    checklist.split("|").map { it.trim() }.filter { it.isNotBlank() }.ifEmpty { defaultChecklist }

fun ProjectEntity.buildPrompt(): String {
    val edited = lastEdited.format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"))
    return """
        Build a native Android app for Smithware Studios called "$name".
        
        Tagline: Turn app ideas into build-ready plans.
        
        App goal:
        ${idea.ifBlank { "Turn a rough idea into a focused Android product plan for public release." }}
        
        Target audience:
        ${audience.ifBlank { "Managers" }}
        
        Core problem:
        ${coreProblem.ifBlank { "Communication problems between managers." }}
        
        MVP scope:
        - Launch checklist
        - Generate build prompt
        - Track MVP progress
        - Edit saved items
        - Create project
        
        Current project status:
        $status, $progress% complete. Last edited $edited.
        
        Launch checklist:
        ${checklistItems().joinToString(separator = "\n") { "- $it" }}
        
        Notes:
        ${notes.ifBlank { "Keep the first release focused, local-only, and clear." }}
        
        Technical requirements:
        Kotlin, Jetpack Compose, Material 3, Room, DataStore, local storage, light and dark mode, empty states, validation, edit/delete/archive support, and no cloud upload in v1.
        
        Privacy requirement:
        Do not upload ideas, prompts, app plans, or project data anywhere in v1. Your app ideas stay on this device.
    """.trimIndent()
}
