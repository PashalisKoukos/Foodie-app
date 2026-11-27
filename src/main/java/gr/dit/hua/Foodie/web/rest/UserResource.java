package gr.dit.hua.Foodie.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.dit.hua.Foodie.core.service.UserDataService;
import gr.dit.hua.Foodie.core.service.model.UserView;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing {@code Person} resource.
 */
@RestController
@RequestMapping(value = "/api/v1/person", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {

    private final UserDataService userDataService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserResource(final UserDataService userDataService) {
        if (userDataService == null) throw new NullPointerException();
        this.userDataService = userDataService;
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("")
    public List<UserView> people() {
        final List<UserView> personViewList = this.userDataService.getAllPeople();
        return personViewList;
    }

    // Σημείωση: Το Spring μετατρέπει τα java instances σε JSON. Παρακάτω, αν θέλαμε να το κάνουμε χειροκίνητα:
    // ΑΚΡΙΒΩΣ το ίδιο αποτέλεσμα. Κάπως έτσι λειτουργεί εσωτερικά το Spring.
    /*
    @GetMapping("/example")
    public String peopleManualJson() throws IOException {
        final List<PersonView> personViewList = this.personDataService.getAllPeople();
        final StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, personViewList);
        final String jsonString = stringWriter.toString();
        return jsonString;
    }
    */
}
