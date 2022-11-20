package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
	private ProjectDao projectDao = new ProjectDao();
	

	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}


	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}


	public Project fetchProjectById(Integer pId) {
		return projectDao.fetchProjectById(pId).orElseThrow(
				() -> new NoSuchElementException(
						"Project with project ID=" + pId 
						+ " does not exist."));
	}


	public void modifyProjectDetails(Project newProject) {
		if(!projectDao.modifyProjectDetails(newProject)){
			throw new DbException("Project with ID=" + newProject.getProjectId() + " does not exist.");
			
		}
		
	}


	public void deleteProject(Integer a) {
		if(!projectDao.deleteProject(a)) {
			throw new DbException("Project with ID=" + a + " does not exist.");
		}
	}
}
