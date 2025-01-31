// package net.smart.home.controller.Controller.GET;

// import org.easymock.EasyMock;
// import org.easymock.Mock;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mockito;
// import org.mockito.MockitoAnnotations;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;



// @WebMvcTest(GetController.class)
// public class GetControllerTest {

//     private MockMvc mockMvc;

//     @Mock
//     private GetController mockController;

//     @InjectMocks
//     private GetController getController;

//     @BeforeEach
//     void setUp(){
//         MockitoAnnotations.openMocks(this);
//         mockMvc = MockMvcBuilders.standaloneSetup(getController).build();
//         Mockito.doNothing().when(mockController).testEc2();
//         Mockito.doNothing().when(mockController).testPi();
//     }

//     @DisplayName("Test of the /testec2 endpoint")
//     @Test
//     void testEc2() throws Exception {
//         mockMvc.perform(get("/api/testec2"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("")); // Expecting empty response
//     }

//     @DisplayName("Test of the /testpi endpoint")
//     @Test
//     void testPi() throws Exception {
//         mockMvc.perform(get("/api/testpi"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(""));
//     }


// }
