package br.com.v1nic1us.todolist.task;

import br.com.v1nic1us.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private  TaskRepository taskRepository;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        taskModel.setIdUser((UUID) request.getAttribute("idUser"));
        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data não pode ser anterior a data atual");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt()) ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data de inicio deve ser maior que a data de termino");
        }

        TaskModel saved = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(saved);
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(HttpServletRequest request){
        var list = this.taskRepository.findByIdUser((UUID) request.getAttribute("idUser"));
        if (list == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("A lista de tarefas do usuário não foi encontrada.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }


    @PutMapping("/fieldUpdate/{id}")
    public ResponseEntity<?> fieldUpdate(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
        var idUser = (UUID) request.getAttribute("idUser");
        var task = this.taskRepository.findById(id);

        if (task.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada");
        }

        if (!idUser.equals(task.get().getIdUser())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Seu usuario não pode alterar essa tarefa");
        }

        Utils.copyNonNullProperties(taskModel, task.get());
        TaskModel updatedTask = this.taskRepository.save(task.get());

        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
        var idUser = (UUID) request.getAttribute("idUser");
        var taskUpdate = this.taskRepository.findById(id);
        if (taskUpdate.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada");
        }

        if (!idUser.equals(taskUpdate.get().getIdUser())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Seu usuario não pode alterar essa tarefa");
        }

        Utils.copyNonNullProperties(taskModel, taskUpdate.get());
        TaskModel updatedTask = this.taskRepository.save(taskUpdate.get());

        return ResponseEntity.status(HttpStatus.OK).body(this.taskRepository.save(updatedTask));
    }
}
