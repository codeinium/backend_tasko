package ru.kpfu.codeinium.tasko.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.codeinium.tasko.constants.UserRole;
import ru.kpfu.codeinium.tasko.dto.ProjectCreateDto;
import ru.kpfu.codeinium.tasko.dto.RegistrationForm;
import ru.kpfu.codeinium.tasko.entity.Project;
import ru.kpfu.codeinium.tasko.entity.ProjectMembership;
import ru.kpfu.codeinium.tasko.entity.ProjectRole;
import ru.kpfu.codeinium.tasko.entity.User;
import ru.kpfu.codeinium.tasko.exception.EmailAlreadyExistsException;
import ru.kpfu.codeinium.tasko.repository.ProjectMembershipRepository;
import ru.kpfu.codeinium.tasko.repository.ProjectRepository;
import ru.kpfu.codeinium.tasko.repository.ProjectRoleRepository;
import ru.kpfu.codeinium.tasko.repository.UserRepository;
import ru.kpfu.codeinium.tasko.service.interfaces.RegistrationServiceInterface;

@Service
@RequiredArgsConstructor
public class RegistrationService implements RegistrationServiceInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectRepository projectRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectMembershipRepository membershipRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public RegistrationForm register(RegistrationForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setName(form.getName());
        user.setRole(UserRole.USER);

        emailService.sendVerificationEmail(user);

        userRepository.save(user);

        Project project = null;
        System.out.println(form.isTeamLead());
        if (form.isTeamLead()) {
            ProjectCreateDto projectRequest = new ProjectCreateDto();
            projectRequest.setName(user.getName() + "'s Project");
            project = createProject(projectRequest, user);

            ProjectRole teamLeadRole = getOrCreateTeamLeadRole();
            user.getProjectRoles().add(teamLeadRole);
            userRepository.save(user);
        }


        return new RegistrationForm(
                user.getEmail(),
                user.getName(),
                user.getPassword(),
                form.isTeamLead(),
                user.getId(),
                user.getRole().name(),
                project != null ? project.getId() : null
        );
    }

    private Project createProject(ProjectCreateDto request, User user) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setTeamLead(user);
        project = projectRepository.save(project);

        ProjectRole teamLeadRole = getOrCreateTeamLeadRole();

        ProjectMembership membership = new ProjectMembership();
        membership.setUser(user);
        membership.setProject(project);
        membership.setRole(teamLeadRole.getName());
        membershipRepository.save(membership);

        return project;
    }

    private ProjectRole getOrCreateTeamLeadRole() {
        return projectRoleRepository.findByName("TeamLead")
                .orElseGet(() -> {
                    ProjectRole role = new ProjectRole();
                    role.setName("TeamLead");
                    role.setDescription("Project creator and manager");
                    return projectRoleRepository.save(role);
                });
    }
}