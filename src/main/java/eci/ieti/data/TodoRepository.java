package eci.ieti.data;

import eci.ieti.data.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;


public interface TodoRepository extends CrudRepository<Todo, Long> {

    public Page<Todo> findByResponsible(String name, Pageable pageable);

    @Query("{ 'dueDate': { $lt : ?0 } }")
    public Page<Todo> findByDateExpired(Date date, Pageable pageable);

    @Query("{ 'responsible': ?0 ,  'priority': { $gte : ?1 } }")
    public Page<Todo> findByResponsibleWithMinimunPriority(String responsible, int priority, Pageable pageable);


    @Query("{ 'description' : { $regex : ?0 } }")
    public Page<Todo> findByDescription(String regex, Pageable pageable);
}
