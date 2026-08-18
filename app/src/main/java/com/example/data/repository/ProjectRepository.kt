package com.example.data.repository

import com.example.data.db.ProjectDao
import com.example.data.model.ProjectEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {

    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    suspend fun saveProject(project: ProjectEntity): Long {
        return projectDao.insertProject(project)
    }

    suspend fun getProjectById(id: Long): ProjectEntity? {
        return projectDao.getProjectById(id)
    }

    suspend fun deleteProject(id: Long) {
        projectDao.deleteProjectById(id)
    }

    suspend fun clearAllProjects() {
        projectDao.clearAll()
    }
}
