package dk.trustworks.invoicewebui.services;


import com.vaadin.server.VaadinSession;
import dk.trustworks.invoicewebui.model.Role;
import dk.trustworks.invoicewebui.model.Salary;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.repositories.UserStatusRepository;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import lombok.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.model.enums.ConsultantType.*;
import static dk.trustworks.invoicewebui.model.enums.StatusType.ACTIVE;
import static dk.trustworks.invoicewebui.model.enums.StatusType.NON_PAY_LEAVE;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserStatusRepository userStatusRepository;

    public UserService(UserRepository userRepository, UserStatusRepository userStatusRepository) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
    }

    public Optional<User> getLoggedInUser() {
        return Optional.of(VaadinSession.getCurrent().getAttribute(UserSession.class).getUser());
    }

    public User findByUUID(String uuid) {
        return userRepository.findOne(uuid);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findBySlackusername(String userId) {
        return userRepository.findBySlackusername(userId);
    }

    public List<User> findAll() {
        return userRepository.findByOrderByUsername();
    }

    public List<User> findCurrentlyEmployedUsers() {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRepository.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                CONSULTANT.toString(), STAFF.toString(), STUDENT.toString());
    }

    public List<User> findCurrentlyWorkingUsers() {
        String[] statusList = {ACTIVE.toString()};
        return userRepository.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                CONSULTANT.toString(), STAFF.toString(), STUDENT.toString());
    }

    public List<User> findEmployedUsersByDate(LocalDate date, ConsultantType... consultantType) {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRepository.findUsersByDateAndStatusListAndTypes(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                Arrays.stream(consultantType).map(Enum::toString).toArray(String[]::new));
    }

    public List<User> findWorkingUsersByDate(LocalDate date, ConsultantType... consultantType) {
        String[] statusList = {ACTIVE.toString()};
        return userRepository.findUsersByDateAndStatusListAndTypes(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                Arrays.stream(consultantType).map(Enum::toString).toArray(String[]::new));
    }

    public List<User> findCurrentlyEmployedUsers(ConsultantType... consultantType) {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRepository.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                Arrays.stream(consultantType).map(Enum::toString).toArray(String[]::new)).stream().sorted(Comparator.comparing(User::getUsername)).collect(Collectors.toList());
    }

    public List<User> countCurrentlyWorkingEmployees() {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRepository.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                CONSULTANT.toString(), STAFF.toString(), STUDENT.toString());
    }

    public int getUserSalary(User user, LocalDate date) {
        Salary salary = user.getSalaries().stream().filter(value -> value.getActivefrom().isBefore(date)).max(Comparator.comparing(Salary::getActivefrom)).orElse(new Salary(date, 0, user));
        return salary.getSalary();
    }

    public UserStatus getUserStatus(User user, LocalDate date) {
        return user.getStatuses().stream().filter(value -> value.getStatusdate().isBefore(date)).max(Comparator.comparing(UserStatus::getStatusdate)).orElse(new UserStatus(user, ConsultantType.STAFF, StatusType.TERMINATED, date, 0));
    }

    public int getMonthSalaries(LocalDate date, String... consultantTypes) {
        System.out.println("date = [" + date + "], consultantTypes = [" + consultantTypes + "]");
        String[] statusList = {ACTIVE.toString()};
        for (User users : userRepository.findUsersByDateAndStatusListAndTypes(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), statusList, consultantTypes)) {
            Salary salary1 = users.getSalaries().stream().filter(salary -> salary.getActivefrom().isBefore(date)).max(Comparator.comparing(Salary::getActivefrom)).orElse(new Salary(date, 0, null));
            System.out.println(users.getUsername()+": "+salary1.getSalary());
        }

        return userRepository.findUsersByDateAndStatusListAndTypes(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), statusList, consultantTypes)
                .stream().mapToInt(value ->
                        value.getSalaries().stream().filter(salary -> salary.getActivefrom().isBefore(date)).max(Comparator.comparing(Salary::getActivefrom)).orElse(new Salary(date, 0, null)).getSalary()
                ).sum();
    }

    public int calculateCapacityByMonthByUser(String useruuid, String statusdate) {
        return userRepository.calculateCapacityByMonthByUser(useruuid, statusdate);
    }

    public LocalDate findEmployedDate(@NonNull User user) {
        return userStatusRepository.findByUserAndTypeAndStatusOrderByStatusdateAsc(user, CONSULTANT, ACTIVE).get(0).getStatusdate();
    }

    public boolean isExternal(User user) {
        boolean isExternal = false;
        for (
                Role role : user.getRoleList()) {
            if(role.getRole().equals(RoleType.EXTERNAL)) isExternal = true;
        }
        return isExternal;
    }

    /**
     * Get user status
     * @param user the user in question
     * @param first is this the first status or latest
     * @param type type of status
     * @return
     */
    public UserStatus getStatus(User user, boolean first, StatusType type) {
        List<UserStatus> statuses = user.getStatuses();
        statuses = statuses.stream().filter(userStatus -> userStatus.getStatus().equals(type)).sorted(Comparator.comparing(UserStatus::getStatusdate)).collect(Collectors.toList());
        return first?statuses.get(0):statuses.get(statuses.size()-1);
    }

    public boolean isEmployed(User user) {
        System.out.println("UserService.isEmployed");
        System.out.println("user = [" + user + "]");
        return findCurrentlyEmployedUsers().stream().anyMatch(employedUser -> employedUser.getUuid().equals(user.getUuid()));
    }

    public boolean isActive(User user, LocalDate onDate, ConsultantType... consultantTypes) {
        List<User> currentlyWorkingUsers = findWorkingUsersByDate(onDate, consultantTypes);

        boolean anyMatch = currentlyWorkingUsers.stream().anyMatch(employedUser -> employedUser.getUuid().equals(user.getUuid()));
        return anyMatch;
    }

    @Transactional
    @CacheEvict("user")
    public User save(User user) {
        return userRepository.save(user);
    }

}
