package vn.finn.spring.demojpawithmysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//Entity (Đối tượng tương ứng với Table trong DB)
//Kiểu dữ liệu của khóa chính (primary key)
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
