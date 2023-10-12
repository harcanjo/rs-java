package com.harcanjo.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
	
	@Autowired
	private ITaskRepository taskRepository;
	
	@PostMapping("/")
	public ResponseEntity<?> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {		
		var idUser = request.getAttribute("idUser");
		taskModel.setIdUser((UUID)idUser);
		
		var currentDate = LocalDateTime.now();		
		// 12/11/2023 - Current
		// 10/10/2023 - startAt
		if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("The start / end date must be greater than the current date");
		}
		
		if(taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("The start date must be less than the end date");
		}		
		
		var task = this.taskRepository.save(taskModel);
		return ResponseEntity.status(HttpStatus.OK).body(task);
	}
	
	@GetMapping("/")
	public List<TaskModel> list(HttpServletRequest request) {
		var idUser = request.getAttribute("idUser");
		var tasks = this.taskRepository.findByIdUser((UUID)idUser);
		return tasks;
	}
	
	//http://localhost:8080/tasks/5c269e75-819144bc959d-8849f535e401
	@PutMapping("/{id}")
	public TaskModel update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
		var idUser = request.getAttribute("idUser");
		taskModel.setIdUser((UUID)idUser);
		taskModel.setId(id);
		return this.taskRepository.save(taskModel);
	}

}
