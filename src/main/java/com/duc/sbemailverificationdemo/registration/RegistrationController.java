package com.duc.sbemailverificationdemo.registration;

import com.duc.sbemailverificationdemo.event.RegistrationCompleteEvent;
import com.duc.sbemailverificationdemo.registration.token.VerificationToken;
import com.duc.sbemailverificationdemo.registration.token.VerificationTokenRepository;
import com.duc.sbemailverificationdemo.user.User;
import com.duc.sbemailverificationdemo.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository tokenRepository;
    @PostMapping
    public String registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest request) {
        User user = userService.registerUser(registrationRequest);
        // publish registration event
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "Success! Please, check your email for complete your registration";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token) {
        VerificationToken theToken = tokenRepository.findByToken(token);
        if(theToken.getUser().isEnabled()) {
            return "This account has already been verify, please, login.";
        }
        String  verificationResult = userService.validateToken(token);
        if(verificationResult.equalsIgnoreCase("valid")) {
            return "Email verified successfully. Now you can login your account";
        }
        return "Invalid verification token";
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }
}
