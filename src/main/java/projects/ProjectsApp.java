package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List Projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete Project"
			);
	
	// @formatter:on
	
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject = new Project();
	
	
	
	public static void main(String[] args) {

		new ProjectsApp().proccessUserSelections();
		

	}


	private void proccessUserSelections() {
		boolean done = false;
		
		while(!done) {
			try{
				int selection = getUserSelection();
				switch (selection) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
				case 4:
					updateProjectDetails();
					break;
				case 5:
					deleteProject();
					break;
				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
					break;
				}
			} catch(Exception e) {
				System.out.println("\nError: " + e + " Try again");
				//proccessUserSelections(); had been used for debugging an issue with the menu
			}
		}
		
	}


	private void deleteProject() {
		listProjects();
		Integer a = getIntInput("Please enter the ID of a project to delete");
		projectService.deleteProject(a);
		System.out.println("Project " + a + " deleted.");
		if(!Objects.isNull(curProject.getProjectId())) {
			if(curProject.getProjectId().equals(a)) {
				curProject = null;
			}
		}
		
		
		
		
	}


	private void updateProjectDetails() {
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}
		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		//String projectStep = getStringInput("Enter the project steps [" + curProject.getSteps() + "]");
		String projectNotes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
		//String projectCategories = getStringInput("Enter the project categories [" + curProject.getCategories() + "]");
		//String projectMaterials = getStringInput("Enter the project materials [" + curProject.getMaterials() + "]"); 
		Integer projectDifficulty = getIntInput("Enter the project difficulty [" + curProject.getDifficulty() + "]");
		BigDecimal projectActualHours = getDecimalInput("Enter the project actual hours [" + curProject.getActualHours() + "]");
		BigDecimal projectEstimatedHours = getDecimalInput("Enter the project estimated hours [" + curProject.getEstimatedHours() + "]");		
		
		Project newProject = new Project();
		
		newProject.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		newProject.setNotes(Objects.isNull(projectNotes) ? curProject.getNotes() : projectNotes);
		newProject.setDifficulty(Objects.isNull(projectDifficulty) ? curProject.getDifficulty() : projectDifficulty);
		newProject.setActualHours(Objects.isNull(projectActualHours) ? curProject.getActualHours() : projectActualHours);
		newProject.setEstimatedHours(Objects.isNull(projectEstimatedHours) ? curProject.getEstimatedHours() : projectEstimatedHours);
		newProject.setProjectId(curProject.getProjectId());
		
		//newProject.setSteps(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		//newProject.setCat(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		//newProject.setMat(Objects.isNull(projectActualHours) ? curProject.getActualHours() : projectActualHours);

		projectService.modifyProjectDetails(newProject);
		curProject = projectService.fetchProjectById(curProject.getProjectId());
	}


	private void selectProject() {
		listProjects();
		Integer pId = getIntInput("Enter a project ID to select a project");
		
		/* clears current project*/
		curProject = null;
		
		/* will throw exception if incorrect option selected */
		curProject = projectService.fetchProjectById(pId);
		
		if (curProject == null){
			System.out.println("Invalid project ID selected.");
		}
		
	}


	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out.println("  " + project.getProjectId() +  ": " + project.getProjectName()));	
	}


	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
		Project project = new Project();
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbproject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbproject);
		
		
		
	}


	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		if(Objects.isNull(input)) {
			return null;
		} 
			try{
				return new BigDecimal(input).setScale(2);
			} catch(NumberFormatException e){
				throw new DbException(input + " is not a valid decimal number.");
			}
		
	}


	private boolean exitMenu() {
		System.out.println("Exiting the menu");
		return true;
	}


	private int getUserSelection() {
		printOperations();
		Integer input = getIntInput("Enter a menu selection");
		
		return Objects.isNull(input) ? -1 : input;
		
	}


	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		if(Objects.isNull(input)) {
			return null;
		}
			try{
				return Integer.valueOf(input);
			} catch(NumberFormatException e){
				throw new DbException(input + " is not a valid number.");
			}
		
	}


	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		
		return input.isBlank() ? null : input.trim();
	}


	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit.");
		operations.forEach(line -> System.out.println(" " + line));
		if (curProject == null || curProject.getProjectId() == null) {
			System.out.println("\nYou are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}

}
