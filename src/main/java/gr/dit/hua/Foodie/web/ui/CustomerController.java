package gr.dit.hua.Foodie.web.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @GetMapping("/customer_page")
    public String home() {
        return "customer/customer_page"; // renders templates/customer/customer_page.html
    }
}