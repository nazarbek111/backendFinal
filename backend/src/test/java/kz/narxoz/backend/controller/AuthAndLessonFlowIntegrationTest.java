package kz.narxoz.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.narxoz.backend.dto.request.ChildRequest;
import kz.narxoz.backend.dto.request.LoginRequest;
import kz.narxoz.backend.dto.request.RegisterRequest;
import kz.narxoz.backend.dto.request.UnitRequest;
import kz.narxoz.backend.dto.request.LessonRequest;
import kz.narxoz.backend.dto.request.ExerciseRequest;
import kz.narxoz.backend.dto.request.ExerciseSubmitRequest;
import kz.narxoz.backend.entity.enums.LessonType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthAndLessonFlowIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    static String accessToken;
    static Long childId;
    static Long unitId;
    static Long lessonId;
    static Long exerciseId;

    @Test @Order(1)
    void registerParent_returns201() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Test Parent");
        req.setEmail("testparent@example.com");
        req.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.role").value("PARENT"))
                .andReturn();

        accessToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
    }

    @Test @Order(2)
    void registerSameEmail_returns409() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Duplicate");
        req.setEmail("testparent@example.com");
        req.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is4xxClientError());
    }

    @Test @Order(3)
    void loginParent_returnsTokens() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("testparent@example.com");
        req.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        accessToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
    }

    @Test @Order(4)
    void loginWithWrongPassword_returns4xx() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("testparent@example.com");
        req.setPassword("wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is4xxClientError());
    }

    @Test @Order(5)
    void createChild_returns201() throws Exception {
        ChildRequest req = new ChildRequest();
        req.setName("Alice");
        req.setAge(5);
        req.setAvatar("avatar1");

        MvcResult result = mockMvc.perform(post("/api/v1/children")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.xpPoints").value(0))
                .andReturn();

        childId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    @Test @Order(6)
    void createChild_invalidAge_returns422() throws Exception {
        ChildRequest req = new ChildRequest();
        req.setName("Baby");
        req.setAge(1); // too young — should fail validation
        req.setAvatar("avatar1");

        mockMvc.perform(post("/api/v1/children")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test @Order(7)
    void getMyChildren_returnsList() throws Exception {
        mockMvc.perform(get("/api/v1/children")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    @Test @Order(8)
    void createUnit_returns201() throws Exception {
        UnitRequest req = new UnitRequest();
        req.setTitle("Letters A-E");
        req.setDescription("Learn the first 5 letters");
        req.setOrderIndex(1);

        MvcResult result = mockMvc.perform(post("/api/v1/units")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Letters A-E"))
                .andReturn();

        unitId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    @Test @Order(9)
    void createLesson_returns201() throws Exception {
        LessonRequest req = new LessonRequest();
        req.setTitle("Letter A");
        req.setType(LessonType.PHONICS);
        req.setUnitId(unitId);
        req.setOrderIndex(1);
        req.setXpReward(20);

        MvcResult result = mockMvc.perform(post("/api/v1/lessons")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Letter A"))
                .andReturn();

        lessonId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    @Test @Order(10)
    void createExercise_returns201() throws Exception {
        ExerciseRequest req = new ExerciseRequest();
        req.setQuestion("What sound does the letter A make?");
        req.setCorrectAnswer("aah");
        req.setOptions("[\"aah\",\"buh\",\"kuh\"]");
        req.setOrderIndex(1);

        MvcResult result = mockMvc.perform(post("/api/v1/exercises/lesson/" + lessonId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.question").exists())
                // FIX: correctAnswer must NOT be in response
                .andExpect(jsonPath("$.correctAnswer").doesNotExist())
                .andReturn();

        exerciseId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    @Test @Order(11)
    void submitExercise_correctAnswer_returnsCorrect() throws Exception {
        ExerciseSubmitRequest req = new ExerciseSubmitRequest();
        req.setChildId(childId);
        req.setAnswer("aah"); // correct answer
        req.setTimeTaken(3000);

        mockMvc.perform(post("/api/v1/exercises/" + exerciseId + "/submit")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Correct!"));
    }

    @Test @Order(12)
    void submitExercise_wrongAnswer_returnsIncorrect() throws Exception {
        ExerciseSubmitRequest req = new ExerciseSubmitRequest();
        req.setChildId(childId);
        req.setAnswer("buh"); // wrong answer
        req.setTimeTaken(5000);

        mockMvc.perform(post("/api/v1/exercises/" + exerciseId + "/submit")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Incorrect. Try again!"));
    }

    @Test @Order(13)
    void completeLesson_awardsXpAndReturnsProgress() throws Exception {
        mockMvc.perform(post("/api/v1/lessons/" + lessonId + "/complete")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("childId", String.valueOf(childId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.xpEarned").value(20));
    }

    @Test @Order(14)
    void completeLesson_twice_returns409() throws Exception {
        mockMvc.perform(post("/api/v1/lessons/" + lessonId + "/complete")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("childId", String.valueOf(childId)))
                .andExpect(status().isConflict());
    }

    @Test @Order(15)
    void getProgress_returnsCompletedLesson() throws Exception {
        mockMvc.perform(get("/api/v1/children/" + childId + "/progress")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].completed").value(true));
    }

    @Test @Order(16)
    void getBadges_returnsFirstLessonBadge() throws Exception {
        mockMvc.perform(get("/api/v1/children/" + childId + "/badges")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("First Lesson"));
    }

    @Test @Order(17)
    void getLessons_withFilters_returnsFiltered() throws Exception {
        mockMvc.perform(get("/api/v1/lessons")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("unitId", String.valueOf(unitId))
                        .param("type", "PHONICS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Letter A"));
    }

    @Test @Order(18)
    void unauthenticated_request_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/children"))
                .andExpect(status().isUnauthorized());
    }

    @Test @Order(19)
    void getNotifications_returnsParentNotifications() throws Exception {
        mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}