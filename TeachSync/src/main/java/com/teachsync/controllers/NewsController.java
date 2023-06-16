package com.teachsync.controllers;

import com.teachsync.dtos.news.NewsReadDTO;
import com.teachsync.dtos.user.UserReadDTO;
import com.teachsync.entities.News;
import com.teachsync.entities.User;
import com.teachsync.repositories.NewsRepository;
import com.teachsync.repositories.UserRepository;
import com.teachsync.services.news.NewsService;
import com.teachsync.utils.MiscUtil;
import com.teachsync.utils.enums.Status;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class NewsController {
    @Autowired
    NewsRepository newsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private NewsService newsService;

    @Autowired
    private MiscUtil miscUtil;

    @GetMapping("/createnews")
    public String createNews(Model model, HttpSession session) {
        UserReadDTO user = (UserReadDTO) session.getAttribute("loginUser");
        if (user == null || user.getRoleId() != 1) {
            return "redirect:/";
        } else return "create-news";
    }

    @PostMapping("/submitcreatenews")
    public String submitCreateNews(Model model, HttpSession session,
                                   @RequestParam String title,
                                   @RequestParam String description,
                                   @RequestParam String content) {

        UserReadDTO user = (UserReadDTO) session.getAttribute("loginUser");
        if (user == null || user.getRoleId() != 1) {
            return "redirect:/";
        }
        System.out.println("user id = " + user.getId());

        User user1 = userRepository.findById(user.getId()).orElse(null);

        News news = new News(title, description, content, user1, user1.getId(), Status.CREATED);

        newsRepository.save(news);
        return "redirect:/";
    }


    @GetMapping("/editnews")
    public String editNews(Model model, HttpSession session,
                           @RequestParam String id) {

        UserReadDTO user = (UserReadDTO) session.getAttribute("loginUser");
        if (user == null || user.getRoleId() != 1) {
            return "redirect:/";
        }
        System.out.println("user id = " + user.getId());


        News news = newsRepository.findAllById(Long.parseLong(id));
        model.addAttribute("news", news);
        return "edit-news";
    }

    @PostMapping("/submiteditnews")
    public String submitEditNews(Model model, HttpSession session,
                                 @RequestParam String idNews,
                                 @RequestParam String title,
                                 @RequestParam String description,
                                 @RequestParam String content) {

        UserReadDTO user = (UserReadDTO) session.getAttribute("loginUser");
        if (user == null || user.getRoleId() != 1) {
            return "redirect:/";
        }
        System.out.println("user id = " + user.getId());

        User user1 = userRepository.findById(user.getId()).orElse(null);

        News news = new News(Long.parseLong(idNews), title, description, content, user1, user1.getId(), Status.UPDATED);

        newsRepository.save(news);
        return "redirect:/";
    }


    @GetMapping("/news")
    public String news(Model model) {
        try {
            Page<NewsReadDTO> dtoPage = newsService.getPageDTOAll(null);

            if (dtoPage != null) {
                model.addAttribute("newsList", dtoPage.getContent());
                model.addAttribute("pageNo", dtoPage.getPageable().getPageNumber());
                model.addAttribute("pageTotal", dtoPage.getTotalPages());

            }
        }catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMsg", "Server error, please try again later");
        }

        return "list-news";
    }

    @GetMapping("/news-detail")
    public String getDetailById(
            @RequestParam Long id,
            Model model) {
        try {
            NewsReadDTO news = newsService.getDTOById(id);

            if (news == null) {
                /* Not found by Id */
                return "redirect:/course";
            }

            model.addAttribute("news", news);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMsg", "Server error, please try again later");
        }

        return "news-detail";
    }


}
