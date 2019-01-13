package dk.trustworks.invoicewebui.services;


import dk.trustworks.invoicewebui.model.Salary;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static dk.trustworks.invoicewebui.model.enums.ConsultantType.*;
import static dk.trustworks.invoicewebui.model.enums.StatusType.ACTIVE;
import static dk.trustworks.invoicewebui.model.enums.StatusType.NON_PAY_LEAVE;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(value = "user")
    public User findByUUID(String uuid) {
        return userRepository.findOne(uuid);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Cacheable("user")
    public User findBySlackusername(String userId) {
        return userRepository.findBySlackusername(userId);
    }

    @Cacheable(value = "user", key = "#root.methodName")
    public List<User> findAll() {
        return userRepository.findByOrderByUsername();
    }

    @Cacheable(value = "user", key = "#root.methodName")
    public List<User> findCurrentlyWorkingEmployees() {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRepository.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                CONSULTANT.toString(), STAFF.toString(), STUDENT.toString());
    }

    @Cacheable("user")
    public List<User> findCurrentlyWorkingEmployees(ConsultantType... consultantType) {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRepository.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                Arrays.stream(consultantType).map(Enum::toString).toArray(String[]::new));
    }

    public List<User> countCurrentlyWorkingEmployees() {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRepository.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                CONSULTANT.toString(), STAFF.toString(), STUDENT.toString());
    }

    @Cacheable("salary")
    public int getUserSalary(User user, LocalDate date) {
        Salary salary = user.getSalaries().stream().filter(value -> value.getActivefrom().isBefore(date)).max(Comparator.comparing(Salary::getActivefrom)).orElse(new Salary(date, 0, user));
        return salary.getSalary();
    }

    @Cacheable("user")
    public int calculateCapacityByMonthByUser(String useruuid, String statusdate) {
        return userRepository.calculateCapacityByMonthByUser(useruuid, statusdate);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

}
