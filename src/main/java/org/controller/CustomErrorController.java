package org.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global handler for /error in order to avoid Spring default JSON: "status 999" when no status code
 * is set (for example RequestRejectedException from Spring Security). We simply log the path and
 * forward the user back to the home page with a generic message.
 */
@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        log.warn("Unhandled error - status: {}, uri: {}, message: {}", status, uri, message);

        // show friendly toast on homepage
        redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, vui lòng thử lại.");
        return "redirect:/home";
    }

    // since Spring Boot 2.3, implementing getErrorPath is no longer required (deprecated),
    // but we leave it empty for compatibility
    public String getErrorPath() {
        return null;
    }
}
