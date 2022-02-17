package vn.finn.spring.demojpawithmysql;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@RequiredArgsConstructor
public class DemoJpaWithMysqlApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DemoJpaWithMysqlApplication.class, args);
        UserRepository userRepository = context.getBean(UserRepository.class);

        userRepository.findAll().forEach(System.out::println);

        User user = userRepository.save(new User());

        System.out.println("User vừa lưu có ID: " + user.getId());

        Long userID = user.getId();
        //Cap nhap User
        user.setAgi(100);
        // Update user
        // Lưu ý, lúc này đối tượng user đã có Id.
        // Nên nó sẽ update vào đối tượng có Id này
        // chứ không insert một bản ghi mới
        userRepository.save(user);

        // Query lấy ra user vừa xong để kiểm tra xem.
        User user2 = userRepository.findById(userID).get();
        System.out.println("User: " + user);
        System.out.println("User2: " + user2);

        // Xóa User khỏi DB
        userRepository.delete(user);

        // In ra kiểm tra xem userId còn tồn tại trong DB không
        User user3 = userRepository.findById(userID).orElse(null);
        System.out.println("User3: " + user2);

    }

}
