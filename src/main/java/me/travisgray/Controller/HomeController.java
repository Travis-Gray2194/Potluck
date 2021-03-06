package me.travisgray.Controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import me.travisgray.Models.Item;
import me.travisgray.Models.User;
import me.travisgray.Repositories.ItemRepository;
import me.travisgray.Repositories.UserRepository;
import me.travisgray.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

/**
 * Created by ${TravisGray} on 11/13/2017.
 */

@Controller
public class HomeController {

    @Autowired
    UserService userService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @RequestMapping("/")
    public String index(){
        return "index3";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/admin")
    public String admin(){
        return "admin";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        model.addAttribute("user",new User());
        return "registration";
    }

    @PostMapping("/register")
    public String processregistration(@Valid @ModelAttribute("user") User user, BindingResult result, Model model ){

        model.addAttribute("user",user);
        if(result.hasErrors()){
            return "registration";
        }else{
            userService.saveUser(user);
            model.addAttribute("message","User Account Successfully Created");
        }
        return "index";
    }

    @GetMapping("/add")
    public String potluckitemForm(Model model){
        model.addAttribute("item", new Item());
        return "additemform";
    }

    @GetMapping("/list")
    public String listApartments(Model model){
        model.addAttribute("itemlist",itemRepository.findAll());
//        Storing Book entries correctly
        return "itemlist";
    }





    //    Must pass created book entry here then save to repository model for thymeleaf loop
    @PostMapping("/add")
    public String processapartmentForm(@Valid @ModelAttribute("item") Item  item, BindingResult result, Model model){

        if (result.hasErrors()){
            return "additemform";
        }

////        Check to see if image value is empty if it is then set default image string for thymeleaf add form
//        System.out.println("Test to see checkout status text field being stored correctly"+readingBook.getCheckoutstatus().equalsIgnoreCase("Borrow"));
//        Need to make sure to add all books to model for thymeleaf access after this route is complete
        itemRepository.save(item);
        model.addAttribute("itemlist",itemRepository.findAll());
        return "itemlist";
    }

    @GetMapping("/update/{id}")
    public String updateBooks(@PathVariable("id") long id, Model model){
        model.addAttribute("item",itemRepository.findOne(id));
        return "redirect:/additemform";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") long id, Model model){
        model.addAttribute("item",itemRepository.findOne(id));
        itemRepository.delete(id);
        return "redirect:/itemlist";
    }

    @RequestMapping("/secure")
    public String secure(HttpServletRequest request, Authentication authentication, Principal principal) {
        Boolean isAdmin = request.isUserInRole("ADMIN");
        Boolean isUSer = request.isUserInRole("USER");
        UserDetails userDetails = (UserDetails)
                authentication.getPrincipal();
        String username = principal.getName();
        return "secure";
    }

    @GetMapping("/addtopledge/{id}")
    public String additemtopledgelist(@PathVariable("id") long id, Model model, Authentication auth){

        Item item = itemRepository.findOne(id);
//        Must use database user not spring security user
        User user = userRepository.findByUsername(auth.getName());
        user.addItem(item);
        model.addAttribute("items4user", itemRepository.findOne(id));
        return "useritemslist";
    }

    @GetMapping("/search")
    public String getSearch(){
        return "searchform";
    }

    @PostMapping("/search")
    public String showSearchResults(HttpServletRequest request, Model model){
        String searchItems = request.getParameter("search");
        model.addAttribute("search",searchItems);
//

//        Expecting multiple parameters or else will throw No parameter available Need to pass as many as are in constructor in Entity.
        model.addAttribute("itemsearch",itemRepository.findAllByItemNameContainingIgnoreCase(searchItems));
//
        return "searchitemlist";
    }
}
