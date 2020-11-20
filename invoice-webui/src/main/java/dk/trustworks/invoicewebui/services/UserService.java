package dk.trustworks.invoicewebui.services;


import com.vaadin.server.VaadinSession;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.dto.Capacity;
import dk.trustworks.invoicewebui.model.dto.LoginToken;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.network.rest.UserRestService;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.model.enums.ConsultantType.*;
import static dk.trustworks.invoicewebui.model.enums.StatusType.ACTIVE;
import static dk.trustworks.invoicewebui.model.enums.StatusType.NON_PAY_LEAVE;
import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

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

    public Optional<LoginToken> getLoggedInUserToken() {
        return Optional.of(VaadinSession.getCurrent().getAttribute(UserSession.class).getLoginToken());
    }

    public User findByUUID(String uuid, boolean shallow) {
        if(uuid==null) return null;
        return userRestService.findOne(uuid, shallow);
    }

    public static User GetUserFromUUID(String useruuid, List<User> users) {
        return users.stream().filter(u -> u.getUuid().equals(useruuid)).findFirst().orElse(new User());
    }

    public User findByUsername(String username) {
        return userRestService.findByUsername(username);
    }

    public User[] findBySlackusername(String userId) {
        return userRestService.findBySlackusername(userId);
    }

    public List<User> findAll(boolean shallow) {
        return userRestService.findByOrderByUsername(true);
    }

    public List<User> findCurrentlyEmployedUsers(boolean shallow) {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                shallow,
                CONSULTANT.toString(), STAFF.toString(), STUDENT.toString());
    }

    public List<User> findEmployedUsersByDate(LocalDate date, boolean shallow, ConsultantType... consultantType) {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                shallow,
                Arrays.stream(consultantType).map(Enum::toString).toArray(String[]::new));
    }

    public List<User> findWorkingUsersByDate(LocalDate date, boolean shallow, ConsultantType... consultantType) {
        String[] statusList = {ACTIVE.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                shallow,
                Arrays.stream(consultantType).map(Enum::toString).toArray(String[]::new));
    }

    public List<User> findCurrentlyEmployedUsers(boolean shallow, ConsultantType... consultantType) {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                shallow,
                Arrays.stream(consultantType).map(Enum::toString).toArray(String[]::new)).stream().sorted(Comparator.comparing(User::getUsername)).collect(Collectors.toList());
    }

    public List<Role> findUserRoles(String useruuid) {
        return userRestService.findUserRoles(useruuid);
    }

    public List<User> countCurrentlyWorkingEmployees() {
        String[] statusList = {ACTIVE.toString(), NON_PAY_LEAVE.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                statusList,
                true,
                CONSULTANT.toString(), STAFF.toString(), STUDENT.toString());
    }

    public UserContactinfo findUserContactinfo(String useruuid) {
        return userRestService.findUserContactinfo(useruuid);
    }

    public void updateUserContactinfo(String useruuid, UserContactinfo userContactinfo) {
        userRestService.updateUserContactinfo(useruuid, userContactinfo);
    }

    public int getUserSalary(User user, LocalDate date) {
        Salary salary = user.getSalaries().stream().filter(value -> value.getActivefrom().isBefore(date)).max(Comparator.comparing(Salary::getActivefrom)).orElse(new Salary(date, 0));
        return salary.getSalary();
    }

    public UserStatus getUserStatus(User user, LocalDate date) {
        return user.getStatuses().stream().filter(value -> value.getStatusdate().isBefore(date)).max(Comparator.comparing(UserStatus::getStatusdate)).orElse(new UserStatus(ConsultantType.STAFF, StatusType.TERMINATED, date, 0));
    }

    public int calcMonthSalaries(LocalDate date, String... consultantTypes) {
        String[] statusList = {ACTIVE.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), statusList, false, consultantTypes)
                .stream()
                .mapToInt(value -> value.getSalaries()
                        .stream()
                        .filter(salary -> salary.getActivefrom().isBefore(date))
                        .max(Comparator.comparing(Salary::getActivefrom))
                        .orElse(new Salary(date, 0))
                        .getSalary()
                ).sum();
    }

    public int calculateCapacityByMonthByUser(String useruuid, String statusdate) {
        return userRestService.calculateCapacityByMonthByUser(useruuid, statusdate);
    }

    public List<Capacity> calculateCapacityByPeriod(LocalDate fromDate, LocalDate toDate) {
        return userRestService.calculateCapacityByPeriod(fromDate, toDate);
    }

    public List<UserStatus> findByUserAndTypeAndStatusOrderByStatusdateAsc(User user, ConsultantType type, StatusType status) {
        return userRestService.findOne(user.getUuid(), true).getStatuses()
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
        System.out.println(user.getUsername()+" statuses = " + statuses.size());
        statuses = statuses.stream().filter(userStatus -> userStatus.getStatus().equals(type)).sorted(Comparator.comparing(UserStatus::getStatusdate).reversed()).collect(Collectors.toList());
        statuses.forEach(System.out::println);
        if(statuses.size()==0) return new UserStatus(CONSULTANT, StatusType.TERMINATED, LocalDate.of(2014,2, 1), 0);
        return first?statuses.get(0):statuses.get(statuses.size()-1);
    }

    public List<User> findByStatus(StatusType statusType) {
        String[] statusTypes = {statusType.toString()};
        return userRestService.findUsersByDateAndStatusListAndTypes(stringIt(LocalDate.now()), statusTypes, true, CONSULTANT.toString(), STAFF.toString(), STUDENT.toString());
    }

    public List<UserStatus> findUserStatusList(String useruuid) {
        return userRestService.findUserStatusList(useruuid);
    }

    public List<Salary> findUserSalaries(String useruuid) {
        return userRestService.findUserSalaries(useruuid);
    }

    public boolean isEmployed(String useruuid) {
        return findCurrentlyEmployedUsers(false).stream().anyMatch(employedUser -> employedUser.getUuid().equals(useruuid));
    }

    public boolean isActive(User user, LocalDate onDate, ConsultantType... consultantTypes) {
        List<User> currentlyWorkingUsers = findWorkingUsersByDate(onDate, true, consultantTypes);
        return currentlyWorkingUsers.stream().anyMatch(employedUser -> employedUser.getUuid().equals(user.getUuid()));
    }

    public User create(User user) {
        return userRestService.create(user);
    }

    public void update(User user) {
        Validate.notNull(user.getUuid());
        userRestService.update(user);
    }

    public void updateBirthday(User user) {
        Validate.notNull(user.getUuid());
        userRestService.updateBirthday(user);
    }

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }

    public static UserService get() {
        return instance;
    }

    public LoginToken login(String username, String password) {
        return userRestService.login(username, password);
    }

    public void deleteSalaries(String useruuid, Set<Salary> salaries) {
        userRestService.deleteSalaries(useruuid, salaries);
    }

    public void deleteUserStatuses(String useruuid, Set<UserStatus> userStatuses) {
        userRestService.deleteUserStatuses(useruuid, userStatuses);
    }

    public void deleteRoles(User user, List<Role> roles) {
        userRestService.deleteRoles(user, roles);
    }

    public void create(String useruuid, Salary salary) {
        userRestService.create(useruuid, salary);
    }

    public void create(String useruuid, UserStatus userStatus) {
        userRestService.create(useruuid, userStatus);
    }

    public void create(String useruuid, Role role) {
        userRestService.create(useruuid, role);
    }

}
