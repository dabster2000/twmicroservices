package dk.trustworks.invoicewebui.services;


import com.vaadin.server.VaadinSession;
import dk.trustworks.invoicewebui.model.Role;
import dk.trustworks.invoicewebui.model.Salary;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.network.rest.UserRestService;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import lombok.NonNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserService implements InitializingBean {

    private static UserService instance;

    private final UserRestService userRestService;

    @Autowired
    public UserService(UserRestService userRestService) {
        this.userRestService = userRestService;
    }

    public Optional<User> getLoggedInUser() {
        return Optional.of(VaadinSession.getCurrent().getAttribute(UserSession.class).getUser());
    }

    public User findByUUID(String uuid) {
        return userRestService.findOne(uuid);
    }

    public User findByUsername(String username) {
        return userRestService.findByUsername(username);
    }

    public User[] findBySlackusername(String userId) {
        return userRestService.findBySlackusername(userId);
    }

    public List<User> findAll() {
        return userRestService.findByOrderByUsername();
    }

    public List<User> findCurrentlyEmployedUsers() {
        System.out.println("UserService.findCurrentlyEmployedUsers");
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                CONSULTANT.toString(), STAFF.toString(), STUDENT.toString());
    }

    public List<User> findCurrentlyWorkingUsers() {
        String[] statusList = {ACTIVE.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                CONSULTANT.toString(), STAFF.toString(), STUDENT.toString());
    }

    public List<User> findEmployedUsersByDate(LocalDate date, ConsultantType... consultantType) {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                Arrays.stream(consultantType).map(Enum::toString).toArray(String[]::new));
    }

    public List<User> findWorkingUsersByDate(LocalDate date, ConsultantType... consultantType) {
        String[] statusList = {ACTIVE.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                Arrays.stream(consultantType).map(Enum::toString).toArray(String[]::new));
    }

    public List<User> findCurrentlyEmployedUsers(ConsultantType... consultantType) {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                Arrays.stream(consultantType).map(Enum::toString).toArray(String[]::new)).stream().sorted(Comparator.comparing(User::getUsername)).collect(Collectors.toList());
    }

    public List<User> countCurrentlyWorkingEmployees() {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                CONSULTANT.toString(), STAFF.toString(), STUDENT.toString());
    }

    public int getUserSalary(User user, LocalDate date) {
        Salary salary = user.getSalaries().stream().filter(value -> value.getActivefrom().isBefore(date)).max(Comparator.comparing(Salary::getActivefrom)).orElse(new Salary(date, 0));
        return salary.getSalary();
    }

    public UserStatus getUserStatus(User user, LocalDate date) {
        return user.getStatuses().stream().filter(value -> value.getStatusdate().isBefore(date)).max(Comparator.comparing(UserStatus::getStatusdate)).orElse(new UserStatus(ConsultantType.STAFF, StatusType.TERMINATED, date, 0));
    }

    public int getMonthSalaries(LocalDate date, String... consultantTypes) {
        String[] statusList = {ACTIVE.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), statusList, consultantTypes)
                .stream().mapToInt(value ->
                        value.getSalaries().stream().filter(salary -> salary.getActivefrom().isBefore(date)).max(Comparator.comparing(Salary::getActivefrom)).orElse(new Salary(date, 0)).getSalary()
                ).sum();
    }

    public int calculateCapacityByMonthByUser(String useruuid, String statusdate) {
        return userRestService.calculateCapacityByMonthByUser(useruuid, statusdate);
    }

    public List<UserStatus> findByUserAndTypeAndStatusOrderByStatusdateAsc(User user, ConsultantType type, StatusType status) {
        return userRestService.findOne(user.getUuid()).getStatuses()
                .stream()
                .filter(userStatus -> userStatus.getStatus().equals(status) && userStatus.getType().equals(type))
                .sorted(Comparator.comparing(UserStatus::getStatusdate).reversed())
                .collect(Collectors.toList());
    }

    public Optional<LocalDate> findEmployedDate(@NonNull User user) {
        List<UserStatus> statusdateAsc = findByUserAndTypeAndStatusOrderByStatusdateAsc(user, CONSULTANT, ACTIVE);
        if(statusdateAsc.size()==0) return Optional.empty();
        return Optional.ofNullable(statusdateAsc.get(0).getStatusdate());
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
        return userRestService.save(user);
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("UserService.afterPropertiesSet");
        instance = this;
    }

    public static UserService get() {
        return instance;
    }

    public boolean login(String username, String password) {
        return userRestService.login(username, password);
    }
}
