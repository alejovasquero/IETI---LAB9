package eci.ieti;

import eci.ieti.config.AppConfiguration;
import eci.ieti.data.CustomerRepository;
import eci.ieti.data.ProductRepository;
import eci.ieti.data.TodoRepository;
import eci.ieti.data.UserRepository;
import eci.ieti.data.model.Customer;
import eci.ieti.data.model.Product;

import eci.ieti.data.model.Todo;
import eci.ieti.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class Application implements CommandLineRunner {


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    private MongoOperations mongoOperation;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);
        mongoOperation = (MongoOperations) applicationContext.getBean("mongoTemplate");


/*
        customerRepository.deleteAll();

        customerRepository.save(new Customer("Alice", "Smith"));
        customerRepository.save(new Customer("Bob", "Marley"));
        customerRepository.save(new Customer("Jimmy", "Page"));
        customerRepository.save(new Customer("Freddy", "Mercury"));
        customerRepository.save(new Customer("Michael", "Jackson"));

        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        
        customerRepository.findAll().stream().forEach(System.out::println);
        System.out.println();
        
        productRepository.deleteAll();

        productRepository.save(new Product(1L, "Samsung S8", "All new mobile phone Samsung S8"));
        productRepository.save(new Product(2L, "Samsung S8 plus", "All new mobile phone Samsung S8 plus"));
        productRepository.save(new Product(3L, "Samsung S9", "All new mobile phone Samsung S9"));
        productRepository.save(new Product(4L, "Samsung S9 plus", "All new mobile phone Samsung S9 plus"));
        productRepository.save(new Product(5L, "Samsung S10", "All new mobile phone Samsung S10"));
        productRepository.save(new Product(6L, "Samsung S10 plus", "All new mobile phone Samsung S10 plus"));
        productRepository.save(new Product(7L, "Samsung S20", "All new mobile phone Samsung S20"));
        productRepository.save(new Product(8L, "Samsung S20 plus", "All new mobile phone Samsung S20 plus"));
        productRepository.save(new Product(9L, "Samsung S20 ultra", "All new mobile phone Samsung S20 ultra"));
        
        System.out.println("Paginated search of products by criteria:");
        System.out.println("-------------------------------");
        
        productRepository.findByDescriptionContaining("plus", PageRequest.of(0, 2)).stream()
        	.forEach(System.out::println);
   
        System.out.println();

 /*
        Pageable pageable = PageRequest.of(0, 50, Sort.by(
                Sort.Order.asc("name"),
                Sort.Order.desc("id")));
        System.out.println(customerRepository.findAll(pageable ).getContent());


        productRepository.findByDescriptionContaining("plus", PageRequest.of(0, Integer.MAX_VALUE)).stream()
                .forEach(System.out::println);
        System.out.println(productRepository.findByDescriptionContaining("plus", pageable).getContent().size());

        productRepository.findByDescriptionContaining("plus", PageRequest.of(0, 2)).stream()
                .forEach(System.out::println);

  */
        /*
        todoRepository.save(new Todo("description", 2, new Date(), "david", "ready"));
        todoRepository.save(new Todo("description", 2, new Date(), "juan", "ready"));
        todoRepository.save(new Todo("description", 2, new Date(), "juan", "ready"));
        todoRepository.save(new Todo("description", 2, new Date(), "jose", "ready"));


        todoRepository.findByResponsible("david", PageRequest.of(0, Integer.MAX_VALUE)).stream()
                .forEach(System.out::println);

        */
        populate();
        useQueries();
        System.out.println("\n\n\n DERIVED QUERIES");
        useRepositories();


    }

    private void useRepositories(){

        System.out.println("EXPIRED");
        todoRepository.findByDateExpired(new Date(), PageRequest.of(0, Integer.MAX_VALUE)).stream().forEach(System.out::println);

        System.out.println("RESPONSIBLE AND PRIORITY");
        todoRepository.findByResponsibleWithMinimunPriority("sebastian", 5, PageRequest.of(0, Integer.MAX_VALUE)).stream().forEach(System.out::println);

        System.out.println("LENGTH");
        todoRepository.findByDescription(".{30}", PageRequest.of(0, Integer.MAX_VALUE)).stream().forEach(System.out::println);
    }

    private void useQueries(){
        System.out.println("EXPIRED");

        Query expiredDate = new Query();
        expiredDate.addCriteria(Criteria.where("dueDate").lt(new Date()));
        mongoOperation.find(expiredDate, Todo.class).stream()
                .forEach(System.out::println);


        System.out.println("RESPONSIBLE AND PRIORITY");
        Query assignAndPriority = new Query();
        assignAndPriority.addCriteria(Criteria.where("responsible").is("alejandro").andOperator(
                Criteria.where("priority").gte(5)
        ));
        mongoOperation.find(assignAndPriority, Todo.class).stream()
                .forEach(System.out::println);

        System.out.println("USERS");
        List<User> users = mongoOperation.findAll(User.class);
        List<User> ans = new ArrayList<User>();

        for(User u: users){
            Query userQuery = new Query();
            userQuery.addCriteria(Criteria.where("responsible").is(u.getName()));
            if(mongoOperation.find(userQuery, Todo.class).size() >= 2){
                ans.add(u);
            }
        }
        ans.stream()
                .forEach(System.out::println);

        System.out.println("LENGTH");
        Query lengthQuery = new Query();
        lengthQuery.addCriteria(Criteria.where("description").regex(".{30}"));
        mongoOperation.find(lengthQuery, Todo.class).stream()
                .forEach(System.out::println);
    }

    private void populate() throws ParseException {

        userRepository.deleteAll();

        userRepository.save(new User("alejandro", "example@example.com"));
        userRepository.save(new User("juan", "example@example.com"));
        userRepository.save(new User("david", "example@example.com"));
        userRepository.save(new User("leonardo", "example@example.com"));
        userRepository.save(new User("luis", "example@example.com"));
        userRepository.save(new User("luisa", "example@example.com"));
        userRepository.save(new User("michael", "example@example.com"));
        userRepository.save(new User("sebastian", "example@example.com"));
        userRepository.save(new User("santiago", "example@example.com"));
        userRepository.save(new User("camilo", "example@example.com"));


        todoRepository.deleteAll();

        SimpleDateFormat format =  new SimpleDateFormat("dd/MM/yyyy");
        todoRepository.save(new Todo("012345678901234567890123456789", 1, format.parse("21/12/2012"), "alejandro", "ready"));
        todoRepository.save(new Todo("test2", 10, format.parse("13/02/2022"), "juan", "ready"));
        todoRepository.save(new Todo("test3", 5, format.parse("21/12/2012"), "sebastian", "done"));
        todoRepository.save(new Todo("test4", 17, format.parse("21/12/2012"), "alejandro", "ready"));
        todoRepository.save(new Todo("test5", 5, format.parse("21/12/2013"), "sebastian", "done"));
        todoRepository.save(new Todo("test6", 1, format.parse("21/12/2014"), "alejandro", "ready"));
        todoRepository.save(new Todo("test7", 0, format.parse("21/12/2015"), "alejandro", "on progress"));
        todoRepository.save(new Todo("test8", 6, format.parse("21/12/2016"), "alejandro", "ready"));
        todoRepository.save(new Todo("test9", 4, format.parse("21/12/2017"), "juan", "ready"));
        todoRepository.save(new Todo("test10", 4, format.parse("21/12/2082"), "alejandro", "done"));
        todoRepository.save(new Todo("test11", 4, format.parse("21/12/2000"), "alejandro", "done"));
        todoRepository.save(new Todo("01234567890123456789012345678901234567sss", 1, format.parse("21/12/2012"), "alejandro", "ready"));
        todoRepository.save(new Todo("test13", 6, format.parse("21/12/2011"), "alejandro", "on progress"));
        todoRepository.save(new Todo("test14", 10, format.parse("21/12/2012"), "alejandro", "ready"));
        todoRepository.save(new Todo("test15", 11, format.parse("21/12/2013"), "alejandro", "on progress"));
        todoRepository.save(new Todo("test16", 12, format.parse("21/12/2014"), "camilo", "ready"));
        todoRepository.save(new Todo("test17", -1, format.parse("21/12/2023"), "camilo", "ready"));
        todoRepository.save(new Todo("test18", 0, format.parse("21/12/2040"), "sebastian", "done"));
        todoRepository.save(new Todo("test19", 6, format.parse("21/12/2050"), "alejandro", "ready"));
        todoRepository.save(new Todo("01234567890123456789012345678901234567addadaaa", 1, format.parse("21/12/2012"), "alejandro", "on progress"));
        todoRepository.save(new Todo("test21", 12, format.parse("21/12/2022"), "camilo", "ready"));
        todoRepository.save(new Todo("test22", -1, format.parse("21/12/2022"), "camilo", "ready"));
        todoRepository.save(new Todo("test23", 0, format.parse("21/12/2022"), "sebastian", "done"));
        todoRepository.save(new Todo("test24", 6, format.parse("21/12/2022"), "alejandro", "ready"));
        todoRepository.save(new Todo("012345sdsdsds67890123456789012345678901234567addadaaa", 1, format.parse("21/12/2012"), "santiago", "on progress"));
    }


}