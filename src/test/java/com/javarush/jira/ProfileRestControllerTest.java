package com.javarush.jira;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.common.util.validation.ValidationUtil;
import com.javarush.jira.login.AuthUser;
import com.javarush.jira.login.User;
import com.javarush.jira.login.internal.UserRepository;
import com.javarush.jira.profile.ContactTo;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.internal.Contact;
import com.javarush.jira.profile.internal.Profile;
import com.javarush.jira.profile.internal.ProfileMapper;
import com.javarush.jira.profile.internal.ProfileRepository;
import com.javarush.jira.profile.web.AbstractProfileController;
import com.javarush.jira.profile.web.ProfileRestController;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static com.javarush.jira.login.internal.web.UserController.REST_URL;
import static com.javarush.jira.login.internal.web.UserTestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Sql(scripts = "classpath:db/test.sql", config = @SqlConfig(encoding = "UTF-8"))
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfileRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = "/api/profile";
    User user = new User();
    ProfileTo result;
    @Autowired
    private ProfileRestController profileController = new ProfileRestController();

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ProfileMapper mapper;
    @Mock
    AbstractProfileController abstractProfileController;

    @BeforeAll
    public void initUser() {
        user.setId(123L);
        user.setEmail("test@gmail.com");
        user.setPassword("test");
        user.setRoles(null);
        user.setDisplayName("test user");
        user.setFirstName("test user");

        Profile profile = new Profile();
        profile.setId(user.getId());
        result = mapper.toTo(profile);
        profileRepository.getOrCreate(profile.getId());
    }

    @Test
    @Rollback
    public void testGetProfile() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getDisplayName());

        AuthUser principal = new AuthUser(user);
        profileRepository.getOrCreate(principal.id());

        ProfileTo testProfile = profileController.get(principal);
        assertNotNull(testProfile);
        assertEquals(result, testProfile);
    }

    @Test
    public void testGetProfileWhenPrincipalIsNull() {
        assertThrows(NullPointerException.class, () -> {
            ProfileTo testProfile = profileController.get(null);
        });
    }

    @Test
    @Rollback
    public void testGetProfileWhenPrincipalIsNotExists() {
        ProfileTo testProfile = profileController.get(100000000);
        assertNotNull(testProfile);
        assertEquals(new ProfileTo(100000000L, null, null), testProfile);
    }

    @Test
    @Rollback
    public void testUpdateProfile() {
        ProfileTo profileTo = profileController.get(1);
        ContactTo contactTo = new ContactTo();
        contactTo.setCode("mobile");
        contactTo.setValue("+01111111111");
        System.out.println(profileTo);
        if (profileTo != null) {
            profileTo.setContacts(Set.of(contactTo));
            profileController.update(profileTo, 1);
            assertEquals(1, profileController.get(1).getContacts().size());
        }
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getUrlByAdmin() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getUrlByUser() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getUrlByAnautorizatedUser() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

}
