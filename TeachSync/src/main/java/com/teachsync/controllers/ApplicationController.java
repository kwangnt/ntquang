package com.teachsync.controllers;

import com.teachsync.dtos.applicationDetail.ApplicationDetailReadDTO;
import com.teachsync.dtos.center.CenterReadDTO;
import com.teachsync.dtos.recruitmentCampaign.RecruitmentCampaignReadDTO;
import com.teachsync.dtos.request.RequestCreateDTO;
import com.teachsync.dtos.request.RequestReadDTO;
import com.teachsync.dtos.user.UserReadDTO;
import com.teachsync.services.applicationDetail.ApplicationDetailService;
import com.teachsync.services.recruitmentCampaign.RecruitmentCampaignService;
import com.teachsync.services.teacherRequest.TeacherRequestService;
import com.teachsync.utils.Constants;
import com.teachsync.utils.enums.ApplicationDetailType;
import com.teachsync.utils.enums.DtoOption;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/application")
public class ApplicationController {
//xong full thì xóa code luồng xử lý cũ teacher-request

    @Autowired
    RecruitmentCampaignService recruitmentCampaignService;

    @Autowired
    ApplicationDetailService applicationDetailService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/view-job")
    public String viewJob(Model model, @ModelAttribute("mess") String mess) {
        try {
            Page<RecruitmentCampaignReadDTO> dtoPage = recruitmentCampaignService.getPageAllDTO(null, List.of(DtoOption.CENTER));

            if (dtoPage != null) {
                model.addAttribute("campaignList", dtoPage.getContent());
                model.addAttribute("pageNo", dtoPage.getPageable().getPageNumber());
                model.addAttribute("pageTotal", dtoPage.getTotalPages());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("mess", mess);

        return "view-list-job";
    }

    @GetMapping("/application-request")
    public String viewApplicationRequest(Model model, HttpServletRequest request, RedirectAttributes redirect,@ModelAttribute("mess") String mess) {
        //check login
        HttpSession session = request.getSession();
        if (ObjectUtils.isEmpty(session.getAttribute("user"))) {
            redirect.addAttribute("mess", "Làm ơn đăng nhập");
            return "redirect:/";
        }

        try {
            if (!ObjectUtils.isEmpty(request.getParameter("id"))) {
                RecruitmentCampaignReadDTO recruitmentCampaignReadDTO = recruitmentCampaignService.getDTOById(Long.parseLong(request.getParameter("id")), List.of(DtoOption.CENTER));
                model.addAttribute("campaign", recruitmentCampaignReadDTO);
                model.addAttribute("option", "edit");
            }
            model.addAttribute("mess", mess);
        } catch (Exception e) {
            logger.error(e.getMessage());
            redirect.addAttribute("mess", "hiển thị chi tiết chiến dịch thất bại ,lỗi : " + e.getMessage());
            return "redirect:/application/view-job";

        }


        return "teacher-request";
    }

    @PostMapping("/application-request")
    public String addApplicationRequest(Model model, HttpServletRequest request, RedirectAttributes redirect
            , @SessionAttribute(value = "user", required = false) UserReadDTO userDTO) {
        //check login
        HttpSession session = request.getSession();
        if (ObjectUtils.isEmpty(session.getAttribute("user"))) {
            redirect.addAttribute("mess", "Làm ơn đăng nhập");
            return "redirect:/";
        }

        if (List.of(Constants.ROLE_ADMIN, Constants.ROLE_TEACHER).contains(userDTO.getRoleId())) {
            /* Nếu roleId ko có trong list role được cấp phép */
            redirect.addAttribute("mess", "Chương trình không hợp lệ");
            return "redirect:/";
        }
        //DB edit :table application_detail , col applicationId ,detailLink,detailNote thanh chấp nhận null , col Id tự tăng
        ApplicationDetailReadDTO applicationDetailReadDTO = new ApplicationDetailReadDTO();
        String detailType = request.getParameter("detailType");
        switch (detailType) {
            case "CV": {
                applicationDetailReadDTO.setDetailType(ApplicationDetailType.CV);
                break;
            }
            case "CITIZEN_ID": {
                applicationDetailReadDTO.setDetailType(ApplicationDetailType.CITIZEN_ID);
                break;
            }
            case "LICENSE": {
                applicationDetailReadDTO.setDetailType(ApplicationDetailType.LICENSE);
                break;
            }
        }
        applicationDetailReadDTO.setDetailLink(request.getParameter("detailLink"));
        applicationDetailReadDTO.setDetailNote(null);//TODO:upload file
        Long campaignId = Long.parseLong(request.getParameter("campaignId"));

        try {
            applicationDetailService.add(applicationDetailReadDTO, userDTO,campaignId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            redirect.addAttribute("mess", "Tạo đơn ứng tuyển ,lỗi : " + e.getMessage());
            return "redirect:/application/application-request?id="+campaignId;

        }

        redirect.addAttribute("mess", "Tạo đơn ứng tuyển thành công");
        return "redirect:/application/application-request?id="+campaignId;
    }
}