package com.smithware.managermeet.data

import java.time.LocalDateTime

class ManagerMeetRepository(private val dao: ManagerMeetDao) {
    val projects = dao.observeProjects()
    val settings = dao.observeSettings()

    suspend fun seedIfEmpty() {
        if (dao.projectCount() == 0) {
            dao.saveSettings(SettingsEntity())
            dao.insertProject(
                ProjectEntity(
                    name = "ManagerMeet Public Release",
                    idea = "Turn rough manager app ideas into build-ready Android product plans.",
                    audience = "Managers who need clearer communication, handoffs, and launch planning.",
                    coreProblem = "Communication problems between managers create vague app specs and scattered priorities.",
                    status = "MVP Locked",
                    notes = "Keep v1 focused: checklist, MVP progress, saved-item editing, and Codex-ready export.",
                    checklist = defaultChecklist.take(4).joinToString("|"),
                    progress = 67
                )
            )
            dao.insertProject(
                ProjectEntity(
                    name = "Shift Handoff Brief",
                    idea = "A lightweight local planner for turning recurring manager updates into structured app requirements.",
                    audience = "Store, operations, and team managers.",
                    coreProblem = "Updates get buried in chats and meetings before they become usable product decisions.",
                    status = "Draft",
                    notes = "Use this demo record to test edit, archive, detail, and export states.",
                    checklist = defaultChecklist.take(2).joinToString("|"),
                    progress = 34
                )
            )
        }
    }

    fun observeProject(id: String) = dao.observeProject(id)

    suspend fun saveProject(project: ProjectEntity) {
        dao.insertProject(project.copy(lastEdited = LocalDateTime.now()))
    }

    suspend fun deleteProject(project: ProjectEntity) = dao.deleteProject(project)

    suspend fun archiveProject(id: String) = dao.archiveProject(id, LocalDateTime.now())

    suspend fun saveSettings(settings: SettingsEntity) = dao.saveSettings(settings)
}
